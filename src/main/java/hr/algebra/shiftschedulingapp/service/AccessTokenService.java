package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.model.jpa.AccessToken;
import hr.algebra.shiftschedulingapp.model.jpa.User;
import hr.algebra.shiftschedulingapp.repository.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.time.OffsetDateTime.now;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

  private final AccessTokenRepository accessTokenRepository;
  private final JwtTokenService jwtTokenService;

  public String generateToken(User user) {
    accessTokenRepository.deleteByUserId(user.getId());

    String accessToken = jwtTokenService.generateToken(user);
    return saveToken(accessToken, user);
  }

  public void revokeToken(String token) {
    accessTokenRepository.findByToken(token)
      .ifPresentOrElse(
        accessToken -> accessTokenRepository.deleteByToken(accessToken.getToken()),
        () -> {
          throw new RestException("Invalid token");
        }
      );
  }

  public boolean isExistingTokenValid(String token) {
    return accessTokenRepository.findByToken(token)
      .map(jwtTokenService::isTokenValid)
      .orElse(false);
  }

  private String saveToken(String token, User user) {
    accessTokenRepository.deleteByUserId(user.getId());

    AccessToken accessToken = new AccessToken()
      .setToken(token)
      .setUser(user)
      .setExpiresAt(jwtTokenService.extractExpiration(token).toInstant().atOffset(now().getOffset()));

    return accessTokenRepository.save(accessToken).getToken();
  }
}
