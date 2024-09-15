package com.mvanalytic.apirest_demo_springboot.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.JwtResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.LoginRequestDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserMapper;
import com.mvanalytic.apirest_demo_springboot.services.user.AuthService;
import com.mvanalytic.apirest_demo_springboot.services.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @Autowired
  private UserService userService;

  /**
   * Autentica al usuario utilizando su nickname.
   *
   * @param loginRequest Objeto que contiene el nickname y la contraseña del
   *                     usuario.
   * @return Respuesta que contiene el token JWT y el nombre de usuario.
   */
  @PostMapping("/login/nickname")
  public ResponseEntity<JwtResponseDTO> authenticateUserByNickname(@RequestBody LoginRequestDTO loginRequest) {
    JwtResponseDTO jwtResponse = authService.authenticateUser(loginRequest.getNickname(), loginRequest.getPassword());
    User user = userService.getUserByNickName(loginRequest.getNickname());
    JwtResponseDTO jwt = new JwtResponseDTO();
    jwt = UserMapper.convertUserToJwtResponse(user);
    jwt.setToken(jwtResponse.getToken());
    return ResponseEntity.ok(jwt);
  }

  /**
   * Autentica al usuario utilizando su email.
   *
   * @param loginRequest Objeto que contiene el email y la contraseña del usuario.
   * @return Respuesta que contiene el token JWT y el nombre de usuario.
   */
  @PostMapping("/login/email")
  public ResponseEntity<JwtResponseDTO> authenticateUserByEmail(@RequestBody LoginRequestDTO loginRequest) {
    JwtResponseDTO jwtResponse = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
    User user = userService.getUserByEmail(loginRequest.getEmail());
    JwtResponseDTO jwt = new JwtResponseDTO();
    jwt = UserMapper.convertUserToJwtResponse(user);
    jwt.setToken(jwtResponse.getToken());
    return ResponseEntity.ok(jwt);
  }

}
