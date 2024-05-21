package ua.spro.securityacl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.spro.securityacl.dto.AddPermissionDto;
import ua.spro.securityacl.repository.UserRepository;
import ua.spro.securityacl.service.AclService;

@RestController
@RequestMapping("/api/acl")
@RequiredArgsConstructor
public class AclController {
  private final UserRepository userRepository;
  private final AclService aclService;

  @PostMapping
  public void addPermission(@RequestBody AddPermissionDto dto) {
    aclService.addPermission(dto);
  }
}
