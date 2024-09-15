package com.mvanalytic.apirest_demo_springboot.dto.user;

import java.util.Set;

/**
 * Clase que representa la respuesta que contiene la información que el
 * ROLE_ADMIN puede ver
 */
public class AdminUserResponseDTO {

  private Long id;
  private String firstName;
  private String lastName;
  private String secondLastName;
  private String nickname;
  private String email;
  private String languageKey;
  private Boolean status;
  private Boolean activated;
  private Set<AuthorityDTO> authorities;

  public AdminUserResponseDTO(Long id, String firstName, String lastName, String secondLastName, String nickname,
      String email,
      String languageKey, Boolean status, Boolean activated, Set<AuthorityDTO> authorities) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.secondLastName = secondLastName;
    this.nickname = nickname;
    this.email = email;
    this.languageKey = languageKey;
    this.status = status;
    this.activated = activated;
    this.authorities = authorities;
  }

  public AdminUserResponseDTO() {
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

  public Boolean isStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public Boolean isActivated() {
    return activated;
  }

  public void setActivated(Boolean activated) {
    this.activated = activated;
  }

  public Set<AuthorityDTO> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<AuthorityDTO> authorities) {
    this.authorities = authorities;
  }

  @Override
  public String toString() {
    return "AdminUserDTO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", secondLastName="
        + secondLastName + ", nickname=" + nickname + ", email=" + email + ", languageKey=" + languageKey + ", status="
        + status + ", activated=" + activated + ", authorities=" + authorities + "]";
  }

}
