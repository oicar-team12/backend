package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "'Right to forget' request DTO")
public class UserDeleteRequestDto {

  @Schema(
    description = "Request ID",
    example = "1"
  )
  private Long id;

  @Schema(
    description = "User ID",
    example = "1"
  )
  private Long userId;

  @Schema(
    description = "Is approved",
    example = "true"
  )
  private Boolean isApproved;
}
