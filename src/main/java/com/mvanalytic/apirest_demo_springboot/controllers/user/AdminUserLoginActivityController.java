package com.mvanalytic.apirest_demo_springboot.controllers.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserLoginActivityResponseDTO;
import com.mvanalytic.apirest_demo_springboot.services.user.UserLoginActivityService;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;
import java.time.Instant;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Page;

/**
 * Controlador para gestionar operaciones CRUD de las sesiones exitosas y
 * fallidos de los usuarios. Este controlador solo puede ser accedido por
 * usuarios con el rol "ROLE_ADMIN".
 */
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/admin/user-login")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminUserLoginActivityController {


  @Autowired
  private UserLoginActivityService uLoginActivityService;

  @Autowired
  private AppUtility appUtility;

  /**
   * Endpoint para obtener todas las actividades de inicio de sesión de usuarios
   * sin paginación.
   * 
   * Este método permite que los administradores (ROLE_ADMIN) consulten todas las
   * actividades de inicio de sesión de los usuarios sin aplicar paginación. Esto
   * podría ser útil en casos donde no hay un gran volumen de datos y se desea
   * obtener la información completa de una sola vez.
   *
   * @return Una respuesta HTTP 200 OK que contiene una lista de objetos
   *         `UserLoginActivityResponseDTO` que representan las actividades de
   *         inicio de sesión.
   */
  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<UserLoginActivityResponseDTO>> getAllSession() {
    return ResponseEntity.ok(uLoginActivityService.getAllSession());
  }

  /**
   * Endpoint para obtener todas las actividades de inicio de sesión de usuarios
   * de manera paginada.
   * 
   * Este método permite que los administradores (ROLE_ADMIN) consulten las
   * actividades de inicio de sesión de todos los usuarios en el sistema,
   * organizadas en páginas para facilitar la carga de datos.
   *
   * @param pageNumber El número de página a consultar, por defecto es 0.
   * @param pageSize   El tamaño de la página, es decir, la cantidad de registros
   *                   por página. Por defecto es 10.
   * @return Una respuesta HTTP 200 OK que contiene una página de objetos
   *         `UserLoginActivityResponseDTO` que representan las actividades de
   *         inicio de sesión.
   */
  @GetMapping("/get-by-page")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Page<UserLoginActivityResponseDTO>> findAllSessionsPaginated(
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    // Llamar al servicio para obtener las actividades de inicio de sesión paginadas
    Page<UserLoginActivityResponseDTO> uLoginDTOs = uLoginActivityService
        .findAllSessionsPaginated(pageNumber, pageSize);

    return ResponseEntity.ok(uLoginDTOs);
  }

  /**
   * Endpoint para obtener actividades de inicio de sesión de un usuario
   * específico filtradas por su estado.
   *
   * Este método permite que los administradores (ROLE_ADMIN) obtengan las
   * actividades de inicio de sesión de un usuario en particular, filtrando por el
   * estado de la sesión (por ejemplo, 'SUCCESS' o 'FAILURE').
   *
   * @param userId El ID del usuario cuyas actividades de inicio de sesión se
   *               desean obtener.
   * @param status El estado de la sesión a filtrar ('SUCCESS', 'FAILURE').
   * @return Una respuesta HTTP 200 OK que contiene una lista de objetos
   *         `UserLoginActivityResponseDTO` que representan las actividades de
   *         inicio de sesión del usuario filtradas por estado.
   */
  @GetMapping("/get-by-userId/{userId}/status/{status}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<UserLoginActivityResponseDTO>> findByUserIdAndSessionStatus(
      @PathVariable Long userId,
      @PathVariable String status) {

    List<UserLoginActivityResponseDTO> uLoginDTOs = uLoginActivityService
        .findByUserIdAndSessionStatus(userId, status);

    return ResponseEntity.ok(uLoginDTOs);
  }

  /**
   * Endpoint para obtener actividades de inicio de sesión de un usuario en un
   * rango de fechas.
   *
   * Este método permite a los administradores (ROLE_ADMIN) obtener actividades de
   * inicio de sesión de un usuario específico en un rango de tiempo determinado.
   *
   * @param userId          El ID del usuario cuyas actividades de inicio de
   *                        sesión se desean obtener.
   * @param startTimeString La fecha y hora de inicio del rango (en formato ISO).
   * @param endTimeString   La fecha y hora de fin del rango (en formato ISO).
   * @return Una respuesta HTTP 200 OK que contiene una lista de objetos
   *         `UserLoginActivityResponseDTO` con las actividades de inicio de
   *         sesión del usuario en el rango de fechas especificado.
   * @throws IllegalArgumentException Si las fechas no tienen un formato válido.
   */
  @GetMapping("/get-by-userId/{userId}/start/{startTimeString}/end/{endTimeString}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<UserLoginActivityResponseDTO>> findByUserIdAndStartTimeBetween(
      @PathVariable Long userId,
      @PathVariable String startTimeString,
      @PathVariable String endTimeString) {

    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(startTimeString) ||
        !appUtility.isValidDateFormat(endTimeString)) {
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }

    // Convertir el string en un objeto Instant
    Instant startTime = Instant.parse(appUtility.convertToUtcString(startTimeString));
    Instant endTime = Instant.parse(appUtility.convertToUtcString(endTimeString));

    // Llamar al servicio para obtener las actividades de inicio de sesión del
    // usuario en el rango de fechas
    List<UserLoginActivityResponseDTO> uLoginDTOs = uLoginActivityService
        .findByUserIdAndSessionTimeBetween(userId, startTime, endTime);

    return ResponseEntity.ok(uLoginDTOs);
  }

  /**
   * Endpoint para obtener actividades de inicio de sesión filtradas por dirección
   * IP y estado de sesión.
   * 
   * Este método permite a los administradores (ROLE_ADMIN) obtener actividades de
   * inicio de sesión filtradas por dirección IP y estado de la sesión (por
   * ejemplo, 'SUCCESS', 'FAILURE').
   * 
   * @param ipAddress     La dirección IP desde la cual se intentó o se inició
   *                      sesión.
   * @param sessionStatus El estado de la sesión, que puede ser 'SUCCESS' o
   *                      'FAILURE'.
   * @return Una respuesta HTTP 200 OK que contiene una lista de objetos
   *         `UserLoginActivityResponseDTO` que representan las actividades de
   *         inicio de sesión asociadas con la dirección IP y el estado de sesión
   *         especificados.
   */
  @GetMapping("/get-by-ipAddress/{ipAddress}/status/{sessionStatus}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<UserLoginActivityResponseDTO>> findByIpAddressAndSessionStatus(
      @PathVariable String ipAddress,
      @PathVariable String sessionStatus) {

    // Llama al servicio para obtener las actividades de inicio de sesión del
    // usuario filtradas por estado
    List<UserLoginActivityResponseDTO> uLoginDTOs = uLoginActivityService
        .findByIpAddressAndSessionStatus(ipAddress, sessionStatus);

    return ResponseEntity.ok(uLoginDTOs);
  }

  /**
   * Endpoint para obtener las actividades de inicio de sesión más recientes de un
   * usuario específico.
   * 
   * Este método permite a los administradores (ROLE_ADMIN) obtener un conjunto
   * paginado de actividades de inicio de sesión de un usuario, ordenadas en orden
   * descendente por el tiempo de inicio de sesión.
   * 
   * @param userId     El ID del usuario para el cual se desean recuperar las
   *                   actividades de inicio de sesión.
   * @param pageNumber El número de página a recuperar (valor por defecto: 0).
   * @param pageSize   El tamaño de la página, es decir, el número de actividades
   *                   de inicio de sesión por página (valor por defecto: 10).
   * @return Una respuesta HTTP 200 OK que contiene una página de objetos
   *         `UserLoginActivityResponseDTO` que representan las actividades de
   *         inicio de sesión de un usuario específico.
   */
  @GetMapping("/get-by-userId/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Page<UserLoginActivityResponseDTO>> findByUserId(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    // Llama al servicio para obtener las actividades de inicio de sesión más
    // recientes del usuario
    Page<UserLoginActivityResponseDTO> uLoginDTOs = uLoginActivityService
        .findByUserId(userId, pageNumber, pageSize);

    return ResponseEntity.ok(uLoginDTOs);
  }

  /**
   * Endpoint para obtener las actividades de inicio de sesión filtradas por
   * dirección IP y entre dos fechas específicas, de manera paginada.
   * 
   * Este método permite a los administradores (ROLE_ADMIN) obtener un conjunto de
   * actividades de inicio de sesión asociadas a una dirección IP específica en un
   * rango de fechas.
   * 
   * @param ipAddress       Dirección IP desde la cual se realizaron las
   *                        actividades de inicio de sesión.
   * @param startTimeString Fecha de inicio del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @param endTimeString   Fecha de fin del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @param pageNumber      Número de página para la paginación (por defecto es
   *                        0).
   * @param pageSize        Tamaño de la página, es decir, cuántos registros se
   *                        retornarán por página (por defecto es 10).
   * @return Una respuesta HTTP 200 OK con una página de objetos
   *         `UserLoginActivityResponseDTO` que representan las actividades de
   *         inicio de sesión filtradas por la IP y rango de fechas.
   */
  @GetMapping("/get-by-ipAddress/{ipAddress}/startTime/{startTimeString}/endTime/{endTimeString}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Page<UserLoginActivityResponseDTO>> findByIpAddressAndSessionTimeBetween(
      @PathVariable String ipAddress,
      @PathVariable String startTimeString,
      @PathVariable String endTimeString,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(startTimeString) ||
        !appUtility.isValidDateFormat(endTimeString)) {
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }
    // Convertir el string en un objeto Instant
    Instant startTime = Instant.parse(appUtility.convertToUtcString(startTimeString));
    Instant endTime = Instant.parse(appUtility.convertToUtcString(endTimeString));

    // Llama al servicio para obtener las actividades de inicio de sesión filtradas
    // por IP y rango de fechas
    Page<UserLoginActivityResponseDTO> uLoginDTOs = uLoginActivityService
        .findByIpAddressAndSessionTimeBetween(
            ipAddress, startTime, endTime, pageNumber, pageSize);

    return ResponseEntity.ok(uLoginDTOs);
  }

  /**
   * Endpoint para obtener las actividades de inicio de sesión filtradas por
   * User-Agent y entre dos fechas específicas, de manera paginada.
   * 
   * Este método permite a los administradores (ROLE_ADMIN) obtener un conjunto de
   * actividades de inicio de sesión asociadas a un User-Agent específico en un
   * rango de fechas.
   * 
   * @param userAgent       El User-Agent asociado a las actividades de inicio de
   *                        sesión (por ejemplo, un navegador o aplicación
   *                        específica).
   * @param startTimeString Fecha de inicio del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @param endTimeString   Fecha de fin del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @param pageNumber      Número de página para la paginación (por defecto es
   *                        0).
   * @param pageSize        Tamaño de la página, es decir, cuántos registros se
   *                        retornarán por página (por defecto es 10).
   * @return Una respuesta HTTP 200 OK con una página de objetos
   *         `UserLoginActivityResponseDTO` que representan las actividades de
   *         inicio de sesión filtradas por el User-Agent y rango de fechas.
   */
  @GetMapping("/get-by-userAgent")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Page<UserLoginActivityResponseDTO>> findByUserAgentAndSessionTimeBetween(
      @RequestParam String userAgent,
      @RequestParam String startTimeString,
      @RequestParam String endTimeString,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(startTimeString) ||
        !appUtility.isValidDateFormat(endTimeString)) {
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }
    // Convertir el string en un objeto Instant
    Instant startTime = Instant.parse(appUtility.convertToUtcString(startTimeString));
    Instant endTime = Instant.parse(appUtility.convertToUtcString(endTimeString));

    // Llama al servicio para obtener las actividades de inicio de sesión filtradas
    // por User-Agent y rango de fechas
    Page<UserLoginActivityResponseDTO> uLoginDTOs = uLoginActivityService
        .findByUserAgentAndSessionTimeBetween(
            userAgent, startTime, endTime, pageNumber, pageSize);

    return ResponseEntity.ok(uLoginDTOs);
  }

  /**
   * Endpoint para contar el número de actividades de inicio de sesión para un
   * usuario específico basado en el estado de la sesión (SUCCESS o FAILURE).
   *
   * @param userId  El identificador único del usuario cuya actividad de inicio de
   *                sesión se va a contar.
   * @param session El estado de la sesión a filtrar, como "SUCCESS" o "FAILURE".
   * @return Un ResponseEntity que contiene el número de sesiones que coinciden
   *         con los criterios especificados.
   */
  @GetMapping("/count-by-userId/{userId}/status/{status}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Long> countByUserIdAndSessionStatus(
      @PathVariable Long userId,
      @PathVariable String status) {
    Long sessions = uLoginActivityService.countByUserIdAndSessionStatus(userId, status);

    return ResponseEntity.ok(sessions);
  }

  /**
   * Endpoint para eliminar todas las actividades de inicio de sesión de un
   * usuario específico basadas en el estado de la sesión (por ejemplo, "SUCCESS"
   * o "FAILURE").
   *
   * @param userId  El identificador único del usuario cuyas actividades de inicio
   *                de sesión serán eliminadas.
   * @param session El estado de la sesión a eliminar, como "SUCCESS" o "FAILURE".
   * @return Un ResponseEntity con un mensaje de confirmación.
   */
  @DeleteMapping("/delete-by-userId/{userId}/status/{status}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> deleteByUserIdAndSessionStatus(
      @PathVariable Long userId,
      @PathVariable String status) {
    // Llama al servicio para eliminar las sesiones del usuario especificado
    uLoginActivityService.deleteByUserIdAndSessionStatus(userId, status);

    return ResponseEntity.ok("Las sesiones han sido eliminadas");
  }

}
