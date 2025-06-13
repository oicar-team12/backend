package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.dto.UserDeleteRequestDto;
import hr.algebra.shiftschedulingapp.model.jpa.UserDeleteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDeleteRequestsRepository extends JpaRepository<UserDeleteRequest, Long> {

  @Query("""
    SELECT new hr.algebra.shiftschedulingapp.model.dto.UserDeleteRequestDto(
        udr.id, udr.user.id, udr.isApproved
    )
    FROM UserDeleteRequest udr
    WHERE udr.isApproved IS NULL
    """)
  List<UserDeleteRequestDto> findByIsApprovedIsNull();

  Long deleteTop1ByUserIdAndIsApprovedIsNullOrderByCreatedAtDesc(Long userId);

  boolean existsByUserIdAndIsApprovedIsNull(Long userId);

  List<UserDeleteRequest> findByUserId(Long userId);
}
