package hr.algebra.shiftschedulingapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftDto {

  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
}
