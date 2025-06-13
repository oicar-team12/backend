package hr.algebra.shiftschedulingapp.aspect;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupManagerRole;
import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.exception.ForbiddenException;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static hr.algebra.shiftschedulingapp.enums.GroupUserRole.MANAGER;
import static hr.algebra.shiftschedulingapp.util.AuthUtil.getCurrentUser;
import static hr.algebra.shiftschedulingapp.util.ParameterExtractorUtil.extractGroupId;

@Aspect
@Component
@RequiredArgsConstructor
public class GroupSecurityAspect {

  private final GroupUserRepository groupUserRepository;
  private final UserRepository userRepository;

  @Before("@annotation(requiresGroupMembership)")
  public void checkGroupMembership(JoinPoint joinPoint, RequiresGroupMembership requiresGroupMembership) {
    Long groupId = extractGroupId(joinPoint, requiresGroupMembership.groupIdParam());
    if (!groupUserRepository.existsByGroupIdAndUserId(groupId, getCurrentUser().getId())) {
      throw new ForbiddenException();
    }
  }

  @Before("@annotation(requiresGroupManagerRole)")
  public void checkGroupManagerRole(JoinPoint joinPoint, RequiresGroupManagerRole requiresGroupManagerRole) {
    Long groupId = extractGroupId(joinPoint, requiresGroupManagerRole.groupIdParam());
    if (!groupUserRepository.existsByGroupIdAndUserIdAndRole(groupId, getCurrentUser().getId(), MANAGER)) {
      throw new ForbiddenException();
    }
  }

  @Before("@annotation(hr.algebra.shiftschedulingapp.annotation.RequiresAdmin)")
  public void checkAdmin() {
    if (!userRepository.existsByIdAndIsAdminIsTrue(getCurrentUser().getId())) {
      throw new ForbiddenException();
    }
  }
}
