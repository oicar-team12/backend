package hr.algebra.shiftschedulingapp.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "schedules")
public class Schedule {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @ColumnDefault("nextval('schedules_id_seq')")
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(optional = false)
  @ColumnDefault("nextval('schedules_shift_id_seq')")
  @JoinColumn(name = "shift_id", nullable = false)
  private Shift shift;

  @NotNull
  @ManyToOne(optional = false)
  @ColumnDefault("nextval('schedules_user_id_seq')")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
