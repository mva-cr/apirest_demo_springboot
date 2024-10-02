package com.mvanalytic.apirest_demo_springboot.services.user;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserSession;
import com.mvanalytic.apirest_demo_springboot.repositories.user.UserSessionRepository;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;
import java.util.UUID;

@Service
public class UserSessionServiceImpl {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(UserSessionServiceImpl.class);

  @Autowired
  private UserSessionRepository userSessionRepository;

  /**
   * Guarda una nueva sesión de usuario en la base de datos.
   *
   * Este método intenta guardar un registro de sesión de usuario en la tabla
   * `user_session`.
   * Si ocurre algún error durante la operación de guardado, se lanza una
   * excepción con un mensaje de error específico.
   *
   * @param userSession La entidad de sesión de usuario que se va a guardar en la
   *                    base de datos.
   * @throws IllegalArgumentException Si ocurre un error al intentar guardar la
   *                                  sesión.
   */
  public void saveUserSession(UserSession userSession) {
    try {
      // Si idSession es nulo, generar un UUID
      if (userSession.getIdSession() == null) {
        userSession.setIdSession(UUID.randomUUID().toString());
      }
      userSessionRepository.save(userSession);
    } catch (Exception e) {
      logger.error("Error al intentar guardar sesión del usuario {}", e.getMessage(), e);
      throw new IllegalArgumentException("172, Error al intentar guardar sesión del usuario");
    }
  }

  public void logOutSession(String idSession) {
    try {
      // Actualizar el estado de la sesión a "LOGGED_OUT"
      userSessionRepository.logOutSession(idSession);
    } catch (Exception e) {
      throw new RuntimeException("Error al cerrar la sesión", e);
    }
  }

}
