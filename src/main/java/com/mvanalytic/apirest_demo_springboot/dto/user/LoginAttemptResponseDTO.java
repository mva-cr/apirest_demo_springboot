package com.mvanalytic.apirest_demo_springboot.dto.user;


import java.time.Instant;

/**
 * Clase que representa la tabla de auditoría de los intentos de inicio de sesión
 * exitosos y fallidos
 */
public class LoginAttemptResponseDTO {

  private Long idAttempt; // ID del intento
    private String userEmail; // Email del usuario (en lugar de retornar el objeto completo User)
    private Instant attemptTime; // Fecha y hora del intento
    private String ipAddress; // Dirección IP desde donde se realizó el intento
    private String userAgent; // Agente de usuario (detalles del navegador/dispositivo)
    private String attemptResult; // Resultado: 'SUCCESS' o 'FAILED'
  public LoginAttemptResponseDTO() {
  }
  public Long getIdAttempt() {
    return idAttempt;
  }
  public void setIdAttempt(Long idAttempt) {
    this.idAttempt = idAttempt;
  }
  public String getUserEmail() {
    return userEmail;
  }
  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
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
