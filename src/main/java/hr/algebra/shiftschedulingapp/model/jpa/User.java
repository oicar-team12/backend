package hr.algebra.shiftschedulingapp.model.jpa;

import hr.algebra.shiftschedulingapp.converter.CryptoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Collections.emptyList;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @ColumnDefault("nextval('users_id_seq')")
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @Convert(converter = CryptoConverter.class)
  @Column(name = "first_name", nullable = false, length = MAX_VALUE)
  private String firstName;

  @NotNull
  @Convert(converter = CryptoConverter.class)
  @Column(name = "last_name", nullable = false, length = MAX_VALUE)
  private String lastName;

  @Size(max = 255)
  @NotNull
  @Convert(converter = CryptoConverter.class)
  @Column(name = "email", nullable = false)
  private String email;

  @NotNull
  @Column(name = "password", nullable = false, length = MAX_VALUE)
  private String password;

  @NotNull
  @Column(name = "is_admin", nullable = false)
  private boolean isAdmin;


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return emptyList();
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
