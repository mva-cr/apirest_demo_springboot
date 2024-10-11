package com.mvanalytic.apirest_demo_springboot.services.user;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserKey;
import com.mvanalytic.apirest_demo_springboot.dto.user.PasswordResetRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserEmailRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserNicknameRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserPasswordRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserStatusRequestDTO;
import com.mvanalytic.apirest_demo_springboot.repositories.user.UserRepository;
import com.mvanalytic.apirest_demo_springboot.services.mail.MailService;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;
import com.mvanalytic.apirest_demo_springboot.utility.RandomKeyGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio para gestionar operaciones CRUD relacionadas con los usuarios.
 * Proporciona métodos para registrar, actualizar, eliminar y obtener usuarios.
 */
@Service
public class UserService {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(UserService.class);

  /**
   * Para cuando se necesita manejar procedimientos almacenados que no se ajustan
   * bien a los métodos simples
   * del repositorio.
   * Permite manejar la sincronización con la base de datos.
   */
  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserKeyServiceImpl userKeyServiceImpl;

  @Autowired
  private MailService mailService;

  @Autowired
  private AppUtility appUtility;

  @Autowired
  @Lazy
  private PasswordEncoder passwordEncoder;

  /**
   * Registra un nuevo usuario en el sistema y gestiona el envío de un correo de
   * activación.
   *
   * El método realiza las siguientes acciones:
   * 1. Verifica si el correo electrónico y el nickname del usuario ya existen en
   * la base de datos. Si ya existen, se lanzan excepciones con los mensajes
   * correspondientes.
   * 2. Si el usuario es creado por un administrador (`ROLE_ADMIN`), se genera una
   * contraseña temporal que se enviará al usuario para que la use en el proceso
   * de activación.
   * 3. Se encripta la contraseña del usuario y se actualiza en el objeto `User`.
   * 4. Se genera un objeto `UserKey` que contiene una clave de activación
   * asociada al usuario.
   * 5. Se registra tanto el usuario como la clave de activación en la base de
   * datos.
   * 6. Dependiendo de si el registro fue realizado por un administrador o por el
   * propio usuario, se envía un correo de activación con la clave temporal (si es
   * un administrador) o sin ella (si es el propio usuario).
   *
   * @param user        El objeto `User` que contiene la información del usuario
   *                    que se va a registrar.
   * @param isRoleAdmin Indica si el registro lo realiza un administrador (`true`)
   *                    o el propio usuario (`false`).
   *
   * @throws IllegalArgumentException Si el email o el nickname ya existen en la
   *                                  base de datos.
   */
  @Transactional
  public void registerUser(User user, boolean isRoleAdmin) {
    // Verifica si el email y el nickname ya existen
    Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
    Optional<User> existingUserByNickname = userRepository.findByNickname(user.getNickname());

    if (existingUserByEmail.isPresent()) {
      logger.error("El email ingresado ya existe");
      throw new IllegalArgumentException("106, El email ingresado ya existe");
    }

    if (existingUserByNickname.isPresent()) {
      logger.error("El nickname ingresado ya existe");
      throw new IllegalArgumentException("105, El nickname ingresado ya existe");
    }

    // genera una clave temporal si es enviado por ROLE_ADMIN
    String passwordTemp = "";
    if (isRoleAdmin) {
      // Agregar un password temporal
      passwordTemp = RandomKeyGenerator.generateRandomKey(6);
      user.setPassword(passwordTemp);
    }

    // Encripty la contraseña y la carga al usuario
    String hashedPassword = encoderPassword(user.getPassword());
    user.setPassword(hashedPassword); // Establece el hash de la contraseña

    // Generar el Objeto UserKey
    UserKey userKey = appUtility.generateKey(user, true);

    // Cargar el UserKey y el User
    // UserKey newUserKey = userKeyServiceImpl.findByIdWithUser(null)

    // Registrar el usuario y el key value en la base de datos
    Long id = createUserAndKey(user, userKey);
    userKey.setId(id);

    if (isRoleAdmin) {
      // Enviar el correo de re-activación con clave temporal
      mailService.sendActivationAccountWithTemporaryPassword(user, userKey, passwordTemp, true);
    } else {
      // envia correo de activación
      mailService.sendActivationAccount(user, userKey);
    }
  }

  /**
   * Reenvía un correo de activación con una nueva clave temporal al usuario
   * especificado por su email.
   *
   * Este método realiza las siguientes acciones:
   * 1. Busca al usuario en la base de datos utilizando el email proporcionado.
   * 2. Genera una nueva contraseña temporal y la encripta.
   * 3. Actualiza la contraseña del usuario en la base de datos y genera una nueva
   * clave de activación.
   * 4. Inserta el registro de la nueva clave de activación en la tabla
   * `user_key`.
   * 5. Envía un correo electrónico al usuario con la nueva contraseña temporal y
   * el enlace para activar la cuenta.
   *
   * @param email El correo electrónico del usuario al que se le reenviará el
   *              enlace de activación.
   * @throws IllegalArgumentException Si el usuario no es encontrado o si ocurre
   *                                  algún error durante el proceso.
   */
  public void resendActivation(String email) {

    // Se carga el User
    User user = getUserByEmail(email);

    // crear un password temporal
    String passwordTemp = RandomKeyGenerator.generateRandomKey(6);

    // Encriptar el password y lo agrega al user
    user.setPassword(encoderPassword(passwordTemp));

    // Generar nueva user_key -clave de activación
    UserKey userKey = appUtility.generateKey(user, true);

    // registro en la tabla userKey
    long id = updatePasswordAndInsertUserKey(user, userKey);

    userKey.setId(id);

    // Enviar el nuevo correo de activación
    mailService.sendActivationAccountWithTemporaryPassword(
        user, userKey, passwordTemp, false);

  }

  /**
   * Genera y envía un informe de activación de cuenta a un usuario administrador.
   * 
   * Este método obtiene al primer usuario con el rol 'ROLE_ADMIN' de la base de
   * datos y envía un correo informándole que un nuevo usuario ha activado su
   * cuenta en la aplicación.
   * 
   * @param user El objeto `UserKey` que contiene información del usuario que ha
   *             activado su cuenta, incluyendo su información personal y su
   *             relación con la entidad `User`.
   * @throws IllegalArgumentException Si ocurre un error durante el proceso de
   *                                  obtención del administrador o el envío del
   *                                  correo.
   */
  public void activationReportToAdmin(UserKey user) {
    try {
      // cargar el user con ROLE_ADMIN
      UserProfileResponseDTO userAdmin = getFirstAdminUser();

      // envio a correo
      mailService.sendActivationReportToAdmin(user.getUser(), userAdmin);

    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Obtiene el primer usuario con el rol 'ROLE_ADMIN' de la base de datos.
   * 
   * Este método ejecuta una consulta a la base de datos utilizando el repositorio
   * `UserRepository` para obtener el primer usuario que tiene el rol de
   * administrador ('ROLE_ADMIN'). Los resultados de la consulta se mapean a un
   * objeto `UserProfileResponseDTO` que contiene los datos principales del
   * usuario.
   * 
   * @return Un objeto `UserProfileResponseDTO` que contiene la información del
   *         primer usuario administrador encontrado. Si no se encuentra ningún
   *         usuario con el rol 'ROLE_ADMIN', se retorna `null`.
   */
  public UserProfileResponseDTO getFirstAdminUser() {
    List<Object[]> result = userRepository.findFirstAdminUser();
    if (!result.isEmpty()) {
      Object[] row = result.get(0);
      return new UserProfileResponseDTO(
          (Long) row[0], // id
          (String) row[1], // firstName
          (String) row[2], // lastName
          (String) row[3], // secondLastName
          (String) row[4], // nickname
          (String) row[5], // email
          (String) row[6] // languageKey
      );
    }
    return null;
  }

  /**
   * Encripta una contraseña utilizando el algoritmo de codificación definido por
   * `passwordEncoder`.
   *
   * Este método toma una contraseña en texto plano y la encripta utilizando el
   * `PasswordEncoder` configurado en la aplicación (generalmente BCrypt). La
   * contraseña resultante es segura para ser almacenada en la base de datos.
   *
   * @param password La contraseña en texto plano que se desea encriptar.
   * @return La contraseña encriptada en formato hash.
   */
  public String encoderPassword(String password) {
    return passwordEncoder.encode(password);
  }

  /**
   * Compara dos contraseñas para verificar si coinciden.
   *
   * Este método toma una contraseña en texto plano (`firstPassword`) y la compara
   * con una contraseña encriptada (`secondPassword`) utilizando el
   * `PasswordEncoder`. Devuelve `true` si la contraseña en texto plano, al ser
   * codificada, coincide con la contraseña encriptada; de lo contrario, devuelve
   * `false`.
   *
   * @param firstPassword  La contraseña en texto plano que se desea comparar.
   * @param secondPassword La contraseña encriptada con la que se desea comparar.
   * @return `true` si las contraseñas coinciden, `false` en caso contrario.
   */
  public boolean isMatchPassword(String firstPassword, String secondPassword) {
    return passwordEncoder.matches(firstPassword, secondPassword);
  }

  /**
   * Valida y procesa la solicitud de restablecimiento de contraseña.
   * 
   * @param passwordResetRequestDTO El DTO que contiene el correo o nickname del
   *                                usuario.
   * @throws IllegalArgumentException Si el usuario no es encontrado o hay un
   *                                  problema en la validación.
   */
  public void processPasswordReset(PasswordResetRequestDTO passwordResetRequestDTO) {

    User user;

    // Cargar el Optional<usuario> por correo o nickname
    if (passwordResetRequestDTO.getEmail() != null) {
      user = getUserByEmail(passwordResetRequestDTO.getEmail());
    } else {
      user = getUserByNickName(passwordResetRequestDTO.getNickname());
    }

    // Generar una nuevo keyValue con KeyPurpose = PASSWORD_RESET
    UserKey userKey = appUtility.generateKey(user, false);

    // Guardar la clave en la base de datos
    UserKey newUserKey = userKeyServiceImpl.registerKeyValue(userKey);

    userKey.setId(newUserKey.getId());

    // Enviar el correo de restablecimiento
    mailService.sendPasswordReset(user, userKey);
  }

  /**
   * Crea un nuevo usuario y registra una clave en la tabla `user_key`.
   *
   * Este método llama al procedimiento almacenado `sp_create_user_and_key` a
   * través del repositorio `userRepository`. Se pasa la información del usuario
   * (nombre, apellidos, email, nickname, contraseña hasheada, idioma) y la clave
   * de activación junto con su propósito (por ejemplo, 'ACCOUNT_ACTIVATION'). Si
   * ocurre algún error, como la existencia de un correo o nickname duplicado, se
   * captura la excepción y se lanza una nueva excepción con el mensaje de error
   * correspondiente.
   *
   * Proceso:
   * 1. Se envía la información del usuario y la clave de activación al
   * procedimiento almacenado.
   * 2. Si el procedimiento almacenado detecta errores (correo o nickname
   * duplicados), se lanza una excepción con el mensaje de error respectivo.
   * 3. Si ocurre algún otro error, también se captura y se registra en los logs
   * para su posterior análisis.
   *
   * @param firstName       El primer nombre del usuario.
   * @param lastName        El primer apellido del usuario.
   * @param secondLasttName El segundo apellido del usuario (puede ser NULL).
   * @param email           El correo electrónico del usuario (debe ser único).
   * @param nickname        El apodo o nombre de usuario (debe ser único).
   * @param passwordHash    La contraseña del usuario en formato hash.
   * @param languageKey     La clave del idioma preferido del usuario.
   * @param keyValue        El valor de la clave de activación generada.
   * @param keyPurpose      El propósito de la clave, como 'ACCOUNT_ACTIVATION' o
   *                        'PASSWORD_RESET'.
   *
   * @throws IllegalArgumentException Si ocurre algún error durante la ejecución
   *                                  del procedimiento, se lanza una excepción
   *                                  con el mensaje de error correspondiente.
   */
  public Long createUserAndKey(User user, UserKey userKey) {
    try {
      return userRepository.spCreateUserAndKey(
          user.getFirstName(), user.getLastName(), user.getSecondLastName(), user.getEmail(),
          user.getNickname(), user.getPassword(), user.getLanguageKey(),
          userKey.getKeyValue(), userKey.getKeyPurpose(), userKey.getCreatedAt());
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Actualiza la contraseña de un usuario y registra una nueva clave de
   * activación en la tabla `user_key`.
   *
   * Este método llama al procedimiento almacenado
   * `sp_update_password_and_insert_key` a través del repositorio
   * `userRepository`. El procedimiento realiza dos acciones dentro de una
   * transacción:
   * 1. Actualiza la columna `password_hash` de la tabla `user_mva` con la nueva
   * contraseña del usuario.
   * 2. Inserta una nueva clave de activación en la tabla `user_key` asociada al
   * usuario.
   *
   * Parámetros:
   * - `user`: Objeto `User` que contiene los datos del usuario, incluyendo el ID
   * y la nueva contraseña hasheada.
   * - `userKey`: Objeto `UserKey` que contiene los datos de la clave de
   * activación, incluyendo el valor de la clave, el propósito de la clave y la
   * fecha de creación.
   *
   * Proceso:
   * 1. Extrae la información necesaria del objeto `User` y `UserKey` y llama al
   * método `spUpdatePasswordAndInserKey` del repositorio, que ejecuta el
   * procedimiento almacenado.
   * 2. Si ocurre un error durante la ejecución del procedimiento almacenado, se
   * captura la excepción, se registra el error en los logs y se lanza una nueva
   * excepción `IllegalArgumentException` con el mensaje del error.
   *
   * @param user    El objeto `User` que contiene el ID y la nueva contraseña
   *                hasheada del usuario.
   * @param userKey El objeto `UserKey` que contiene los datos de la clave de
   *                activación.
   *
   * @throws IllegalArgumentException Si ocurre algún error durante la ejecución
   *                                  del procedimiento almacenado.
   */
  public Long updatePasswordAndInsertUserKey(User user, UserKey userKey) {
    try {
      return userRepository.spUpdatePasswordAndInsertUserKey(
          user.getId(), user.getPassword(),
          userKey.getKeyValue(), userKey.getKeyPurpose(), userKey.getCreatedAt());
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Procesa la solicitud de restablecimiento de contraseña para un usuario.
   * Este método lleva a cabo varias validaciones y acciones para completar el
   * proceso de restablecimiento de contraseña.
   * 
   * 1. Verifica si existe una solicitud de restablecimiento de contraseña activa.
   * 2. Verifica si el token de restablecimiento de contraseña ha expirado.
   * 3. Verifica si el usuario asociado a la clave de restablecimiento existe en
   * la base de datos.
   * 4. Codifica la nueva contraseña proporcionada y la actualiza en la base de
   * datos.
   * 
   * @param id          El ID del registro en la tabla `user_key` asociado a la
   *                    solicitud de restablecimiento.
   * @param newPassword La nueva contraseña proporcionada por el usuario.
   * @param keValue     La clave de restablecimiento de contraseña proporcionada
   *                    en el enlace de restablecimiento.
   * 
   * @throws IllegalArgumentException si hay algún error durante el proceso, ya
   *                                  sea por una clave expirada, un usuario no
   *                                  existente, o cualquier otra excepción que
   *                                  ocurra durante el proceso.
   * 
   *                                  Ejemplo de uso:
   * 
   *                                  <pre>
   * {@code
   * try {
   *     userService.processPasswordChange(123L, "NewPassword2024!", "abcd1234-reset-key");
   *     // Contraseña restablecida con éxito
   * } catch (IllegalArgumentException e) {
   *   // Manejar el error de restablecimiento de contraseña
   * }
   * }
   * </pre>
   */
  public void changePasswordByReset(Long id, String newPassword, String keValue) {
    try {

      // 1. verificar si hay una solicitud de reset password y está activa
      UserKey userKey = userKeyServiceImpl.findByIdWithUser(id);

      // 2. Verificar si la clave de restablecimiento ha expirado
      appUtility.verifyExpirationResetPassword(userKey);

      // 3. verificar si el usuario existe en la tabla user_mva
      User user = getUserById(userKey.getUser().getId());

      // validar que la nueva contraseña sea diferente a la actual
      // almacenada
      if (passwordEncoder.matches(newPassword, user.getPassword())) {
        throw new IllegalArgumentException("109, La nueva contraseña es igual a la contraseña vigente");
      }

      // 4. Encripta las contraseñas antes de guardarla
      String hashedPassword = passwordEncoder.encode(newPassword);

      // 5. Envío a procesar la solicitud de restablecimiento
      executeChangePasswordByReset(id, keValue, hashedPassword);

    } catch (Exception e) {
      String errorMessage = appUtility.extractErrorMessage(e.getMessage());
      logger.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
  }

  /**
   * Actualiza la contraseña temporal de un usuario en la base de datos.
   * 
   * Este método toma el ID del usuario y una nueva contraseña (en formato hash) y
   * actualiza la contraseña temporal del usuario en la base de datos.
   * 
   * @param id       El ID del usuario al que se le va a actualizar la contraseña.
   * @param password La nueva contraseña en formato hash.
   * @throws IllegalArgumentException Si ocurre un error al intentar actualizar la
   *                                  contraseña, se lanza esta excepción con un
   *                                  código de error específico.
   */
  public void updateTemporaryPassword(Long id, String password) {
    try {
      userRepository.updatePasswordById(id, password);
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new IllegalArgumentException("157, Error al actualizar contraseña");
    }
  }

  /**
   * Obtiene un usuario por su id.
   *
   * @param id El id del usuario a buscar.
   * @return El usuario encontrado.
   */
  public User getUserById(Long id) {
    try {
      // Intenta encontrar al usuario por ID
      return userRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("102, El usuario no existe"));
    } catch (EntityNotFoundException e) {
      // Loguea el error con el mensaje de excepción
      logger.error("El usuario no existe con el id: {}", id, e.getMessage());
      // Relanza la excepción para que sea manejada por otros manejadores de
      // excepciones
      throw e;
    }
  }

  /**
   * Obtiene un usuario por su email.
   *
   * @param email El correo del usuario a buscar.
   * @return El usuario encontrado.
   */
  public User getUserByEmail(String email) {
    try {
      // intenta encontrar el usaurio con el email
      return userRepository.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("102, El usuario no existe"));
    } catch (UsernameNotFoundException e) {
      // Loguea el error con el mensaje de excepción
      logger.error("El usuario no existe con el email: {}", email, e);
      // Relanza la excepción para que sea manejada por otros manejadores de
      // excepciones
      throw e;
    }
  }

  /**
   * Obtiene un usuario por su nickname.
   *
   * @param nickname El nickname del usuario a buscar.
   * @return El usuario encontrado.
   */
  public User getUserByNickName(String nickname) {
    try {
      // Intenta encontrar el usuario por su nickname
      Optional<User> optionalUser = userRepository.findByNickname(nickname);

      if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        return user;
      } else {
        // Si el usuario no existe, lanza una excepción
        logger.error("El usuario no existe con el nickname: {}", nickname);
        throw new UsernameNotFoundException("102, El usuario no existe");
      }

    } catch (UsernameNotFoundException e) {
      logger.error("Error al buscar usuario por nickname: {}", e.getMessage());
      throw e; // Relanza la excepción para que sea manejada por los manejadores de excepciones
    } catch (Exception e) {
      logger.error("Error inesperado al buscar usuario por nickname: {}", e.getMessage());
      throw new RuntimeException("114, Error inesperado al buscar el usuario", e);
    }
  }

  /**
   * Método para obtener un usuario por su nickname. Si el usuario no existe,
   * retorna null.
   *
   * @param nickname El nickname del usuario que se quiere buscar.
   * @return Un objeto User si el usuario existe, o null si no se encuentra.
   */
  public User getUserByNickNameNullable(String nickname) {
    // Intenta encontrar el usuario por su nickname
    Optional<User> optionalUser = userRepository.findByNickname(nickname);

    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      return user;
    } else {
      return null;
    }
  }

  /**
   * Método para obtener un usuario por su correo electrónico. Si el usuario no
   * existe, retorna null.
   *
   * @param email El correo electrónico del usuario que se quiere buscar.
   * @return Un objeto User si el usuario existe, o null si no se encuentra.
   */
  public User getUserByEmailNullable(String email) {
    // Intenta encontrar el usuario por su nickname
    Optional<User> optionalUser = userRepository.findByEmail(email);

    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      return user;
    } else {
      return null;
    }
  }

  /**
   * Obtiene una lista de todos los usuarios en la base de datos.
   * 
   * @return Lista de usuarios.
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User registerUser(User user) {
    try {
      return userRepository.save(user);
    } catch (Exception e) {
      logger.error("Error al registrar el usuario {}", e.getMessage());
      throw new IllegalArgumentException("158, Error al cargar el usuario");
    }
  }

  /**
   * Actualiza la información de un usuario existente.
   * correspondiente a los atributos: activated y status.
   * Solo ROLE_ADMIN puede acceder a este método
   *
   * @param adminUserDTO El usuario con la información a actualizar.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateStatusUser(UserStatusRequestDTO userStatusUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID
      getUserById(userStatusUpdateRequestDTO.getId());

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spUpdateUserByRoleAdmin(
          userStatusUpdateRequestDTO.getId(),
          userStatusUpdateRequestDTO.getActivated(),
          userStatusUpdateRequestDTO.getStatus());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userStatusUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error de acceso a datos al actualizar el usuario: {}", e.getMostSpecificCause().getMessage());
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (Exception e) {
      logger.error("Error inesperado al actualizar el usuario: {}", e.getMessage());
      throw new IllegalArgumentException(
          "115, Error inesperado al actualizar el usuario " + e.getMessage());
    }

  }

  /**
   * Actualiza la información de un usuario existente.
   * correspondiente a los atributos: firstName, lastName, secondLastName y
   * lenguageKey.
   * Solo ROLE_USER puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateProfileUser(UserProfileRequestDTO userProfileUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      getUserById(userProfileUpdateRequestDTO.getId());

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spUpdateUserByRoleUser(
          userProfileUpdateRequestDTO.getId(),
          userProfileUpdateRequestDTO.getFirstName(),
          userProfileUpdateRequestDTO.getLastName(),
          userProfileUpdateRequestDTO.getSecondLastName(),
          userProfileUpdateRequestDTO.getLanguageKey());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userProfileUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error inesperado al actualizar el usuario: {}", e.getMessage());
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    }
  }

  /**
   * Actualiza el nickname de un usuario existente.
   * Solo ROLE_USER puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateNickname(UserNicknameRequestDTO userNicknameUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      getUserById(userNicknameUpdateRequestDTO.getId());

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spChangeNickname(
          userNicknameUpdateRequestDTO.getId(),
          userNicknameUpdateRequestDTO.getNickname());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userNicknameUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error al actualizar el nickname: {}", e.getMessage());
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("Error de validación: {}", e.getMessage());
      throw e; // Relanzar la excepción para que sea manejada por un controlador global de
               // excepciones
    }
  }

  /**
   * Actualiza el email de un usuario existente.
   * Solo ROLE_USER puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateEmail(UserEmailRequestDTO userEmailUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      getUserById(userEmailUpdateRequestDTO.getId());

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spChangeEmail(
          userEmailUpdateRequestDTO.getId(),
          userEmailUpdateRequestDTO.getEmail());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userEmailUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error al actualizar el correo: {}", e.getMessage());
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("Error de validación del correo: {}", e.getMessage());
      throw e; // Relanzar la excepción para que sea manejada por un controlador global de
               // excepciones
    }
  }

  /**
   * Actualiza el password de un usuario existente.
   * Solo ROLE_USER puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updatePassword(UserPasswordRequestDTO userPasswordUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      User user = getUserById(userPasswordUpdateRequestDTO.getId());

      // Verificar que la contraseña actual proporcionada coincida con la que está
      // almacenada
      if (!passwordEncoder.matches(userPasswordUpdateRequestDTO.getOldPassword(), user.getPassword())) {
        throw new IllegalArgumentException("108, La contraseña no coincide con la registrada");
      }

      // Codifica las contraseñas antes de guardarla
      String hashedPassword = passwordEncoder.encode(userPasswordUpdateRequestDTO.getNewPassword());

      // Verifica que la nueva contraseña no sea igual a la actual
      if (passwordEncoder.matches(userPasswordUpdateRequestDTO.getNewPassword(), user.getPassword())) {
        throw new IllegalArgumentException("109, La nueva contraseña es igual a la contraseña vigente");
      }

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spChangePassword(
          userPasswordUpdateRequestDTO.getId(),
          hashedPassword,
          user.getPassword());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userPasswordUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error al actualizar la contraseña: {}", e.getMessage());
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("Error al actualizar la contraseña: {}", e.getMessage());
      throw e; // Relanzar la excepción para que sea manejada por un controlador global de
               // excepciones
    }
  }

  /**
   * Procesa el restablecimiento de contraseña para un usuario.
   *
   * Este método se encarga de invocar el procedimiento almacenado
   * `spResetPassword` para cambiar la contraseña de un usuario utilizando un
   * `key_value` (clave de restablecimiento) generado previamente y su
   * identificación de usuario (`id`).
   * 
   * Si el procedimiento almacenado detecta algún error (como clave vencida o
   * clave no válida), se captura el mensaje de error y se lanza una excepción con
   * el mensaje proporcionado por el procedimiento.
   *
   * @param id          El identificador del usuario que está restableciendo la
   *                    contraseña.
   * @param keyValue    La clave de restablecimiento proporcionada por el usuario.
   * @param newPassword La nueva contraseña que el usuario desea establecer.
   * @throws IllegalArgumentException Si ocurre un error en el procedimiento
   *                                  almacenado y se recibe un mensaje de error
   *                                  desde la base de datos.
   */
  public void executeChangePasswordByReset(Long id, String keyValue, String newHashedPassword) {
    try {

      // cargar el tiempo de expiración
      int expiracionTimeActivation = appUtility.getExpirationActivation();

      // Invoca el procedimiento almacenado para generar la clave de restablecimiento

      userRepository.spChangePasswordByReset(
          id,
          keyValue,
          newHashedPassword,
          expiracionTimeActivation);

      logger.info("Clave de restablecimiento generada exitosamente");
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new IllegalArgumentException(e.getMessage());
    }
  }

}
