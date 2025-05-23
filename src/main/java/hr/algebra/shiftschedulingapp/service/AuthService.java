package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.model.dto.GenericAuthDto;
import hr.algebra.shiftschedulingapp.model.dto.LoginRequestDto;
import hr.algebra.shiftschedulingapp.model.dto.RegisterRequestDto;
import hr.algebra.shiftschedulingapp.model.jpa.User;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hr.algebra.shiftschedulingapp.util.AuthUtil.getCurrentUserAccessToken;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;
  private final AccessTokenService accessTokenService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public GenericAuthDto login(LoginRequestDto loginRequest, HttpServletResponse servletResponse) {
    try {
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
      User user = (User) authentication.getPrincipal();

      String accessToken = accessTokenService.generateToken(user);
      refreshTokenService.generateTokenAndSetCookie(user, servletResponse);

      return new GenericAuthDto(accessToken);
    } catch (BadCredentialsException ex) {
      throw new RestException("Invalid credentials");
    }
  }

  public GenericAuthDto refresh(HttpServletRequest servletRequest) {
    User user = refreshTokenService.validate(servletRequest);
    return new GenericAuthDto(accessTokenService.generateToken(user));
  }

  public void logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    accessTokenService.revokeToken(getCurrentUserAccessToken());
    refreshTokenService.revokeTokenAndRemoveCookie(servletRequest, servletResponse);
    getContext().setAuthentication(null);
  }

  public void register(RegisterRequestDto registerRequest) {
    if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
      throw new RestException("Email already exists");
    }

    User user = new User()
      .setEmail(registerRequest.getEmail())
      .setFirstName(registerRequest.getFirstName())
      .setLastName(registerRequest.getLastName())
      .setPassword(passwordEncoder.encode(registerRequest.getPassword()));

    userRepository.save(user);
  }
}
