package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.dto.GenericAuthDto;
import hr.algebra.shiftschedulingapp.model.dto.RegisterRequestDto;
import hr.algebra.shiftschedulingapp.repository.AccessTokenRepository;
import hr.algebra.shiftschedulingapp.repository.RefreshTokenRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import hr.algebra.shiftschedulingapp.util.Credentials;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/auth.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class AuthControllerTest extends IntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccessTokenRepository accessTokenRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  private static final String REFRESH_PATH = "/auth/refresh";
  private static final String LOGOUT_PATH = "/auth/logout";
  private static final String REGISTER_PATH = "/auth/register";

  private static final String REGISTER_NEW_EMAIL = "test@user.com";
  private static final String REGISTER_NEW_FIRST_NAME = "Test";
  private static final String REGISTER_NEW_LAST_NAME = "User";

  private static final String REGISTER_OLD_EMAIL = "john@email.com";
  private static final String REGISTER_OLD_FIRST_NAME = "John";
  private static final String REGISTER_OLD_LAST_NAME = "Doe";

  private static final String REGISTER_PASSWORD_OK = "qwe654321";
  private static final String REGISTER_PASSWORD_TOO_SHORT = "123";

  private static final String LOGIN_PASSWORD_INCORRECT = "password123";
  private static final String ERROR_EMAIL_EXISTS = "Email already exists";

  @Test
  void login_validCredentials_ok() throws Exception {
    MvcResult result = performLogin(REGISTER_OLD_EMAIL, LOGIN_PASSWORD)
      .andExpect(status().isOk())
      .andExpect(cookie().exists(REFRESH_TOKEN_COOKIE_NAME))
      .andExpect(jsonPath("$.accessToken").exists())
      .andReturn();

    GenericAuthDto response = fromJsonString(
      result.getResponse().getContentAsString(),
      GenericAuthDto.class
    );

    assertNotNull(response.getAccessToken());

    Long userId = userRepository.findByEmail(LOGIN_EMAIL).get().getId();
    assertEquals(1, accessTokenRepository.countByUserId(userId));
    assertEquals(1, refreshTokenRepository.countByUserId(userId));
  }

  @Test
  void login_invalidCredentials_badRequest() throws Exception {
    performLogin(REGISTER_OLD_EMAIL, LOGIN_PASSWORD_INCORRECT)
      .andExpect(status().isBadRequest())
      .andExpect(cookie().doesNotExist(REFRESH_TOKEN_COOKIE_NAME))
      .andExpect(jsonPath("$.accessToken").doesNotExist())
      .andReturn();

    Long userId = userRepository.findByEmail(LOGIN_EMAIL).get().getId();
    assertEquals(0, accessTokenRepository.countByUserId(userId));
    assertEquals(0, refreshTokenRepository.countByUserId(userId));
  }

  @Test
  void refresh_validCredentials_ok() throws Exception {
    Credentials initialCredentials = login();

    MvcResult result = mockMvc.perform(post(REFRESH_PATH)
        .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, initialCredentials.getRefreshToken())))
      .andExpect(status().isOk())
      .andExpect(cookie().doesNotExist(REFRESH_TOKEN_COOKIE_NAME))
      .andExpect(jsonPath("$.accessToken").exists())
      .andReturn();

    Long userId = userRepository.findByEmail(LOGIN_EMAIL).get().getId();
    assertEquals(1, accessTokenRepository.countByUserId(userId));
    assertEquals(1, refreshTokenRepository.countByUserId(userId));

    Credentials responseCredentials = extractTokens(result);

    assertEquals(1, accessTokenRepository.countByToken(responseCredentials.getAccessToken()));
    assertEquals(1, refreshTokenRepository.countByToken(initialCredentials.getRefreshToken()));
  }

  @Test
  void refresh_invalidCredentials_unauthorized() throws Exception {
    Credentials initialCredentials = login();
    String invalidRefreshToken = randomUUID().toString();

    mockMvc.perform(post(REFRESH_PATH)
        .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, invalidRefreshToken)))
      .andExpect(status().isUnauthorized())
      .andExpect(cookie().doesNotExist(REFRESH_TOKEN_COOKIE_NAME))
      .andExpect(jsonPath("$.accessToken").doesNotExist())
      .andReturn();

    Long userId = userRepository.findByEmail(LOGIN_EMAIL).get().getId();
    assertEquals(1, accessTokenRepository.countByUserId(userId));
    assertEquals(1, refreshTokenRepository.countByUserId(userId));

    assertEquals(1, accessTokenRepository.countByToken(initialCredentials.getAccessToken()));
    assertEquals(1, refreshTokenRepository.countByToken(initialCredentials.getRefreshToken()));
    assertEquals(0, refreshTokenRepository.countByToken(invalidRefreshToken));
  }

  @Test
  void logout_validCredentials_ok() throws Exception {
    Credentials initialCredentials = login();

    mockMvc.perform(delete(LOGOUT_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + initialCredentials.getAccessToken())
        .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, initialCredentials.getRefreshToken())))
      .andExpect(status().isOk())
      .andExpect(cookie().value(REFRESH_TOKEN_COOKIE_NAME, ""))
      .andExpect(cookie().maxAge(REFRESH_TOKEN_COOKIE_NAME, 0))
      .andReturn();

    assertEquals(0, accessTokenRepository.countByUserId(1L));
    assertEquals(0, refreshTokenRepository.countByUserId(1L));
  }

  @Test
  void logout_invalidCredentials_badRequest() throws Exception {
    login();

    mockMvc.perform(delete(LOGOUT_PATH)
        .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, randomUUID().toString())))
      .andExpect(status().isBadRequest())
      .andReturn();

    Long userId = userRepository.findByEmail(LOGIN_EMAIL).get().getId();
    assertEquals(1, accessTokenRepository.countByUserId(userId));
    assertEquals(1, refreshTokenRepository.countByUserId(userId));
  }

  @Test
  void register_validData_ok() throws Exception {
    mockMvc.perform(post(REGISTER_PATH)
        .content(asJsonString(new RegisterRequestDto(REGISTER_NEW_FIRST_NAME, REGISTER_NEW_LAST_NAME, REGISTER_NEW_EMAIL, REGISTER_PASSWORD_OK))))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(1, userRepository.countByEmail(REGISTER_NEW_EMAIL));
    assertEquals(2, userRepository.count());
  }

  @Test
  void register_invalidData_badRequest() throws Exception {
    mockMvc.perform(post(REGISTER_PATH)
        .content(asJsonString(new RegisterRequestDto(REGISTER_NEW_FIRST_NAME, REGISTER_NEW_LAST_NAME, REGISTER_NEW_EMAIL, REGISTER_PASSWORD_TOO_SHORT))))
      .andExpect(status().isBadRequest())
      .andReturn();

    assertEquals(0, userRepository.countByEmail(REGISTER_NEW_EMAIL));
    assertEquals(1, userRepository.count());
  }

  @Test
  void register_emailExists_badRequest() throws Exception {
    mockMvc.perform(post(REGISTER_PATH)
        .content(asJsonString(new RegisterRequestDto(REGISTER_OLD_FIRST_NAME, REGISTER_OLD_LAST_NAME, REGISTER_OLD_EMAIL, REGISTER_PASSWORD_OK))))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(ERROR_EMAIL_EXISTS))
      .andReturn();

    assertEquals(1, userRepository.countByEmail(REGISTER_OLD_EMAIL));
    assertEquals(1, userRepository.count());
  }
}
