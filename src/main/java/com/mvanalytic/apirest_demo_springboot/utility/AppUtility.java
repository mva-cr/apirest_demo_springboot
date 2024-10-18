package com.mvanalytic.apirest_demo_springboot.utility;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

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

  @Value("${app.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Value("${spring.profiles.active}")
  private String mode;

  /**
   * Método para enviar mensajes de log en la aplicación.
   * 
   * Dependiendo del perfil de la aplicación, se registra la información de la
   * excepción para desarrollo, mientras que en producción se mantiene más simple.
   *
   * @param message  Mensaje principal del log. Puede describir el contexto o la
   *                 operación en la cual ocurrió el error.
   * @param eMessage Mensaje de la excepción (detalles del error), que puede ser
   *                 registrado en modo desarrollo para facilitar el diagnóstico.
   * @author Mario Martínez Lanuza
   */
  public void sendLog(String message, String eMessage) {
    if ("dev".equals(mode)) {
      logger.error("{}: {}", message, eMessage);
    } else {
      logger.error("{}", message);
    }
  }

  public String getMode() {
    return this.mode;
  }

  public void createLogFile(String folderName, String fileName) {
    if ("dev".equals(getMode())) {
      String logDirectory = LogPathConfig.getLogDirectoryPath(folderName, fileName);
      System.setProperty("LOG_PATH", logDirectory);
      System.setProperty("LOG_FILE_NAME", fileName);
    }
  }

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
   *                            cuenta. Si es false, se asume que es para
   *                            restablecimiento de contraseña.
   * @return La entidad UserKey creada y registrada.
   * @author Mario Martínez Lanuza
   */
  public UserKey generateKey(User user, boolean isAccountActivation) {
    String activationKey = getUUIDString();
    UserKey userKey = new UserKey();
    userKey.setUser(user);
    userKey.setCreatedAt(Instant.now());
    userKey.setKeyValue(activationKey);
    userKey.setKeyPurpose(isAccountActivation ? "ACCOUNT_ACTIVATION" : "PASSWORD_RESET");
    return userKey;
  }

  /**
   * Genera un identificador único universal (UUID) en formato de cadena.
   * 
   * Este método crea un UUID, que es un identificador único utilizado en muchas
   * aplicaciones para garantizar la singularidad. Los UUID son aleatorios y
   * prácticamente no se repiten, lo que los convierte en una opción adecuada para
   * generar identificadores únicos en elementos como tokens de refresco,
   * identificadores de transacciones, entre otros.
   * 
   * @return Un string que representa un UUID, en formato estándar
   *         (por ejemplo, "ddeb27fb-d9a0-4624-be4d-4615062daed4").
   * @author Mario Martínez Lanuza
   */
  public String getUUIDString() {
    return UUID.randomUUID().toString();
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
      sendLog("147, La clave de activación ha expirado", e.getMessage());
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
      sendLog("147, La clave de activación ha expirado", e.getMessage());
      throw new IllegalArgumentException("147, La clave de activación ha expirado", e);
    }
  }

  /**
   * Obtiene el tiempo de expiración de la clave de activación en minutos. Este
   * valor es utilizado para determinar el tiempo máximo en el que una clave de
   * activación es válida antes de que expire.
   * 
   * @return Un valor entero que representa el tiempo de expiración en minutos.
   * @throws IllegalArgumentException si ocurre un error al intentar obtener el
   *                                  valor.
   * 
   *                                  Ejemplo de uso:
   *                                  - Este método es utilizado para controlar el
   *                                  tiempo de validez de una clave de activación
   *                                  en el sistema.
   *                                  - Puede usarse para definir el límite de
   *                                  tiempo en el que un usuario debe activar su
   *                                  cuenta o su clave temporal expira.
   */
  public int getExpirationActivation() {
    try {
      return expirationActivation;
    } catch (Exception e) {
      sendLog("159, Error al cargar el tiempo de expiración", e.getMessage());
      throw new IllegalArgumentException("159, Error al cargar el tiempo de expiración");
    }

  }

  /**
   * Obtiene la URL base de la aplicación, que puede ser utilizada para generar
   * enlaces completos en correos electrónicos, redireccionamientos, o cualquier
   * otra operación que necesite la URL completa de la aplicación.
   * 
   * @return Una cadena que representa la URL base de la aplicación.
   * 
   *         Ejemplo de uso:
   *         - Puede ser utilizado para construir enlaces en correos electrónicos
   *         o en respuestas HTTP.
   *         - Ejemplo de salida: "http://localhost:8080" o
   *         "https://www.miapp.com"
   */
  public String getBaseUrl() {
    try {
      return baseUrl;
    } catch (Exception e) {
      sendLog("169, Error al cargar la url base", e.getMessage());
      throw new IllegalArgumentException("169, Error al cargar la url base");
    }
  }

  /**
   * Obtiene la ruta de almacenamiento de archivos definida en la configuración de
   * la aplicación.
   * 
   * @return Una cadena que representa la ubicación del almacenamiento de
   *         archivos. Esta ubicación generalmente se define en el archivo de
   *         configuración de la aplicación (como `application.properties` o
   *         `application.yml`).
   * 
   *         Ejemplo de uso:
   *         - Puede ser utilizado para cargar o guardar archivos en una ubicación
   *         específica del servidor.
   * 
   *         Ejemplo de salida: "src/main/resources/static/pdf"
   */
  public String getFileStorageLocation() {
    try {
      return storageLocation;
    } catch (Exception e) {
      sendLog("170, Error al cargar la ruta de los archivos para descarga", e.getMessage());
      throw new IllegalArgumentException("170, Error al cargar la ruta de los archivos para descarga");
    }
  }

  /**
   * Devuelve la duración del refresh token configurada en la propiedad
   * 'app.jwtRefreshExpirationMs' de application.properties.
   * 
   * @return Long La duración del refresh token en milisegundos.
   * @throws IllegalArgumentException Si ocurre un error al intentar cargar la
   *                                  variable 'refreshTokenDurationMs'.
   */
  public Long getRefreshTokenDurationMs() {
    try {
      return refreshTokenDurationMs;
    } catch (Exception e) {
      sendLog("173, Error al cargar el refreshTokenDurationMs", e.getMessage());
      throw new IllegalArgumentException("173, Error al cargar el refreshTokenDurationMs");
    }
  }

  /**
   * Formatea una instancia de tiempo de un intento de inicio de sesión (Instant)
   * a una cadena con un formato específico, tomando en cuenta la zona horaria
   * local.
   * 
   * @param attemptTime El instante de tiempo (Instant) que representa cuándo
   *                    ocurrió el intento de inicio de sesión.
   * @return Una cadena con la fecha y hora formateadas en el formato "dd/MM/yyyy
   *         hh:mm:ss a" ajustada a la zona horaria de "America/Costa_Rica".
   * 
   *         Ejemplo de salida: "29/09/2024 06:01:47 PM"
   */
  public String formatAttemptTime(Instant attemptTime) {
    try {
      // Obtener la zona horaria local (puedes usar la del sistema o definir
      // manualmente)
      ZoneId localZoneId = ZoneId.of("America/Costa_Rica"); // Definir la zona horaria

      // Convertir el Instant a la hora local
      ZonedDateTime localDateTime = attemptTime.atZone(localZoneId);

      // Definir el formato de salida
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");

      // Formatear la fecha y hora
      return localDateTime.format(formatter);

    } catch (Exception e) {
      sendLog("171, Error al formatear la fecha y hora del intento", e.getMessage());
      throw new IllegalArgumentException("171, Error al formatear la fecha y hora del intento", e);
    }
  }

  /**
   * Convierte una fecha en formato "YYYY-MM-DDTHH:MM" de la zona horaria
   * "America/Costa_Rica" a UTC.
   *
   * @param dateTimeString La fecha y hora en formato "YYYY-MM-DDTHH:MM" (ej.
   *                       "2024-10-02T14:40").
   * @return La fecha y hora convertida a formato UTC en un String.
   * @throws IllegalArgumentException si ocurre un error durante el proceso de
   *                                  conversión.
   */
  public String convertToUtcString(String localDateTimeString) {
    try {
      // Definir el formato esperado de la fecha y hora recibida
      DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

      // Parsear la fecha y hora desde el String recibido
      LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeString, inputFormatter);

      // Aplicar la zona horaria de Costa Rica
      ZoneId localZoneId = ZoneId.of("America/Costa_Rica");

      // Convertir el LocalDateTime a ZonedDateTime con la zona horaria de Costa Rica
      ZonedDateTime zonedDateTime = localDateTime.atZone(localZoneId);

      // Convertir la fecha y hora a UTC
      ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

      // Formatear la fecha y hora UTC como un String
      DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
      return utcDateTime.format(outputFormatter);

    } catch (Exception e) {
      sendLog("185, Error al convertir la fecha a UTC", e.getMessage());
      throw new IllegalArgumentException("185, Error al convertir la fecha a UTC", e);
    }
  }

  /**
   * Valida si una cadena de fecha sigue el formato "YYYY-MM-DDTHH:MM".
   *
   * @param dateString La fecha en formato "YYYY-MM-DDTHH:MM".
   * @return true si el formato es válido, false en caso contrario.
   */
  public boolean isValidDateFormat(String dateString) {
    try {
      // Definir el formato esperado
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

      // Intentar parsear la fecha según el formato definido
      LocalDateTime.parse(dateString, formatter);

      return true;
    } catch (DateTimeParseException e) {
      // Retornar false si el formato no es válido
      return false;
    }
  }

}
