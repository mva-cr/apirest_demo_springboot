package com.mvanalytic.apirest_demo_springboot.services.user;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.Instant;
import com.mvanalytic.apirest_demo_springboot.domain.user.LoginAttempt;
import com.mvanalytic.apirest_demo_springboot.domain.user.RefreshToken;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserSession;
import com.mvanalytic.apirest_demo_springboot.dto.user.JwtResponseDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserMapper;
import com.mvanalytic.apirest_demo_springboot.services.mail.MailService;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;
import com.mvanalytic.apirest_demo_springboot.utility.JwtUtils;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Servicio para gestionar la autenticación de usuarios.
 * Utiliza AuthenticationManager para validar las credenciales del usuario y
 * JwtUtils para generar tokens JWT.
 */
@Service
public class AuthService {
  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(AuthService.class);

  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserService userService;

  @Autowired
  private LoginAttemptServiceImpl loginAttemptServiceImpl;

  @Autowired
  private UserSessionServiceImpl userSessionServiceImpl;

  @Autowired
  private RefreshTokenService refreshTokenService;

  // @Autowired
  // private AppUtility appUtility;

  @Autowired
  private MailService mailService;

  // @Autowired
  public AuthService(@Lazy AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
  }

  /**
   * Autentica a un usuario basado en su identificador (puede ser su correo
   * electrónico o nickname) y contraseña. Si la autenticación es exitosa, se
   * generan tanto el token de acceso JWT como el refresh token, y se notifica al
   * usuario por correo electrónico sobre el inicio de sesión exitoso. En caso de
   * fallar, se registran los intentos fallidos de inicio de sesión.
   * 
   * @param identifier El identificador del usuario, que puede ser un email o
   *                   nickname.
   * @param password   La contraseña del usuario para autenticarse.
   * @param request    La solicitud HTTP, utilizada para obtener información de IP
   *                   y User-Agent.
   * @return JwtResponseDTO Un objeto que contiene la información del usuario
   *         autenticado, junto con el token JWT y el refresh token.
   * 
   * @throws UsernameNotFoundException   Si el identificador no corresponde a
   *                                     ningún usuario registrado.
   * @throws BadCredentialsException     Si la contraseña es incorrecta.
   * @throws CredentialsExpiredException Si las credenciales han expirado.
   * @throws DisabledException           Si la cuenta del usuario está
   *                                     deshabilitada.
   * @throws RuntimeException            Para cualquier otro error inesperado que
   *                                     ocurra durante la autenticación.
   */
  // TODO: revisar flujo chart
  public JwtResponseDTO authenticateUser(
      String identifier,
      String password,
      HttpServletRequest request) {
    try {
      // Intenta autenticar usando el AuthenticationManager
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(identifier, password));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      // Genera el token JWT
      String jwt = jwtUtils.generateJwtToken(authentication);

      // Se carga el user
      User user = getUserByIdentifier(identifier);

      // Crear el refreshToken
      RefreshToken refreshToken = refreshTokenService.createRefreshTokenByUser(user);

      // Devuelve la respuesta del JWT con el username del UserDetails
      JwtResponseDTO jwtResponse = UserMapper.convertUserToJwtResponse(user);

      // Se asignar los token al DTO
      jwtResponse.setToken(jwt);
      jwtResponse.setRefreshToken(refreshToken.getToken());

      // notificación por correo de inicio de sesion
      logSuccessfulAttempt(user, request);

      return jwtResponse;

    } catch (UsernameNotFoundException e) {
      // Registro del intento fallido por credenciales incorrectas
      logFailedAttempt(identifier, request, "FAILED");
      // Manejo de excepción cuando el nickname no existe
      logger.error("El identificador no existe ", e.getMessage());
      throw new UsernameNotFoundException("111, El identificador no existe");
    } catch (BadCredentialsException e) {
      // Registro del intento fallido por credenciales incorrectas
      logFailedAttempt(identifier, request, "FAILED");
      // Maneja el caso cuando el password es incorrecto
      logger.error("La contraseña no coincide con la registrada {}", e.getMessage());
      throw new BadCredentialsException("108, La contraseña no coincide con la registrada");

    } catch (CredentialsExpiredException e) {
      // Registro del intento fallido por credenciales incorrectas
      logFailedAttempt(identifier, request, "FAILED");
      // Maneja el caso cuando las credenciales han expirado
      logger.error("Las credenciales han expirado {}", e.getMessage());
      throw new CredentialsExpiredException("112, Las credenciales han expirado");

    } catch (DisabledException e) {
      // Registro del intento fallido por credenciales incorrectas
      logFailedAttempt(identifier, request, "FAILED");
      // Maneja el caso cuando la cuenta está deshabilitada
      logger.error("Cuenta deshabilitada {}", e.getMessage());
      throw new DisabledException("110, Cuenta deshabilitada");
    } catch (Exception e) {
      // Registro del intento fallido por credenciales incorrectas
      logFailedAttempt(identifier, request, "FAILED");
      // Manejo de cualquier otra excepción de autenticación
      logger.error("Error en la autenticación {}", e.getMessage());
      throw new RuntimeException("155, Error en tiempo de ejecución " + e.getMessage(), e);
    }

  }

  /**
   * Obtiene un usuario de la base de datos a partir de su identificador. El
   * identificador puede ser un correo electrónico o un nombre de usuario
   * (nickname). Este método utiliza otros métodos de `userService` para buscar el
   * usuario ya sea por su correo electrónico o por su nickname.
   *
   * @param identifier El identificador del usuario, que puede ser un correo
   *                   electrónico (en cuyo caso contendrá un símbolo '@') o un
   *                   nickname.
   * @return El objeto `User` correspondiente al identificador proporcionado.
   * @throws UsernameNotFoundException Si no se encuentra el usuario con el correo
   *                                   o nickname proporcionado.
   */
  public User getUserByIdentifier(String identifier) {
    // Buscar el usuario por nickname o email
    User user = new User();
    if (identifier.contains("@")) {
      // Verifica si es un email
      user = userService.getUserByEmail(identifier);
    } else {
      user = userService.getUserByNickName(identifier);
    }
    return user;
  }

  /**
   * Registra un intento fallido de inicio de sesión y envía una notificación al
   * usuario si es identificado.
   *
   * @param identifier El identificador del usuario, que puede ser su correo
   *                   electrónico o su nickname.
   * @param request    La solicitud HTTP que contiene información del cliente (IP
   *                   y User-Agent).
   * @param result     El resultado del intento de inicio de sesión (por ejemplo,
   *                   "FAILED").
   */
  private void logFailedAttempt(String identifier, HttpServletRequest request, String result) {
    // Obtener la IP del cliente
    String ipAddress = request.getRemoteAddr();

    // Obtener el User-Agent del cliente
    String userAgent = request.getHeader("User-Agent");

    // carga el User
    User user = getUserByIdentifier(identifier);

    // Crear el objeto de intento fallido
    LoginAttempt attempt = new LoginAttempt();
    attempt.setUser(user); // Puede ser null si el usuario no existe
    attempt.setIpAddress(ipAddress);
    attempt.setUserAgent(userAgent);
    attempt.setAttemptTime(Instant.now());
    attempt.setAttemptResult(result);

    // Guardar el intento fallido en la base de datos
    loginAttemptServiceImpl.saveAttempt(attempt);

    // Si el usuario fue encontrado (no es null), enviar un correo notificando el
    // intento fallido
    if (user != null) {
      mailService.sendFailedLoginAttempt(user, ipAddress, userAgent, attempt.getAttemptTime());
    }
  }

  /**
   * Registra un intento de inicio de sesión exitoso y envía una notificación por
   * correo al usuario.
   * 
   * Este método realiza las siguientes operaciones:
   * 1. Obtiene la dirección IP y el User-Agent (información sobre el
   * navegador/dispositivo) del cliente.
   * 2. Crea un registro de sesión activa con la información del usuario, la IP,
   * el User-Agent y la hora de inicio.
   * 3. Guarda la sesión en la base de datos.
   * 4. Envía un correo electrónico al usuario notificando el inicio de sesión
   * exitoso.
   * 
   * @param user    El objeto de tipo {@link User} que representa al usuario que
   *                inició sesión exitosamente.
   * @param request El objeto de tipo {@link HttpServletRequest} que contiene
   *                información sobre la solicitud HTTP del cliente, incluyendo la
   *                dirección IP y el User-Agent.
   */
  public void logSuccessfulAttempt(User user, HttpServletRequest request) {
    // Obtener la IP del cliente
    String ipAddress = request.getRemoteAddr();

    // Obtener el User-Agent del cliente
    String userAgent = request.getHeader("User-Agent");

    // Crear un registro de la sesión activa
    UserSession session = new UserSession();
    session.setUser(user);
    session.setIpAddress(ipAddress);
    session.setUserAgent(userAgent);
    session.setStartTime(Instant.now());
    session.setSessionStatus("ACTIVE");

    // Guardar la sesión en la base de datos (asumiendo que existe un servicio para
    // esto)
    userSessionServiceImpl.saveUserSession(session);

    // Enviar el correo notificando la sesión activa
    mailService.sendSuccessfulLoginAttempt(user, ipAddress, userAgent, session.getStartTime());
  }

  /**
   * Método para cerrar la sesión de un usuario.
   * 
   * @param idSession El ID de la sesión que se va a cerrar.
   */
  // TODO: aca voy
  public void logout(String idSession) {
    try {
      // Actualizar el estado de la sesión a "LOGGED_OUT"
      userSessionServiceImpl.logOutSession(idSession);
    } catch (Exception e) {
      throw new RuntimeException("Error al cerrar la sesión", e);
    }
  }

}
