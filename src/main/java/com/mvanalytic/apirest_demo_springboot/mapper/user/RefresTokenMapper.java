package com.mvanalytic.apirest_demo_springboot.mapper.user;

import com.mvanalytic.apirest_demo_springboot.domain.user.RefreshToken;
import com.mvanalytic.apirest_demo_springboot.dto.user.RefreshTokenResponseDTO;

public class RefresTokenMapper {


  /**
   * Convierte un objeto `RefreshToken` en un `RefreshTokenResponseDTO`.
   * 
   * Este método toma un objeto de entidad `RefreshToken` y lo transforma en un
   * objeto `RefreshTokenResponseDTO`, que es una versión simplificada que
   * contiene solo la información relevante para las respuestas del controlador.
   * 
   * @param refreshToken El objeto `RefreshToken` que se desea convertir a
   *                     `RefreshTokenResponseDTO`.
   * @return Un objeto `RefreshTokenResponseDTO` con los datos mapeados desde el
   *         `RefreshToken` proporcionado.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión.
   */
  public static RefreshTokenResponseDTO convertRefreshTokenResponseDTO(RefreshToken refreshToken) {
    try {
      // Crear una nueva instancia del DTO
      RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO();

      // Mapear los campos del RefreshToken al DTO
      dto.setId(refreshToken.getId());
      dto.setUserId(refreshToken.getUser().getId());
      dto.setFirstName(refreshToken.getUser().getFirstName());
      dto.setLastName(refreshToken.getUser().getLastName());
      dto.setEmail(refreshToken.getUser().getEmail());
      dto.setNickname(refreshToken.getUser().getNickname());
      dto.setToken(refreshToken.getToken());
      dto.setExpiryDate(refreshToken.getExpiryDate());

      // Devolver el DTO mapeado
      return dto;
    } catch (Exception e) {
      throw new IllegalArgumentException("200, Error al mappear el RefreshToken a su DTO");
    }
  }

}
