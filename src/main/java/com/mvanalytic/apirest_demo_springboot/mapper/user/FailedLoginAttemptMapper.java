package com.mvanalytic.apirest_demo_springboot.mapper.user;

import com.mvanalytic.apirest_demo_springboot.domain.user.FailedLoginAttempt;
import com.mvanalytic.apirest_demo_springboot.dto.user.FailedLoginAttemptResponseDTO;

/**
 * Clase que realiza conversiones entre UserLoginActivity y su DTO's
 */
public class FailedLoginAttemptMapper {

  /**
   * Convierte una entidad FailedLoginAttempt a FailedLoginAttemptResponseDTO
   * 
   * @param FailedLoginAttempt Entidad a convertir
   * @return DTO FailedLoginAttemptResponseDTO
   */
  public static FailedLoginAttemptResponseDTO convertFailedLoginAttemptToFailedLoginAttemptResponseDTO(
      FailedLoginAttempt fAttempt) {
    try {
      FailedLoginAttemptResponseDTO fDto = new FailedLoginAttemptResponseDTO();
      fDto.setIdAttempt(fAttempt.getIdAttempt());
      fDto.setUserEmail(fAttempt.getEmail());
      fDto.setNickname(fAttempt.getNickname());
      fDto.setAttemptTime(fAttempt.getAttemptTime());
      fDto.setIpAddress(fAttempt.getIpAddress());
      fDto.setUserAgent(fAttempt.getUserAgent());
      return fDto;
    } catch (Exception e) {
      throw new IllegalArgumentException("201, Error al mappear el FailedLoginAttempt a su DTO");
    }
  }

}
