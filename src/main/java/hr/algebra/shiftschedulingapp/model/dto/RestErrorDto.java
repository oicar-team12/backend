package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Schema(description = "REST error DTO")
public class RestErrorDto {

  @Schema(
    description = "HTTP status code",
    example = "400"
  )
  private int status;

  @Schema(
    description = "Error message",
    example = "Validation failed"
  )
  private String message;

  @Schema(
    description = "List of more detailed error causes (not always present)",
    example = "{\"email\": \"must be a valid email address\", \"password\": \"must be at least 8 characters\"}"
  )
  private Map<String, String> errors;

  public RestErrorDto(int status, String message) {
    this(status, message, null);
  }
}
