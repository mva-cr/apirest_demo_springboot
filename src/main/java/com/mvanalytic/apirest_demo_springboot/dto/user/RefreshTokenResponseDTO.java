package com.mvanalytic.apirest_demo_springboot.dto.user;

import java.time.Instant;

/**
 * Clase DTO (Data Transfer Object) utilizada para transferir información
 * relacionada con un token de actualización (refresh token) sin exponer
 * información sensible, como la contraseña del usuario.
 * 
 * Este DTO incluye información básica del usuario asociado al token, como su
 * nombre, correo electrónico y nickname, junto con el token de actualización y
 * su fecha de expiración.
 * 
 * La finalidad de este DTO es proporcionar al administrador (ROLE_ADMIN) los
 * datos necesarios para gestionar los tokens de actualización sin comprometer
 * la seguridad de la aplicación.
 * 
 * Campos:
 * - id: Identificador único del token de actualización.
 * - userId: Identificador único del usuario asociado al token.
 * - firstName: Primer nombre del usuario.
 * - lastName: Apellido del usuario.
 * - email: Correo electrónico del usuario.
 * - nickname: Nickname del usuario.
 * - token: El token de actualización generado.
 * - expiryDate: Fecha y hora en que expira el token de actualización.
 * 
 * Nota: No se incluye el campo "password" en este DTO para garantizar que la
 * información sensible no se exponga a los administradores durante la gestión
 * de los tokens.
 * 
 * Autor: [Tu Nombre]
 * Fecha: [Fecha]
 */
public class RefreshTokenResponseDTO {

  private Long id;

  private Long userId;

  private String firstName;

  private String lastName;

  private String email;

  private String nickname;

  private String token;

  private Instant expiryDate;

  public RefreshTokenResponseDTO() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Instant getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Instant expiryDate) {
    this.expiryDate = expiryDate;
  }

}
