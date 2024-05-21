package ua.spro.securityacl.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.spro.securityacl.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(username)
        .map(
            user ->
                new User(
                    user.getEmail(),
                    user.getPassword(),
                    List.of(
                        new SimpleGrantedAuthority("ROLE_MANAGER")
//                        new SimpleGrantedAuthority("ROLE_ADMIN")
                    )
                ))
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
