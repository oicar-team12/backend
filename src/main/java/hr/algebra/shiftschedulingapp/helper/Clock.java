package hr.algebra.shiftschedulingapp.helper;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Clock {

  public Date getDate() {
    return new Date();
  }

  public Date getDate(long date) {
    return new Date(date);
  }
}
