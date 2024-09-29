package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * DTO utilizado para gestionar el restablecimiento de la contraseña
 */
public class ChangePasswordByResetRequestDTO {

    private String newPassword;

    public ChangePasswordByResetRequestDTO() {
    }

    public String getNewPassword() {
      return newPassword;
    }

    public void setNewPassword(String newPassword) {
      this.newPassword = newPassword;
    }
  
}
