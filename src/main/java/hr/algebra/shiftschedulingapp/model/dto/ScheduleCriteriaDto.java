package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Schedule criteria DTO used for selectively fetching schedules")
public class ScheduleCriteriaDto {

  @Schema(
    description = "User's ID to be searched",
    example = "1"
  )
  private Long userId;

  @Schema(
    description = "Shift's ID to be searched",
    example = "1"
  )
  private Long shiftId;

  @Schema(
    description = "Earliest schedule date to be searched",
    example = "2025-05-09"
  )
  private LocalDate startDate;

  @Schema(
    description = "Latest schedule date to be searched",
    example = "2025-05-16"
  )
  private LocalDate endDate;
}
