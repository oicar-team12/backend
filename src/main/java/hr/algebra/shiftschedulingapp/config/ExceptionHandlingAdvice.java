package hr.algebra.shiftschedulingapp.config;

import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.exception.UnauthorizedException;
import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestControllerAdvice
public class ExceptionHandlingAdvice {

  @ExceptionHandler(RestException.class)
  public ResponseEntity<Object> restException(RestException ex) {
    RestErrorDto restErrorDto = new RestErrorDto(BAD_REQUEST.value(), ex.getMessage());
    return new ResponseEntity<>(restErrorDto, BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    RestErrorDto restErrorDto = new RestErrorDto(
      BAD_REQUEST.value(),
      "Validation failed",
      errors
    );
    return new ResponseEntity<>(restErrorDto, BAD_REQUEST);
  }


  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<Object> unauthorizedException() {
    return new ResponseEntity<>(UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> accessDeniedException() {
    if (getContext().getAuthentication().getPrincipal() instanceof String) {
      return new ResponseEntity<>(UNAUTHORIZED);
    }
    return new ResponseEntity<>(FORBIDDEN);
  }
}
