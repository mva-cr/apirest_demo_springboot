package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa una solicitud de inicio de sesión.
 * Utilizada para transportar los datos de autenticación del usuario desde el
 * cliente hasta el servidor.
 */
public class LoginRequestDTO {

  private String nickname;
  private String email;
  private String password;

  // Getters y Setters

  /**
   * Obtiene el nickname del usuario.
   * 
   * @return El nickname del usuario.
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * Establece el nickname del usuario.
   * 
   * @param nickname El nickname a establecer para el usuario.
   */
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  /**
   * Obtiene la contraseña del usuario.
   * 
   * @return La contraseña del usuario.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Establece la contraseña para el usuario.
   * 
   * @param password La contraseña a establecer para el usuario.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Obtiene el email del usuario.
   * 
   * @return El correo del usuario.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Establece la email para el usuario.
   * 
   * @param email El email a establecer para el usuario.
   */
  public void setEmail(String email) {
    this.email = email;
  }

}
