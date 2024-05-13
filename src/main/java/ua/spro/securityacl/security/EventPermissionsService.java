package ua.spro.securityacl.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.spro.securityacl.entity.User;
import ua.spro.securityacl.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EventPermissionsService {

  private final UserRepository userRepository;

  public boolean hasPermissions(Authentication authentication, Long eventId, String permission) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    return user.getPermissions().stream()
        .anyMatch(
            p -> p.getPermission().equals(permission) && p.getEvent().getId().equals(eventId));
  }
}
