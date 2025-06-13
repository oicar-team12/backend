package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.model.dto.GroupDto;
import hr.algebra.shiftschedulingapp.service.GroupUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

  private final GroupUserService groupUserService;

  @Operation(
    summary = "Get user's groups",
    description = "Returns a list of groups where the current user belongs to"
  )
  @ApiResponse(
    responseCode = "200",
    description = "List of user's groups returned successfully"
  )
  @GetMapping("groups")
  public List<GroupDto> getGroups() {
    return groupUserService.getGroupsForCurrentUser();
  }
}
