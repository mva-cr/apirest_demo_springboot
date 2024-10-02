package com.mvanalytic.apirest_demo_springboot.dto.user;

import org.apache.logging.log4j.core.time.Instant;

/**
 * DTO que representa los detalles de una sesión de usuario,
 * diseñado para ser mostrado al administrador.
 * Facilita la auditoría de sesiones activas y pasadas con información clave
 * como la IP y el estado de la sesión.
 */
public class UserSessionResponseDTO {

  private String idSession; // ID de la sesión
  private String userEmail; // Email del usuario
  private Instant startTime; // Inicio de la sesión
  private Instant endTime; // Fin de la sesión, si está disponible
  private String ipAddress; // Dirección IP desde donde se inició la sesión
  private String userAgent; // Agente de usuario
  private String sessionStatus; // Estado: 'ACTIVE', 'EXPIRED', 'LOGGED_OUT'

  public UserSessionResponseDTO() {
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
