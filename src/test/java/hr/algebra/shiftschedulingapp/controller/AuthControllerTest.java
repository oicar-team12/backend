package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.dto.GenericAuthDto;
import hr.algebra.shiftschedulingapp.repository.AccessTokenRepository;
import hr.algebra.shiftschedulingapp.repository.RefreshTokenRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import hr.algebra.shiftschedulingapp.util.Credentials;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("/sql/auth/login.sql")
@Transactional
class AuthControllerTest extends IntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccessTokenRepository accessTokenRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  private static final String REFRESH_PATH = "/auth/refresh";
  private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
  private static final String LOGIN_PASSWORD_INCORRECT = "password123";

  @Test
  void login_validCredentials_succeeds() throws Exception {
    MvcResult result = performLogin(LOGIN_PASSWORD)
      .andExpect(status().isOk())
      .andExpect(cookie().exists(REFRESH_TOKEN_COOKIE_NAME))
      .andExpect(jsonPath("$.accessToken").exists())
      .andReturn();

    GenericAuthDto response = fromJsonString(
      result.getResponse().getContentAsString(),
      GenericAuthDto.class
    );

    assertNotNull(response.getAccessToken());
    assertEquals(1, accessTokenRepository.countByUser_Id((1L)));
    assertEquals(1, refreshTokenRepository.countByUser_Id(1L));
  }

  @Test
  void login_invalidCredentials_fails() throws Exception {
    performLogin(LOGIN_PASSWORD_INCORRECT)
      .andExpect(status().isBadRequest())
      .andExpect(cookie().doesNotExist(REFRESH_TOKEN_COOKIE_NAME))
      .andExpect(jsonPath("$.accessToken").doesNotExist())
      .andReturn();

    assertEquals(0, accessTokenRepository.countByUser_Id((1L)));
    assertEquals(0, refreshTokenRepository.countByUser_Id(1L));
  }

  @Test
  void refresh_validCredentials_succeeds() throws Exception {
    Credentials initialCredentials = login();

    MvcResult result = mockMvc.perform(post(REFRESH_PATH)
        .contentType(APPLICATION_JSON)
        .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, initialCredentials.getRefreshToken())))
      .andExpect(status().isOk())
      .andExpect(cookie().doesNotExist(REFRESH_TOKEN_COOKIE_NAME))
      .andExpect(jsonPath("$.accessToken").exists())
      .andReturn();

    assertEquals(1, accessTokenRepository.countByUser_Id(1L));
    assertEquals(1, refreshTokenRepository.countByUser_Id(1L));

    Credentials responseCredentials = extractTokens(result);

    assertEquals(1, accessTokenRepository.countByToken(responseCredentials.getAccessToken()));
    assertEquals(1, refreshTokenRepository.countByToken(UUID.fromString(initialCredentials.getRefreshToken())));
  }
}
