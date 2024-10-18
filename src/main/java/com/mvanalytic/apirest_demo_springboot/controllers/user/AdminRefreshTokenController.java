package com.mvanalytic.apirest_demo_springboot.controllers.user;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.RefreshTokenResponseDTO;
import com.mvanalytic.apirest_demo_springboot.services.user.RefreshTokenService;
import com.mvanalytic.apirest_demo_springboot.services.user.UserService;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;

/**
 * Controlador para gestionar operaciones CRUD de los Refresh Token.
 * Este controlador solo puede ser accedido por usuarios con el rol
 * "ROLE_ADMIN".
 */
@RestController
@RequestMapping("/api/admin/refresh-tokens")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminRefreshTokenController {

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  UserService userService;

  @Autowired
  private AppUtility appUtility;

  /**
   * Endpoint que permite al administrador obtener todos los tokens de
   * actualización (RefreshToken) almacenados en la base de datos.
   *
   * Este método está diseñado para ser accedido a través de una petición HTTP
   * GET, y devuelve una lista de todos los tokens de actualización que existen en
   * la base de datos. Utiliza el servicio `refreshTokenService` para obtener los
   * tokens y devolver una respuesta HTTP 200 con la lista de tokens en el cuerpo
   * de la respuesta.
   *
   * @return ResponseEntity<List<RefreshToken>> - Una respuesta HTTP con el estado
   *         200 (OK) y la lista de tokens de actualización.
   * @throws IllegalArgumentException - Si ocurre algún error durante la
   *                                  recuperación de los RefreshToken.
   */
  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<RefreshTokenResponseDTO>> getAllTokens() {
    List<RefreshTokenResponseDTO> tokens = refreshTokenService.getAllTokens();
    return ResponseEntity.ok(tokens);
  }

  /**
   * Endpoint para obtener todos los RefreshTokens que expiran entre dos fechas
   * especificadas.
   * 
   * @param startDateString La fecha de inicio del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @param endDateString   La fecha de fin del rango en formato ISO
   *                        (yyyy-MM-ddTHH:mm).
   * @return Una lista de objetos RefreshTokenResponseDTO que representan los
   *         tokens que expiran dentro del rango especificado.
   * @throws IllegalArgumentException Si las fechas no tienen un formato válido o
   *                                  si ocurre algún error en la lógica del
   *                                  servicio.
   */
  @GetMapping("/expiry-from/{startDateString}/to/{endDateString}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<RefreshTokenResponseDTO>> getRefreshTokenExpiryDateBetween(
      @PathVariable String startDateString,
      @PathVariable String endDateString) {
    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(startDateString) ||
        !appUtility.isValidDateFormat(endDateString)) {
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }

    // Convertir las fechas a formato UTC
    Instant startDate = Instant.parse(appUtility.convertToUtcString(startDateString));
    Instant endDate = Instant.parse(appUtility.convertToUtcString(endDateString));

    // Buscar los RefreshTokens en el rango de fechas y devolver la respuesta
    List<RefreshTokenResponseDTO> tokens = refreshTokenService.findByExpiryDateBetween(startDate, endDate);
    return ResponseEntity.ok(tokens);
  }

  /**
   * Endpoint para obtener un RefreshToken específico basado en su ID de token.
   * Solo accesible por usuarios con el rol de 'ROLE_ADMIN'.
   *
   * @param idToken El ID del token de refresco que se desea obtener.
   * @return Un objeto ResponseEntity que contiene el RefreshTokenResponseDTO
   *         correspondiente al ID proporcionado, o un mensaje de error si el
   *         token no es encontrado.
   * @throws IllegalArgumentException Si no se encuentra el token de refresco con
   *                                  el ID proporcionado.
   */
  @GetMapping("/by-idToken/{idToken}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<RefreshTokenResponseDTO> getRefreshTokenByID(
      @PathVariable Long idToken) {
    RefreshTokenResponseDTO token = refreshTokenService.getRefreshTokenDTOById(idToken);
    return ResponseEntity.ok(token);
  }

  /**
   * Endpoint para obtener un RefreshToken específico basado en su valor de token.
   * Solo accesible por usuarios con el rol de 'ROLE_ADMIN'.
   *
   * @param token El valor del token de refresco que se desea obtener.
   * @return Un objeto ResponseEntity que contiene el RefreshTokenResponseDTO
   *         correspondiente al token proporcionado, o un mensaje de error si el
   *         token no es encontrado.
   * @throws IllegalArgumentException Si no se encuentra el token de refresco con
   *                                  el valor proporcionado.
   */
  @GetMapping("/by-token/{token}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<RefreshTokenResponseDTO> getRefreshTokenByToken(
      @PathVariable String token) {
    RefreshTokenResponseDTO refreshTokenResponseDTO = refreshTokenService.getRefreshTokenByToken(token);
    return ResponseEntity.ok(refreshTokenResponseDTO);
  }

  /**
   * Endpoint para obtener un RefreshToken específico basado en el ID del usuario.
   * Solo accesible por usuarios con el rol de 'ROLE_ADMIN'.
   *
   * @param idUser El ID del usuario cuyo token de refresco se desea obtener.
   * @return Un objeto ResponseEntity que contiene el RefreshTokenResponseDTO
   *         correspondiente al usuario proporcionado, o un mensaje de error si no
   *         se encuentra el token.
   * @throws IllegalArgumentException Si no se encuentra el usuario o el token de
   *                                  refresco para ese usuario.
   */
  @GetMapping("/by-idUser/{idUser}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<RefreshTokenResponseDTO> getRefreshTokenByIdUser(
      @PathVariable Long idUser) {
    User user = userService.getUserById(idUser);
    RefreshTokenResponseDTO refreshTokenResponseDTO = refreshTokenService.getRefreshTokenByUser(user);
    return ResponseEntity.ok(refreshTokenResponseDTO);
  }

  /**
   * Endpoint para eliminar todos los RefreshTokens que han expirado antes de una
   * fecha específica.
   * 
   * formato de fecha: YYYY-MM-DDTHH:MM
   * Detalle del formato:
   * YYYY: Año (4 dígitos).
   * MM: Mes (2 dígitos).
   * DD: Día (2 dígitos).
   * T: Separador que indica el inicio de la parte de la hora.
   * HH: Hora en formato de 24 horas (2 dígitos).
   * MM: Minutos (2 dígitos).
   *
   * @param dateString la fecha límite en formato YYYY-MM-DDTHH:MM (por ejemplo,
   *                   "2024-10-10T16:00").
   * @return ResponseEntity con un mensaje de éxito o un error.
   */
  @DeleteMapping("/delete-expired-before/{dateString}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> deleteExpiredTokens(@PathVariable String dateString) {
    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(dateString)) {
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }
    // Convertir el string en un objeto Instant
    Instant expiryDate = Instant.parse(appUtility.convertToUtcString(dateString));

    // Eliminar los tokens que han expirado antes de la fecha especificada
    refreshTokenService.deleteRefreshTokenByExpirationDateBefore(expiryDate);

    return ResponseEntity.ok("RefreshTokens de actualización anteriores a " + dateString + " han sido eliminados.");
  }

  /**
   * Endpoint para eliminar todos los tokens de refresco en la base de datos. Solo
   * los usuarios con el rol 'ROLE_ADMIN' pueden acceder a este recurso.
   * 
   * @DeleteMapping: Define que este método se accede mediante una solicitud HTTP
   *                 DELETE.
   * @PreAuthorize: Restringe el acceso a este endpoint solo a los usuarios con el
   *                rol 'ROLE_ADMIN'.
   *
   * @return ResponseEntity<String> con un mensaje de éxito si la operación se
   *         realiza correctamente.
   */
  @DeleteMapping("/delete-all")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> deleteAllToken() {
    // Llama al servicio para eliminar todos los tokens de refresco
    refreshTokenService.deleteAllToken();
    return ResponseEntity.ok("RefreshTokens eliminados exitosamente");
  }

  /**
   * Endpoint que elimina un RefreshToken por su ID. Solo los usuarios con el rol
   * de ADMIN pueden acceder a esta operación.
   * 
   * @DeleteMapping: Mapea el endpoint a una solicitud HTTP DELETE.
   * @PreAuthorize: Asegura que solo los usuarios con el rol de "ROLE_ADMIN"
   *                puedan ejecutar esta acción.
   * 
   * @param id El ID del refresh token que se va a eliminar.
   * @return Un mensaje indicando que el token ha sido eliminado exitosamente.
   */
  @DeleteMapping("/delete-by-idToken/{idToken}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> deleteRefreshTokenById(@PathVariable Long idToken) {

    // Envío al servicio para eliminar el refreshToken por su Id
    refreshTokenService.deleteRefreshTokenByIdToken(idToken);

    return ResponseEntity.ok("RefreshToken eliminado exitosamente");
  }

  /**
   * Elimina un refresh token asociado a un usuario específico basado en el ID del
   * usuario.
   * 
   * Este endpoint está protegido por la autorización de ROLE_ADMIN, lo que
   * significa que solo los usuarios con este rol pueden acceder a él. El método
   * elimina el refresh token asociado con el usuario identificado por el
   * parámetro de ruta `idUser`.
   * 
   * @param idUser El ID del usuario cuyo refresh token debe ser eliminado.
   * @return Una respuesta HTTP con un mensaje de éxito si el refresh token fue
   *         eliminado correctamente.
   * @throws IllegalArgumentException Si ocurre un error durante la eliminación
   *                                  del refresh token.
   */
  @DeleteMapping("/delete-by-idUser/{idUser}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> deleteRefreshTokenByUserId(@PathVariable Long idUser) {

    // Envío al servicio para eliminar el refreshToken por su Id
    refreshTokenService.deleteByUserId(idUser);

    return ResponseEntity.ok("RefreshToken eliminado exitosamente");
  }

}
