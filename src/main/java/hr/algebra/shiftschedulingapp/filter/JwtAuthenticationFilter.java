package hr.algebra.shiftschedulingapp.filter;

import hr.algebra.shiftschedulingapp.exception.UnauthorizedException;
import hr.algebra.shiftschedulingapp.model.jpa.User;
import hr.algebra.shiftschedulingapp.service.AccessTokenService;
import hr.algebra.shiftschedulingapp.service.JwtTokenService;
import hr.algebra.shiftschedulingapp.service.UserService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static java.util.Collections.emptyList;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenService jwtTokenService;
  private final AccessTokenService accessTokenService;
  private final UserService userService;

  @Override
  protected void doFilterInternal(
    @Nonnull HttpServletRequest request,
    @Nonnull HttpServletResponse response,
    @Nonnull FilterChain filterChain
  ) throws ServletException, IOException {
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String authHeader = request.getHeader("Authorization");

      if (isNotEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        try {
          if (accessTokenService.isExistingTokenValid(token)) {
            Long userId = jwtTokenService.extractUserId(token);
            User user = userService.loadById(userId)
              .orElseThrow(UnauthorizedException::new);

            Authentication authentication = new UsernamePasswordAuthenticationToken(user, token, emptyList());
            getContext().setAuthentication(authentication);
          }
        } catch (Exception e) {
          throw new UnauthorizedException();
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
