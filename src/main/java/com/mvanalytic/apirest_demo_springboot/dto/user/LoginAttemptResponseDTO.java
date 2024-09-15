package com.mvanalytic.apirest_demo_springboot.dto.user;


import java.time.Instant;

/**
 * Clase que representa la tabla de auditoría de los intentos de inicio de sesión
 * exitosos y fallidos
 */
public class LoginAttemptResponseDTO {

  private Long idAttempt;
  private Long idUser;
  private Instant attemptTime;
  private String ipAddress;
  private String userAgent;
  private String attemptResult;
  public LoginAttemptResponseDTO() {
  }
  public Long getIdAttempt() {
    return idAttempt;
  }
  public void setIdAttempt(Long idAttempt) {
    this.idAttempt = idAttempt;
  }
  public Long getIdUser() {
    return idUser;
  }
  public void setIdUser(Long idUser) {
    this.idUser = idUser;
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
