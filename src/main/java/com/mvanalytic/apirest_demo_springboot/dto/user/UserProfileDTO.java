package com.mvanalytic.apirest_demo_springboot.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Clase que representa la respuesta que contiene la información que el usario
 * puede ver y será utilizado para recibir información de user para hacer
 * modificaciones
 * Es clase la utiliza unicamente el ROLE_USER
 */
public class UserProfileDTO {

  @NotNull
  private Long id;
  @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
  private String firstName;
  @Size(max = 50, message = "El apellido no puede tener más de 50 caracteres.")
  private String lastName;
  @Size(max = 50, message = "El segundo apellido no puede tener más de 50 caracteres.")
  private String secondLastName;
  @Pattern(regexp = "^[_.@A-Za-z0-9-]*$", message = "El nickName contiene caracteres no permitidos.")
  @Size(min = 1, max = 50, message = "El nickName debe tener entre 1 y 50 caracteres.")
  private String nickName;
  @Email(message = "El email debe tener un formato válido.")
  @Size(min = 5, max = 254, message = "El email debe tener entre 5 y 254 caracteres.")
  private String email;
  @Size(min = 2, max = 2, message = "El código de idioma debe tener 2 caracteres.")
  private String languageKey;

  public UserProfileDTO(Long id, String firstName, String lastName, String secondLastName, String nickName,
      String email, String languageKey) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.secondLastName = secondLastName;
    this.nickName = nickName;
    this.email = email;
    this.languageKey = languageKey;
  }

  public UserProfileDTO() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
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

  @Override
  public String toString() {
    return "UserProfileDTO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", secondLastName="
        + secondLastName + ", nickName=" + nickName + ", email=" + email + ", languageKey=" + languageKey + "]";
  }

}
