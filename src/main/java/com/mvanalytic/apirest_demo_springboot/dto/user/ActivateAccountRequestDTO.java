package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa la solicitud de activación de una cuenta
 */
public class ActivateAccountRequestDTO {
  
  private String tempPassword;
  private String newPassword;
  
  public ActivateAccountRequestDTO() {
  }

  public String getTempPassword() {
    return tempPassword;
  }

  public void setTempPassword(String tempPassword) {
    this.tempPassword = tempPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  
}
