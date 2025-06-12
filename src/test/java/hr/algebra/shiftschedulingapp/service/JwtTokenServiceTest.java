package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.UnitTest;
import hr.algebra.shiftschedulingapp.helper.Clock;
import hr.algebra.shiftschedulingapp.model.jpa.AccessToken;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class JwtTokenServiceTest extends UnitTest {

  @Mock
  private Clock clock;

  private JwtTokenService jwtTokenService;

  private static final String MOCK_JWT_SECRET = "5eb2a801c9msh9fa39b1fc78692fp118655jsn959cd091155d";
  private static final String TOKEN_VALID = "eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjEsInN1YiI6Imhhcmxpa29kYXNtYUBnbWFpbC5jb20iLCJpYXQiOjE3NDk3NDgwODYsImV4cCI6MTc1MDM0ODA4Nn0.2V060YnhMO-bsDS93dzuzz0WzC2GykxjeplfWgR7Lhc";
  private static final String TOKEN_EXPIRED = "eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjEsInN1YiI6Imhhcmxpa29kYXNtYUBnbWFpbC5jb20iLCJpYXQiOjE3NDk3NDgzOTcsImV4cCI6MTc0OTc0ODY5N30.mzR6VwZYxuATkr1v9jG1JT58p3x2U5jfaOdDKk6ntGs";

  @BeforeEach
  void setUp() {
    jwtTokenService = new JwtTokenService(clock, MOCK_JWT_SECRET);
  }

  @Test
  void isTokenValid_hasNotExpired_ok() {
    when(clock.getDate()).thenReturn(Date.from(Instant.parse("2025-06-12T20:10:00.00Z")));
    assertTrue(jwtTokenService.isTokenValid(new AccessToken(TOKEN_VALID)));
  }

  @Test
  void isTokenValid_hasExpired_notOk() {
    AccessToken accessToken = new AccessToken(TOKEN_EXPIRED);
    assertThrows(ExpiredJwtException.class, () -> jwtTokenService.isTokenValid(accessToken));
  }
}
