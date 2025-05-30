package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDto {

  @Schema(
    description = "User ID",
    example = "1"
  )
  private Long userId;

  @Schema(
    description = "Shift ID",
    example = "1"
  )
  private Long shiftId;
}
