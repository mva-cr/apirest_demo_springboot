package com.mvanalytic.apirest_demo_springboot.dto.user;

import java.time.Instant;

/**
 * Clase que representa la información de la tabla user_audit y contiene los
 * cambios
 * que sobre cada usuario se han dado, esta solo el ROLE_ADMIN la puede acceder.
 * LoginAttemptResponseDTO.java
 */
public class UserAuditResponseDTO {

  private Long idAudit;
  private Long idUser;
  private Long changedByUserId;
  private String firstName;
  private String lastName;
  private String email;
  private String nickname;
  private Boolean passwordChanged;
  private Boolean activated;
  private Boolean status;
  private Instant changeDate;
  private String changeType;

  public UserAuditResponseDTO() {
  }

  public Long getIdAudit() {
    return idAudit;
  }

  public void setIdAudit(Long idAudit) {
    this.idAudit = idAudit;
  }

  public Long getIdUser() {
    return idUser;
  }

  public void setIdUser(Long idUser) {
    this.idUser = idUser;
  }

  public Long getChangedByUserId() {
    return changedByUserId;
  }

  public void setChangedByUserId(Long changedByUserId) {
    this.changedByUserId = changedByUserId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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

  public Boolean getPasswordChanged() {
    return passwordChanged;
  }

  public void setPasswordChanged(Boolean passwordChanged) {
    this.passwordChanged = passwordChanged;
  }

  public Boolean getActivated() {
    return activated;
  }

  public void setActivated(Boolean activated) {
    this.activated = activated;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public Instant getChangeDate() {
    return changeDate;
  }

  public void setChangeDate(Instant changeDate) {
    this.changeDate = changeDate;
  }

  public String getChangeType() {
    return changeType;
  }

  public void setChangeType(String changeType) {
    this.changeType = changeType;
  }

}
