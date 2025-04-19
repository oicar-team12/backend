package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.exception.UnauthorizedException;
import hr.algebra.shiftschedulingapp.model.jpa.RefreshToken;
import hr.algebra.shiftschedulingapp.model.jpa.User;
import hr.algebra.shiftschedulingapp.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static hr.algebra.shiftschedulingapp.constant.AuthConstants.COOKIE_HEADER_NAME;
import static hr.algebra.shiftschedulingapp.constant.AuthConstants.REFRESH_TOKEN_COOKIE_NAME;
import static hr.algebra.shiftschedulingapp.constant.AuthConstants.REFRESH_TOKEN_EXPIRES_IN_SECONDS;
import static java.lang.String.format;
import static java.time.OffsetDateTime.now;
import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  public User validate(HttpServletRequest servletRequest) {
    RefreshToken token = refreshTokenRepository.findByToken(getRefreshToken(servletRequest))
      .orElseThrow(UnauthorizedException::new);

    if (token.getExpiresAt().isBefore(now())) {
      refreshTokenRepository.deleteByUserId(token.getUser().getId());
      throw new UnauthorizedException();
    }

    return token.getUser();
  }

  public void generateTokenAndSetCookie(User user, HttpServletResponse servletResponse) {
    createCookie(generateToken(user), servletResponse);
  }

  public void revokeTokenAndRemoveCookie(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    refreshTokenRepository.findByToken(getRefreshToken(servletRequest))
      .ifPresentOrElse(
        refreshToken -> refreshTokenRepository.deleteByToken(refreshToken.getToken()),
        () -> {
          throw new RestException("Invalid token");
        }
      );
    removeCookie(servletResponse);
  }

  private UUID generateToken(User user) {
    refreshTokenRepository.deleteByUserId(user.getId());

    RefreshToken refreshToken = new RefreshToken()
      .setToken(randomUUID())
      .setExpiresAt(now().plusSeconds(REFRESH_TOKEN_EXPIRES_IN_SECONDS))
      .setUser(user);

    return refreshTokenRepository.save(refreshToken).getToken();
  }

  private void createCookie(UUID token, HttpServletResponse servletResponse) {
    String cookie = format(
      "%s=%s; Max-Age=%d; SameSite=Strict; HttpOnly; Secure; Path=/",
      REFRESH_TOKEN_COOKIE_NAME, token, REFRESH_TOKEN_EXPIRES_IN_SECONDS
    );
    servletResponse.addHeader(COOKIE_HEADER_NAME, cookie);
  }

  private void removeCookie(HttpServletResponse response) {
    String cookie = format(
      "%s=; Max-Age=0; SameSite=Strict; HttpOnly; Secure; Path=/",
      REFRESH_TOKEN_COOKIE_NAME
    );
    response.addHeader(COOKIE_HEADER_NAME, cookie);
  }

  private UUID getRefreshToken(HttpServletRequest servletRequest) {
    if (servletRequest.getCookies() == null) {
      return null;
    }

    Cookie tokenCookie = stream(servletRequest.getCookies())
      .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
      .findFirst().orElseThrow(UnauthorizedException::new);

    return UUID.fromString(tokenCookie.getValue());
  }
}
