package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Login request DTO")
public class LoginRequestDto {

  @NotBlank
  @Schema(
    description = "User's email address",
    example = "user@example.com",
    requiredMode = REQUIRED,
    minLength = 5
  )
  private String email;

  @NotBlank
  @Schema(
    description = "User's password",
    example = "password123",
    requiredMode = REQUIRED,
    minLength = 8
  )
  private String password;
}
