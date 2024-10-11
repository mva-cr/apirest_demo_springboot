package com.mvanalytic.apirest_demo_springboot.domain.user;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa un token de actualización (refresh token) en el
 * sistema.
 * Los refresh tokens se utilizan para permitir a los usuarios obtener nuevos
 * tokens JWT sin necesidad de volver a autenticarse.
 */
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_token")
  private Long id;

  /**
   * Relación uno a uno con la entidad `User`, asociando el refresh token con un
   * usuario específico. Se utiliza carga perezosa (lazy loading) para optimizar
   * el rendimiento.
   */
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_user", referencedColumnName = "id")
  private User user;

  /**
   * Token de actualización único. Este campo no puede ser nulo y debe ser único
   * en la base de datos.
   */
  @Column(nullable = false, unique = true)
  private String token;

  /**
   * Fecha y hora en la que el token de actualización expira. Este campo es
   * obligatorio.
   */
  @Column(name = "expiry_date", columnDefinition = "DATETIME2", nullable = false)
  private Instant expiryDate;

  public RefreshToken() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Instant getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Instant expiryDate) {
    this.expiryDate = expiryDate;
  }

}
