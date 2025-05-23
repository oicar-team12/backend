package hr.algebra.shiftschedulingapp.util;

import hr.algebra.shiftschedulingapp.model.jpa.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AuthUtil {

  public static User getCurrentUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public static String getCurrentUserAccessToken() {
    return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
  }
}
