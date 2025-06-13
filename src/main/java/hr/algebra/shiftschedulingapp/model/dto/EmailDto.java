package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Generic email DTO")
public class EmailDto {

  @Schema(
    description = "User's email address",
    example = "user@example.com"
  )
  private String email;
}
