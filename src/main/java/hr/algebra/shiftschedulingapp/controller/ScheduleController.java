package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupManagerRole;
import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleCriteriaDto;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleDto;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleGroupedDto;
import hr.algebra.shiftschedulingapp.service.ScheduleService;
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
@Tag(name = "Schedule", description = "Schedule management APIs")
public class ScheduleController {

  private final ScheduleService scheduleService;

  @Operation(
    summary = "Get schedules",
    description = "Returns schedules in a given group and according to the provided criteria"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "List of schedules returned successfully",
      content = @Content(
        array = @ArraySchema(
          schema = @Schema(implementation = ScheduleGroupedDto.class),
          minItems = 0
        )
      )
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not part of the group or is trying to view another user's schedules without having a manager role",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @GetMapping("schedules")
  @RequiresGroupMembership
  public List<ScheduleGroupedDto> getSchedules(@PathVariable Long groupId, @ModelAttribute @ParameterObject ScheduleCriteriaDto scheduleCriteriaDto) {
    return scheduleService.getSchedules(groupId, scheduleCriteriaDto);
  }

  @Operation(
    summary = "Add a new schedule",
    description = "Creates a new schedule for the user in the group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Schedule created successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "User not found / shift not found / user has already been scheduled for this shift",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PostMapping("schedule")
  @RequiresGroupManagerRole
  public void addSchedule(@PathVariable Long groupId, @RequestBody ScheduleDto scheduleDto) {
    scheduleService.addSchedule(groupId, scheduleDto);
  }

  @Operation(
    summary = "Delete a schedule",
    description = "Deletes an existing schedule from the group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @Parameter(
    name = "id",
    description = "ID of the schedule",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Schedule deleted successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Schedule not found",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @DeleteMapping("schedule/{id}")
  @RequiresGroupManagerRole
  public void removeSchedule(@PathVariable Long groupId, @PathVariable Long id) {
    scheduleService.removeSchedule(groupId, id);
  }
}
