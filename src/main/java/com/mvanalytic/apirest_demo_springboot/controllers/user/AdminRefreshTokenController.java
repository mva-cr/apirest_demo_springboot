package com.mvanalytic.apirest_demo_springboot.controllers.user;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.mvanalytic.apirest_demo_springboot.dto.user.RefreshTokenResponseDTO;
import com.mvanalytic.apirest_demo_springboot.services.user.RefreshTokenService;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;

import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/admin/refresh-tokens")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminRefreshTokenController {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(AdminRefreshTokenController.class);

  @Autowired
  private RefreshTokenService refreshTokenService;

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
   * @param dateString la fecha límite en formato ISO (por ejemplo,
   *                   "2024-10-01T00:00:00Z").
   * @return ResponseEntity con un mensaje de éxito o un error.
   */
  @DeleteMapping("/delete-expired-tokens/{dateString}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> deleteExpiredTokens(@PathVariable String dateString) {
    // Validar el formato de la fecha recibida como parámetro
    if (!appUtility.isValidDateFormat(dateString)) {
      logger.error("Error en el formato de la fecha y hora");
      throw new IllegalArgumentException("184, Error en el formato de la fecha y hora");
    }
    // Convertir el string en un objeto Instant
    Instant expiryDate = Instant.parse(appUtility.convertToUtcString(dateString));

    // Eliminar los tokens que han expirado antes de la fecha especificada
    refreshTokenService.deleteRefreshTokenByExpirationDateBefore(expiryDate);

    return ResponseEntity.ok("Tokens de actualización anteriores a " + dateString + " han sido eliminados.");
  }

}
