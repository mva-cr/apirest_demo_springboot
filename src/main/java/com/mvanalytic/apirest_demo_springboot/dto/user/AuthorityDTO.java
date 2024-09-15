package com.mvanalytic.apirest_demo_springboot.dto.user;

import java.io.Serializable;

/**
 * Clase que representa la el rol del usuario
 */
public class AuthorityDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;

  public AuthorityDTO() {
  }

  public AuthorityDTO(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "AuthorityDTO [name=" + name + "]";
  }

}
