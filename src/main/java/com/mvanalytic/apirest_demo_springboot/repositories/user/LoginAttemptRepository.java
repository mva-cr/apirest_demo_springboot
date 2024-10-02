package com.mvanalytic.apirest_demo_springboot.repositories.user;

import org.apache.logging.log4j.core.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
// import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.mvanalytic.apirest_demo_springboot.domain.user.LoginAttempt;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

  /**
   * Método para obtener una lista de intentos de inicio de sesión basados en el
   * correo electrónico del usuario. Este método utiliza una consulta
   * personalizada de JPQL que selecciona todos los intentos de inicio de sesión
   * asociados con el correo electrónico del usuario proporcionado.
   *
   * @param email El correo electrónico del usuario cuyos intentos de inicio de
   *              sesión se desean recuperar.
   * @return Una lista de objetos LoginAttempt asociados con el usuario que tiene
   *         el correo electrónico dado. Si no se encuentran intentos, la lista
   *         estará vacía.
   */
  // TODO: pendiente
  @Query("SELECT la FROM LoginAttempt la WHERE la.user.email = :email")
  List<LoginAttempt> findByUserEmail(String email);

  /**
   * Método para eliminar todos los intentos de inicio de sesión anteriores a una
   * fecha específica para un usuario determinado. Este método es útil para
   * limpiar intentos de inicio de sesión fallidos o antiguos de un usuario en
   * particular.
   *
   * @param userId      El ID del usuario cuyos intentos de inicio de sesión se
   *                    desean eliminar.
   * @param attemptTime La fecha y hora límite; todos los intentos de inicio de
   *                    sesión anteriores a este valor serán eliminados.
   */
  // TODO: pendiente
  void deleteByUserIdAndAttemptTimeBefore(Long userId, Instant attemptTime);

  /**
   * Método para eliminar todos los intentos de inicio de sesión anteriores a una
   * fecha específica para todos los usuarios. Este método es útil para limpiar
   * intentos de inicio de sesión fallidos o antiguos a nivel global en la
   * aplicación.
   *
   * @param attemptTime La fecha y hora límite; todos los intentos de inicio de
   *                    sesión anteriores a este valor serán eliminados para todos
   *                    los usuarios.
   */
  // TODO: pendiente
  void deleteByAttemptTimeBefore(Instant attemptTime);

  /**
   * Método que cuenta la cantidad de intentos de inicio de sesión fallidos para
   * un usuario específico dentro de un rango de tiempo determinado. Este método
   * es útil para monitorear el comportamiento de inicio de sesión de un usuario y
   * aplicar medidas de seguridad, como bloqueos temporales tras varios intentos
   * fallidos.
   *
   * @param userId    El ID del usuario para el cual se cuentan los intentos
   *                  fallidos.
   * @param startTime La fecha y hora de inicio del rango en el que se quiere
   *                  contar los intentos fallidos.
   * @param endTime   La fecha y hora de fin del rango en el que se quiere contar
   *                  los intentos fallidos.
   * @return El número total de intentos de inicio de sesión fallidos para el
   *         usuario en el rango de tiempo especificado.
   */
  // TODO: pendiente
  @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.user.id = :userId AND la.attemptResult = 'FAILED' AND la.attemptTime >= :startTime AND la.attemptTime <= :endTime")
  Long countFailedAttemptsByUserIdAndTimeRange(Long userId, Instant startTime, Instant endTime);

  /**
   * Método que obtiene una lista paginada de intentos de inicio de sesión para un
   * usuario específico filtrados por el resultado del intento (por ejemplo,
   * 'SUCCESS' o 'FAILED'). Los intentos se ordenan de manera descendente según la
   * fecha y hora del intento más reciente.
   * 
   * Este método es útil para obtener un resumen de los intentos de inicio de
   * sesión exitosos o fallidos de un usuario, permitiendo al administrador ver un
   * historial reciente de sus actividades de inicio de sesión.
   *
   * @param userId        El ID del usuario para el cual se están buscando los
   *                      intentos de inicio de sesión.
   * @param attemptResult El resultado del intento de inicio de sesión, ya sea
   *                      'SUCCESS' o 'FAILED'.
   * @param pageable      Objeto de paginación que especifica el número de
   *                      registros por página y el número de página.
   * @return Lista paginada de intentos de inicio de sesión filtrados por usuario
   *         y resultado del intento.
   */
  // TODO: pendiente
  @Query("SELECT la FROM LoginAttempt la WHERE la.user.id = :userId AND la.attemptResult = :attemptResult ORDER BY la.attemptTime DESC")
  List<LoginAttempt> findLoginAttemptsByUserIdAndAttemptResult(
      @Param("userId") Long userId,
      @Param("attemptResult") String attemptResult,
      Pageable pageable);

}
