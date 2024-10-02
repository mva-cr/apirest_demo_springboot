package com.mvanalytic.apirest_demo_springboot.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * La entidad `UserSession` rastrea las sesiones de usuario en la aplicación,
 * incluyendo información como la hora de inicio y fin de sesión, la dirección
 * IP
 * y el agente de usuario utilizado para la sesión.
 */
@Entity
@Table(name = "user_session")
public class UserSession {

  @Id
  @Column(name = "id_session", length = 128)
  private String idSession;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_id_user_user_session"))
  private User user; // Relación con el usuario

  @Column(name = "start_time", columnDefinition = "DATETIME2", nullable = false)
  private Instant startTime;

  @Column(name = "end_time", columnDefinition = "DATETIME2")
  private Instant endTime;

  @Column(name = "ip_address", nullable = false, length = 50)
  private String ipAddress;

  @Column(name = "user_agent", length = 512)
  private String userAgent;

  // ('ACTIVE', 'EXPIRED', 'LOGGED_OUT')
  @Column(name = "session_status", nullable = false, length = 50)
  private String sessionStatus;

  public UserSession() {
  }

  /**
   * Constructor para inicializar todos los campos de la entidad `UserSession`.
   * 
   * @param idSession     ID único de la sesión.
   * @param user          Usuario asociado con la sesión.
   * @param startTime     Tiempo de inicio de la sesión.
   * @param endTime       Tiempo de finalización de la sesión, puede ser nulo.
   * @param ipAddress     Dirección IP de la sesión.
   * @param userAgent     Información del agente de usuario.
   * @param sessionStatus Estado de la sesión.
   */
  public UserSession(String idSession, User user, Instant startTime, Instant endTime, String ipAddress,
      String userAgent, String sessionStatus) {
    this.idSession = idSession;
    this.user = user;
    this.startTime = startTime;
    this.endTime = endTime;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
    this.sessionStatus = sessionStatus;
  }

  public String getIdSession() {
    return idSession;
  }

  public void setIdSession(String idSession) {
    this.idSession = idSession;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public void setStartTime(Instant startTime) {
    this.startTime = startTime;
  }

  public Instant getEndTime() {
    return endTime;
  }

  public void setEndTime(Instant endTime) {
    this.endTime = endTime;
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

  public String getSessionStatus() {
    return sessionStatus;
  }

  public void setSessionStatus(String sessionStatus) {
    this.sessionStatus = sessionStatus;
  }

}
