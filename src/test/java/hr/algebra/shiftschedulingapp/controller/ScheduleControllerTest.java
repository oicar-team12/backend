package hr.algebra.shiftschedulingapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleDto;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleGroupedDto;
import hr.algebra.shiftschedulingapp.repository.ScheduleRepository;
import hr.algebra.shiftschedulingapp.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/schedule.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class ScheduleControllerTest extends IntegrationTest {

  @Autowired
  private ScheduleRepository scheduleRepository;

  private static final String SCHEDULES_PATH = "/group/%s/schedules";
  private static final String SCHEDULE_PATH = "/group/%s/schedule";
  private static final String SCHEDULE_PATH_WITH_ID = "/group/%s/schedule/%s";

  private static final String MANAGER_EMAIL = "john@email.com";
  private static final String EMPLOYEE_EMAIL = "james@email.com";

  private static final Long SHIFT_2 = 2L;
  private static final Long SHIFT_3 = 3L;
  private static final Long SHIFT_4 = 4L;

  private static final Long SCHEDULE_10 = 10L;

  private static final String ERROR_DUPLICATE = "This user has already been scheduled for this shift";
  private static final String ERROR_USER_NOT_FOUND = "User not found";
  private static final String ERROR_SHIFT_NOT_FOUND = "Shift not found";
  private static final String ERROR_NOT_FOUND = "Schedule not found";

  private static final ScheduleDto NEW_SCHEDULE = new ScheduleDto(USER_2, SHIFT_3);
  private static final ScheduleDto OLD_SCHEDULE = new ScheduleDto(USER_2, SHIFT_2);
  private static final ScheduleDto USER_NOT_IN_GROUP_SCHEDULE = new ScheduleDto(USER_3, SHIFT_2);
  private static final ScheduleDto SHIFT_NOT_FOUND_SCHEDULE = new ScheduleDto(USER_3, SHIFT_4);

  @Test
  void getSchedules_managerAndNoFilters_returnsAllSchedules() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    MvcResult result = mockMvc.perform(get(format(SCHEDULES_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<ScheduleGroupedDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(2, items.size());
    assertEquals(1, items.getFirst().getUsers().size());
    assertEquals(1, items.getLast().getUsers().size());
  }

  @Test
  void getSchedules_managerAndFilters_returnsMatchingSchedules() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    MvcResult result = mockMvc.perform(get(format(SCHEDULES_PATH, GROUP_1) + "?userId=2&startDate=2021-01-01")
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<ScheduleGroupedDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(1, items.size());
    assertEquals(1, items.getFirst().getUsers().size());
  }

  @Test
  void getSchedules_employeeNotPartOfGroup_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(get(format(SCHEDULES_PATH, GROUP_2))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  void getSchedules_employeeRequestingAnotherEmployeesSchedules_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(get(format(SCHEDULES_PATH, GROUP_1) + "?userId=1")
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  void getSchedules_employeeAndNoFilters_returnsOnlyOwnSchedules() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    MvcResult result = mockMvc.perform(get(format(SCHEDULES_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<ScheduleGroupedDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(1, items.size());
    assertEquals(1, items.getFirst().getUsers().size());
  }

  @Test
  void addSchedule_manager_ok() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(SCHEDULE_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(NEW_SCHEDULE)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(2, scheduleRepository.countByUser_Id(USER_2));
  }

  @Test
  void addSchedule_duplicate_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(SCHEDULE_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(OLD_SCHEDULE)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_DUPLICATE))
      .andReturn();

    assertEquals(1, scheduleRepository.countByUser_Id(USER_2));
  }

  @Test
  void addSchedule_managerNotPartOfGroup_forbidden() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(SCHEDULE_PATH, GROUP_2))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(NEW_SCHEDULE)))
      .andExpect(status().isForbidden())
      .andReturn();

    assertEquals(1, scheduleRepository.countByUser_Id(USER_2));
  }

  @Test
  void addSchedule_userNotFound_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(SCHEDULE_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(USER_NOT_IN_GROUP_SCHEDULE)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_USER_NOT_FOUND))
      .andReturn();

    assertEquals(1, scheduleRepository.countByUser_Id(USER_2));
  }

  @Test
  void addSchedule_shiftNotFound_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(SCHEDULE_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(SHIFT_NOT_FOUND_SCHEDULE)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_SHIFT_NOT_FOUND))
      .andReturn();

    assertEquals(1, scheduleRepository.countByUser_Id(USER_2));
  }

  @Test
  void deleteSchedule_manager_ok() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);
    Long scheduleId = scheduleRepository.getFirstByUser_Id(USER_2).getId();

    mockMvc.perform(delete(format(SCHEDULE_PATH_WITH_ID, GROUP_1, scheduleId))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(0, scheduleRepository.countByUser_Id(USER_2));
  }

  @Test
  void deleteSchedule_userIsNotManager_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);
    Long scheduleId = scheduleRepository.getFirstByUser_Id(USER_2).getId();

    mockMvc.perform(delete(format(SCHEDULE_PATH_WITH_ID, GROUP_1, scheduleId))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();

    assertEquals(1, scheduleRepository.countByUser_Id(USER_2));
  }

  @Test
  void deleteSchedule_scheduleNotFound_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(delete(format(SCHEDULE_PATH_WITH_ID, GROUP_1, SCHEDULE_10))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_NOT_FOUND))
      .andReturn();
  }
}
