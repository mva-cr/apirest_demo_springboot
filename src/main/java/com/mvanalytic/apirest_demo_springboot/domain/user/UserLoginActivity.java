package com.mvanalytic.apirest_demo_springboot.domain.user;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * La entidad `UserLoginActivity` registra los intentos de inicio de sesión de
 * usuario del sistema exitosos ('SUCCESS') y fallidos ('FAILURE) incluyendo
 * información como la hora de la sesión, la dirección IP y el agente de usuario
 * utilizado para la sesión.
 */
@Entity
@Table(name = "user_login_activity")
public class UserLoginActivity {

  @Id
  @Column(name = "id_session", length = 128)
  private String idSession;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_id_user_user_login_activity"))
  private User user;

  @Column(name = "session_time", columnDefinition = "DATETIME2", nullable = false)
  private Instant sessionTime;

  @Column(name = "ip_address", nullable = false, length = 50)
  private String ipAddress;

  @Column(name = "user_agent", length = 512)
  private String userAgent;

  // ('SUCCESS', 'FAILURE')
  @Column(name = "session_status", nullable = false, length = 50)
  private String sessionStatus;

  public UserLoginActivity(String idSession, User user, Instant sessionTime, String ipAddress, String userAgent,
      String sessionStatus) {
    this.idSession = idSession;
    this.user = user;
    this.sessionTime = sessionTime;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
    this.sessionStatus = sessionStatus;
  }

  public UserLoginActivity() {
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

  public Instant getSessionTime() {
    return sessionTime;
  }

  public void setSessionTime(Instant sessionTime) {
    this.sessionTime = sessionTime;
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
