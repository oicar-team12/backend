package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.interfaces.AvailabilityProjection;
import hr.algebra.shiftschedulingapp.model.jpa.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

  @Query(value = """
    SELECT json_build_object(
        'id', u.id,
        'firstName', u.first_name,
        'lastName', u.last_name
    ) AS "user",
    json_agg(json_build_object(
        'id', a.id,
        'date', a.date,
        'available', a.is_available
    ) ORDER BY a.date) AS availabilities
    FROM availabilities a
    JOIN users u ON u.id = a.user_id
    WHERE a.group_id = :groupId
        AND (:userId IS NULL OR a.user_id = :userId)
        AND (CAST(:startDate AS DATE) IS NULL OR a.date >= :startDate)
        AND (CAST(:endDate AS DATE) IS NULL OR a.date <= :endDate)
    GROUP BY u.id, u.first_name, u.last_name
    """, nativeQuery = true)
  List<AvailabilityProjection> findByGroupIdAndUserId(
    @Param("groupId") Long groupId,
    @Param("userId") Long userId,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate
  );

  boolean existsByGroup_IdAndUser_IdAndDate(Long groupId, Long userId, LocalDate date);

  boolean existsByIdAndGroup_IdAndUser_Id(Long id, Long groupId, Long userId);
}
