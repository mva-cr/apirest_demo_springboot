package com.mvanalytic.apirest_demo_springboot.services.user;

import org.springframework.stereotype.Service;
import com.mvanalytic.apirest_demo_springboot.domain.user.FailedLoginAttempt;
import com.mvanalytic.apirest_demo_springboot.dto.user.FailedLoginAttemptResponseDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.FailedLoginAttemptMapper;
import com.mvanalytic.apirest_demo_springboot.repositories.user.FailedLoginAttemptRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class FailedLoginAttemptService {

  @Autowired
  private FailedLoginAttemptRepository fRepository;

  /**
   * Guarda un intento de inicio de sesión en la base de datos.
   * <p>
   * Este método se encarga de persistir un objeto {@link FailedLoginAttempt} en
   * la base
   * de datos utilizando el repositorio {@link FailedLoginAttemptRepository}. Si
   * ocurre
   * alguna excepción durante el proceso de guardado, se captura y se lanza una
   * excepción {@link IllegalArgumentException} con un mensaje de error
   * personalizado.
   * </p>
   *
   * @param FailedLoginAttempt El objeto {@link FailedLoginAttempt} que representa
   *                           el intento de inicio de sesión a guardar. Este debe
   *                           contener la información necesaria como el usuario,
   *                           dirección IP, agente de usuario, el resultado del
   *                           intento, etc.
   * 
   * @throws IllegalArgumentException Si ocurre un error al intentar guardar el
   *                                  intento de inicio de sesión en la base de
   *                                  datos.
   * @see FailedLoginAttempt
   * @see FailedLoginAttemptRepository
   */
  public void saveFailedAttempt(FailedLoginAttempt fAttempt) {
    try {
      fRepository.save(fAttempt);
    } catch (Exception e) {
      throw new IllegalArgumentException("166, Error al intentar guardar el registro FailedLoginAttempt");
    }
  }

  /**
   * Método para obtener una lista de intentos fallidos de inicio de sesión
   * basados en el correo electrónico,
   * con soporte para paginación.
   * 
   * @param email      El correo electrónico utilizado en el intento de inicio de
   *                   sesión.
   * @param pageNumber El número de la página (inicia en 0).
   * @param pageSize   El tamaño de la página (cantidad de registros por página).
   * @return Una página de intentos fallidos de inicio de sesión como DTOs.
   */
  public Page<FailedLoginAttemptResponseDTO> findFailedLoginAttemptsByEmailPaginated(
      String email, int pageNumber, int pageSize) {

    // Configurar la paginación
    Pageable pageable = PageRequest.of(
        pageNumber, pageSize, Sort.by("attemptTime").descending());

    // Obtener los resultados paginados desde el repositorio
    Page<FailedLoginAttempt> attemptsPage = fRepository.findByEmail(email, pageable);

    // Convertir los intentos fallidos a DTOs
    Page<FailedLoginAttemptResponseDTO> fPagesDTO = transformToDTO(attemptsPage);

    return fPagesDTO;
  }

  /**
   * Método para obtener una lista de intentos fallidos de inicio de sesión
   * basados en el nickname, con soporte para paginación.
   * 
   * @param nickname   El nickname utilizado en el intento de inicio de sesión.
   * @param pageNumber El número de la página (inicia en 0).
   * @param pageSize   El tamaño de la página (cantidad de registros por página).
   * @return Una página de intentos fallidos de inicio de sesión como DTOs.
   */
  public Page<FailedLoginAttemptResponseDTO> findFailedLoginAttemptsByNicknamePaginated(
      String nickname, int pageNumber, int pageSize) {

    // Configurar la paginación
    Pageable pageable = PageRequest.of(
        pageNumber, pageSize, Sort.by("attemptTime").descending());

    // Obtener los resultados paginados desde el repositorio
    Page<FailedLoginAttempt> attemptsPage = fRepository.findByNickname(nickname, pageable);

    // Convertir los intentos fallidos a DTOs
    Page<FailedLoginAttemptResponseDTO> fPagesDTO = transformToDTO(attemptsPage);

    return fPagesDTO;
  }

  /**
   * Elimina todos los intentos de inicio de sesión
   * (FailedLoginAttemptResponseDTO) de un email específico que ocurrieron antes
   * de una fecha y hora específica.
   *
   * Este método utiliza el repositorio de FailedLoginAttemptRepository para
   * eliminar los intentos de inicio de sesión asociados a un usuario,
   * identificados por su email, cuyo tiempo de intento (attemptTime) sea anterior
   * a la fecha y hora especificada.
   * 
   * @param email       El email del usuario cuyos intentos de inicio de sesión se
   *                    desean eliminar.
   * @param attemptTime La fecha y hora límite; se eliminarán todos los intentos
   *                    previos a esta.
   * 
   * @throws IllegalArgumentException Si ocurre algún error durante el proceso de
   *                                  eliminación.
   */
  @Transactional
  public void deleteByEmailAndAttemptTimeBefore(String email, Instant attemptTime) {
    try {
      fRepository.deleteByEmailAndAttemptTimeBefore(email, attemptTime);
    } catch (Exception e) {
      throw new IllegalArgumentException("208, Error al eliminar los FailedLoginAttempt previos a la fecha del correo");
    }
  }

  /**
   * Elimina todos los intentos de inicio de sesión
   * (FailedLoginAttemptResponseDTO) de un nickname específico que ocurrieron
   * antes
   * de una fecha y hora específica.
   *
   * Este método utiliza el repositorio de FailedLoginAttemptRepository para
   * eliminar los intentos de inicio de sesión asociados a un usuario,
   * identificados por su nickname, cuyo tiempo de intento (attemptTime) sea
   * anterior
   * a la fecha y hora especificada.
   * 
   * @param nickname    El nickname del usuario cuyos intentos de inicio de sesión
   *                    se
   *                    desean eliminar.
   * @param attemptTime La fecha y hora límite; se eliminarán todos los intentos
   *                    previos a esta.
   * 
   * @throws IllegalArgumentException Si ocurre algún error durante el proceso de
   *                                  eliminación.
   */
  @Transactional
  public void deleteByNicknameAndAttemptTimeBefore(String nickname, Instant attemptTime) {
    try {
      fRepository.deleteByNicknameAndAttemptTimeBefore(nickname, attemptTime);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "220, Error al eliminar los FailedLoginAttempt previos a la fecha del nickname");
    }
  }

  /**
   * Elimina todos los intentos de inicio de sesión (FailedLoginAttempt) que
   * ocurrieron antes de una fecha y hora específica.
   *
   * Este método utiliza el repositorio de FailedLoginAttempt para eliminar los
   * intentos de inicio de sesión cuyo tiempo de intento (attemptTime) sea
   * anterior a la fecha y hora especificada.
   * 
   * @param attemptTime La fecha y hora límite; se eliminarán todos los intentos
   *                    previos a esta.
   * 
   * @throws IllegalArgumentException Si ocurre algún error durante el proceso de
   *                                  eliminación.
   */
  @Transactional
  public void deleteByAttemptTimeBefore(Instant attemptTime) {
    try {
      fRepository.deleteByAttemptTimeBefore(attemptTime);
    } catch (Exception e) {
      throw new IllegalArgumentException("207, Error al eliminar los loginAttempt previos a la fecha");
    }
  }

  /**
   * Cuenta el número de intentos de inicio de sesión fallidos basados en el
   * correo electrónico.
   *
   * Este método consulta el repositorio para obtener el número de intentos
   * fallidos de inicio de sesión que coinciden con el correo electrónico
   * proporcionado.
   *
   * @param email El correo electrónico por el cual se desea contar los intentos
   *              fallidos.
   * @return El número total de intentos de inicio de sesión fallidos asociados
   *         con el correo electrónico.
   * @throws IllegalArgumentException Si ocurre un error al realizar la consulta
   *                                  en la base de datos.
   */
  public Long countByEmail(String email) {
    try {
      return fRepository.countByEmail(email);
    } catch (Exception e) {
      throw new IllegalArgumentException("221, Error al contar los intentos de sesión por correo");
    }
  }

  /**
   * Cuenta el número de intentos de inicio de sesión fallidos basados en el
   * nickname.
   *
   * Este método consulta el repositorio para obtener el número de intentos
   * fallidos de inicio de sesión que coinciden con el nickname proporcionado.
   *
   * @param nickname El nombre de usuario (nickname) por el cual se desea contar
   *                 los intentos fallidos.
   * @return El número total de intentos de inicio de sesión fallidos asociados
   *         con el nickname.
   * @throws IllegalArgumentException Si ocurre un error al realizar la consulta
   *                                  en la base de datos.
   */
  public Long countByNickname(String nickname) {
    try {
      return fRepository.countByNickname(nickname);
    } catch (Exception e) {
      throw new IllegalArgumentException("222, Error al contar los intentos de sesión por nickname");
    }
  }

  /**
   * Cuenta el número de intentos de inicio de sesión fallidos basados en el
   * ipAddress.
   *
   * Este método consulta el repositorio para obtener el número de intentos
   * fallidos de inicio de sesión que coinciden con el ipAddress proporcionado.
   *
   * @param ipAddress El nombre de usuario (ipAddress) por el cual se desea contar
   *                  los intentos fallidos.
   * @return El número total de intentos de inicio de sesión fallidos asociados
   *         con el ipAddress.
   * @throws IllegalArgumentException Si ocurre un error al realizar la consulta
   *                                  en la base de datos.
   */
  public Long countByIpAddress(String ipAddress) {
    try {
      return fRepository.countByIpAddress(ipAddress);
    } catch (Exception e) {
      throw new IllegalArgumentException("225, Error al contar los intentos de sesión por ipAddress");
    }
  }

  /**
   * Cuenta el número de intentos de inicio de sesión fallidos por correo
   * electrónico en un rango de tiempo específico.
   *
   * Este método consulta el repositorio para contar los intentos fallidos de
   * inicio de sesión que coinciden con el correo electrónico y que ocurrieron
   * dentro del rango de tiempo especificado.
   *
   * @param email     El correo electrónico por el cual se desea contar los
   *                  intentos fallidos.
   * @param startTime El inicio del rango de tiempo (inclusive).
   * @param endTime   El final del rango de tiempo (inclusive).
   * @return El número total de intentos de inicio de sesión fallidos asociados
   *         con el correo electrónico dentro del rango de tiempo especificado.
   * @throws IllegalArgumentException Si ocurre un error al realizar la consulta
   *                                  en la base de datos.
   */
  public Long countByEmailAndAttemptTimeBetween(
      String email, Instant startTime, Instant endTime) {
    try {
      Long fLong = fRepository.countByEmailAndAttemptTimeBetween(email, startTime, endTime);
      return fLong;
    } catch (Exception e) {
      throw new IllegalArgumentException("223, Error al contar los intentos de sesión por correo por rango");
    }
  }

  /**
   * Cuenta el número de intentos de inicio de sesión fallidos por nickname en un
   * rango de tiempo específico.
   *
   * Este método consulta el repositorio para contar los intentos fallidos de
   * inicio de sesión que coinciden con el nickname y que ocurrieron dentro del
   * rango de tiempo especificado.
   *
   * @param nickname  El nickname por el cual se desea contar los intentos
   *                  fallidos.
   * @param startTime El inicio del rango de tiempo (inclusive).
   * @param endTime   El final del rango de tiempo (inclusive).
   * @return El número total de intentos de inicio de sesión fallidos asociados
   *         con el nickname dentro del rango de tiempo especificado.
   * @throws IllegalArgumentException Si ocurre un error al realizar la consulta
   *                                  en la base de datos.
   */
  public Long countByNicknameAndAttemptTimeBetween(
      String nickname, Instant startTime, Instant endTime) {
    try {
      Long fLong = fRepository.countByNicknameAndAttemptTimeBetween(nickname, startTime, endTime);
      return fLong;
    } catch (Exception e) {
      throw new IllegalArgumentException("224, Error al contar los intentos de sesión por nickname por rango");
    }
  }

  /**
   * Cuenta el número de intentos de inicio de sesión fallidos por ipAddress en un
   * rango de tiempo específico.
   *
   * Este método consulta el repositorio para contar los intentos fallidos de
   * inicio de sesión que coinciden con el ipAddress y que ocurrieron dentro del
   * rango de tiempo especificado.
   *
   * @param ipAddress El ipAddress por el cual se desea contar los intentos
   *                  fallidos.
   * @param startTime El inicio del rango de tiempo (inclusive).
   * @param endTime   El final del rango de tiempo (inclusive).
   * @return El número total de intentos de inicio de sesión fallidos asociados
   *         con el ipAddress dentro del rango de tiempo especificado.
   * @throws IllegalArgumentException Si ocurre un error al realizar la consulta
   *                                  en la base de datos.
   */
  public Long countByIpAddressAndAttemptTimeBetween(
      String ipAddress, Instant startTime, Instant endTime) {
    try {
      Long fLong = fRepository.countByIpAddressAndAttemptTimeBetween(ipAddress, startTime, endTime);
      return fLong;
    } catch (Exception e) {
      throw new IllegalArgumentException("226, Error al contar los intentos de sesión por ipAddress por rango");
    }
  }

  /**
   * Transforma una página de entidades FailedLoginAttempt a una página de DTOs
   * FailedLoginAttemptResponseDTO.
   *
   * Este método utiliza un mapper estático para convertir cada entidad
   * FailedLoginAttempt en su correspondiente DTO FailedLoginAttemptResponseDTO,
   * manteniendo la estructura de paginación.
   *
   * @param fPages Página de entidades FailedLoginAttempt a transformar.
   * @return Página de DTOs FailedLoginAttemptResponseDTO con los datos
   *         transformados.
   * @throws IllegalArgumentException Si ocurre un error durante la conversión de
   *                                  los datos.
   */
  private Page<FailedLoginAttemptResponseDTO> transformToDTO(
      Page<FailedLoginAttempt> fPages) {
    try {
      Page<FailedLoginAttemptResponseDTO> fPagesDTO = fPages
          .map(FailedLoginAttemptMapper::convertFailedLoginAttemptToFailedLoginAttemptResponseDTO);
      return fPagesDTO;
    } catch (Exception e) {
      throw new IllegalArgumentException("219, Error mappear las sesiones");
    }
  }

}
