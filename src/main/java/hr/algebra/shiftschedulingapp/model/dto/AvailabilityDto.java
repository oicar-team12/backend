package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityDto {

  @Schema(
    description = "Availability ID",
    example = "1"
  )
  private Long id;

  @Schema(
    description = "Availability date",
    example = "2025-05-16"
  )
  private LocalDate date;

  @Schema(
    description = "Is available",
    example = "true"
  )
  private boolean available;
}
