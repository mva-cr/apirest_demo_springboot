package com.mvanalytic.apirest_demo_springboot.dto.user;

import java.time.Instant;

/**
 * DTO que representa los detalles de una sesión de usuario fallidos y exitosos
 * de usuarios.
 */
public class UserLoginActivityResponseDTO {
  private String idSession; // ID de la sesión
  private String userEmail; // Email del usuario
  private Instant sessionTime; // Inicio de la sesión
  private String ipAddress; // Dirección IP desde donde se inició la sesión
  private String userAgent; // Agente de usuario
  private String sessionStatus; // Estado: 'ACTIVE', 'FAILED'

  public UserLoginActivityResponseDTO() {
  }

  public String getIdSession() {
    return idSession;
  }

  public void setIdSession(String idSession) {
    this.idSession = idSession;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
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
