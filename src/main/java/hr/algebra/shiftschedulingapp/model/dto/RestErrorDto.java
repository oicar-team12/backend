package hr.algebra.shiftschedulingapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class RestErrorDto {

  private int status;
  private String message;
  private Map<String, String> errors;

  public RestErrorDto(int status, String message) {
    this(status, message, null);
  }
}
