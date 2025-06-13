package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.jpa.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

  Long countById(Long id);
}
