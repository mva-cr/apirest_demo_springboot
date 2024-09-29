package com.mvanalytic.apirest_demo_springboot.domain.user;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Clase que representa la clave compuesta para la entidad 'UserAuthority'.
 * 
 * Esta clase se utiliza para definir la clave primaria compuesta de la tabla
 * 'user_authority', que consiste en 'user_id' (ID del usuario) y
 * 'authority_name' (nombre de la autoridad o rol).
 */
@Embeddable
public class UserAuthorityId implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Identificador del usuario al que se le asigna la autoridad (rol).
   */
  @Column(name = "user_id")
  private Long userId;

  /**
   * Nombre de la autoridad (rol) asignada al usuario.
   */
  @Column(name = "authority_name")
  private String authorityName;

  public UserAuthorityId() {
  }

  public UserAuthorityId(Long userId, String authorityName) {
    this.userId = userId;
    this.authorityName = authorityName;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getAuthorityName() {
    return authorityName;
  }

  public void setAuthorityName(String authorityName) {
    this.authorityName = authorityName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, authorityName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserAuthorityId that = (UserAuthorityId) o;
    return Objects.equals(userId, that.userId) && Objects.equals(authorityName, that.authorityName);
  }

}
