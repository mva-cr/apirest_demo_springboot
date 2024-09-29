package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa la petición de cambio de los atributos
 * status y activated, esta petición solo la puede enviar el
 * ROLE_ADMIN
 */
public class UserStatusRequestDTO {
  
  private Long id;
  private Boolean status;
  private Boolean activated;
  
  public UserStatusRequestDTO() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public Boolean getActivated() {
    return activated;
  }

  public void setActivated(Boolean activated) {
    this.activated = activated;
  }
  
}
