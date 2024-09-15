package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa la solicitud de cambio de email del usuaro
 * con sesión activa
 */
public class UserEmailUpdateRequestDTO {
  
  private Long id;
  private String email;
  
  public UserEmailUpdateRequestDTO() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
  
}
