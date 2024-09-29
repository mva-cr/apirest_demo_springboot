package com.mvanalytic.apirest_demo_springboot.domain.user;

import java.io.Serializable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Entidad que representa la tabla de autoridad de usuario (user_authority).
 * Esta tabla de unión gestiona las relaciones entre los usuarios y las
 * autoridades (roles).
 */
@Entity
@Table(name = "user_authority")
public class UserAuthority implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private UserAuthorityId id;

  /**
   * Relación muchos a uno con la entidad `User`.
   * Representa que múltiples registros en esta tabla pueden estar asociados con
   * un solo usuario.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId") // Mapea esta columna como parte de la clave compuesta
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * Relación muchos a uno con la entidad `Authority`.
   * Indica que múltiples registros en esta tabla pueden estar asociados con una
   * sola autoridad.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("authorityName") // Mapea esta columna como parte de la clave compuesta
  @JoinColumn(name = "authority_name", nullable = false)
  private Authority authority;

  /**
   * Constructor por defecto.
   */
  public UserAuthority() {
  }

  /**
   * Constructor que inicializa una nueva instancia de UserAuthority con un
   * usuario y una autoridad.
   *
   * @param user      El usuario asociado a esta autoridad.
   * @param authority La autoridad asociada al usuario.
   * @throws IllegalArgumentException Si el usuario o la autoridad son nulos.
   */
  public UserAuthority(User user, Authority authority) {
    if (user == null || authority == null) {
      throw new IllegalArgumentException("Usuario y autoridad no pueden ser nulos.");
    }
    this.user = user;
    this.authority = authority;
    this.id = new UserAuthorityId(user.getId(), authority.getName());
  }

  public UserAuthorityId getId() {
    return id;
  }

  public void setId(UserAuthorityId id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Authority getAuthority() {
    return authority;
  }

  public void setAuthority(Authority authority) {
    this.authority = authority;
  }
}
