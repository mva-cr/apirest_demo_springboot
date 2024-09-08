package com.mvanalytic.apirest_demo_springboot.dto.user;

import java.util.Set;

public class AdminUserDTO {

  private long id;
  private String firstName;
  private String lastName;
  private String secondLastName;
  private String nickName;
  private String email;
  private String languageKey;
  private boolean status;
  private boolean activated;
  private Set<AuthorityDTO> authorities;

  public AdminUserDTO(long id, String firstName, String lastName, String secondLastName, String nickName, String email,
      String languageKey, boolean status, boolean activated, Set<AuthorityDTO> authorities) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.secondLastName = secondLastName;
    this.nickName = nickName;
    this.email = email;
    this.languageKey = languageKey;
    this.status = status;
    this.activated = activated;
    this.authorities = authorities;
  }

  public AdminUserDTO() {
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

  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
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
        + secondLastName + ", nickName=" + nickName + ", email=" + email + ", languageKey=" + languageKey + ", status="
        + status + ", activated=" + activated + ", authorities=" + authorities + "]";
  }

}
