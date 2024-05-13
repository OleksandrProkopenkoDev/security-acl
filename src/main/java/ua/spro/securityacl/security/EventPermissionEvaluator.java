package ua.spro.securityacl.security;

import java.io.Serializable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import ua.spro.securityacl.entity.Event;
import ua.spro.securityacl.entity.PermissionEntry;

public class EventPermissionEvaluator implements PermissionEvaluator {

  @Override
  public boolean hasPermission(
      Authentication authentication, Object targetDomainObject, Object permission) {
    if (targetDomainObject != null) {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      return userDetails.getAuthorities().stream()
          .map(PermissionEntry.class::cast)
          .anyMatch(
              p ->
                  p.getPermission().equals(permission)
                      && p.getEvent().getId().equals(((Event) targetDomainObject).getId()));
    }
    return false;
  }

  @Override
  public boolean hasPermission(
      Authentication authentication, Serializable targetId, String targetType, Object permission) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    return userDetails.getAuthorities().stream()
        .map(PermissionEntry.class::cast)
        .anyMatch(
            p ->
                p.getPermission().equals(permission)
                    && p.getEvent().getId().equals(Long.valueOf(targetId.toString())));
  }
}
