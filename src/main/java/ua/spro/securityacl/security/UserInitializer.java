package ua.spro.securityacl.security;

import java.time.LocalDate;
import java.time.Month;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.spro.securityacl.entity.User;
import ua.spro.securityacl.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserInitializer {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @EventListener(value = ApplicationStartedEvent.class)
  public void initUsers() {
    createUserIfNotExists(
        "vasily@gmail.com", "vasily", "Vasily", "Vasily", LocalDate.of(1997, Month.APRIL, 15));
    createUserIfNotExists(
        "petro@gmail.com", "petro", "Petro", "Petro", LocalDate.of(1995, Month.AUGUST, 11));
  }

  private void createUserIfNotExists(
      String email, String password, String firstname, String lastname, LocalDate dateOfBirth) {
    if(!userRepository.existsByEmail(email)){
      var user = new User();
      user.setEmail(email);
      user.setPassword(passwordEncoder.encode(password));
      user.setFirstname(firstname);
      user.setLastname(lastname);
      user.setDateOfBirth(dateOfBirth);
      userRepository.save(user);
    }
  }
}
