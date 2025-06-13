package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.jpa.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

  @Query("FROM AccessToken at LEFT JOIN FETCH at.user WHERE at.token = :token")
  Optional<AccessToken> findByToken(@Param("token") String token);

  void deleteByUserId(Long userId);

  void deleteByToken(String token);

  int countByUserId(Long userId);

  int countByToken(String token);
}
