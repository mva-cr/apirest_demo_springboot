package com.mvanalytic.apirest_demo_springboot.domain.user;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * La clase 'FailedLoginAttempt' registra los intentos de sesión fallidos de no
 * usuarios del sistema, almacena la información relevante con la que haya
 * intentado iniciar la sesión como el coreo o el nickname, así como la
 * dirección IP, y el agente de usuario
 */
@Entity
@Table(name = "failed_login_attempt", schema = "dbo")
public class FailedLoginAttempt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_attempt")
  private Long idAttempt;

  @Email
  @Size(min = 5, max = 254)
  @Column(name = "email", length = 254, nullable = true)
  private String email;

  @Size(min = 1, max = 50)
  @Column(name = "nickname", length = 50, nullable = true)
  private String nickname;

  @Column(name = "attempt_time", columnDefinition = "DATETIME2", nullable = false)
  private Instant attemptTime;

  @Column(name = "ip_address", nullable = false, length = 50)
  private String ipAddress;

  @Column(name = "user_agent", length = 512)
  private String userAgent;

  public FailedLoginAttempt(Long idAttempt, @Email @Size(min = 5, max = 254) String email,
      @Size(min = 1, max = 50) String nickname, Instant attemptTime, String ipAddress, String userAgent) {
    this.idAttempt = idAttempt;
    this.email = email;
    this.nickname = nickname;
    this.attemptTime = attemptTime;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
  }

  public FailedLoginAttempt() {
  }

  public Long getIdAttempt() {
    return idAttempt;
  }

  public void setIdAttempt(Long idAttempt) {
    this.idAttempt = idAttempt;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
