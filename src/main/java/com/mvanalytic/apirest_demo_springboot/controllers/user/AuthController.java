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
import com.mvanalytic.apirest_demo_springboot.services.user.RefreshTokenService;
import com.mvanalytic.apirest_demo_springboot.services.user.UserService;
import com.mvanalytic.apirest_demo_springboot.utility.UserValidationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
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

  @Autowired
  private RefreshTokenService refreshTokenService;

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
  public ResponseEntity<JwtResponseDTO> authenticateUserByNickname(
      @RequestBody LoginRequestDTO loginRequest,
      HttpServletRequest request) {
    JwtResponseDTO jwtResponse = authService.authenticateUser(
        loginRequest.getNickname(),
        loginRequest.getPassword(),
        request);
    return ResponseEntity.ok(jwtResponse);
  }

  /**
   * Autentica al usuario utilizando su email.
   *
   * @param loginRequest Objeto que contiene el email y la contraseña del usuario.
   * @return Respuesta que contiene el token JWT y el nombre de usuario.
   */
  @PostMapping("/login/email")
  public ResponseEntity<JwtResponseDTO> authenticateUserByEmail(
      @RequestBody LoginRequestDTO loginRequest,
      HttpServletRequest request) {
    JwtResponseDTO jwtResponse = authService.authenticateUser(
        loginRequest.getEmail(),
        loginRequest.getPassword(),
        request);
    return ResponseEntity.ok(jwtResponse);
  }

  /**
   * Endpoint que permite a un usuario obtener un nuevo token JWT utilizando su
   * refresh token. Este método recibe el ID del usuario, busca el refresh token
   * asociado al usuario, verifica su validez y genera un nuevo token JWT junto
   * con un nuevo refresh token. Si el refresh token ha expirado o no es válido,
   * se maneja la excepción en el servicio correspondiente.
   *
   * @param id_user El ID del usuario que solicita un nuevo token.
   * @return ResponseEntity con el nuevo JWT y el nuevo refresh token en el cuerpo
   *         de la respuesta.
   */
  @PostMapping("/refresh-token/{id_user}")
  public ResponseEntity<JwtResponseDTO> refreshToken(
      @PathVariable Long id_user) {

    JwtResponseDTO jwtResponseDTO = refreshTokenService.recreateRefreshTokenByIdUser(id_user);

    return ResponseEntity.ok(jwtResponseDTO);
  }

}
