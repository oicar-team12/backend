package hr.algebra.shiftschedulingapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.dto.EmailDto;
import hr.algebra.shiftschedulingapp.model.dto.GroupUserDto;
import hr.algebra.shiftschedulingapp.model.jpa.GroupUser;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
import hr.algebra.shiftschedulingapp.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static hr.algebra.shiftschedulingapp.enums.GroupUserRole.EMPLOYEE;
import static hr.algebra.shiftschedulingapp.enums.GroupUserRole.MANAGER;
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

@Sql(scripts = "/sql/group-user.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class GroupUserControllerTest extends IntegrationTest {

  @Autowired
  private GroupUserRepository groupUserRepository;

  private static final String GROUP_USERS_PATH = "/group/%s/users";
  private static final String GROUP_USER_PATH = "/group/%s/user";
  private static final String GROUP_USER_WITH_ID_PATH = "/group/%s/user/%s";
  private static final String GROUP_USER_ROLE_PATH = "/group/%s/user/%s/role/%s";

  private static final String MANAGER_EMAIL = "john@email.com";
  private static final String EMPLOYEE_EMAIL = "james@email.com";
  private static final String TERTIARY_EMAIL = "jane@email.com";
  private static final String NONEXISTENT_EMAIL = "none@email.com";

  private static final String ERROR_DUPLICATE = "User already exists in group";
  private static final String ERROR_USER_NOT_FOUND = "User not found";
  private static final String ERROR_USER_NOT_FOUND_IN_GROUP = "User does not exist in group";

  @Test
  void getGroupUsers_sameGroup_returnsAllGroupUsers() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    MvcResult result = mockMvc.perform(get(format(GROUP_USERS_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<GroupUserDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(2, items.size());
    assertEquals(MANAGER_EMAIL, items.getFirst().getEmail());
    assertEquals(EMPLOYEE_EMAIL, items.getLast().getEmail());
  }

  @Test
  void getGroupUsers_employeeNotPartOfGroup_forbidden() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(get(format(GROUP_USERS_PATH, GROUP_2))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  void addUserToGroup_validData_ok() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(GROUP_USER_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(new EmailDto(TERTIARY_EMAIL))))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(3, groupUserRepository.countByGroupId(GROUP_1));
  }

  @Test
  void addUserToGroup_userNotFound_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(GROUP_USER_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(new EmailDto(NONEXISTENT_EMAIL))))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_USER_NOT_FOUND))
      .andReturn();

    assertEquals(2, groupUserRepository.countByGroupId(GROUP_1));
  }

  @Test
  void addUserToGroup_userAlreadyInGroup_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(post(format(GROUP_USER_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(new EmailDto(EMPLOYEE_EMAIL))))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_DUPLICATE))
      .andReturn();

    assertEquals(2, groupUserRepository.countByGroupId(GROUP_1));
  }

  @Test
  void addUserToGroup_userIsNotManager_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(post(format(GROUP_USER_PATH, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(new EmailDto(TERTIARY_EMAIL))))
      .andExpect(status().isForbidden())
      .andReturn();

    assertEquals(2, groupUserRepository.countByGroupId(GROUP_1));
  }

  @Test
  void modifyUserGroup_validData_ok() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(put(format(GROUP_USER_ROLE_PATH, GROUP_1, USER_2, MANAGER.name()))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<GroupUser> modifiedGroupUser = groupUserRepository.findByUserId(USER_2);
    assertEquals(MANAGER, modifiedGroupUser.getFirst().getRole());
  }

  @Test
  void modifyUserGroup_userNotFound_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(put(format(GROUP_USER_ROLE_PATH, GROUP_1, USER_4, EMPLOYEE.name()))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_USER_NOT_FOUND_IN_GROUP))
      .andReturn();
  }

  @Test
  void modifyUserGroup_userIsNotManager_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(put(format(GROUP_USER_ROLE_PATH, GROUP_1, USER_1, EMPLOYEE.name()))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  void removeUserFromGroup_validData_ok() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(delete(format(GROUP_USER_WITH_ID_PATH, GROUP_1, USER_2))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(1, groupUserRepository.countByGroupId(GROUP_1));
  }

  @Test
  void removeUserFromGroup_userNotFound_badRequest() throws Exception {
    Credentials credentials = login(MANAGER_EMAIL);

    mockMvc.perform(delete(format(GROUP_USER_WITH_ID_PATH, GROUP_1, USER_4))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_USER_NOT_FOUND_IN_GROUP))
      .andReturn();

    assertEquals(2, groupUserRepository.countByGroupId(GROUP_1));
  }

  @Test
  void removeUserFromGroup_userIsNotManager_forbidden() throws Exception {
    Credentials credentials = login(EMPLOYEE_EMAIL);

    mockMvc.perform(delete(format(GROUP_USER_WITH_ID_PATH, GROUP_1, USER_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();

    assertEquals(2, groupUserRepository.countByGroupId(GROUP_1));
  }
}
