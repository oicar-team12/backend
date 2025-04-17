package hr.algebra.shiftschedulingapp.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.hash;

@Getter
@Setter
@Embeddable
public class GroupUserId implements Serializable {

  @Serial
  private static final long serialVersionUID = -3856462674912630840L;

  @NotNull
  @ColumnDefault("nextval('group_users_group_id_seq')")
  @Column(name = "group_id", nullable = false)
  private Long groupId;

  @NotNull
  @ColumnDefault("nextval('group_users_user_id_seq')")
  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }

    GroupUserId entity = (GroupUserId) o;
    return Objects.equals(this.groupId, entity.groupId) && Objects.equals(this.userId, entity.userId);
  }

  @Override
  public int hashCode() {
    return hash(groupId, userId);
  }
}
