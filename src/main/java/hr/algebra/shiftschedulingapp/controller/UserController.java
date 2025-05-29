package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import hr.algebra.shiftschedulingapp.service.AuthService;
import hr.algebra.shiftschedulingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  @Operation(
    summary = "Delete user",
    description = "Logs out and deletes the user"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "User deleted successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid token(s)",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @DeleteMapping("delete-account")
  public void deleteAccount(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    userService.deleteUser();
    authService.logout(servletRequest, servletResponse);
  }
}
