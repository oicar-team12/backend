package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleGroupedDto {

  @Schema(description = "Shift")
  private ShiftDto shift;

  @Schema(description = "List of users assigned to the shift")
  private List<UserDto> users;
}
