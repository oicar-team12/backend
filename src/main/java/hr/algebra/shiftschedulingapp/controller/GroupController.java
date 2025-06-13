package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupManagerRole;
import hr.algebra.shiftschedulingapp.model.dto.GroupDto;
import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import hr.algebra.shiftschedulingapp.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@RestController
@RequestMapping("group")
@RequiredArgsConstructor
@Tag(name = "Group", description = "Group management APIs")
public class GroupController {

  private final GroupService groupService;

  @Operation(
    summary = "Create a new group",
    description = "Creates a new group and assigns the user as manager of the group"
  )
  @ApiResponse(
    responseCode = "200",
    description = "New group created successfully"
  )
  @PostMapping
  public GroupDto createGroup(@RequestBody GroupDto groupDto) {
    return groupService.createGroup(groupDto);
  }

  @Operation(
    summary = "Modify a group",
    description = "Modifies the group with new provided data"
  )
  @Parameter(
    name = "id",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Group modified successfully"
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PutMapping("{id}")
  @RequiresGroupManagerRole(groupIdParam = "id")
  public GroupDto modifyGroup(@PathVariable Long id, @RequestBody GroupDto groupDto) {
    return groupService.modifyGroup(id, groupDto);
  }

  @Operation(
    summary = "Delete a group",
    description = "Deletes the group"
  )
  @Parameter(
    name = "id",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Group deleted successfully"
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @DeleteMapping("{id}")
  @RequiresGroupManagerRole(groupIdParam = "id")
  public void deleteGroup(@PathVariable Long id) {
    groupService.deleteGroup(id);
  }
}
