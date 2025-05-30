package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityGroupedDto {

  @Schema(description = "User")
  private UserDto user;

  @Schema(description = "List of user's availabilities")
  private List<AvailabilityDto> availabilities;
}
