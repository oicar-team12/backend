package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.model.jpa.AccessToken;
import hr.algebra.shiftschedulingapp.model.jpa.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static hr.algebra.shiftschedulingapp.constant.AuthConstants.ACCESS_TOKEN_EXPIRES_IN_MILLISECONDS;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.lang.System.currentTimeMillis;

@Service
public class JwtTokenService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  public String generateToken(User user) {
    return Jwts.builder()
      .claims(generateTokenClaims(user))
      .subject(user.getEmail())
      .issuedAt(new Date())
      .expiration(new Date(currentTimeMillis() + ACCESS_TOKEN_EXPIRES_IN_MILLISECONDS))
      .signWith(getSignKey())
      .compact();
  }

  public boolean isTokenValid(AccessToken accessToken) {
    return extractExpiration(accessToken.getToken()).after(new Date());
  }

  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("uid", Integer.class)).longValue();
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith((SecretKey) getSignKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  private Map<String, Object> generateTokenClaims(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("uid", user.getId());
    return claims;
  }

  private Key getSignKey() {
    byte[] keyBytes = BASE64.decode(jwtSecret);
    return hmacShaKeyFor(keyBytes);
  }
}
