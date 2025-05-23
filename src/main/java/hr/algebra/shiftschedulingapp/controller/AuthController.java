package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.model.dto.GenericAuthDto;
import hr.algebra.shiftschedulingapp.model.dto.LoginRequestDto;
import hr.algebra.shiftschedulingapp.model.dto.RegisterRequestDto;
import hr.algebra.shiftschedulingapp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("login")
  public GenericAuthDto login(@RequestBody @Valid LoginRequestDto loginRequest, HttpServletResponse servletResponse) {
    return authService.login(loginRequest, servletResponse);
  }

  @PostMapping("refresh")
  public GenericAuthDto refresh(HttpServletRequest servletRequest) {
    return authService.refresh(servletRequest);
  }

  @DeleteMapping("logout")
  public void logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    authService.logout(servletRequest, servletResponse);
  }

  @PostMapping("register")
  public void register(@RequestBody @Valid RegisterRequestDto registerRequest) {
    authService.register(registerRequest);
  }
}
