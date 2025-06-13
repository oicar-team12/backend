package hr.algebra.shiftschedulingapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.shiftschedulingapp.config.MockMvcConfig;
import hr.algebra.shiftschedulingapp.config.TestContainersConfig;
import hr.algebra.shiftschedulingapp.model.dto.GenericAuthDto;
import hr.algebra.shiftschedulingapp.model.dto.LoginRequestDto;
import hr.algebra.shiftschedulingapp.util.Credentials;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.sql.DataSource;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestContainersConfig.class, MockMvcConfig.class})
@TestMethodOrder(OrderAnnotation.class)
public abstract class IntegrationTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  private DataSource dataSource;

  protected static final String LOGIN_EMAIL = "john@email.com";
  protected static final String LOGIN_PASSWORD = "asd123456";
  protected static final String BEARER_PREFIX = "Bearer ";
  protected static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
  protected static final Long GROUP_1 = 1L;
  protected static final Long GROUP_2 = 2L;
  protected static final Long USER_1 = 1L;
  protected static final Long USER_2 = 2L;
  protected static final Long USER_3 = 3L;
  protected static final Long USER_4 = 4L;

  private static final String LOGIN_PATH = "/auth/login";

  @AfterEach
  void cleanupDatabase() {
    new ResourceDatabasePopulator(new ClassPathResource("/sql/cleanup.sql")).execute(dataSource);
  }

  protected Credentials login() throws Exception {
    return extractTokens(performLogin(LOGIN_EMAIL, LOGIN_PASSWORD).andReturn());
  }

  protected Credentials login(String email) throws Exception {
    return extractTokens(performLogin(email, LOGIN_PASSWORD).andReturn());
  }

  protected ResultActions performLogin(String email, String password) throws Exception {
    return mockMvc.perform(post(LOGIN_PATH)
      .content(asJsonString(new LoginRequestDto(email, password))));
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

  protected <T> T fromJsonString(String json, TypeReference<T> typeReference) throws IOException {
    return objectMapper.readValue(json, typeReference);
  }
}
