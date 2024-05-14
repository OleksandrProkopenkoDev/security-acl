package ua.spro.securityacl.dto;

import java.io.Serializable;

public record AddPermissionDto(
    ObjectIdentityDto objectIdentity,
    Long userId,
    Integer permission,
    boolean granting
) {

  public record ObjectIdentityDto(
      String type,
      Serializable id
  ){}
}
