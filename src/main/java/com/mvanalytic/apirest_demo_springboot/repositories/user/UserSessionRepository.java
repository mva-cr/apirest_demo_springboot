package com.mvanalytic.apirest_demo_springboot.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserSession;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {

  /**
   * Encuentra las sesiones de un usuario específico que tengan un estado
   * determinado.
   *
   * @param userId        El ID del usuario cuyas sesiones se desean buscar.
   * @param sessionStatus El estado de la sesión a buscar, como 'ACTIVE',
   *                      'EXPIRED', o 'LOGGED_OUT'.
   * @return Una lista de objetos {@link UserSession} que coincidan con el userId
   *         y el sessionStatus proporcionados.
   */
  // TODO: pendiente
  List<UserSession> findByUserIdAndSessionStatus(Long userId, String sessionStatus);

  /**
   * Encuentra las sesiones de un usuario específico que comenzaron entre dos
   * fechas dadas.
   *
   * @param userId    El ID del usuario cuyas sesiones se desean buscar.
   * @param startDate La fecha y hora de inicio del intervalo de búsqueda.
   * @param endDate   La fecha y hora de finalización del intervalo de búsqueda.
   * @return Una lista de objetos {@link UserSession} que coincidan con el userId
   *         y cuyos tiempos de inicio estén entre startDate y endDate.
   */
  // TODO: pendiente
  List<UserSession> findByUserIdAndStartTimeBetween(Long userId, Instant startDate, Instant endDate);

  /**
   * Encuentra las sesiones de usuario basadas en la dirección IP y el estado de
   * la sesión.
   *
   * @param ipAddress     La dirección IP desde la cual se inició la sesión.
   * @param sessionStatus El estado de la sesión (por ejemplo, 'ACTIVE',
   *                      'EXPIRED', 'LOGGED_OUT').
   * @return Una lista de objetos {@link UserSession} que coincidan con la
   *         dirección IP proporcionada y el estado de la sesión.
   */
  // TODO: pendiente
  List<UserSession> findByIpAddressAndSessionStatus(String ipAddress, String sessionStatus);

  // TODO: pendiente
  List<UserSession> findTopNByUserIdOrderByStartTimeDesc(Long userId, Pageable pageable);

  /**
   * Encuentra las últimas N sesiones de usuario basadas en el ID del usuario,
   * ordenadas por la hora de inicio de forma descendente.
   *
   * @param userId   El ID del usuario cuyas sesiones se desean recuperar.
   * @param pageable Un objeto {@link Pageable} que permite especificar el número
   *                 de resultados (N) a devolver y la paginación.
   * @return Una lista de las últimas sesiones de usuario {@link UserSession}
   *         ordenadas por la hora de inicio de forma descendente.
   */
  // TODO: pendiente
  void deleteByUserIdAndSessionStatus(Long userId, String sessionStatus);

  /**
   * Encuentra las sesiones de usuario basadas en la dirección IP y en el rango de
   * tiempo de inicio.
   *
   * @param ipAddress La dirección IP desde la cual se realizaron las sesiones.
   * @param startDate La fecha y hora de inicio del rango en el cual buscar
   *                  sesiones.
   * @param endDate   La fecha y hora de fin del rango en el cual buscar sesiones.
   * @return Una lista de sesiones de usuario {@link UserSession} que coinciden
   *         con la dirección IP y el rango de tiempo especificado.
   */
  // TODO: pendiente
  List<UserSession> findByIpAddressAndStartTimeBetween(String ipAddress, Instant startDate, Instant endDate);

  /**
   * Encuentra las sesiones de usuario basadas en el agente de usuario
   * (User-Agent) y en el rango de tiempo de inicio.
   *
   * @param userAgent El User-Agent que identifica el navegador o dispositivo
   *                  desde el cual se realizaron las sesiones.
   * @param startDate La fecha y hora de inicio del rango en el cual buscar
   *                  sesiones.
   * @param endDate   La fecha y hora de fin del rango en el cual buscar sesiones.
   * @return Una lista de sesiones de usuario {@link UserSession} que coinciden
   *         con el User-Agent y el rango de tiempo especificado.
   */
  // TODO: pendiente
  List<UserSession> findByUserAgentAndStartTimeBetween(String userAgent, Instant startDate, Instant endDate);

  /**
   * Cuenta el número de sesiones de usuario basadas en el ID del usuario y el
   * estado de la sesión.
   *
   * @param userId        El ID del usuario cuyas sesiones se van a contar.
   * @param sessionStatus El estado de la sesión (por ejemplo, 'ACTIVE',
   *                      'EXPIRED', 'LOGGED_OUT').
   * @return El número de sesiones de usuario que coinciden con el ID del usuario
   *         y el estado de la sesión.
   */
  // TODO: pendiente
  long countByUserIdAndSessionStatus(Long userId, String sessionStatus);

  /**
   * Actualiza el estado de una sesión de usuario específica a 'LOGGED_OUT' en la
   * base de datos.
   *
   * Este método utiliza una consulta personalizada para modificar el estado de la
   * sesión de usuario en función del ID de la sesión. El estado se cambia a
   * 'LOGGED_OUT', indicando que el usuario ha cerrado sesión.
   *
   * @param idSession El ID de la sesión de usuario que se desea actualizar.
   */
  @Modifying
  @Query("UPDATE UserSession us SET us.sessionStatus = 'LOGGED_OUT' WHERE us.idSession = :idSession")
  void logOutSession(String idSession);

}
