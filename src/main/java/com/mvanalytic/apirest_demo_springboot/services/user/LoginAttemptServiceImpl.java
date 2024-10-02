package com.mvanalytic.apirest_demo_springboot.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mvanalytic.apirest_demo_springboot.domain.user.LoginAttempt;
import com.mvanalytic.apirest_demo_springboot.repositories.user.LoginAttemptRepository;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;

import org.apache.logging.log4j.Logger;

@Service
public class LoginAttemptServiceImpl {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(LoginAttemptServiceImpl.class);

  @Autowired
  private LoginAttemptRepository loginAttemptRepository;

  /**
   * Guarda un intento de inicio de sesión en la base de datos.
   * <p>
   * Este método se encarga de persistir un objeto {@link LoginAttempt} en la base
   * de datos utilizando el repositorio {@link LoginAttemptRepository}. Si ocurre
   * alguna excepción durante el proceso de guardado, se captura y se lanza una
   * excepción {@link IllegalArgumentException} con un mensaje de error
   * personalizado.
   * </p>
   *
   * @param loginAttempt El objeto {@link LoginAttempt} que representa el intento
   *                     de inicio de sesión a guardar. Este debe contener la
   *                     información necesaria como el usuario, dirección IP,
   *                     agente de usuario, el resultado del intento, etc.
   * 
   * @throws IllegalArgumentException Si ocurre un error al intentar guardar el
   *                                  intento de inicio de sesión en la base de
   *                                  datos.
   * @see LoginAttempt
   * @see LoginAttemptRepository
   */
  public void saveAttempt(LoginAttempt loginAttempt) {
    try {
      loginAttemptRepository.save(loginAttempt);
    } catch (Exception e) {
      logger.error("Error al intentar guardar el registro loginAttempt {}", e.getMessage());
      throw new IllegalArgumentException("166, Error al intentar guardar el registro loginAttempt");
    }
  }

}
