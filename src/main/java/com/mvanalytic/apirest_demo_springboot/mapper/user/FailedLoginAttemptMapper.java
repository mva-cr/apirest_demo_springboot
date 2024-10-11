package com.mvanalytic.apirest_demo_springboot.mapper.user;

import org.apache.logging.log4j.Logger;

import com.mvanalytic.apirest_demo_springboot.domain.user.FailedLoginAttempt;
import com.mvanalytic.apirest_demo_springboot.dto.user.FailedLoginAttemptResponseDTO;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;

/**
 * Clase que realiza conversiones entre UserLoginActivity y su DTO's
 */
public class FailedLoginAttemptMapper {
  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(FailedLoginAttemptMapper.class);

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
      logger.error("Error al mappear el FailedLoginAttempt a su DTO {}", e.getMessage());
      throw new IllegalArgumentException("201, Error al mappear el FailedLoginAttempt a su DTO");
    }
  }

}
