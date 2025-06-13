package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.dto.GroupDto;
import hr.algebra.shiftschedulingapp.model.jpa.Group;
import hr.algebra.shiftschedulingapp.model.jpa.GroupUser;
import hr.algebra.shiftschedulingapp.repository.AvailabilityRepository;
import hr.algebra.shiftschedulingapp.repository.GroupRepository;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
import hr.algebra.shiftschedulingapp.repository.ScheduleRepository;
import hr.algebra.shiftschedulingapp.repository.ShiftRepository;
import hr.algebra.shiftschedulingapp.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static hr.algebra.shiftschedulingapp.enums.GroupUserRole.MANAGER;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/group.sql", executionPhase = BEFORE_TEST_METHOD)
class GroupControllerTest extends IntegrationTest {

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private GroupUserRepository groupUserRepository;

  @Autowired
  private AvailabilityRepository availabilityRepository;

  @Autowired
  private ShiftRepository shiftRepository;

  @Autowired
  private ScheduleRepository scheduleRepository;

  private static final String GROUP_PATH = "/group";
  private static final String GROUP_PATH_WITH_ID = "/group/%s";

  private static final String GROUP_MANAGER_EMAIL = "john@email.com";
  private static final String GROUP_EMPLOYEE_EMAIL = "james@email.com";

  private static final String ORIGINAL_GROUP_NAME = "First Group";
  private static final String NEW_GROUP_NAME = "New Group";
  private static final String MODIFIED_GROUP_NAME = "New Group Name";

  @Test
  void createGroup_validData_ok() throws Exception {
    Credentials credentials = login(GROUP_MANAGER_EMAIL);

    MvcResult result = mockMvc.perform(post(format(GROUP_PATH))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(new GroupDto(NEW_GROUP_NAME))))
      .andExpect(status().isOk())
      .andReturn();

    GroupDto group = fromJsonString(result.getResponse().getContentAsString(), GroupDto.class);
    assertEquals(NEW_GROUP_NAME, group.getName());

    assertEquals(2, groupRepository.count());
    assertEquals(2, groupUserRepository.countByUserId(USER_1));

    List<GroupUser> groupUsers = groupUserRepository.findByUserId(USER_1);
    assertEquals(MANAGER, groupUsers.getLast().getRole());
    assertEquals(groupUsers.getLast().getGroup().getId(), group.getId());
  }

  @Test
  void modifyGroup_manager_ok() throws Exception {
    Credentials credentials = login(GROUP_MANAGER_EMAIL);

    MvcResult result = mockMvc.perform(put(format(GROUP_PATH_WITH_ID, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(new GroupDto(MODIFIED_GROUP_NAME))))
      .andExpect(status().isOk())
      .andReturn();

    GroupDto groupResult = fromJsonString(result.getResponse().getContentAsString(), GroupDto.class);
    assertEquals(MODIFIED_GROUP_NAME, groupResult.getName());

    Group groupFromDb = groupRepository.findById(GROUP_1).get();
    assertEquals(MODIFIED_GROUP_NAME, groupFromDb.getName());
  }

  @Test
  void modifyGroup_userIsNotManager_forbidden() throws Exception {
    Credentials credentials = login(GROUP_EMPLOYEE_EMAIL);

    mockMvc.perform(put(format(GROUP_PATH_WITH_ID, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .content(asJsonString(new GroupDto(ORIGINAL_GROUP_NAME))))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  void deleteGroup_manager_ok() throws Exception {
    Credentials credentials = login(GROUP_MANAGER_EMAIL);

    mockMvc.perform(delete(format(GROUP_PATH_WITH_ID, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(0, groupRepository.countById(GROUP_1));
    assertEquals(0, groupUserRepository.countByGroupId(GROUP_1));
    assertEquals(0, availabilityRepository.count());
    assertEquals(0, shiftRepository.count());
    assertEquals(0, scheduleRepository.count());
  }

  @Test
  void deleteGroup_userIsNotManager_forbidden() throws Exception {
    Credentials credentials = login(GROUP_EMPLOYEE_EMAIL);

    mockMvc.perform(delete(format(GROUP_PATH_WITH_ID, GROUP_1))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();
  }
}
