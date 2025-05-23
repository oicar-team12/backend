package hr.algebra.shiftschedulingapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityGroupedDto {

  private UserDto user;
  private List<AvailabilityDto> availabilities;
}
