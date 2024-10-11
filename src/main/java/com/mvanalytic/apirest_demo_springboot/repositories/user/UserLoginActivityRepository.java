package com.mvanalytic.apirest_demo_springboot.repositories.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserLoginActivity;

@Repository
public interface UserLoginActivityRepository extends JpaRepository<UserLoginActivity, String> {

  /**
   * Recupera todas las sesiones de usuario de la base de datos, pero con
   * paginación.
   * 
   * @param pageable un objeto Pageable que define el tamaño de la página y la
   *                 información de ordenamiento.
   * @return una página de UserLoginActivity.
   */
  @NonNull
  Page<UserLoginActivity> findAll(@NonNull Pageable pageable);

  /**
   * Encuentra las sesiones de un usuario específico que tengan un estado
   * determinado.
   *
   * @param userId        El ID del usuario cuyas sesiones se desean buscar.
   * @param sessionStatus El estado de la sesión a buscar, como 'SUCCESS' o
   *                      'FAILURE'.
   * @return Una lista de objetos {@link UserLoginActivity} que coincidan con el
   *         userId
   *         y el sessionStatus proporcionados.
   */
  List<UserLoginActivity> findByUserIdAndSessionStatus(Long userId, String sessionStatus);

  /**
   * Encuentra las sesiones de un usuario específico que comenzaron entre dos
   * fechas dadas.
   *
   * @param userId    El ID del usuario cuyas sesiones se desean buscar.
   * @param startDate La fecha y hora de inicio del intervalo de búsqueda.
   * @param endDate   La fecha y hora de finalización del intervalo de búsqueda.
   * @return Una lista de objetos {@link UserLoginActivity} que coincidan con el
   *         userId
   *         y cuyos tiempos de inicio estén entre startDate y endDate.
   */
  List<UserLoginActivity> findByUserIdAndSessionTimeBetween(Long userId, Instant startDate, Instant endDate);

  /**
   * Encuentra las sesiones de usuario basadas en la dirección IP y el estado de
   * la sesión.
   *
   * @param ipAddress     La dirección IP desde la cual se inició la sesión.
   * @param sessionStatus El estado de la sesión (por ejemplo, 'SUCCESS' o
   *                      'FAILURE').
   * @return Una lista de objetos {@link UserLoginActivity} que coincidan con la
   *         dirección IP proporcionada y el estado de la sesión.
   */
  List<UserLoginActivity> findByIpAddressAndSessionStatus(String ipAddress, String sessionStatus);

  /**
   * Método para obtener una página de actividades de inicio de sesión de un
   * usuario específico.
   *
   * @param userId   El ID del usuario cuyas actividades de inicio de sesión se
   *                 desean obtener.
   * @param pageable Un objeto Pageable que define los parámetros de paginación
   *                 como el número de página, el tamaño de la página y el orden
   *                 de los resultados.
   * @return Un objeto Page que contiene una lista de actividades de inicio de
   *         sesión (UserLoginActivity) del usuario especificado, limitado por los
   *         parámetros de paginación.
   */
  Page<UserLoginActivity> findByUserId(Long userId, Pageable pageable);

  /**
   * Método para obtener una página de actividades de inicio de sesión filtradas
   * por dirección IP y un rango de fechas especificado.
   *
   * @param ipAddress La dirección IP desde la cual se realizaron las actividades
   *                  de inicio de sesión.
   * @param startDate La fecha y hora de inicio del rango en el cual buscar las
   *                  actividades de inicio de sesión.
   * @param endDate   La fecha y hora de fin del rango en el cual buscar las
   *                  actividades de inicio de sesión.
   * @param pageable  Un objeto Pageable que define los parámetros de paginación,
   *                  como el número de página, el tamaño de la página y el orden
   *                  de los resultados.
   * @return Un objeto Page que contiene una lista de actividades de inicio de
   *         sesión (UserLoginActivity) filtradas por la dirección IP y dentro del
   *         rango de fechas especificado, limitado por los parámetros de
   *         paginación.
   */
  Page<UserLoginActivity> findByIpAddressAndSessionTimeBetween(
      String ipAddress,
      Instant startDate,
      Instant endDate,
      Pageable pageable);

  /**
   * Busca actividades de inicio de sesión basadas en el agente de usuario y un
   * rango de tiempo específico.
   *
   * Este método se utiliza para obtener un listado paginado de actividades de
   * inicio de sesión en función del agente de usuario (`userAgent`) y un rango de
   * tiempo determinado (`startDate` y `endDate`).
   *
   * @param userAgent El agente de usuario (User-Agent) asociado con el intento de
   *                  inicio de sesión. Generalmente es una cadena que describe el
   *                  navegador o dispositivo utilizado por el usuario.
   * @param startDate La fecha de inicio del rango de búsqueda (inclusive). Solo
   *                  se incluirán actividades de inicio de sesión que hayan
   *                  ocurrido en o después de esta fecha.
   * @param endDate   La fecha de fin del rango de búsqueda (inclusive). Solo se
   *                  incluirán actividades de inicio de sesión que hayan ocurrido
   *                  en o antes de esta fecha.
   * @param pageable  Un objeto `Pageable` que contiene la información sobre la
   *                  paginación, como el número de página y el tamaño de página,
   *                  además de las opciones de ordenación.
   *
   * @return Un `Page<UserLoginActivity>` que contiene las actividades de inicio
   *         de sesión que coinciden con los criterios de búsqueda.
   */
  Page<UserLoginActivity> findByUserAgentAndSessionTimeBetween(String userAgent, Instant startDate, Instant endDate,
      Pageable pageable);

  /**
   * Cuenta el número de sesiones de usuario basadas en el ID del usuario y el
   * estado de la sesión.
   *
   * @param userId        El ID del usuario cuyas sesiones se van a contar.
   * @param sessionStatus El estado de la sesión (por ejemplo, 'SUCCESS' o
   *                      'FAILURE'').
   * @return El número de sesiones de usuario que coinciden con el ID del usuario
   *         y el estado de la sesión.
   */
  long countByUserIdAndSessionStatus(Long userId, String sessionStatus);

  /**
   * Elimina todas las sesiones de usuario basadas en su estado de sesión.
   *
   * Este método se utiliza para eliminar todas las sesiones relacionadas con un
   * usuario específico que tengan un estado de sesión determinado. Por ejemplo,
   * se pueden eliminar todas las sesiones con estado "SUCCESS" o "FAILURE" para
   * un
   * usuario en particular.
   *
   * @param userId        El identificador único del usuario (ID) cuyas sesiones
   *                      se quieren eliminar.
   * @param sessionStatus El estado de la sesión que debe coincidir para que la
   *                      sesión sea eliminada. Los valores posibles incluyen
   *                      "SUCCESS", "FAILURE" u otros estados personalizados.
   */
  void deleteByUserIdAndSessionStatus(Long userId, String sessionStatus);

  /**
   * Invoca el procedimiento almacenado 'sp_success_login' para registrar la
   * actividad de inicio de sesión exitosa y actualizar el refresh token.
   *
   * @param userId        ID del usuario que inicia sesión.
   * @param newToken      Nuevo refresh token generado.
   * @param expiryDate    Fecha de expiración del token.
   * @param ipAddress     Dirección IP desde la cual se inició la sesión.
   * @param userAgent     Información del agente de usuario.
   * @param idSession     ID de la sesión de usuario.
   * @param sessionTime   Hora de inicio de la sesión.
   * @param sessionStatus Estado de la sesión (por ejemplo, 'SUCCESS').
   */
  @Procedure("sp_register_successful_login")
  void spRegisterSuccessfulLogin(
      @Param("userId") Long userId,
      @Param("newToken") String newToken,
      @Param("expiryDate") Instant expiryDate,
      @Param("ipAddress") String ipAddress,
      @Param("userAgent") String userAgent,
      @Param("idSession") String idSession,
      @Param("sessionTime") Instant sessionTime,
      @Param("sessionStatus") String sessionStatus);

}
