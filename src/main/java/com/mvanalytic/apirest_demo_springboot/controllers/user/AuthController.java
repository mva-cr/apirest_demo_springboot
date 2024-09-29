package com.mvanalytic.apirest_demo_springboot.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.JwtResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.LoginRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRegistrationRequestDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserMapper;
import com.mvanalytic.apirest_demo_springboot.services.user.AuthService;
import com.mvanalytic.apirest_demo_springboot.services.user.UserService;
import com.mvanalytic.apirest_demo_springboot.utility.UserValidationService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @Autowired
  private UserService userService;

  @Autowired
  private UserValidationService userValidationService;

  // @Autowired
  // private UserKeyServiceImpl userKeyServiceImpl;

  // @Autowired
  // private AppUtility appUtility;

  // @Autowired
  // private MailService mailServiceImpl;

  /**
   * Registra un nuevo usuario en el sistema.
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   *
   * @param user El usuario a registrar.
   * @return Una respuesta indicando el éxito del registro.
   */
  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequestDTO userRegistrationDTO) {

    // validar los datos de la solicitud
    userValidationService.validateUserRegistrationRequestDTO(userRegistrationDTO);

    // Mapear el DTO a la entidad User
    User user = UserMapper.convertUserRegistrationDTOToUser(userRegistrationDTO);

    // Envio a procesar solicitud
    userService.registerUser(user, false);

    // Generar la clave de activación
    // UserKey userKey = appUtility.generateKey(user, true);

    // Registrar la clave en la base de datos
    // userKeyServiceImpl.registerKeyValue(userKey);

    // Enviar el correo de activación

    // mailServiceImpl.sendActivationEmail(user, userKey);
    return ResponseEntity.ok("Usuario registrado exitosamente");
  }

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
