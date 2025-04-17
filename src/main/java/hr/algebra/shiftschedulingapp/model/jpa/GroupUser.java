package hr.algebra.shiftschedulingapp.model.jpa;

import hr.algebra.shiftschedulingapp.enums.GroupUserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@Entity
@Table(name = "group_users")
public class GroupUser {

  @EmbeddedId
  private GroupUserId id;

  @NotNull
  @Enumerated(STRING)
  @Column(name = "role", nullable = false)
  private GroupUserRole role;
}
