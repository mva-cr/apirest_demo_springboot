package com.mvanalytic.apirest_demo_springboot.dto.user;

import java.time.Instant;

/**
 * DTO que representa la tabla de auditoría de los intentos de inicio de
 * sesión fallidos de NO usuarios
 */
public class FailedLoginAttemptResponseDTO {
  private Long idAttempt; // ID del intento
  private String userEmail; // correo del intento
  private String nickname; // nickname del intento
  private Instant attemptTime; // Fecha y hora del intento
  private String ipAddress; // Dirección IP desde donde se realizó el intento
  private String userAgent; // Agente de usuario (detalles del navegador/dispositivo)

  public FailedLoginAttemptResponseDTO() {
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

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
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

}
