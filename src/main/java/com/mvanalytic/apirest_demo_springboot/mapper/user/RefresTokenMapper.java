package com.mvanalytic.apirest_demo_springboot.mapper.user;

import com.mvanalytic.apirest_demo_springboot.domain.user.RefreshToken;
import com.mvanalytic.apirest_demo_springboot.dto.user.RefreshTokenResponseDTO;
import java.time.Instant;

public class RefresTokenMapper {

  /**
   * Método estático que mapea un objeto de tipo RefreshToken a su correspondiente
   * RefreshTokenResponseDTO.
   * 
   * @param refreshToken El objeto RefreshToken que contiene la información del
   *                     token de actualización y el usuario asociado.
   * @return Un objeto RefreshTokenResponseDTO con los datos mapeados.
   */
  public static RefreshTokenResponseDTO convertToRefreshTokenResponseDTO(RefreshToken refreshToken) {
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
  }

}
