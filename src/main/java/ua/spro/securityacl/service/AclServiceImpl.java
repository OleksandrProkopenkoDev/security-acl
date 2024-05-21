package ua.spro.securityacl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ua.spro.securityacl.dto.AddPermissionDto;
import ua.spro.securityacl.entity.User;
import ua.spro.securityacl.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AclServiceImpl implements AclService {

  private final UserRepository userRepository;
  private final JdbcMutableAclService mutableAclService;

  @Override
  public void addPermission(@RequestBody AddPermissionDto dto) {
    User user = userRepository.findById(dto.userId()).orElseThrow();
    ObjectIdentityImpl objectIdentity;
    try {
      objectIdentity =
          new ObjectIdentityImpl(
              Class.forName(dto.objectIdentity().type()), dto.objectIdentity().id());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    MutableAcl acl = (MutableAcl) mutableAclService.readAclById(objectIdentity);
    PrincipalSid sid = new PrincipalSid(user.getEmail());

    log.info("owner_sid in acl : {}", acl.getOwner());
    log.info("new sid: {}", sid);

    acl.insertAce(
        0, mapPermission(dto.permission()), sid, dto.granting());
    mutableAclService.updateAcl(acl);
  }

  private Permission mapPermission(int permission) {
    return switch (permission) {
      case 1 -> BasePermission.READ;
      case 2 -> BasePermission.WRITE;
      case 8 -> BasePermission.DELETE;
      default -> null;
    };
  }
}
