package ua.spro.securityacl.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Entity
public class PermissionEntry implements GrantedAuthority {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, nullable = false)
  private Long id;

  private String permission;

  private String targetDomainObjectType;
  private String targetDomainObjectId;

  @ManyToMany(mappedBy = "permissions")
  private List<User> users = new ArrayList<>();

  @Override
  public String getAuthority() {
    return permission;
  }
}
