package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * DTO utilizado para gestionar el restablecimiento de la contraseña
 * por el ROLE_ADMIN
 */
public class ResendActivationRequestDTO {

  private String email;

  public ResendActivationRequestDTO() {
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

}
