package hr.algebra.shiftschedulingapp.model.jpa;

import hr.algebra.shiftschedulingapp.converter.CryptoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "refresh_tokens")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @ColumnDefault("nextval('refresh_tokens_id_seq')")
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Convert(converter = CryptoConverter.class)
  @Column(name = "token", nullable = false)
  private String token;

  @NotNull
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;
}
