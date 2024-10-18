package com.mvanalytic.apirest_demo_springboot.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserKey;
import com.mvanalytic.apirest_demo_springboot.repositories.user.UserKeyRepository;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@Service
public class UserKeyServiceImpl {

  /**
   * Para cuando se necesita manejar procedimientos almacenados que no se ajustan
   * bien a los métodos simples
   * del repositorio.
   * Permite manejar la sincronización con la base de datos.
   */
  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private UserKeyRepository userKeyRepository;

  @Autowired
  private AppUtility appUtility;

  @Autowired
  @Lazy
  private PasswordEncoder passwordEncoder;

  /**
   * Obtiene un usuario por su id.
   *
   * @param id El id del usuario a buscar.
   * @return El usuario encontrado.
   */
  public UserKey findByIdWithUser(Long id) {
    try {
      // intenta encontrar el usuario por Id
      return userKeyRepository.findByIdWithUser(id)
          .orElseThrow(() -> new EntityNotFoundException("102, El usuario no existe"));
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * Registra una nueva clave de activación o restablecimiento de contraseña para
   * un usuario.
   *
   * @param userKey El objeto UserKey que contiene los datos de la clave.
   * @return El objeto UserKey registrado.
   */
  public UserKey registerKeyValue(UserKey userKey) {
    try {
      return userKeyRepository.save(userKey);
    } catch (Exception e) {
      throw new IllegalArgumentException("508, Error al cargar el registro de llaves");
    }

  }

  /**
   * Activa la cuenta del usuario basado en la clave proporcionada, para la
   * creación de ROLE_USER o por la creación temporal de un password por parte de
   * ROLE_ADMIN
   *
   * @param id            El identificador del registro de UserKey.
   * @param activationKey La clave de activación proporcionada.
   * @throws IllegalArgumentException Si la clave es inválida o no se encuentra.
   */
  @Transactional
  public UserKey activateAccount(Long id, String activationKey, String tempPassword) {
    try {
      // Cargar el user_key que cumple con los parámetros id y activationKey
      UserKey userKey = findByIdWithUser(id);

      if (tempPassword != null &&
          !isMatchPassword(tempPassword, userKey.getUser().getPassword())) {
        throw new IllegalArgumentException("113, La contraseña temporal no coindice con la enviada");
      }

      // Verificar si la clave de restablecimiento ha expirado
      appUtility.verifyExpirationActivation(userKey);

      // verificar si el usuario existe en la tabla user_mva
      // userService.getUserById(userKey.getUser().getId());

      // envio de solicitud de activiación al store procedure
      executeAcivateAccount(id, activationKey);

      return userKey;
    } catch (Exception e) {
      // Obtiene solo el mensaje del procedimiento almacenado
      String errorMessage = appUtility.extractErrorMessage(e.getMessage());
      throw new IllegalArgumentException(errorMessage); // Lanza solo el mensaje relevante
    }

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
   * Activa la cuenta de un usuario utilizando su ID y una clave de activación.
   *
   * Este método realiza las siguientes acciones:
   * 1. Carga el tiempo de expiración para la activación de la cuenta, definido en
   * la configuración de la aplicación.
   * 2. Llama al procedimiento almacenado `spActivateAccount` en el repositorio
   * `userKeyRepository`, enviando el ID del usuario, la clave de activación y el
   * tiempo de expiración.
   * 3. Si la activación es exitosa, se registra un mensaje de éxito en los logs.
   * 4. Si ocurre un error durante el proceso de activación, captura la excepción,
   * la registra en los logs y lanza una excepción con el mensaje de error.
   *
   * @param id       El ID del usuario cuya cuenta se va a activar.
   * @param keyValue La clave de activación que se utilizará para activar la
   *                 cuenta.
   *
   * @throws IllegalArgumentException Si ocurre algún error durante el proceso de
   *                                  activación.
   */
  public void executeAcivateAccount(Long id, String keyValue) {
    try {

      // cargar el tiempo de expiración
      int expiracionTimeActivation = appUtility.getExpirationActivation();

      // Enviar al procedimiento almacenado
      userKeyRepository.spActivateAccount(
          id,
          keyValue,
          expiracionTimeActivation);
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

}
