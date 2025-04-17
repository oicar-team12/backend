package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.jpa.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
