package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa la solicitud para cambiar la información del
 * usuario. Esta información solo puede ser modificada por el usuario
 * dueño de la cuenta. En esta clase no se aplican las validaciones
 */
public class UserProfileRequestDTO {
  
  private Long id;
  private String firstName;
  private String lastName;
  private String secondLastName;
  private String languageKey;
  
  public UserProfileRequestDTO() {
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
  public String getLanguageKey() {
    return languageKey;
  }
  public void setLanguageKey(String languageKey) {
    this.languageKey = languageKey;
  }

  
}
