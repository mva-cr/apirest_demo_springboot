package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * DTO para la transferencia de datos al registrar un nuevo usuario.
 * Incluye únicamente los campos necesarios para el registro.
 */
public class UserRegistrationRequestDTO {

  private String firstName;
  private String lastName;
  private String secondLastName;
  private String email;
  private String nickname;
  private String password;
  private String languageKey = "es"; // Valor predeterminado para el idioma

  public UserRegistrationRequestDTO() {
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

  public String getSecondLastName() {
    return secondLastName;
  }

  public void setSecondLastName(String secondLastName) {
    this.secondLastName = secondLastName;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getLanguageKey() {
    return languageKey;
  }

  public void setLanguageKey(String languageKey) {
    this.languageKey = languageKey;
  }

}
