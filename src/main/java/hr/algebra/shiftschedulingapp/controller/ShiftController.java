package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupManagerRole;
import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import hr.algebra.shiftschedulingapp.model.dto.ShiftCriteriaDto;
import hr.algebra.shiftschedulingapp.model.dto.ShiftDto;
import hr.algebra.shiftschedulingapp.service.ShiftService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@RestController
@RequestMapping("group/{groupId}")
@RequiredArgsConstructor
@Tag(name = "Shift", description = "Shift management APIs")
public class ShiftController {

  private final ShiftService shiftService;

  @Operation(
    summary = "Get shifts",
    description = "Returns shifts in a given group and according to the provided criteria"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "List of shifts returned successfully",
      content = @Content(
        array = @ArraySchema(
          schema = @Schema(implementation = ShiftDto.class),
          minItems = 0
        )
      )
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not part of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @GetMapping("shifts")
  @RequiresGroupMembership
  public List<ShiftDto> getShifts(@PathVariable Long groupId, @ModelAttribute @ParameterObject ShiftCriteriaDto shiftCriteriaDto) {
    return shiftService.getShifts(groupId, shiftCriteriaDto);
  }

  @Operation(
    summary = "Add a new shift",
    description = "Creates a new shift for the group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Shift created successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "A shift on this date with the same start and end time already exists in this group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PostMapping("shift")
  @RequiresGroupManagerRole
  public void addShift(@PathVariable Long groupId, @RequestBody ShiftDto shiftDto) {
    shiftService.addShift(groupId, shiftDto);
  }

  @Operation(
    summary = "Modify a shift",
    description = "Modifies an existing shift in the group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Shift modified successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Shift not found / shift on this date with the same start and end time already exists in this group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PutMapping("shift")
  @RequiresGroupManagerRole
  public void modifyShift(@PathVariable Long groupId, @RequestBody ShiftDto shiftDto) {
    shiftService.modifyShift(groupId, shiftDto);
  }

  @Operation(
    summary = "Delete a shift",
    description = "Deletes an existing shift from the group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @Parameter(
    name = "id",
    description = "ID of the shift",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Shift deleted successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Shift not found",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @DeleteMapping("shift/{id}")
  @RequiresGroupManagerRole
  public void removeShift(@PathVariable Long groupId, @PathVariable Long id) {
    shiftService.removeShift(groupId, id);
  }
}
