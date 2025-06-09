package hr.algebra.shiftschedulingapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.shiftschedulingapp.model.dto.GenericAuthDto;
import hr.algebra.shiftschedulingapp.model.dto.LoginRequestDto;
import hr.algebra.shiftschedulingapp.util.Credentials;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@TestMethodOrder(OrderAnnotation.class)
public abstract class IntegrationTest {

  protected static final String LOGIN_EMAIL = "john@email.com";
  protected static final String LOGIN_PASSWORD = "asd123456";

  private static final String LOGIN_PATH = "/auth/login";
  private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  protected Credentials login() throws Exception {
    return extractTokens(performLogin(LOGIN_PASSWORD).andReturn());
  }

  protected ResultActions performLogin(String password) throws Exception {
    return mockMvc.perform(post(LOGIN_PATH)
      .contentType(APPLICATION_JSON)
      .content(asJsonString(new LoginRequestDto(LOGIN_EMAIL, password))));
  }

  protected Credentials extractTokens(MvcResult result) throws Exception {
    String accessToken = fromJsonString(
      result.getResponse().getContentAsString(),
      GenericAuthDto.class
    ).getAccessToken();

    Cookie refreshTokenCookie = result.getResponse().getCookie(REFRESH_TOKEN_COOKIE_NAME);
    String refreshToken = refreshTokenCookie != null ? refreshTokenCookie.getValue() : null;

    return new Credentials(accessToken, refreshToken);
  }

  protected String asJsonString(Object obj) throws JsonProcessingException {
    return objectMapper.writeValueAsString(obj);
  }

  protected <T> T fromJsonString(String json, Class<T> clazz) throws IOException {
    return objectMapper.readValue(json, clazz);
  }
}
