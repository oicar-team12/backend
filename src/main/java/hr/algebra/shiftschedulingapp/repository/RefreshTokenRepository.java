package hr.algebra.shiftschedulingapp.repository;

import hr.algebra.shiftschedulingapp.model.jpa.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  @Query("FROM RefreshToken rt LEFT JOIN FETCH rt.user WHERE rt.token = :token")
  Optional<RefreshToken> findByToken(@Param("token") String token);

  void deleteByUserId(Long userId);

  void deleteByToken(String token);

  int countByUserId(Long userId);

  int countByToken(String token);
}
