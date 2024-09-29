package com.mvanalytic.apirest_demo_springboot.dto.user;

/**
 * Clase que representa la solicitud de cambio de nickname del usuario
 * que hace la petición.
 */
public class UserNicknameRequestDTO {

  private Long id;
  private String nickname;

  public UserNicknameRequestDTO() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

}
