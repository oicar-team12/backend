package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.enums.GroupUserRole;
import hr.algebra.shiftschedulingapp.model.dto.GroupUserDto;
import hr.algebra.shiftschedulingapp.model.jpa.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {

  @Query("""
    SELECT new hr.algebra.shiftschedulingapp.model.dto.GroupUserDto(
        gu.user.id, gu.user.firstName, gu.user.lastName, gu.user.email, gu.role
    )
    FROM GroupUser gu
    WHERE gu.group.id = :groupId
    """)
  List<GroupUserDto> findByGroupId(@Param("groupId") Long groupId);

  @Query("UPDATE GroupUser gu SET gu.role = :role WHERE gu.group.id = :groupId AND gu.user.id = :userId")
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  void updateRoleByGroupIdAndUserId(@Param("role") GroupUserRole role, @Param("groupId") Long groupId, @Param("userId") Long userId);

  boolean existsByGroupIdAndUserId(Long groupId, Long userId);

  boolean existsByGroupIdAndUserIdAndRole(Long groupId, Long userId, GroupUserRole role);

  void deleteByGroupIdAndUserId(Long groupId, Long userId);

  void deleteByUserId(Long userId);

  @Query("SELECT gu.role FROM GroupUser gu WHERE gu.group.id = :groupId AND gu.user.id = :userId")
  GroupUserRole getRoleByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

  int countByUserId(Long userId);

  int countByGroupId(Long groupId);

  GroupUser findByUserId(Long userId);
}
