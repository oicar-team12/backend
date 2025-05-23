package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.model.jpa.User;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
import hr.algebra.shiftschedulingapp.repository.NotificationRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static hr.algebra.shiftschedulingapp.util.AuthUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final GroupUserRepository groupUserRepository;
  private final NotificationRepository notificationRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username)
      .orElseThrow(() -> new RestException("Invalid credentials"));
  }

  public Optional<User> loadById(Long id) {
    return userRepository.findById(id);
  }

  public void deleteUser() {
    Long userId = getCurrentUser().getId();

    groupUserRepository.deleteByUserId(userId);
    notificationRepository.deleteByUserId(userId);
    userRepository.deleteUser(userId);
  }
}
