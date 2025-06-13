package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.model.dto.GenericAuthDto;
import hr.algebra.shiftschedulingapp.model.dto.LoginRequestDto;
import hr.algebra.shiftschedulingapp.model.dto.LoginResponseDto;
import hr.algebra.shiftschedulingapp.model.dto.RegisterRequestDto;
import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import hr.algebra.shiftschedulingapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.COOKIE;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

  private final AuthService authService;

  @Operation(
    summary = "Log in user",
    description = "Authenticates a user and returns authentication tokens - refresh token as a cookie, access token as a payload"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Logged in successfully",
      content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid credentials",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PostMapping("login")
  public LoginResponseDto login(@RequestBody @Valid LoginRequestDto loginRequest, HttpServletResponse servletResponse) {
    return authService.login(loginRequest, servletResponse);
  }

  @Operation(
    summary = "Refresh access token",
    description = "Returns a new access token and invalidates the previous one"
  )
  @Parameter(
    name = "refreshToken",
    description = "Refresh token",
    in = COOKIE
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Created a new access token successfully",
      content = @Content(schema = @Schema(implementation = GenericAuthDto.class))
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Invalid refresh token",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PostMapping("refresh")
  public GenericAuthDto refresh(HttpServletRequest servletRequest) {
    return authService.refresh(servletRequest);
  }

  @Operation(
    summary = "Log out user",
    description = "Invalidates the user's refresh and access tokens"
  )
  @Parameter(
    name = "refreshToken",
    description = "Refresh token",
    in = COOKIE
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Logged out successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid token(s)",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @DeleteMapping("logout")
  public void logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    authService.logout(servletRequest, servletResponse);
  }

  @Operation(
    summary = "Register a new user",
    description = "Creates a new user"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "User registered successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Email already exists or invalid data is provided",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PostMapping("register")
  public void register(@RequestBody @Valid RegisterRequestDto registerRequest) {
    authService.register(registerRequest);
  }
}
