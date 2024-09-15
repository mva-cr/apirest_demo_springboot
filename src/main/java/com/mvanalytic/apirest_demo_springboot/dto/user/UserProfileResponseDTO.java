package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa la respuesta que contiene la información que el usario
 * puede ver
 */
public class UserProfileResponseDTO {

  private Long id;
  private String firstName;
  private String lastName;
  private String secondLastName;
  private String nickname;
  private String email;
  private String languageKey;

  public UserProfileResponseDTO(Long id, String firstName, String lastName, String secondLastName, String nickname,
      String email, String languageKey) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.secondLastName = secondLastName;
    this.nickname = nickname;
    this.email = email;
    this.languageKey = languageKey;
  }

  public UserProfileResponseDTO() {
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

  @Override
  public String toString() {
    return "UserProfileDTO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", secondLastName="
        + secondLastName + ", nickname=" + nickname + ", email=" + email + ", languageKey=" + languageKey + "]";
  }

}
