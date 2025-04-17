package hr.algebra.shiftschedulingapp.config;

import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

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

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> accessDeniedException() {
    // todo needs testing
    if (getContext().getAuthentication().getPrincipal() instanceof String) {
      return new ResponseEntity<>(UNAUTHORIZED);
    }
    return new ResponseEntity<>(FORBIDDEN);
  }
}
