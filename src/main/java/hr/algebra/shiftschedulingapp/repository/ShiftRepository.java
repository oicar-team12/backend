package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.dto.ShiftDto;
import hr.algebra.shiftschedulingapp.model.jpa.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

  @Query("""
    SELECT new hr.algebra.shiftschedulingapp.model.dto.ShiftDto(
        s.id, s.date, s.startTime, s.endTime
    )
    FROM Shift s
    WHERE s.group.id = :groupId
        AND (CAST(:startDate AS DATE) IS NULL OR s.date >= :startDate)
        AND (CAST(:endDate AS DATE) IS NULL OR s.date <= :endDate)
    ORDER BY s.date, s.startTime
    """)
  List<ShiftDto> findByCriteria(
    @Param("groupId") Long groupId,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate
  );

  boolean existsByGroupIdAndDateAndStartTimeAndEndTime(Long groupId, LocalDate date, LocalTime startTime, LocalTime endTime);

  boolean existsByIdAndGroupId(Long id, Long groupId);

  int countByGroupId(Long groupId);

  Shift getFirstByGroupId(Long groupId);

  void deleteByGroupId(Long groupId);
}
