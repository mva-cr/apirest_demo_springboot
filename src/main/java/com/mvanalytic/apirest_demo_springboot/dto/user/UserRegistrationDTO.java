package com.mvanalytic.apirest_demo_springboot.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la transferencia de datos al registrar un nuevo usuario.
 * Incluye únicamente los campos necesarios para el registro.
 */
public class UserRegistrationDTO {

  @NotNull
  @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
  private String firstName;

  @NotNull
  @Size(max = 50, message = "El apellido no puede tener más de 50 caracteres.")
  private String lastName;

  @Size(max = 50, message = "El segundo apellido no puede tener más de 50 caracteres.")
  private String secondLastName;

  @NotNull
  @Email(message = "El email debe tener un formato válido.")
  @Size(min = 5, max = 254, message = "El email debe tener entre 5 y 254 caracteres.")
  private String email;

  @NotNull
  @Pattern(regexp = "^[_.@A-Za-z0-9-]*$", message = "El nickName contiene caracteres no permitidos.")
  @Size(min = 1, max = 50, message = "El nickName debe tener entre 1 y 50 caracteres.")
  private String nickName;

  @NotNull
  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
  private String password;

  @Size(min = 2, max = 2, message = "El código de idioma debe tener 2 caracteres.")
  private String languageKey = "es"; // Valor predeterminado para el idioma

  public UserRegistrationDTO() {
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

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
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
