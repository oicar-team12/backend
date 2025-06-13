package hr.algebra.shiftschedulingapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.dto.UserDeleteRequestDto;
import hr.algebra.shiftschedulingapp.model.jpa.User;
import hr.algebra.shiftschedulingapp.model.jpa.UserDeleteRequest;
import hr.algebra.shiftschedulingapp.repository.AccessTokenRepository;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
import hr.algebra.shiftschedulingapp.repository.NotificationRepository;
import hr.algebra.shiftschedulingapp.repository.RefreshTokenRepository;
import hr.algebra.shiftschedulingapp.repository.UserDeleteRequestsRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import hr.algebra.shiftschedulingapp.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static hr.algebra.shiftschedulingapp.enums.RightToForgetRequestDecision.APPROVE;
import static hr.algebra.shiftschedulingapp.enums.RightToForgetRequestDecision.DENY;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/right-to-forget.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class RightToForgetControllerTest extends IntegrationTest {

  @Autowired
  private UserDeleteRequestsRepository userDeleteRequestsRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GroupUserRepository groupUserRepository;

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private AccessTokenRepository accessTokenRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  private static final String REQUEST_PATH = "/right-to-forget/request";
  private static final String REQUESTS_PATH = "/right-to-forget/requests";
  private static final String REQUEST_DECISION_PATH = "/right-to-forget/request/%s/decision/%s";

  private static final Long USER_1_ID = 2L;
  private static final Long USER_2_ID = 3L;

  private static final Long REQUEST_1 = 1L;
  private static final Long REQUEST_2 = 2L;
  private static final Long REQUEST_5 = 5L;

  private static final String ADMIN_EMAIL = "john@email.com";
  private static final String USER_1_EMAIL = "james@email.com";
  private static final String USER_2_EMAIL = "jane@email.com";

  private static final String ERROR_NOT_FOUND = "Request not found";
  private static final String ERROR_ALREADY_FINALIZED = "Request has already been finalized";
  private static final String ERROR_NOT_YET_FINALIZED = "Previous request is not yet finalized";

  @Test
  void getRequests_userIsAdmin_ok() throws Exception {
    Credentials credentials = login(ADMIN_EMAIL);

    MvcResult result = mockMvc.perform(get(REQUESTS_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<UserDeleteRequestDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(1, items.size());
  }

  @Test
  void getRequests_userIsNotAdmin_forbidden() throws Exception {
    Credentials credentials = login(USER_1_EMAIL);

    mockMvc.perform(get(REQUESTS_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  void requestDeletion_validData_ok() throws Exception {
    Credentials credentials = login(USER_2_EMAIL);

    mockMvc.perform(post(REQUEST_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<UserDeleteRequest> items = userDeleteRequestsRepository.findByUserId(USER_2_ID);
    assertEquals(2, items.size());
    assertFalse(items.getFirst().getIsApproved());
    assertNull(items.getLast().getIsApproved());
  }

  @Test
  void requestDeletion_previousRequestNotFinalized_badRequest() throws Exception {
    Credentials credentials = login(USER_1_EMAIL);

    mockMvc.perform(post(REQUEST_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_NOT_YET_FINALIZED))
      .andReturn();

    List<UserDeleteRequest> items = userDeleteRequestsRepository.findByUserId(USER_1_ID);
    assertEquals(1, items.size());
    assertNull(items.getFirst().getIsApproved());
  }

  @Test
  void finalizeRequest_approved_userIsDeleted() throws Exception {
    login(USER_1_EMAIL);
    Credentials credentials = login(ADMIN_EMAIL);

    mockMvc.perform(put(format(REQUEST_DECISION_PATH, REQUEST_1, APPROVE))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    assertUser1Deleted();
    assertTrue(userDeleteRequestsRepository.findById(REQUEST_1).get().getIsApproved());
  }

  @Test
  void finalizeRequest_denied_userIsNotDeleted() throws Exception {
    login(USER_1_EMAIL);
    Credentials credentials = login(ADMIN_EMAIL);

    mockMvc.perform(put(format(REQUEST_DECISION_PATH, REQUEST_1, DENY))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    assertUser1NotDeleted();
    assertFalse(userDeleteRequestsRepository.findById(REQUEST_2).get().getIsApproved());
  }

  @Test
  void finalizeRequest_requestNotFound_badRequest() throws Exception {
    login(USER_1_EMAIL);
    Credentials credentials = login(ADMIN_EMAIL);

    mockMvc.perform(put(format(REQUEST_DECISION_PATH, REQUEST_5, APPROVE))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_NOT_FOUND))
      .andReturn();

    assertUser1NotDeleted();
  }

  @Test
  void finalizeRequest_requestAlreadyFinalized_badRequest() throws Exception {
    login(USER_2_EMAIL);
    Credentials credentials = login(ADMIN_EMAIL);

    mockMvc.perform(put(format(REQUEST_DECISION_PATH, REQUEST_2, APPROVE))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_ALREADY_FINALIZED))
      .andReturn();

    assertUser2NotDeleted();
    assertFalse(userDeleteRequestsRepository.findById(REQUEST_2).get().getIsApproved());
  }

  @Test
  void finalizeRequest_userIsNotAdmin_forbidden() throws Exception {
    login(USER_1_EMAIL);
    Credentials credentials = login(USER_1_EMAIL);

    mockMvc.perform(put(format(REQUEST_DECISION_PATH, REQUEST_1, APPROVE))
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isForbidden())
      .andReturn();

    assertUser1NotDeleted();
    assertNull(userDeleteRequestsRepository.findById(REQUEST_1).get().getIsApproved());
  }

  @Test
  void deleteRequest_validData_ok() throws Exception {
    Credentials credentials = login(USER_1_EMAIL);

    mockMvc.perform(delete(REQUEST_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<UserDeleteRequest> items = userDeleteRequestsRepository.findByUserId(USER_1_ID);
    assertEquals(0, items.size());
  }

  @Test
  void deleteRequest_requestNotFound_badRequest() throws Exception {
    Credentials credentials = login(USER_2_EMAIL);

    mockMvc.perform(delete(REQUEST_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_NOT_FOUND))
      .andReturn();

    List<UserDeleteRequest> items = userDeleteRequestsRepository.findByUserId(USER_2_ID);
    assertEquals(1, items.size());
    assertFalse(items.getFirst().getIsApproved());
  }

  private void assertUser1Deleted() {
    User user = userRepository.findById(USER_1_ID).get();
    assertEquals("deleted@user.invalid", user.getEmail());
    assertEquals("Deleted", user.getFirstName());
    assertEquals("User", user.getLastName());

    assertEquals(0, groupUserRepository.countByUserId(USER_1_ID));
    assertEquals(0, notificationRepository.countByUserId(USER_1_ID));

    assertEquals(0, accessTokenRepository.countByUserId(USER_1_ID));
    assertEquals(0, refreshTokenRepository.countByUserId(USER_1_ID));
  }

  private void assertUser1NotDeleted() {
    User user = userRepository.findById(USER_1_ID).get();
    assertEquals(USER_1_EMAIL, user.getEmail());
    assertEquals("James", user.getFirstName());
    assertEquals("Moe", user.getLastName());

    assertEquals(1, groupUserRepository.countByUserId(USER_1_ID));
    assertEquals(1, notificationRepository.countByUserId(USER_1_ID));

    assertEquals(1, accessTokenRepository.countByUserId(USER_1_ID));
    assertEquals(1, refreshTokenRepository.countByUserId(USER_1_ID));
  }

  private void assertUser2NotDeleted() {
    User user = userRepository.findById(USER_2_ID).get();
    assertEquals(USER_2_EMAIL, user.getEmail());
    assertEquals("Jane", user.getFirstName());
    assertEquals("Joe", user.getLastName());

    assertEquals(1, groupUserRepository.countByUserId(USER_2_ID));
    assertEquals(1, notificationRepository.countByUserId(USER_2_ID));

    assertEquals(1, accessTokenRepository.countByUserId(USER_2_ID));
    assertEquals(1, refreshTokenRepository.countByUserId(USER_2_ID));
  }
}
