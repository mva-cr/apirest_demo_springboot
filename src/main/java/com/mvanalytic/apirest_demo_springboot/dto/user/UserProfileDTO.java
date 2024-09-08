package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa la respuesta que contiene la información que el usario
 * con
 * ROLE_USER requiere
 */
public class UserProfileDTO {

  private long id;
  private String firstName;
  private String lastName;
  private String secondLastName;
  private String nickName;
  private String email;
  private String languageKey;

  public UserProfileDTO(long id, String firstName, String lastName, String secondLastName, String nickName,
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

  public long getId() {
    return id;
  }

  public void setId(long id) {
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
