package com.mvanalytic.apirest_demo_springboot.utility;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserKey;

@Component
public class AppUtility {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(AppUtility.class);

  // cargar el valor baseUrl desde el archivo properties
  @Value("${app.base-url}")
  private String baseUrl;

  @Value("${app.activation.key.expiry.hours}")
  private int expirationActivation;

  @Value("${app.reset.password.key.expiry.hours}")
  private int expirationResetPassword;

  @Value("${file.storage.location}")
  private String storageLocation;

  /**
   * Método para extraer el mensaje de error principal de una excepción.
   *
   * @param exceptionMessage Mensaje completo de la excepción
   * @return Mensaje filtrado con solo el código y el mensaje relevante
   */
  public String extractErrorMessage(String exceptionMessage) {
    // Busca el mensaje del procedimiento almacenado
    if (exceptionMessage != null && exceptionMessage.contains("[")) {
      int startIndex = exceptionMessage.indexOf("[") + 1;
      int endIndex = exceptionMessage.indexOf("]");
      if (startIndex >= 0 && endIndex > startIndex) {
        // Retorna solo el mensaje dentro de los corchetes, por ejemplo: "105, El
        // usuario ya está activado"
        return exceptionMessage.substring(startIndex, endIndex);
      }
    }
    // Retorna el mensaje completo si no se encuentra el patrón esperado
    return exceptionMessage;
  }

  /**
   * Genera una clave (key) para la activación de cuenta o el restablecimiento de
   * contraseña.
   *
   * @param user                El usuario para el cual se genera la clave.
   * @param isAccountActivation Indica si la clave es para la activación de la
   *                            cuenta.
   *                            Si es false, se asume que es para restablecimiento
   *                            de contraseña.
   * @return La entidad UserKey creada y registrada.
   */
  public UserKey generateKey(User user, boolean isAccountActivation) {
    String activationKey = UUID.randomUUID().toString();
    UserKey userKey = new UserKey();
    userKey.setUser(user);
    userKey.setCreatedAt(Instant.now());
    userKey.setKeyValue(activationKey);
    userKey.setKeyPurpose(isAccountActivation ? "ACCOUNT_ACTIVATION" : "PASSWORD_RESET");
    return userKey;
  }

  /**
   * Verifica si la clave de restablecimiento de contraseña ha expirado.
   *
   * @param userKey La instancia de UserKey que contiene la información de la
   *                clave.
   * @throws IllegalArgumentException Si la clave ha expirado.
   */
  public void verifyExpirationResetPassword(UserKey userKey) {
    try {
      Instant keyCreatedAt = userKey.getCreatedAt();
      Instant now = Instant.now();

      // Calcula la diferencia en horas entre el momento actual y el momento en que se
      // creó la clave
      long hoursDifference = Duration.between(keyCreatedAt, now).toMinutes();

      // Verifica si la clave ha expirado
      if (hoursDifference > expirationResetPassword * 60) {
        throw new IllegalArgumentException("147, La clave de activación ha expirado");
      }

    } catch (Exception e) {
      logger.error("Error al verificar la expiración de la clave de restablecimiento: {}", e.getMessage());
      throw new IllegalArgumentException("147, La clave de activación ha expirado", e);
    }
  }

  /**
   * Verifica si la clave de restablecimiento de contraseña ha expirado.
   *
   * @param userKey La instancia de UserKey que contiene la información de la
   *                clave.
   * @throws IllegalArgumentException Si la clave ha expirado.
   */
  public void verifyExpirationActivation(UserKey userKey) {
    try {
      Instant keyCreatedAt = userKey.getCreatedAt();
      Instant now = Instant.now();

      // Calcula la diferencia en horas entre el momento actual y el momento en que se
      // creó la clave
      long hoursDifference = Duration.between(keyCreatedAt, now).toMinutes();

      // Verifica si la clave ha expirado
      if (hoursDifference > expirationActivation * 60) {
        throw new IllegalArgumentException("147, La clave de activación ha expirado");
      }

    } catch (Exception e) {
      logger.error("Error al verificar la expiración de la clave de restablecimiento: {}", e.getMessage());
      throw new IllegalArgumentException("147, La clave de activación ha expirado", e);
    }
  }

  public int getExpirationActivation() {
    try {
      return expirationActivation;
    } catch (Exception e) {
      logger.error("Error al cargar el tiempo de expiración de la clave de activación: {}", e.getMessage());
      throw new IllegalArgumentException("159, Error al cargar el tiempo de expiración", e);
    }

  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getFileStorageLocation() {
    return storageLocation;
  }
}
