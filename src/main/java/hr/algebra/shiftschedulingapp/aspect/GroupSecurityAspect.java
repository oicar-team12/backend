package hr.algebra.shiftschedulingapp.aspect;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupManagerRole;
import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.exception.ForbiddenException;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
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

  @Before("@annotation(requiresGroupMembership)")
  public void checkGroupMembership(JoinPoint joinPoint, RequiresGroupMembership requiresGroupMembership) {
    Long groupId = extractGroupId(joinPoint, requiresGroupMembership.groupIdParam());
    if (!groupUserRepository.existsByGroup_IdAndUser_Id(groupId, getCurrentUser().getId())) {
      throw new ForbiddenException();
    }
  }

  @Before("@annotation(requiresGroupManagerRole)")
  public void checkGroupManagerRole(JoinPoint joinPoint, RequiresGroupManagerRole requiresGroupManagerRole) {
    Long groupId = extractGroupId(joinPoint, requiresGroupManagerRole.groupIdParam());
    if (!groupUserRepository.existsByGroup_IdAndUser_IdAndRole(groupId, getCurrentUser().getId(), MANAGER)) {
      throw new ForbiddenException();
    }
  }
}
