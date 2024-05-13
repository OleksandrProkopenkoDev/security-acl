package ua.spro.securityacl.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ua.spro.securityacl.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserRepository userRepository;
  private final EventPermissionsService eventPermissionsService;

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http) throws Exception {
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
        .authorizeHttpRequests(config -> config.anyRequest().authenticated())
        .addFilterBefore(
            new EventPermissionFilter(
                eventPermissionsService, new AntPathRequestMatcher("/api/events/**")),
            AuthorizationFilter.class);
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
}
