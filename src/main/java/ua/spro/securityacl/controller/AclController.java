package ua.spro.securityacl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Permission;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.spro.securityacl.dto.AddPermissionDto;
import ua.spro.securityacl.entity.User;
import ua.spro.securityacl.repository.UserRepository;

@RestController
@RequestMapping("/api/acl")
@RequiredArgsConstructor
public class AclController {
  private final UserRepository userRepository;
  private final JdbcMutableAclService aclService;

  @PostMapping
  public void addPermission(@RequestBody AddPermissionDto dto) throws ClassNotFoundException {
    User user = userRepository.findById(dto.userId()).orElseThrow();
    var objectIdentity = new ObjectIdentityImpl(
        Class.forName(dto.objectIdentity().type()), dto.objectIdentity().id());
    MutableAcl acl = (MutableAcl) aclService.readAclById(objectIdentity);
    acl.insertAce(0, mapPermission(dto.permission()), new PrincipalSid(user.getEmail()), dto.granting());
    aclService.updateAcl(acl);
  }

  private Permission mapPermission(int permission){
    return switch (permission){
      case 1 -> BasePermission.READ;
      case 2 -> BasePermission.WRITE;
      case 8 -> BasePermission.DELETE;
      default -> null;
    };
  }
}
