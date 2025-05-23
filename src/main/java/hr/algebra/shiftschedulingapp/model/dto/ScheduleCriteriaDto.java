package hr.algebra.shiftschedulingapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCriteriaDto {

  private Long userId;
  private LocalDate startDate;
  private LocalDate endDate;
}
