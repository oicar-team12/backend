package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Login response DTO")
public class LoginResponseDto {

  @Schema(
    description = "User ID",
    example = "1"
  )
  private Long id;

  @Schema(
    description = "User's first name",
    example = "John"
  )
  private String firstName;

  @Schema(
    description = "User's last name",
    example = "Smith"
  )
  private String lastName;

  @Schema(
    description = "Is admin",
    example = "true"
  )
  private boolean admin;

  @Schema(
    description = "JWT access token",
    example = "eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjEsInN1YiI6Imhhcmxpa29kYXNtYUBnbWFpbC5jb20iLCJpYXQiOjE3NDgzODY0MDEsImV4cCI6MTc0ODk4NjQwMX0.7BWk36b3AbbHz_XYm9QLUYJKS7nJ3eBT4kAW3mPVj2c"
  )
  private String accessToken;
}
