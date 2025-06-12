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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.Integer.MAX_VALUE;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "access_tokens")
public class AccessToken {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @ColumnDefault("nextval('access_tokens_id_seq')")
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Column(name = "token", nullable = false, length = MAX_VALUE)
  private String token;

  @NotNull
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  public AccessToken(String token) {
    this.token = token;
  }
}
