package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Register request DTO")
public class RegisterRequestDto {

  @NotBlank
  @Schema(
    description = "First name",
    example = "John"
  )
  private String firstName;

  @NotBlank
  @Schema(
    description = "Last name",
    example = "Smith"
  )
  private String lastName;

  @NotBlank
  @Email
  @Schema(
    description = "Email address",
    example = "user@example.com"
  )
  private String email;

  @NotBlank
  @Size(min = 8)
  @Schema(
    description = "Password",
    example = "Th!sI$aP@ssw0rd%"
  )
  private String password;
}
