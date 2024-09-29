package com.mvanalytic.apirest_demo_springboot.dto.user;

import java.util.Set;

/**
 * Clase que representa la solicitud de cambio de role de
 * un usario, esta solicitud solo el ROLE_ADMIN la puede
 * procesar
 */
public class UserAuthorityRequestDTO {

  private Long userId;
  private Set<AuthorityDTO> authorities;

  public UserAuthorityRequestDTO() {
  }

  public Set<AuthorityDTO> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<AuthorityDTO> authorities) {
    this.authorities = authorities;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

}
