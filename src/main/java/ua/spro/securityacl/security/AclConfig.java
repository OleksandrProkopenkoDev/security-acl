package ua.spro.securityacl.security;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
@RequiredArgsConstructor
public class AclConfig {

  private final DataSource dataSource;

  @Bean
  MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
    var expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(permissionEvaluator());
    return expressionHandler;
  }

  @Bean
  PermissionEvaluator permissionEvaluator() {
    return new AclPermissionEvaluator(aclService());
  }

  @Bean
  JdbcMutableAclService aclService() {
    JdbcMutableAclService jdbcMutableAclService =
        new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());

    jdbcMutableAclService.setClassIdentityQuery("SELECT currval('event_id_seq')");
    jdbcMutableAclService.setSidIdentityQuery("SELECT currval('event_id_seq')");
    jdbcMutableAclService.setAclClassIdSupported(false);

    return jdbcMutableAclService;
  }

  @Bean
  AclAuthorizationStrategy aclAuthorizationStrategy() {
    return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  @Bean
  PermissionGrantingStrategy permissionGrantingStrategy() {
    return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
  }

  @Bean
  LookupStrategy lookupStrategy() {
    return new BasicLookupStrategy(
        dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
  }

  @Bean
  SpringCacheBasedAclCache aclCache() {
    return new SpringCacheBasedAclCache(
        caffeineCache(), permissionGrantingStrategy(), aclAuthorizationStrategy());
  }

  @Bean
  Cache caffeineCache() {
    var caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(
        Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).maximumSize(100));
    return caffeineCacheManager.getCache("acl");
  }
}
