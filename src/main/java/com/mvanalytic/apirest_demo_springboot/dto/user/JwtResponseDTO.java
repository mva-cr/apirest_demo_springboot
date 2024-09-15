package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa la respuesta que contiene el token JWT después de un
 * inicio de sesión exitoso.
 * Se utiliza para enviar el token y la información relevante del usuario al
 * cliente.
 */
public class JwtResponseDTO {

  private long id;
  private String firstname;
  private String lastName;
  private String secondLastName;
  private String nickname;
  private String email;
  private String languageKey;

  /**
   * El token de acceso JWT emitido tras un inicio de sesión exitoso.
   */
  private String token;

  /**
   * El tipo de token, que es 'Bearer' por defecto.
   */
  private String type = "Bearer";

  /**
   * Constructor para crear una respuesta JWT.
   * 
   * @param id          El id del usuario en la base de datos
   * @param firstname   Primer nombre del usuario
   * @param lastname    Apellido
   * @param nickname    El nickname del usuario para el cual se emitió el token.
   * @param accessToken El token JWT emitido.
   */
  public JwtResponseDTO(
      long id, String firstname, String lastName, String secondLastName,
      String nickname, String accesToken, String email, String languageKey) {
    this.id = id;
    this.firstname = firstname;
    this.lastName = lastName;
    this.secondLastName = secondLastName;
    this.nickname = nickname;
    this.email = email;
    this.languageKey = languageKey;
    this.token = accesToken;
  }

  // Getters y Setters

  public JwtResponseDTO() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getSecondLastName() {
    return secondLastName;
  }

  public void setSecondLastName(String secondLastName) {
    this.secondLastName = secondLastName;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLanguageKey() {
    return languageKey;
  }

  public void setLanguageKey(String languageKey) {
    this.languageKey = languageKey;
  }

  /**
   * Obtiene el token JWT.
   * 
   * @return El token JWT.
   */
  public String getToken() {
    return token;
  }

  /**
   * Establece el token JWT.
   * 
   * @param token El token JWT a establecer.
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * Obtiene el tipo de token.
   * 
   * @return El tipo de token, que es 'Bearer'.
   */
  public String getType() {
    return type;
  }

  /**
   * Establece el tipo de token.
   * 
   * @param type El tipo de token a establecer.
   */
  public void setType(String type) {
    this.type = type;
  }

}
