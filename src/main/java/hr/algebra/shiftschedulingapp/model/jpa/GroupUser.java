package hr.algebra.shiftschedulingapp.model.jpa;

import hr.algebra.shiftschedulingapp.enums.GroupUserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import static org.hibernate.type.SqlTypes.NAMED_ENUM;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "group_users")
public class GroupUser {

  @EmbeddedId
  private GroupUserId id;

  @ManyToOne(optional = false)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(optional = false)
  @MapsId("groupId")
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  @NotNull
  @Column(name = "role", nullable = false)
  @JdbcTypeCode(NAMED_ENUM)
  private GroupUserRole role;

  public GroupUser(User user, Group group, GroupUserRole role) {
    this.id = new GroupUserId();
    this.user = user;
    this.group = group;
    this.role = role;
  }
}
