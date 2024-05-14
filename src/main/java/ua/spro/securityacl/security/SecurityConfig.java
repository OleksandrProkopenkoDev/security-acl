package ua.spro.securityacl.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import ua.spro.securityacl.repository.UserRepository;

@Configuration
// @EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserRepository userRepository;
  private final PermissionEvaluator permissionEvaluator;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .userDetailsService(userDetailsService())
        .formLogin(
            config -> {
              config.successHandler(
                  (request, response, authentication) -> response.setStatus(HttpStatus.OK.value()));
              config.failureHandler(
                  new AuthenticationEntryPointFailureHandler(
                      new HttpStatusEntryPoint(HttpStatus.BAD_REQUEST)));
            })
        .logout(
            config -> {
              config.deleteCookies("JSESSIONID");
              config.invalidateHttpSession(true);
              config.logoutSuccessHandler(
                  (request, response, authentication) -> response.setStatus(HttpStatus.OK.value()));
            })
        .exceptionHandling(
            config ->
                config.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .authorizeHttpRequests(
            config -> {
              config
                  .requestMatchers(HttpMethod.PATCH, "/api/{id}")
                  .access(
                      webExpressionAuthorizationManager(
                          "hasPermission(new Long(#id), 'ua.spro.securityacl.entity.Event', 'WRITE')"));
              config.anyRequest().authenticated();
            });
    return http.build();
  }

  public WebExpressionAuthorizationManager webExpressionAuthorizationManager(String expression) {
    var authorizationManager = new WebExpressionAuthorizationManager(
        expression);
    authorizationManager.setExpressionHandler(defaultHttpSecurityExpressionHandler());
    return authorizationManager;
  }

  @Bean
  DefaultHttpSecurityExpressionHandler defaultHttpSecurityExpressionHandler(
      ) {
    var expressionHandler = new DefaultHttpSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(permissionEvaluator);
    return expressionHandler;
  }

  @Bean
  CustomUserDetailsService userDetailsService() {
    return new CustomUserDetailsService(userRepository);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
