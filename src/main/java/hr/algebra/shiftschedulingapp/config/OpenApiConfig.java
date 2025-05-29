package hr.algebra.shiftschedulingapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Shift Scheduling App")
        .description("API documentation for Shift Scheduling Application")
        .version("1.0")
        .contact(new Contact()
          .name("OICAR Team 12")
          .url("https://github.com/oicar-team12")
        )
      )
      .components(new Components()
        .addSecuritySchemes("Bearer token",
          new SecurityScheme()
            .type(HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT token authentication")
        )
      );
  }
}
