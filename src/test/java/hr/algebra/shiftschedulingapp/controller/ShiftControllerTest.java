package hr.algebra.shiftschedulingapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.dto.ShiftDto;
import hr.algebra.shiftschedulingapp.model.jpa.Shift;
import hr.algebra.shiftschedulingapp.repository.ShiftRepository;
import hr.algebra.shiftschedulingapp.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/shift.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class ShiftControllerTest extends IntegrationTest {

  @Autowired
  private ShiftRepository shiftRepository;

  private static final String SHIFTS_PATH = "/group/%s/shifts";
  private static final String SHIFT_PATH = "/group/%s/shift";
  private static final String SHIFT_PATH_WITH_ID = "/group/%s/shift/%s";

  private static final Long SHIFT_1 = 1L;
  private static final Long SHIFT_10 = 10L;

  private static final String MANAGER_EMAIL = "john@email.com";
  private static final String EMPLOYEE_EMAIL = "james@email.com";

  private static final String ERROR_DUPLICATE = "A shift on this date with the same start and end time already exists in this group";
  private static final String ERROR_NOT_FOUND = "Shift not found";

  private static final ShiftDto NEW_SHIFT = new ShiftDto(SHIFT_1, LocalDate.of(2024, 2, 3), LocalTime.of(14, 0), LocalTime.of(21, 0));
  private static final ShiftDto OLD_SHIFT = new ShiftDto(SHIFT_1, LocalDate.of(2020, 1, 1), LocalTime.of(8, 0), LocalTime.of(12, 0));
  private static final ShiftDto MODIFIED_SHIFT = new ShiftDto(SHIFT_1, LocalDate.of(2020, 1, 2), LocalTime.of(8, 0), LocalTime.of(13, 5));
  private static final ShiftDto MODIFIED_SHIFT_NOT_FOUND = new ShiftDto(SHIFT_10, LocalDate.of(2020, 1, 2), LocalTime.of(8, 0), LocalTime.of(13, 5));

  @Test
  void getShifts_employeeAndNoFilters_returnsAllShifts() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    MvcResult result = mockMvc.perform(get(format(SHIFTS_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<ShiftDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(3, items.size());
  }

  @Test
  void getShifts_employeeAndFilters_returnsMatchingShifts() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    MvcResult result = mockMvc.perform(get(format(SHIFTS_PATH, GROUP_1) + "?endDate=2021-05-06")
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<ShiftDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(2, items.size());
  }

  @Test
  void getShifts_employeeNotPartOfGroup_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(get(format(SHIFTS_PATH, GROUP_2))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  void addShift_manager_ok() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(SHIFT_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(NEW_SHIFT)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(4, shiftRepository.countByGroup_Id(GROUP_1));
  }

  @Test
  void addShift_duplicate_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(SHIFT_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(OLD_SHIFT)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_DUPLICATE))
      .andReturn();

    assertEquals(3, shiftRepository.countByGroup_Id(GROUP_1));
  }

  @Test
  void addShift_userIsNotManager_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(post(format(SHIFT_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(NEW_SHIFT)))
      .andExpect(status().isForbidden())
      .andReturn();

    assertEquals(3, shiftRepository.countByGroup_Id(GROUP_1));
  }

  @Test
  void modifyShift_manager_ok() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(put(format(SHIFT_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(MODIFIED_SHIFT)))
      .andExpect(status().isOk())
      .andReturn();

    Shift shift = shiftRepository.findById(SHIFT_1).get();
    assertEquals(LocalDate.of(2020, 1, 2), shift.getDate());
    assertEquals(LocalTime.of(13, 5), shift.getEndTime());
  }

  @Test
  void modifyShift_shiftNotFound_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(put(format(SHIFT_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(MODIFIED_SHIFT_NOT_FOUND)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_NOT_FOUND))
      .andReturn();
  }

  @Test
  void modifyShift_duplicate_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(put(format(SHIFT_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(OLD_SHIFT)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_DUPLICATE))
      .andReturn();

    Shift shift = shiftRepository.findById(SHIFT_1).get();
    assertEquals(LocalDate.of(2020, 1, 1), shift.getDate());
    assertEquals(LocalTime.of(8, 0), shift.getStartTime());
    assertEquals(LocalTime.of(12, 0), shift.getEndTime());
  }

  @Test
  void modifyShift_userIsNotManager_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(put(format(SHIFT_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(NEW_SHIFT)))
      .andExpect(status().isForbidden())
      .andReturn();

    Shift shift = shiftRepository.findById(SHIFT_1).get();
    assertEquals(LocalDate.of(2020, 1, 1), shift.getDate());
    assertEquals(LocalTime.of(8, 0), shift.getStartTime());
    assertEquals(LocalTime.of(12, 0), shift.getEndTime());
  }

  @Test
  void deleteShift_manager_ok() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);
    Long shiftId = shiftRepository.getFirstByGroup_Id(GROUP_1).getId();

    mockMvc.perform(delete(format(SHIFT_PATH_WITH_ID, GROUP_1, shiftId))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(2, shiftRepository.countByGroup_Id(GROUP_1));
  }

  @Test
  void deleteShift_userIsNotManager_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);
    Long shiftId = shiftRepository.getFirstByGroup_Id(GROUP_1).getId();

    mockMvc.perform(delete(format(SHIFT_PATH_WITH_ID, GROUP_1, shiftId))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();

    assertEquals(3, shiftRepository.countByGroup_Id(GROUP_1));
  }

  @Test
  void deleteShift_shiftNotFound_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(delete(format(SHIFT_PATH_WITH_ID, GROUP_1, SHIFT_10))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_NOT_FOUND))
      .andReturn();

    assertEquals(3, shiftRepository.countByGroup_Id(GROUP_1));
  }
}
