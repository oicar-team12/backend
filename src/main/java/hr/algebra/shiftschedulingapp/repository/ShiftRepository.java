package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.jpa.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
