package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Shift criteria DTO used for selectively fetching shifts")
public class ShiftCriteriaDto {

  @Schema(
    description = "Earliest shift date to be searched",
    example = "2025-05-09"
  )
  private LocalDate startDate;

  @Schema(
    description = "Latest shift date to be searched",
    example = "2025-05-16"
  )
  private LocalDate endDate;
}
