package com.mvanalytic.apirest_demo_springboot.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * La clase `LoginAttempt` representa un intento de inicio de sesión en el
 * sistema,
 * rastreando tanto los intentos exitosos como los fallidos. Cada intento de
 * inicio de sesión
 * se almacena con información relevante como el usuario, la dirección IP, el
 * agente de usuario,
 * y el resultado del intento.
 */
@Entity
@Table(name = "login_attempt", schema = "dbo")
public class LoginAttempt {

  /** ID único generado para cada intento de inicio de sesión. */

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_attempt")
  private Long idAttempt;

  /**
   * Usuario que intentó iniciar sesión. Puede ser `null` si el intento de inicio
   * de sesión fue
   * anónimo o si el usuario no fue identificado correctamente.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_id_user_login_attempt"))
  private User user; // Relación con el usuario, puede ser null

  /** Fecha y hora en que se realizó el intento de inicio de sesión. */
  @Column(name = "attempt_time", columnDefinition = "DATETIME2", nullable = false)
  private Instant attemptTime;

  /** Dirección IP desde donde se realizó el intento de inicio de sesión. */
  @Column(name = "ip_address", nullable = false, length = 50)
  private String ipAddress;

  /**
   * Información del agente de usuario (navegador, sistema operativo, etc.) que se
   * utilizó en el intento.
   */
  @Column(name = "user_agent", length = 512)
  private String userAgent;

  /**
   * Resultado del intento de inicio de sesión, que puede ser 'SUCCESS' (éxito) o
   * 'FAILED' (fallido).
   */
  @Column(name = "attempt_result", nullable = false, length = 20)
  private String attemptResult;

  public LoginAttempt() {
  }

  /**
   * Constructor con todos los campos de la entidad `LoginAttempt`.
   *
   * @param idAttempt     ID del intento de inicio de sesión.
   * @param user          Usuario que realizó el intento (puede ser `null`).
   * @param attemptTime   Fecha y hora del intento de inicio de sesión.
   * @param ipAddress     Dirección IP desde donde se realizó el intento.
   * @param userAgent     Información sobre el agente de usuario.
   * @param attemptResult Resultado del intento ('SUCCESS' o 'FAILED').
   */
  public LoginAttempt(Long idAttempt, User user, Instant attemptTime, String ipAddress, String userAgent,
      String attemptResult) {
    this.idAttempt = idAttempt;
    this.user = user;
    this.attemptTime = attemptTime;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
    this.attemptResult = attemptResult;
  }

  public Long getIdAttempt() {
    return idAttempt;
  }

  public void setIdAttempt(Long idAttempt) {
    this.idAttempt = idAttempt;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Instant getAttemptTime() {
    return attemptTime;
  }

  public void setAttemptTime(Instant attemptTime) {
    this.attemptTime = attemptTime;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getAttemptResult() {
    return attemptResult;
  }

  public void setAttemptResult(String attemptResult) {
    this.attemptResult = attemptResult;
  }

}
