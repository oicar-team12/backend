package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  @Query("""
    UPDATE User u SET
        u.firstName = "Deleted",
        u.lastName = "User",
        u.email = "deleted@user.invalid",
        u.password = "$2a$10$invalidhashxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    WHERE u.id = :userId
    """)
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  void deleteUser(@Param("userId") Long id);

  int countByEmail(String email);
}
