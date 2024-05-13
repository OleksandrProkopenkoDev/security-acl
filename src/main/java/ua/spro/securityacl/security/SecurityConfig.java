package ua.spro.securityacl.security;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import ua.spro.securityacl.entity.Equipment;
import ua.spro.securityacl.entity.Event;
import ua.spro.securityacl.repository.UserRepository;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserRepository userRepository;

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
        .authorizeHttpRequests(config -> config.anyRequest().authenticated());
    return http.build();
  }

  @Bean
  CustomUserDetailsService userDetailsService() {
    return new CustomUserDetailsService(userRepository);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
    var expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(permissionEvaluator());
    return expressionHandler;
  }

  @Bean
  PermissionEvaluator permissionEvaluator() {
    return new PermissionEvaluatorCompositor(Map.of(
        Event.class.getSimpleName(), new TargetedPermissionEvaluator() {
          @Override
          public Object getId(Object targetDomainObject) {
            return ((Event)targetDomainObject).getId();
          }
        },
        Equipment.class.getSimpleName(), new TargetedPermissionEvaluator() {
          @Override
          public Object getId(Object targetDomainObject) {
            return ((Equipment)targetDomainObject).getId();
          }
        }
    ));
  }
}
