package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * DTO (Data Transfer Object) utilizado para gestionar las solicitudes de
 * restablecimiento de contraseña. Este objeto puede recibir el correo
 * electrónico o el nickname del usuario que solicita el restablecimiento
 * de su contraseña.
 * 
 * @version 1.0
 */
public class PasswordResetRequestDTO {

  /**
   * Correo electrónico del usuario que solicita el restablecimiento de la
   * contraseña.
   * Este campo debe ser nulo si se proporciona un nickname.
   */
  private String email;

  /**
   * Nickname del usuario que solicita el restablecimiento de la contraseña.
   * Este campo debe ser nulo si se proporciona un correo electrónico.
   */
  private String nickname;

  public PasswordResetRequestDTO() {
  }

  public PasswordResetRequestDTO(String email, String nickname) {
    this.email = email;
    this.nickname = nickname;
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

}
