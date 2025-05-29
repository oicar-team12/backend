package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Generic authentication DTO used in multiple API responses")
public class GenericAuthDto {

  @NotBlank
  @Schema(
    description = "JWT access token",
    example = "eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjEsInN1YiI6Imhhcmxpa29kYXNtYUBnbWFpbC5jb20iLCJpYXQiOjE3NDgzODY0MDEsImV4cCI6MTc0ODk4NjQwMX0.7BWk36b3AbbHz_XYm9QLUYJKS7nJ3eBT4kAW3mPVj2c"
  )
  private String accessToken;
}
