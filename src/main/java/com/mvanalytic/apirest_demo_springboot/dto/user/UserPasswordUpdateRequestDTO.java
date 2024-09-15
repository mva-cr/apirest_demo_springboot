package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa la solicitud de cambio de contraseña del usuario
 * con sesión activa
 */
public class UserPasswordUpdateRequestDTO {

  private Long id;
  private String newPassword;
  private String oldPassword;
  
  public UserPasswordUpdateRequestDTO() {
  }
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getNewPassword() {
    return newPassword;
  }
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
  public String getOldPassword() {
    return oldPassword;
  }
  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }
  
}
