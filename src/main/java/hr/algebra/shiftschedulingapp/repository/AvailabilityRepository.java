package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.dto.AvailabilityDto;
import hr.algebra.shiftschedulingapp.model.jpa.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

  @Query("""
    SELECT new hr.algebra.shiftschedulingapp.model.dto.AvailabilityDto(a.date, a.isAvailable)
    FROM Availability a
    WHERE a.group.id = :groupId
        AND a.user.id = :userId
    """)
  List<AvailabilityDto> findByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

  boolean existsByGroup_IdAndUser_IdAndDate(Long groupId, Long userId, LocalDate date);

  boolean existsByIdAndGroup_IdAndUser_Id(Long id, Long groupId, Long userId);
}
