package com.mvanalytic.apirest_demo_springboot.dto.user;

import java.util.Set;

/**
 * Clase que representa la solicitud de cambio de role de
 * un usario, esta solicitud solo el ROLE_ADMIN la puede
 * procesar
 */
public class UserRoleUpdateRequestDTO {

  private Long id;
  private Set<AuthorityDTO> authorities;

  public UserRoleUpdateRequestDTO() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<AuthorityDTO> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<AuthorityDTO> authorities) {
    this.authorities = authorities;
  }

}
