package hr.algebra.shiftschedulingapp.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {

  private String accessToken;
  private String refreshToken;
}
