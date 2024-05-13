package ua.spro.securityacl.security;

import java.io.Serializable;
import java.util.Map;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.core.Authentication;

public class PermissionEvaluatorCompositor implements PermissionEvaluator {

  private final PermissionEvaluator denyAll = new DenyAllPermissionEvaluator();
  private final Map<String, PermissionEvaluator> permissionEvaluators;

  public PermissionEvaluatorCompositor(Map<String, PermissionEvaluator> permissionEvaluators) {
    this.permissionEvaluators = permissionEvaluators;
  }

  @Override
  public boolean hasPermission(
      Authentication authentication, Object targetDomainObject, Object permission) {
    PermissionEvaluator permissionEvaluator =
        permissionEvaluators.getOrDefault(targetDomainObject.getClass().getSimpleName(), denyAll);
    return permissionEvaluator.hasPermission(authentication, targetDomainObject, permission);
  }

  @Override
  public boolean hasPermission(
      Authentication authentication, Serializable targetId, String targetType, Object permission) {
    PermissionEvaluator permissionEvaluator =
        permissionEvaluators.getOrDefault(targetType, denyAll);
    return permissionEvaluator.hasPermission(authentication, targetId, targetType, permission);
  }
}
