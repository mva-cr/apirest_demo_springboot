package com.mvanalytic.apirest_demo_springboot.mapper.user;

import com.mvanalytic.apirest_demo_springboot.domain.user.UserLoginActivity;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserLoginActivityResponseDTO;

/**
 * Clase que realiza conversiones entre UserLoginActivity y su DTO's
 */
public class UserLoginActivityMapper {

  /**
   * Convierte un objeto `UserLoginActivity` en un `UserLoginActivityResponseDTO`.
   * 
   * Este método toma un objeto de la entidad `UserLoginActivity` y lo transforma
   * en un objeto `UserLoginActivityResponseDTO`, que es una versión simplificada
   * para las respuestas del controlador, mostrando solo los campos relevantes.
   * 
   * @param UserLoginActivity El objeto `UserLoginActivity` que se desea convertir
   *                          a `UserLoginActivityResponseDTO`.
   * @return Un objeto `UserLoginActivityResponseDTO` con los datos mapeados desde
   *         el `UserLoginActivity` proporcionado.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión.
   */
  public static UserLoginActivityResponseDTO convertUserLoginActivityToUserLoginActivityResponseDTO(
      UserLoginActivity userLoginActivity) {
    try {
      UserLoginActivityResponseDTO dto = new UserLoginActivityResponseDTO();
      dto.setIdSession(userLoginActivity.getIdSession());
      dto.setUserEmail(userLoginActivity.getUser().getEmail());
      dto.setSessionTime(userLoginActivity.getSessionTime());
      dto.setIpAddress(userLoginActivity.getIpAddress());
      dto.setUserAgent(userLoginActivity.getUserAgent());
      dto.setSessionStatus(userLoginActivity.getSessionStatus());

      return dto;
    } catch (Exception e) {
      throw new IllegalArgumentException("202, Error al mappear el UserLoginActivity a su DTO");
    }
  }

}
