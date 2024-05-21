package ua.spro.securityacl.service;

import org.springframework.web.bind.annotation.RequestBody;
import ua.spro.securityacl.dto.AddPermissionDto;

public interface AclService {

  void addPermission(@RequestBody AddPermissionDto dto);
}
