package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.enums.GroupUserRole;
import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.model.dto.GroupUserDto;
import hr.algebra.shiftschedulingapp.model.jpa.GroupUser;
import hr.algebra.shiftschedulingapp.repository.GroupRepository;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupUserService {

  private final GroupUserRepository groupUserRepository;
  private final GroupRepository groupRepository;
  private final UserRepository userRepository;

  public List<GroupUserDto> getGroupUsers(Long groupId) {
    return groupUserRepository.findByGroupId(groupId);
  }

  public void addUserToGroup(Long groupId, String email, GroupUserRole role) {
    validateUserExistence(email, groupId);

    groupUserRepository.save(new GroupUser(
      userRepository.getReferenceByEmail(email),
      groupRepository.getReferenceById(groupId),
      role
    ));
  }

  public void modifyUserGroup(Long groupId, Long userId, GroupUserRole role) {
    validateGroupMembership(groupId, userId);
    groupUserRepository.updateRoleByGroupIdAndUserId(role, groupId, userId);
  }

  public void removeUserFromGroup(Long groupId, Long userId) {
    validateGroupMembership(groupId, userId);
    groupUserRepository.deleteByGroupIdAndUserId(groupId, userId);
  }

  public void removeByGroupId(Long groupId) {
    groupUserRepository.deleteByGroupId(groupId);
  }

  private void validateUserExistence(String email, Long groupId) {
    if (!userRepository.existsByEmail(email)) {
      throw new RestException("User not found");
    }

    if (groupUserRepository.existsByGroupIdAndUserEmail(groupId, email)) {
      throw new RestException("User already exists in group");
    }
  }

  private void validateGroupMembership(Long groupId, Long userId) {
    if (!groupUserRepository.existsByGroupIdAndUserId(groupId, userId)) {
      throw new RestException("User does not exist in group");
    }
  }
}
