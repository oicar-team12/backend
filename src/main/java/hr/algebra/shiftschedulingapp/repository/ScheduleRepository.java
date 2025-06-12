package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.interfaces.ScheduleProjection;
import hr.algebra.shiftschedulingapp.model.jpa.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  @Query(value = """
    WITH filtered_shifts AS (
        SELECT DISTINCT shifts.id, shifts.date, shifts.start_time, shifts.end_time
        FROM schedules sch
        JOIN shifts ON shifts.id = sch.shift_id
        WHERE shifts.group_id = :groupId
            AND (:userId IS NULL OR EXISTS (
                SELECT 1 FROM schedules sch2
                WHERE sch2.shift_id = shifts.id
                AND sch2.user_id = :userId
            ))
            AND (CAST(:startDate AS DATE) IS NULL OR shifts.date >= :startDate)
            AND (CAST(:endDate AS DATE) IS NULL OR shifts.date <= :endDate)
        )
    SELECT json_build_object(
        'id', fs.id,
        'date', fs.date,
        'startTime', fs.start_time,
        'endTime', fs.end_time
    ) AS shift,
    json_agg(json_build_object(
        'id', u.id,
        'firstName', u.first_name,
        'lastName', u.last_name
    )) AS users
    FROM filtered_shifts fs
    JOIN schedules sch ON sch.shift_id = fs.id
    JOIN users u ON u.id = sch.user_id
    GROUP BY fs.id, fs.date, fs.start_time, fs.end_time
    ORDER BY fs.date, fs.start_time
    """, nativeQuery = true)
  List<ScheduleProjection> findByCriteria(
    @Param("groupId") Long groupId,
    @Param("userId") Long userId,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate
  );

  boolean existsByShift_IdAndUser_Id(Long shiftId, Long userId);

  boolean existsByShift_GroupId_AndId(Long shiftGroupId, Long id);

  int countByUser_Id(Long userId);

  Schedule getFirstByUser_Id(Long userId);
}
