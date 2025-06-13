package hr.algebra.shiftschedulingapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityDto;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityGroupedDto;
import hr.algebra.shiftschedulingapp.repository.AvailabilityRepository;
import hr.algebra.shiftschedulingapp.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
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

@Sql(scripts = "/sql/availability.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class AvailabilityControllerTest extends IntegrationTest {

  @Autowired
  private AvailabilityRepository availabilityRepository;

  private static final String AVAILABILITIES_PATH = "/group/%s/availabilities";
  private static final String AVAILABILITY_PATH = "/group/%s/availability";
  private static final String AVAILABILITY_PATH_WITH_ID = "/group/%s/availability/%s";

  private static final String MANAGER_EMAIL = "john@email.com";
  private static final String EMPLOYEE_EMAIL = "james@email.com";

  private static final String ERROR_DUPLICATE = "The specified date is already marked in this group";
  private static final String ERROR_NOT_FOUND = "Availability not found";

  private static final AvailabilityDto AVAILABILITY_2023_1_1 = new AvailabilityDto(LocalDate.of(2023, 1, 1), false);
  private static final AvailabilityDto AVAILABILITY_2024_7_17 = new AvailabilityDto(LocalDate.of(2024, 7, 17), true);

  @Test
  void getAvailabilities_managerAndNoFilters_returnsAllAvailabilities() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    MvcResult result = mockMvc.perform(get(format(AVAILABILITIES_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<AvailabilityGroupedDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(2, items.size());
    assertEquals(1, items.getFirst().getAvailabilities().size());
    assertEquals(2, items.getLast().getAvailabilities().size());
  }

  @Test
  void getAvailabilities_managerAndFilters_returnsMatchingAvailabilities() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    MvcResult result = mockMvc.perform(get(format(AVAILABILITIES_PATH, GROUP_1) + "?userId=2&startDate=2022-06-01")
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<AvailabilityGroupedDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(1, items.size());
    assertEquals(1, items.getFirst().getAvailabilities().size());
  }

  @Test
  void getAvailabilities_employeeNotPartOfGroup_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(get(format(AVAILABILITIES_PATH, GROUP_2))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  void getAvailabilities_employeeRequestingAnotherEmployeesAvailabilities_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(get(format(AVAILABILITIES_PATH, GROUP_1) + "?userId=1")
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  void getAvailabilities_employeeAndNoFilters_returnsOnlyOwnAvailabilities() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    MvcResult result = mockMvc.perform(get(format(AVAILABILITIES_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<AvailabilityGroupedDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(1, items.size());
    assertEquals(2, items.getFirst().getAvailabilities().size());
  }

  @Test
  void addAvailability_employee_ok() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(post(format(AVAILABILITY_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(AVAILABILITY_2024_7_17)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(3, availabilityRepository.countByUserId(USER_2));
  }

  @Test
  void addAvailability_duplicate_badRequest() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(post(format(AVAILABILITY_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(AVAILABILITY_2023_1_1)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_DUPLICATE))
      .andReturn();

    assertEquals(2, availabilityRepository.countByUserId(USER_2));
  }

  @Test
  void addAvailability_employeeNotPartOfGroup_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(post(format(AVAILABILITY_PATH, GROUP_2))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(AVAILABILITY_2023_1_1)))
      .andExpect(status().isForbidden())
      .andReturn();

    assertEquals(2, availabilityRepository.countByUserId(USER_2));
  }

  @Test
  void deleteAvailability_employee_ok() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);
    Long availabilityId = availabilityRepository.getFirstByUserId(USER_2).getId();

    mockMvc.perform(delete(format(AVAILABILITY_PATH_WITH_ID, GROUP_1, availabilityId))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(1, availabilityRepository.countByUserId(USER_2));
  }

  @Test
  void deleteAvailability_availabilityNotFound_badRequest() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(delete(format(AVAILABILITY_PATH_WITH_ID, GROUP_1, 99))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_NOT_FOUND))
      .andReturn();

    assertEquals(2, availabilityRepository.countByUserId(USER_2));
  }

  @Test
  void deleteAvailability_employeeNotPartOfGroup_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(delete(format(AVAILABILITY_PATH_WITH_ID, GROUP_2, 1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();

    assertEquals(2, availabilityRepository.countByUserId(USER_2));
  }
}
