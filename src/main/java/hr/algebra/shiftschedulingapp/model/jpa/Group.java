package hr.algebra.shiftschedulingapp.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.Integer.MAX_VALUE;

@Getter
@Setter
@Entity
@Table(name = "groups")
public class Group {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @ColumnDefault("nextval('groups_id_seq')")
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "name", nullable = false, length = MAX_VALUE)
  private String name;
}
