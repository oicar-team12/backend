package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupManagerRole;
import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.enums.GroupUserRole;
import hr.algebra.shiftschedulingapp.model.dto.GroupUserDto;
import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import hr.algebra.shiftschedulingapp.service.GroupUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@RestController
@RequestMapping("group/{groupId}")
@RequiredArgsConstructor
@Tag(name = "Group user", description = "Group user management APIs")
public class GroupUserController {

  private final GroupUserService groupUserService;

  @Operation(
    summary = "Get group users",
    description = "Returns users in a given group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "List of group users returned successfully",
      content = @Content(
        array = @ArraySchema(
          schema = @Schema(implementation = GroupUserDto.class),
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
  @GetMapping("users")
  @RequiresGroupMembership
  public List<GroupUserDto> getGroupUsers(@PathVariable Long groupId) {
    return groupUserService.getGroupUsers(groupId);
  }

  @Operation(
    summary = "Add a new group user",
    description = "Adds a user to the group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @Parameter(
    name = "userId",
    description = "ID of the user",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "User added to the group successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "User not found / user already exists in group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PostMapping("user/{userId}")
  @RequiresGroupManagerRole
  public void addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
    groupUserService.addUserToGroup(groupId, userId);
  }

  @Operation(
    summary = "Modify a group user",
    description = "Modifies the role of a group user"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @Parameter(
    name = "userId",
    description = "ID of the user",
    in = PATH
  )
  @Parameter(
    name = "role",
    description = "New role to be assigned to the user",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Group user modified successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "User does not exist in group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PutMapping("user/{userId}/role/{role}")
  @RequiresGroupManagerRole
  public void modifyUserGroup(@PathVariable Long groupId, @PathVariable Long userId, @PathVariable GroupUserRole role) {
    groupUserService.modifyUserGroup(groupId, userId, role);
  }

  @Operation(
    summary = "Delete a group user",
    description = "Removes a user from the group"
  )
  @Parameter(
    name = "groupId",
    description = "ID of the group",
    in = PATH
  )
  @Parameter(
    name = "userId",
    description = "ID of the user",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Group user deleted successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "User does not exist in group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not a manager of the group",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @DeleteMapping("user/{userId}")
  @RequiresGroupManagerRole
  public void removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
    groupUserService.removeUserFromGroup(groupId, userId);
  }
}
