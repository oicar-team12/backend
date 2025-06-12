package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.jpa.User;
import hr.algebra.shiftschedulingapp.repository.AccessTokenRepository;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
import hr.algebra.shiftschedulingapp.repository.NotificationRepository;
import hr.algebra.shiftschedulingapp.repository.RefreshTokenRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import hr.algebra.shiftschedulingapp.util.Credentials;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/user.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class UserControllerTest extends IntegrationTest {

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

  private static final String DELETE_USER_PATH = "/user/delete-account";

  @Test
  void deleteUser_validCredentials_ok() throws Exception {
    Credentials credentials = login();
    Long userId = userRepository.findByEmail(LOGIN_EMAIL).get().getId();

    mockMvc.perform(delete(DELETE_USER_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, credentials.getRefreshToken())))
      .andExpect(status().isOk())
      .andExpect(cookie().value(REFRESH_TOKEN_COOKIE_NAME, ""))
      .andExpect(cookie().maxAge(REFRESH_TOKEN_COOKIE_NAME, 0))
      .andReturn();

    User user = userRepository.findById(userId).get();
    assertEquals("deleted@user.invalid", user.getEmail());
    assertEquals("Deleted", user.getFirstName());
    assertEquals("User", user.getLastName());

    assertEquals(0, groupUserRepository.countByUser_Id(userId));
    assertEquals(0, notificationRepository.countByUser_Id(userId));

    assertEquals(0, accessTokenRepository.countByUser_Id(userId));
    assertEquals(0, refreshTokenRepository.countByUser_Id(userId));
  }

  @Test
  void deleteUser_invalidCredentials_badRequest() throws Exception {
    Credentials credentials = login();

    mockMvc.perform(delete(DELETE_USER_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken())
        .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, UUID.randomUUID().toString())))
      .andExpect(status().isBadRequest())
      .andReturn();

    User user = userRepository.findByEmail(LOGIN_EMAIL).get();
    assertEquals("john@email.com", user.getEmail());
    assertEquals("John", user.getFirstName());
    assertEquals("Doe", user.getLastName());

    assertEquals(1, groupUserRepository.countByUser_Id(user.getId()));
    assertEquals(1, notificationRepository.countByUser_Id(user.getId()));

    assertEquals(1, accessTokenRepository.countByUser_Id(user.getId()));
    assertEquals(1, refreshTokenRepository.countByUser_Id(user.getId()));
  }
}
