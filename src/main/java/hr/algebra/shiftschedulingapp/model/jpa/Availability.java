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

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "availabilities")
public class Availability {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @ColumnDefault("nextval('availabilities_id_seq')")
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(optional = false)
  @ColumnDefault("nextval('availabilities_user_id_seq')")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Column(name = "date", nullable = false)
  private LocalDate date;

  @NotNull
  @Column(name = "is_available", nullable = false)
  private boolean isAvailable;
}
