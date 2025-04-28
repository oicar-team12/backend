package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupManagerRole;
import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.enums.GroupUserRole;
import hr.algebra.shiftschedulingapp.model.dto.GroupUserDto;
import hr.algebra.shiftschedulingapp.service.GroupUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("group/{groupId}")
@RequiredArgsConstructor
public class GroupUserController {

  private final GroupUserService groupUserService;

  @GetMapping("users")
  @RequiresGroupMembership
  public List<GroupUserDto> getGroupUsers(@PathVariable Long groupId) {
    return groupUserService.getGroupUsers(groupId);
  }

  @PostMapping("user/{userId}")
  @RequiresGroupManagerRole
  public void addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
    groupUserService.addUserToGroup(groupId, userId);
  }

  @PutMapping("user/{userId}/role/{role}")
  @RequiresGroupManagerRole
  public void modifyUserGroup(@PathVariable Long groupId, @PathVariable Long userId, @PathVariable GroupUserRole role) {
    groupUserService.modifyUserGroup(groupId, userId, role);
  }

  @DeleteMapping("user/{userId}")
  @RequiresGroupManagerRole
  public void removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
    groupUserService.removeUserFromGroup(groupId, userId);
  }
}
