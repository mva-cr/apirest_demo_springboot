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
import java.util.UUID;
import com.mvanalytic.apirest_demo_springboot.domain.user.FailedLoginAttempt;
import com.mvanalytic.apirest_demo_springboot.domain.user.RefreshToken;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserLoginActivity;
import com.mvanalytic.apirest_demo_springboot.dto.user.JwtResponseDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserMapper;
import com.mvanalytic.apirest_demo_springboot.services.mail.MailService;
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
  private FailedLoginAttemptService fLoginAttemptService;

  @Autowired
  private UserLoginActivityService uLoginActivityService;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private MailService mailService;

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
   * <p>
   * La notación <code>@Transactional</code> garantizará que todas las operaciones
   * relacionadas con la base de datos (como eliminar, crear o actualizar
   * registros) se ejecuten dentro de una única transacción. Si alguna de estas
   * operaciones falla, Spring automáticamente realizará un rollback, deshaciendo
   * todos los cambios que se hayan hecho hasta ese punto en la base de datos.
   * Así, te aseguras de que las operaciones o se completen correctamente, o
   * ninguna se aplicará.
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
  // @Transactional
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

      // Transforma el User en JwtResponseDTO
      JwtResponseDTO jwtResponse = UserMapper.convertUserToJwtResponse(user);

      // Se asignar los token al DTO
      jwtResponse.setToken(jwt);
      jwtResponse.setRefreshToken(refreshToken.getToken());

      // Se crea objeto UserLoginActivity
      UserLoginActivity userLoginActivity = createUserLoginActivity(user, request, "SUCCESS");

      // Se llama al SProcedure para cargar de forma atómica toda la transaccion de
      // login exitoso
      uLoginActivityService.registerSuccessfulLogin(
          user.getId(),
          refreshToken.getToken(),
          refreshToken.getExpiryDate(),
          userLoginActivity.getIpAddress(),
          userLoginActivity.getUserAgent(),
          userLoginActivity.getIdSession(),
          userLoginActivity.getSessionTime(),
          userLoginActivity.getSessionStatus());

      // notificación por correo de inicio de sesion
      sendMail(user, identifier, password, userLoginActivity.getSessionTime(), userLoginActivity.getSessionStatus());

      return jwtResponse;

    } catch (UsernameNotFoundException e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
      // Manejo de excepción cuando el nickname no existe
      logger.error("El identificador no existe ", e.getMessage());
      throw new UsernameNotFoundException("111, El identificador no existe");
    } catch (BadCredentialsException e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
      // Maneja el caso cuando el password es incorrecto
      logger.error("La contraseña no coincide con la registrada {}", e.getMessage());
      throw new BadCredentialsException("108, La contraseña no coincide con la registrada");

    } catch (CredentialsExpiredException e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
      // Maneja el caso cuando las credenciales han expirado
      logger.error("Las credenciales han expirado {}", e.getMessage());
      throw new CredentialsExpiredException("112, Las credenciales han expirado");

    } catch (DisabledException e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
      // Maneja el caso cuando la cuenta está deshabilitada
      logger.error("Cuenta deshabilitada {}", e.getMessage());
      throw new DisabledException("110, Cuenta deshabilitada");
    } catch (Exception e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
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
   * Obtiene un usuario de la base de datos a partir de su identificador. El
   * identificador puede ser un correo electrónico o un nombre de usuario
   * (nickname). Este método utiliza otros métodos de `userService` para buscar el
   * usuario ya sea por su correo electrónico o por su nickname. Este método
   * utiliza los metodos nullables del servicio para obtener el objeto a un null
   *
   * @param identifier El identificador del usuario, que puede ser un correo
   *                   electrónico (en cuyo caso contendrá un símbolo '@') o un
   *                   nickname.
   * @return El objeto `User` correspondiente al identificador proporcionado.
   * @throws UsernameNotFoundException Si no se encuentra el usuario con el correo
   *                                   o nickname proporcionado.
   */
  public User getUserByIdentifierNullable(String identifier) {
    // Buscar el usuario por nickname o email
    User user = new User();
    if (identifier.contains("@")) {
      // Verifica si es un email
      user = userService.getUserByEmailNullable(identifier);
    } else {
      user = userService.getUserByNickNameNullable(identifier);
    }
    return user;
  }

  private FailedLoginAttempt createFailedLoginAttempt(
      String email, String nickName, HttpServletRequest request) {
    // Obtener la IP del cliente
    String ipAddress = request.getRemoteAddr();

    // Obtener el User-Agent del cliente
    String userAgent = request.getHeader("User-Agent");

    // Crear el objeto de intento fallido
    FailedLoginAttempt attempt = new FailedLoginAttempt();
    attempt.setEmail(email); // Puede ser null si el usuario no existe
    attempt.setNickname(nickName); // Puede ser null si el usuario no existe
    attempt.setAttemptTime(Instant.now());
    attempt.setIpAddress(ipAddress);
    attempt.setUserAgent(userAgent);
    return attempt;
  }

  /**
   * Método que maneja el registro de un intento de autenticación fallido.
   * 
   * Si el identificador proporcionado (puede ser correo electrónico o nickname)
   * no corresponde a un usuario en la base de datos, se registra el intento
   * fallido sin asociarlo a un usuario específico. Si el usuario es encontrado,
   * se registra la actividad de inicio de sesión fallida para ese usuario.
   *
   * @param identifier El identificador usado para intentar autenticarse, puede
   *                   ser un correo electrónico o un nickname.
   * @param request    La solicitud HTTP que contiene información del cliente,
   *                   como IP y User-Agent.
   */
  public void failAuth(String identifier, HttpServletRequest request) {
    User user = getUserByIdentifierNullable(identifier);
    if (user == null) {
      String email = identifier.contains("@") ? identifier : null;
      String nickname = identifier.contains("@") ? null : identifier;
      FailedLoginAttempt failedLoginAttempt = createFailedLoginAttempt(email, nickname, request);
      fLoginAttemptService.saveFailedAttempt(failedLoginAttempt);
      // logFailedAttempt(email, nickname, request);
    } else {
      UserLoginActivity userLoginActivity = createUserLoginActivity(user, request, "FAILURE");
      saveLoginActivity(userLoginActivity);
      sendMail(
          user, userLoginActivity.getIpAddress(),
          userLoginActivity.getUserAgent(), userLoginActivity.getSessionTime(),
          userLoginActivity.getSessionStatus());
    }
  }

  /**
   * Envía un correo electrónico al usuario basado en el resultado del intento de
   * inicio de sesión.
   *
   * Dependiendo del valor del parámetro 'status', se enviará un correo de
   * notificación de inicio de sesión exitoso o de intento fallido. Si el estado
   * es "SUCCESS", se envía un correo por el inicio de sesión exitoso; de lo
   * contrario, se envía un correo por intento fallido.
   *
   * @param user        El usuario que intentó iniciar sesión.
   * @param ipAddress   La dirección IP desde donde se realizó el intento de
   *                    inicio de sesión.
   * @param userAgent   La información del agente de usuario (navegador,
   *                    dispositivo).
   * @param sessionTime El momento en que ocurrió el intento de inicio de sesión.
   * @param status      El estado del intento de inicio de sesión, puede ser
   *                    "SUCCESS" o "FAILURE".
   */
  private void sendMail(
      User user, String ipAddress, String userAgent, Instant sessionTime, String status) {
    if (status.equals("SUCCESS")) {
      mailService.sendSuccessfulLoginAttempt(user, ipAddress, userAgent, sessionTime);
    } else {
      mailService.sendFailedLoginAttempt(user, ipAddress, userAgent, sessionTime);
    }
  }

  /**
   * Crea una nueva instancia de UserLoginActivity para registrar la actividad de
   * inicio de sesión del usuario.
   *
   * Este método obtiene la dirección IP del cliente, el User-Agent y otros
   * detalles de la solicitud, luego genera un nuevo registro de actividad de
   * inicio de sesión para el usuario, incluyendo el estado de la sesión (por
   * ejemplo, "SUCCESS" o "FAILURE") y un ID de sesión único.
   *
   * @param user    El usuario que está iniciando sesión.
   * @param request La solicitud HTTP que contiene los detalles del cliente (IP,
   *                User-Agent).
   * @param status  El estado de la sesión, que puede ser "SUCCESS" o "FAILURE".
   * @return Una instancia de UserLoginActivity con la información de la sesión
   *         del usuario.
   * @throws RuntimeException Si ocurre algún error durante la creación de la
   *                          actividad de inicio de sesión.
   */
  private UserLoginActivity createUserLoginActivity(
      User user, HttpServletRequest request, String status) {
    try {
      // Obtener la IP del cliente
      String ipAddress = request.getRemoteAddr();

      // Obtener el User-Agent del cliente
      String userAgent = request.getHeader("User-Agent");

      // Crear un registro de la sesión activa
      UserLoginActivity session = new UserLoginActivity();
      session.setUser(user);
      session.setIpAddress(ipAddress);
      session.setUserAgent(userAgent);
      session.setSessionTime(Instant.now());
      session.setSessionStatus(status);
      session.setIdSession(UUID.randomUUID().toString());
      return session;
    } catch (Exception e) {
      logger.error("Error al crear el UserLoginActivity {}", e.getMessage());
      throw new RuntimeException("227, Error al crear el UserLoginActivity");
    }
  }

  /**
   * Guarda la actividad de inicio de sesión del usuario en la base de datos.
   *
   * Este método delega la operación de guardado al servicio de actividad de
   * inicio de sesión, para registrar la actividad de inicio de sesión del usuario
   * en la base de datos.
   *
   * @param userLoginActivity La entidad UserLoginActivity que contiene los
   *                          detalles de la sesión del usuario.
   */
  private void saveLoginActivity(UserLoginActivity userLoginActivity) {
    uLoginActivityService.saveLoginActivity(userLoginActivity);
  }

}
