package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityCriteriaDto;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityDto;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityGroupedDto;
import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import hr.algebra.shiftschedulingapp.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@RestController
@RequestMapping("group/{groupId}")
@RequiredArgsConstructor
@Tag(name = "Availability", description = "Availability management APIs")
public class AvailabilityController {

  private final AvailabilityService availabilityService;

  @Operation(
    summary = "Get availabilities",
    description = "Returns availabilities in a given group and according to the provided criteria"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "List of availabilities returned successfully",
      content = @Content(
        array = @ArraySchema(
          schema = @Schema(implementation = AvailabilityGroupedDto.class),
          minItems = 0
        )
      )
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not part of the group or is trying to view another user's availabilities without having a manager role",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @GetMapping("availabilities")
  @RequiresGroupMembership
  public List<AvailabilityGroupedDto> getAvailabilities(@PathVariable Long groupId, @ModelAttribute @ParameterObject AvailabilityCriteriaDto availabilityCriteriaDto) {
    return availabilityService.getAvailabilities(groupId, availabilityCriteriaDto);
  }

  @Operation(
    summary = "Add a new availability",
    description = "Creates a new availability for the user in the group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Availability created successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "User has already marked their availability for this date in the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not part of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PostMapping("availability")
  @RequiresGroupMembership
  public void addAvailability(@PathVariable Long groupId, @RequestBody AvailabilityDto availabilityDto) {
    availabilityService.addAvailability(groupId, availabilityDto);
  }

  @Operation(
    summary = "Delete an availability",
    description = "Deletes an existing availability from the group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @Parameter(
    name = "id",
    description = "ID of the availability",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Availability deleted successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Availability not found",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not part of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @DeleteMapping("availability/{id}")
  @RequiresGroupMembership
  public void removeAvailability(@PathVariable Long groupId, @PathVariable Long id) {
    availabilityService.removeAvailability(groupId, id);
  }
}
