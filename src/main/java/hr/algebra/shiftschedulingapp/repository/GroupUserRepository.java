package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.jpa.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
}
