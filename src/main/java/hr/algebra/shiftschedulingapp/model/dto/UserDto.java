package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

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
}
