package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.jpa.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  void deleteByUserId(Long userId);
}
