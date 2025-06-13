package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Shift DTO")
public class ShiftDto {

  @Schema(
    description = "Shift ID",
    example = "1"
  )
  private Long id;

  @Schema(
    description = "Shift date",
    example = "2025-05-16"
  )
  private LocalDate date;

  @Schema(
    description = "Shift start time",
    example = "14:00:00"
  )
  private LocalTime startTime;

  @Schema(
    description = "Shift end time",
    example = "21:00:00"
  )
  private LocalTime endTime;
}
