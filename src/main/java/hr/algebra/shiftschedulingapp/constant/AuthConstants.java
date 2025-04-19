package hr.algebra.shiftschedulingapp.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AuthConstants {

  public static final int ACCESS_TOKEN_EXPIRES_IN_MILLISECONDS = 1000 * 60 * 5; // 5 minutes
  public static final int REFRESH_TOKEN_EXPIRES_IN_MILLISECONDS = 1000 * 60 * 60 * 24; // 1 day
  public static final int REFRESH_TOKEN_EXPIRES_IN_SECONDS = REFRESH_TOKEN_EXPIRES_IN_MILLISECONDS / 1000;

  public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
  public static final String COOKIE_HEADER_NAME = "Set-Cookie";
}
