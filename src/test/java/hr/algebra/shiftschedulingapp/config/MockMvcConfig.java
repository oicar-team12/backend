package hr.algebra.shiftschedulingapp.config;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@TestConfiguration
public class MockMvcConfig {

  @Bean
  public MockMvcBuilderCustomizer defaultJsonCustomizer() {
    return builder ->
      builder.defaultRequest(
        MockMvcRequestBuilders.get("/")
          .contentType(APPLICATION_JSON)
      );
  }
}
