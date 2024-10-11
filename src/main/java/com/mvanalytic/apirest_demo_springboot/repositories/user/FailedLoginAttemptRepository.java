package com.mvanalytic.apirest_demo_springboot.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.mvanalytic.apirest_demo_springboot.domain.user.FailedLoginAttempt;

@Repository
public interface FailedLoginAttemptRepository extends JpaRepository<FailedLoginAttempt, Long> {

  /**
   * Método para obtener una lista de intentos fallidos de inicio de sesión
   * basados en el correo electrónico,
   * con soporte para paginación.
   *
   * @param email    El correo electrónico utilizado en el intento de inicio de
   *                 sesión.
   * @param pageable El objeto Pageable que define el número de página, el tamaño
   *                 de página y el orden de los resultados.
   * @return Una página de intentos fallidos de inicio de sesión que coinciden con
   *         el correo electrónico dado.
   */
  Page<FailedLoginAttempt> findByEmail(String email, Pageable pageable);

  /**
   * Método para obtener una lista paginada de intentos fallidos de inicio de
   * sesión basados en el nickname.
   *
   * @param nickname El nickname utilizado en el intento de inicio de sesión.
   * @param pageable Parámetros de paginación (número de página, tamaño de página,
   *                 etc.).
   * @return Una página de intentos fallidos que coinciden con el nickname dado.
   */
  Page<FailedLoginAttempt> findByNickname(String nickname, Pageable pageable);

  /**
   * Método para eliminar todos los intentos de inicio de sesión anteriores a una
   * fecha específica basados en el correo electrónico.
   * 
   * @param email       El correo electrónico del usuario.
   * @param attemptTime Fecha límite para eliminar intentos anteriores.
   */
  void deleteByEmailAndAttemptTimeBefore(String email, Instant attemptTime);

  /**
   * Método para eliminar todos los intentos de inicio de sesión anteriores a una
   * fecha específica basados en el nickname.
   * 
   * @param nickname    El nickname del usuario.
   * @param attemptTime Fecha límite para eliminar intentos anteriores.
   */
  void deleteByNicknameAndAttemptTimeBefore(String nickname, Instant attemptTime);

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
  void deleteByAttemptTimeBefore(Instant attemptTime);

  /**
   * Cuenta el número de intentos fallidos de inicio de sesión para un usuario
   * basado en el correo electrónico.
   * 
   * @param email El correo electrónico del usuario.
   * @return El número de intentos fallidos de inicio de sesión.
   */
  Long countByEmail(String email);

  /**
   * Cuenta el número de intentos fallidos de inicio de sesión para un usuario
   * basado en el nickname.
   * 
   * @param nickname El nickname del usuario.
   * @return El número de intentos fallidos de inicio de sesión.
   */
  Long countByNickname(String nickname);

  /**
   * Cuenta el número total de intentos fallidos de inicio de sesión basados en la
   * dirección IP.
   * 
   * @param ipAddress La dirección IP desde la cual se intentó iniciar sesión.
   * @return El número de intentos fallidos de inicio de sesión desde esa IP.
   */
  Long countByIpAddress(String ipAddress);

  /**
   * Cuenta el número de intentos fallidos de inicio de sesión para un usuario
   * basado en el correo electrónico,
   * entre un lapso de tiempo definido.
   * 
   * @param email     El correo electrónico del usuario.
   * @param startTime El tiempo de inicio del rango de búsqueda.
   * @param endTime   El tiempo de finalización del rango de búsqueda.
   * @return El número de intentos fallidos de inicio de sesión en ese periodo.
   */
  Long countByEmailAndAttemptTimeBetween(String email, Instant startTime, Instant endTime);

  /**
   * Cuenta el número de intentos fallidos de inicio de sesión para un usuario
   * basado en el nickname,
   * entre un lapso de tiempo definido.
   * 
   * @param nickname  El nickname del usuario.
   * @param startTime El tiempo de inicio del rango de búsqueda.
   * @param endTime   El tiempo de finalización del rango de búsqueda.
   * @return El número de intentos fallidos de inicio de sesión en ese periodo.
   */
  Long countByNicknameAndAttemptTimeBetween(String nickname, Instant startTime, Instant endTime);

  

  /**
   * Cuenta el número de intentos fallidos de inicio de sesión basados en la
   * dirección IP,
   * en un rango de tiempo específico.
   * 
   * @param ipAddress La dirección IP desde la cual se intentó iniciar sesión.
   * @param startTime El tiempo de inicio del rango de búsqueda.
   * @param endTime   El tiempo de finalización del rango de búsqueda.
   * @return El número de intentos fallidos de inicio de sesión desde esa IP en el
   *         rango de fechas dado.
   */
  Long countByIpAddressAndAttemptTimeBetween(String ipAddress, Instant startTime, Instant endTime);

}
