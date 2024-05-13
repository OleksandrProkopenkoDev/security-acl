package ua.spro.securityacl.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class EventPermissionFilter extends OncePerRequestFilter {
  private final EventPermissionsService eventPermissionsService;

  private final RequestMatcher requestMatcher;

  @Override
  protected void doFilterInternal(
      @Nonnull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain)
      throws ServletException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String[] parts = request.getServletPath().split("/");
    Long id = Long.valueOf(parts[parts.length - 1]);
    if (!eventPermissionsService.hasPermissions(authentication, id)){
      response.setStatus(HttpStatus.FORBIDDEN.value());
      return;
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(@Nonnull HttpServletRequest request) throws ServletException {
    return !requestMatcher.matches(request);
  }
}
