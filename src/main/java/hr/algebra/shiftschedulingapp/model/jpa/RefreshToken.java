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

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @ColumnDefault("nextval('refresh_tokens_id_seq')")
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(optional = false)
  @ColumnDefault("nextval('refresh_tokens_user_id_seq')")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Column(name = "token", nullable = false)
  private UUID token;

  @NotNull
  @ColumnDefault("false")
  @Column(name = "revoked", nullable = false)
  private boolean revoked = false;
}
