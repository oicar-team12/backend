package hr.algebra.shiftschedulingapp.model.dto;

import hr.algebra.shiftschedulingapp.enums.GroupUserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserDto {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private GroupUserRole role;
}
