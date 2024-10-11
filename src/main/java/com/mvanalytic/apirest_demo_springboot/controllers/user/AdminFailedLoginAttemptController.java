package com.mvanalytic.apirest_demo_springboot.controllers.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mvanalytic.apirest_demo_springboot.dto.user.FailedLoginAttemptResponseDTO;
import com.mvanalytic.apirest_demo_springboot.services.user.FailedLoginAttemptService;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;
import com.mvanalytic.apirest_demo_springboot.utility.UserValidationService;
import org.apache.logging.log4j.Logger;
import java.time.Instant;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

/**
 * Controlador para gestionar operaciones CRUD de los Intentos de login fallidos
 * de No usario de la aplicación. Este controlador solo puede ser accedido por
 * usuarios con el rol "ROLE_ADMIN".
 */
@RestController
@RequestMapping("/api/admin/failed-login")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminFailedLoginAttemptController {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(AdminFailedLoginAttemptController.class);

  @Autowired
  private FailedLoginAttemptService fLoginAttemptService;

  @Autowired
  private AppUtility appUtility;

  @Autowired
  private UserValidationService userValidationService;

  /**
   * Endpoint para obtener los intentos de inicio de sesión fallidos basados en el
   * correo electrónico, con soporte de paginación.
   * 
   * Este método permite a los administradores (ROLE_ADMIN) consultar intentos de
   * inicio de sesión fallidos asociados a un correo electrónico específico,
   * paginando los resultados.
   *
   * @param email      El correo electrónico del usuario cuyos intentos fallidos
   *                   se desean consultar.
   * @param pageNumber El número de página que se solicita (por defecto, es 0).
   * @param pageSize   El tamaño de la página que se solicita (por defecto, es
   *                   10).
   * @return Una respuesta HTTP con una página de DTOs que representan los
   *         intentos fallidos de inicio de sesión.
   * @throws IllegalArgumentException Si el formato del correo electrónico es
   *                                  inválido.
   */
  @GetMapping("/get-by-email/{email}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Page<FailedLoginAttemptResponseDTO>> getFailedLoginByEmail(
      @PathVariable String email,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    // validar si el correo es correcto el formato
    if (!userValidationService.isValidGeneralEmail(email)) {
      logger.error("El correo no cumple el formato definido");
      throw new IllegalArgumentException("123, El correo no cumple el formato definido");
    }
    // Se llama al servicio para obtener los intentos de inicio de sesión paginados
    Page<FailedLoginAttemptResponseDTO> fDtos = fLoginAttemptService
        .findFailedLoginAttemptsByEmailPaginated(email, pageNumber, pageSize);

    // Devolver la respuesta con los intentos paginados
    return ResponseEntity.ok(fDtos);
  }

  /**
   * Endpoint para obtener los intentos de inicio de sesión fallidos basados en el
   * nickname, con soporte de paginación.
   * 
   * Este método permite a los administradores (ROLE_ADMIN) consultar intentos de
   * inicio de sesión fallidos asociados a un nickname específico, paginando los
   * resultados.
   *
   * @param nickname   El nickname del usuario cuyos intentos fallidos se desean
   *                   consultar.
   * @param pageNumber El número de página que se solicita (por defecto, es 0).
   * @param pageSize   El tamaño de la página que se solicita (por defecto, es
   *                   10).
   * @return Una respuesta HTTP con una página de DTOs que representan los
   *         intentos fallidos de inicio de sesión.
   */
  @GetMapping("/get-by-nickname/{nickname}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Page<FailedLoginAttemptResponseDTO>> getFailedLoginByNickname(
      @PathVariable String nickname,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {
    // validar el nickname
    if (!userValidationService.isValidNickname(nickname, 1, 50)) {
      throw new IllegalArgumentException("124, El nickname no cumple el formato definido");
    }
    // Se llama al servicio para obtener los intentos de inicio de sesión paginados
    Page<FailedLoginAttemptResponseDTO> fDtos = fLoginAttemptService
        .findFailedLoginAttemptsByNicknamePaginated(nickname, pageNumber, pageSize);

    return ResponseEntity.ok(fDtos);
  }

  /**
   * Endpoint para eliminar los intentos de inicio de sesión asociados a un correo
   * electrónico antes de una fecha específica.
   *
   * Este método permite a los administradores (ROLE_ADMIN) eliminar todos los
   * intentos de inicio de sesión fallidos que pertenecen a un correo específico y
   * que ocurrieron antes de una fecha determinada.
   *
   * @param email             El correo electrónico del usuario cuyos intentos se
   *                          eliminarán.
   * @param attemptTimeString La fecha y hora antes de la cual los intentos serán
   *                          eliminados (formato ISO).
   * @return Una respuesta HTTP con el mensaje de confirmación de la eliminación.
   * @throws IllegalArgumentException Si el correo o la fecha tienen un formato
   *                                  inválido.
   */
  @DeleteMapping("/delete-by-email/{email}/attemptTime/{attemptTimeString}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> deleteLogingAttemptByEmailAndTime(
      @PathVariable String email, @PathVariable String attemptTimeString) {
    // validar si el correo es correcto el formato
    if (!userValidationService.isValidGeneralEmail(email)) {
      logger.error("El correo no cumple el formato definido");
      throw new IllegalArgumentException("123, El correo no cumple el formato definido");
    }
    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(attemptTimeString)) {
      logger.error("Error en el formato de la fecha y hora");
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }

    // Convertir el string en un objeto Instant
    Instant startTime = Instant.parse(appUtility.convertToUtcString(attemptTimeString));

    // llamada al servicio para que elimine los intentos
    fLoginAttemptService.deleteByEmailAndAttemptTimeBefore(email, startTime);

    return ResponseEntity.ok("Se eliminaron los intentos de sesión del email y fecha");
  }

  /**
   * Endpoint para eliminar los intentos de inicio de sesión asociados a un
   * nickname antes de una fecha específica.
   *
   * Este método permite a los administradores (ROLE_ADMIN) eliminar todos los
   * intentos de inicio de sesión fallidos que pertenecen a un nickname específico
   * y que ocurrieron antes de una fecha determinada.
   *
   * @param nickname          El nickname del usuario cuyos intentos se
   *                          eliminarán.
   * @param attemptTimeString La fecha y hora antes de la cual los intentos serán
   *                          eliminados (formato ISO).
   * @return Una respuesta HTTP con el mensaje de confirmación de la eliminación.
   * @throws IllegalArgumentException Si el nickname o la fecha tienen un formato
   *                                  inválido.
   */
  @DeleteMapping("/delete-by-nickname/{nickname}/attemptTime/{attemptTimeString}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> deleteLogingAttemptByNicknameAndTime(
      @PathVariable String nickname, @PathVariable String attemptTimeString) {
    // validar el nickname
    if (!userValidationService.isValidNickname(nickname, 1, 50)) {
      throw new IllegalArgumentException("124, El nickname no cumple el formato definido");
    }

    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(attemptTimeString)) {
      logger.error("Error en el formato de la fecha y hora");
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }

    // Convertir el string en un objeto Instant
    Instant startTime = Instant.parse(appUtility.convertToUtcString(attemptTimeString));

    // llamada al servicio para que elimine los intentos
    fLoginAttemptService.deleteByNicknameAndAttemptTimeBefore(nickname, startTime);

    return ResponseEntity.ok("Se eliminaron los intentos de sesión del nickname y fecha");
  }

  /**
   * Endpoint para eliminar los intentos de inicio de sesión fallidos que
   * ocurrieron antes de una fecha específica.
   *
   * Este método permite a los administradores (ROLE_ADMIN) eliminar todos los
   * intentos de inicio de sesión fallidos que ocurrieron antes de una fecha
   * específica, sin tener en cuenta el correo electrónico o el nickname.
   *
   * @param attemptTimeString La fecha y hora antes de la cual los intentos serán
   *                          eliminados (formato ISO).
   * @return Una respuesta HTTP con el mensaje de confirmación de la eliminación.
   * @throws IllegalArgumentException Si la fecha proporcionada tiene un formato
   *                                  inválido.
   */
  @DeleteMapping("/delete-by-time-before/{attemptTimeString}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> deleteLogingAttemptByTimeBefore(
      @PathVariable String attemptTimeString) {
    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(attemptTimeString)) {
      logger.error("Error en el formato de la fecha y hora");
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }

    // Convertir el string en un objeto Instant
    Instant startTime = Instant.parse(appUtility.convertToUtcString(attemptTimeString));

    // llamada al servicio para que elimine los intentos
    fLoginAttemptService.deleteByAttemptTimeBefore(startTime);

    return ResponseEntity.ok("Se eliminaron los intentos de sesión por fecha");
  }

  /**
   * Endpoint para contar los intentos de inicio de sesión fallidos por correo
   * electrónico.
   *
   * Este método permite contar cuántos intentos de inicio de sesión fallidos se
   * han realizado con un correo electrónico específico. Antes de realizar el
   * conteo, se valida que el correo proporcionado tenga un formato correcto.
   *
   * @param email El correo electrónico a validar y contar intentos fallidos.
   * @return Una respuesta HTTP con el número de intentos de inicio de sesión
   *         fallidos asociados al correo.
   * @throws IllegalArgumentException Si el correo no cumple con el formato
   *                                  definido.
   */
  @GetMapping("/count-by-email/{email}")
  public ResponseEntity<Long> countByEmail(@PathVariable String email) {
    // validar si el correo es correcto el formato
    if (!userValidationService.isValidGeneralEmail(email)) {
      logger.error("El correo no cumple el formato definido");
      throw new IllegalArgumentException("123, El correo no cumple el formato definido");
    }
    Long count = fLoginAttemptService.countByEmail(email);
    return ResponseEntity.ok(count);
  }

  /**
   * Endpoint para contar los intentos de inicio de sesión fallidos por nickname.
   *
   * Este método permite contar cuántos intentos de inicio de sesión fallidos se
   * han realizado con un nickname específico. Antes de realizar el conteo, se
   * valida que el nickname proporcionado cumpla con el formato adecuado (entre 1
   * y 50 caracteres).
   *
   * @param nickname El nickname del usuario que se utilizará para contar los
   *                 intentos fallidos.
   * @return Una respuesta HTTP con el número de intentos de inicio de sesión
   *         fallidos asociados al nickname.
   * @throws IllegalArgumentException Si el nickname no cumple con el formato
   *                                  definido.
   */
  @GetMapping("/count-by-nickname/{nickname}")
  public ResponseEntity<Long> countByNickname(@PathVariable String nickname) {
    // validar el nickname
    if (!userValidationService.isValidNickname(nickname, 1, 50)) {
      throw new IllegalArgumentException("124, El nickname no cumple el formato definido");
    }
    Long count = fLoginAttemptService.countByNickname(nickname);
    return ResponseEntity.ok(count);
  }

  /**
   * Endpoint para contar los intentos de inicio de sesión fallidos por dirección
   * IP.
   *
   * Este método permite contar cuántos intentos de inicio de sesión fallidos se
   * han realizado desde una dirección IP específica. No se ha implementado una
   * validación específica de la dirección IP, pero podría agregarse si se desea.
   *
   * @param ipAddress La dirección IP desde la cual se han realizado los intentos
   *                  de inicio de sesión fallidos.
   * @return Una respuesta HTTP con el número de intentos de inicio de sesión
   *         fallidos asociados a la dirección IP.
   */
  @GetMapping("/count-by-ipAddress/{ipAddress}")
  public ResponseEntity<Long> countByIpAddress(@PathVariable String ipAddress) {
    Long count = fLoginAttemptService.countByIpAddress(ipAddress);
    return ResponseEntity.ok(count);
  }

  /**
   * Endpoint para contar los intentos de inicio de sesión fallidos por correo
   * electrónico dentro de un rango de fechas.
   *
   * Este método permite contar cuántos intentos de inicio de sesión fallidos se
   * han realizado para un correo electrónico específico dentro de un rango de
   * fechas proporcionado.
   * 
   * @param email           El correo electrónico del usuario cuyos intentos de
   *                        inicio de sesión fallidos se quieren contar.
   * @param startTimeString La fecha de inicio del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @param endTimeString   La fecha de fin del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @return Una respuesta HTTP con el número de intentos de inicio de sesión
   *         fallidos en el rango de fechas.
   * @throws IllegalArgumentException Si el formato del correo o las fechas es
   *                                  incorrecto.
   */
  @GetMapping("/count-by-email/{email}/startTime/{startTimeString}/endTime/{endTimeString}")
  public ResponseEntity<Long> countByEmailAndAttemptTimeBetween(
      @PathVariable String email,
      @PathVariable String startTimeString,
      @PathVariable String endTimeString) {
    // validar si el correo es correcto el formato
    if (!userValidationService.isValidGeneralEmail(email)) {
      logger.error("El correo no cumple el formato definido");
      throw new IllegalArgumentException("123, El correo no cumple el formato definido");
    }
    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(startTimeString) ||
        !appUtility.isValidDateFormat(endTimeString)) {
      logger.error("Error en el formato de la fecha y hora");
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }
    // Convertir el string en un objeto Instant
    Instant startTime = Instant.parse(appUtility.convertToUtcString(startTimeString));
    Instant endTime = Instant.parse(appUtility.convertToUtcString(endTimeString));

    Long count = fLoginAttemptService.countByEmailAndAttemptTimeBetween(email, startTime, endTime);
    return ResponseEntity.ok(count);
  }

  /**
   * Endpoint para contar los intentos de inicio de sesión fallidos por nickname
   * dentro de un rango de fechas.
   *
   * Este método cuenta cuántos intentos de inicio de sesión fallidos se han
   * realizado para un nickname específico en un rango de fechas determinado.
   * 
   * @param nickname        El nickname del usuario cuyos intentos de inicio de
   *                        sesión fallidos se quieren contar.
   * @param startTimeString La fecha de inicio del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @param endTimeString   La fecha de fin del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @return Una respuesta HTTP con el número de intentos de inicio de sesión
   *         fallidos en el rango de fechas.
   * @throws IllegalArgumentException Si el formato del nickname o las fechas es
   *                                  incorrecto.
   */
  @GetMapping("/count-by-nickname/{nickname}/startTime/{startTimeString}/endTime/{endTimeString}")
  public ResponseEntity<Long> countByNicknameAndAttemptTimeBetween(
      @PathVariable String nickname,
      @PathVariable String startTimeString,
      @PathVariable String endTimeString) {

    // validar el nickname
    if (!userValidationService.isValidNickname(nickname, 1, 50)) {
      throw new IllegalArgumentException("124, El nickname no cumple el formato definido");
    }
    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(startTimeString) ||
        !appUtility.isValidDateFormat(endTimeString)) {
      logger.error("Error en el formato de la fecha y hora");
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }
    // Convertir el string en un objeto Instant
    Instant startTime = Instant.parse(appUtility.convertToUtcString(startTimeString));
    Instant endTime = Instant.parse(appUtility.convertToUtcString(endTimeString));

    Long count = fLoginAttemptService.countByNicknameAndAttemptTimeBetween(nickname, startTime, endTime);
    return ResponseEntity.ok(count);
  }

  /**
   * Endpoint para contar los intentos de inicio de sesión fallidos por dirección
   * IP dentro de un rango de fechas.
   *
   * Este método cuenta cuántos intentos de inicio de sesión fallidos se han
   * realizado desde una dirección IP específica en un rango de fechas
   * determinado.
   * 
   * @param ipAddress       La dirección IP desde la cual se realizaron los
   *                        intentos fallidos.
   * @param startTimeString La fecha de inicio del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @param endTimeString   La fecha de fin del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @return Una respuesta HTTP con el número de intentos de inicio de sesión
   *         fallidos en el rango de fechas.
   * @throws IllegalArgumentException Si el formato de las fechas es incorrecto.
   */
  @GetMapping("/count-by-ipAddress/{ipAddress}/startTime/{startTimeString}/endTime/{endTimeString}")
  public ResponseEntity<Long> countByIpAddressAndAttemptTimeBetween(
      @PathVariable String ipAddress,
      @PathVariable String startTimeString,
      @PathVariable String endTimeString) {
    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(startTimeString) ||
        !appUtility.isValidDateFormat(endTimeString)) {
      logger.error("Error en el formato de la fecha y hora");
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }
    // Convertir el string en un objeto Instant
    Instant startTime = Instant.parse(appUtility.convertToUtcString(startTimeString));
    Instant endTime = Instant.parse(appUtility.convertToUtcString(endTimeString));

    Long count = fLoginAttemptService.countByIpAddressAndAttemptTimeBetween(ipAddress, startTime, endTime);
    return ResponseEntity.ok(count);
  }

}
