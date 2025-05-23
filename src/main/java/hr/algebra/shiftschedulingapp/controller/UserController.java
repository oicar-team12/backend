package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.service.AuthService;
import hr.algebra.shiftschedulingapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  @DeleteMapping("delete-account")
  public void deleteAccount(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    userService.deleteUser();
    authService.logout(servletRequest, servletResponse);
  }
}
