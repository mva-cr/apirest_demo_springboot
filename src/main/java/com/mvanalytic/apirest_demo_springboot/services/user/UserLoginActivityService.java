package com.mvanalytic.apirest_demo_springboot.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserLoginActivity;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserLoginActivityResponseDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserLoginActivityMapper;
import com.mvanalytic.apirest_demo_springboot.repositories.user.UserLoginActivityRepository;
import java.time.Instant;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLoginActivityService {

  @Autowired
  private UserLoginActivityRepository userLoginActivityRepository;

  /**
   * Guarda una nueva sesión de usuario en la base de datos ya sea 'SUCCESS' o
   * 'FAILURE'.
   *
   * Este método intenta guardar un registro de sesión de usuario en la tabla
   * `user_login_activity` Si ocurre algún error durante la operación de guardado,
   * se lanza una excepción con un mensaje de error específico.
   *
   * @param userLoginActivity La entidad de sesión de usuario que se va a guardar
   *                          en la base de datos.
   * @throws IllegalArgumentException Si ocurre un error al intentar guardar la
   *                                  sesión.
   */
  public void saveLoginActivity(UserLoginActivity userLoginActivity) {
    try {
      userLoginActivityRepository.save(userLoginActivity);
    } catch (Exception e) {
      throw new IllegalArgumentException("172, Error al intentar guardar sesión del usuario");
    }
  }

  /**
   * Método que obtiene todas las sesiones de usuario almacenadas en la base de
   * datos y las transforma a su respectivo DTO (Data Transfer Object) para su
   * exposición en la capa de presentación.
   *
   * @return Una lista de `UserLoginActivityResponseDTO` que contiene todas las
   *         sesiones de usuario.
   * @throws IllegalArgumentException Si ocurre algún error durante la carga o
   *                                  transformación de las sesiones de usuario.
   */
  public List<UserLoginActivityResponseDTO> getAllSession() {
    try {
      // Cargar todas las sesiones de usuario desde el repositorio
      List<UserLoginActivity> userSessions = userLoginActivityRepository.findAll();
      // Transformar las entidades UserSession en sus correspondientes DTO
      List<UserLoginActivityResponseDTO> uResponseDTOs = transformToDTO(userSessions);
      return uResponseDTOs;
    } catch (Exception e) {
      throw new IllegalArgumentException("211, Error al cargar las sesiones");
    }
  }

  /**
   * Método para obtener una página de sesiones de usuario con paginación.
   * 
   * Este método recupera las sesiones de usuario en orden descendente por la
   * fecha de inicio (`sessionTime`), basado en los parámetros de paginación
   * proporcionados (número de página y tamaño de página). Luego, convierte cada
   * objeto `UserLoginActivity` en un `UserLoginActivityResponseDTO` utilizando el
   * mapper.
   * 
   * @param pageNumber El número de la página que se desea obtener (comienza desde
   *                   0).
   * @param pageSize   El número de elementos por página.
   * @return Una página de objetos `UserLoginActivityResponseDTO` que contiene los
   *         resultados paginados.
   * @throws IllegalArgumentException Si ocurre un error durante la carga de
   *                                  sesiones o el proceso de conversión.
   */
  public Page<UserLoginActivityResponseDTO> findAllSessionsPaginated(int pageNumber, int pageSize) {
    try {
      // Configurar la paginación y el orden
      Pageable pageable = PageRequest.of(
          pageNumber, pageSize, Sort.by("sessionTime").descending());

      // Obtener la página de resultados
      Page<UserLoginActivity> sessionsPage = userLoginActivityRepository.findAll(pageable);

      // Convertir cada UserLoginActivity a su DTO correspondiente
      Page<UserLoginActivityResponseDTO> sPage = transformToDTOPage(sessionsPage);

      return sPage;

    } catch (Exception e) {
      throw new IllegalArgumentException("211, Error al cargar las sesiones");
    }
  }

  /**
   * Método que busca sesiones de usuario basadas en el ID del usuario y el estado
   * de la sesión,luego las transforma en su correspondiente DTO (Data Transfer
   * Object) para la exposición.
   *
   * @param userId        El ID del usuario cuyas sesiones se desean buscar.
   * @param sessionStatus El estado de la sesión a filtrar (por ejemplo, 'SUCCESS'
   *                      o 'FAILURE').
   * @return Una lista de `UserLoginActivityResponseDTO` que contiene las sesiones
   *         de
   *         usuario que coinciden con el ID del usuario y el estado de la sesión.
   * @throws IllegalArgumentException Si ocurre algún error durante la búsqueda o
   *                                  transformación de las sesiones de usuario.
   */
  public List<UserLoginActivityResponseDTO> findByUserIdAndSessionStatus(
      Long userId, String sessionStatus) {
    try {
      // Buscar las sesiones de usuario en la base de datos por userId y estado de la
      // sesión
      List<UserLoginActivity> userSessions = userLoginActivityRepository.findByUserIdAndSessionStatus(userId,
          sessionStatus);
      // Transformar las entidades UserSession en sus correspondientes DTO
      List<UserLoginActivityResponseDTO> uResponseDTOs = transformToDTO(userSessions);
      return uResponseDTOs;
    } catch (Exception e) {
      throw new IllegalArgumentException("212, Error buscar la sesión por el userId y status y status");
    }
  }

  /**
   * Método que busca sesiones de usuario basadas en el ID del usuario y un rango
   * de fechas de inicio, luego las transforma en su correspondiente DTO (Data
   * Transfer Object) para la exposición. Este método es útil para obtener
   * información sobre las sesiones iniciadas.
   *
   * @param userId    El ID del usuario cuyas sesiones se desean buscar.
   * @param startDate La fecha de inicio del rango en el cual buscar las sesiones.
   * @param endDate   La fecha de fin del rango en el cual buscar las sesiones.
   * @return Una lista de `UserLoginActivityResponseDTO` que contiene las sesiones
   *         de
   *         usuario que coinciden con el ID del usuario y que fueron iniciadas en
   *         el rango de fechas especificado.
   * @throws IllegalArgumentException Si ocurre algún error durante la búsqueda o
   *                                  transformación de las sesiones de usuario.
   */
  public List<UserLoginActivityResponseDTO> findByUserIdAndSessionTimeBetween(
      Long userId, Instant startDate, Instant endDate) {
    try {
      // Buscar las sesiones de usuario en la base de datos por userId y rango de
      // fechas
      List<UserLoginActivity> userSessions = userLoginActivityRepository.findByUserIdAndSessionTimeBetween(
          userId, startDate, endDate);
      // Transformar las entidades UserLoginActivity en sus correspondientes DTO
      List<UserLoginActivityResponseDTO> uResponseDTOs = transformToDTO(userSessions);
      return uResponseDTOs;
    } catch (Exception e) {
      throw new IllegalArgumentException("213, Error al buscar la sesión por el userId y rango de fechas");
    }
  }

  /**
   * Método que busca sesiones de usuario basadas en la dirección IP y el estado
   * de la sesión, luego las transforma en su correspondiente DTO (Data Transfer
   * Object) para la exposición.
   *
   * @param ipAddress     La dirección IP desde la cual se iniciaron las sesiones.
   * @param sessionStatus El estado de la sesión ('SUCCESS' o 'FAILURE').
   * @return Una lista de `UserLoginActivityResponseDTO` que contiene las sesiones
   *         de
   *         usuario que coinciden con la dirección IP y el estado especificado.
   * @throws IllegalArgumentException Si ocurre algún error durante la búsqueda o
   *                                  transformación de las sesiones de usuario.
   */
  public List<UserLoginActivityResponseDTO> findByIpAddressAndSessionStatus(
      String ipAddress, String sessionStatus) {
    try {
      List<UserLoginActivity> userSessions = userLoginActivityRepository.findByIpAddressAndSessionStatus(
          ipAddress, sessionStatus);
      List<UserLoginActivityResponseDTO> uResponseDTOs = transformToDTO(userSessions);
      return uResponseDTOs;
    } catch (Exception e) {
      throw new IllegalArgumentException("214, Error al buscar la sesión por idAddress y estatus");
    }
  }

  /**
   * Método para buscar actividades de inicio de sesión de un usuario en la base
   * de datos, ordenadas por la hora de sesión en orden descendente y paginadas.
   *
   * @param userId     El ID del usuario cuyas actividades de inicio de sesión se
   *                   desean obtener.
   * @param pageNumber El número de la página a solicitar (inicia en 0).
   * @param pageSize   El número de elementos por página.
   * @return Un objeto Page<UserLoginActivityResponseDTO> que contiene una lista
   *         paginada de las actividades de inicio de sesión del usuario,
   *         transformadas en objetos UserLoginActivityResponseDTO.
   * @throws IllegalArgumentException Si ocurre un error durante la consulta o
   *                                  transformación de los datos.
   */
  public Page<UserLoginActivityResponseDTO> findByUserId(
      Long userId, int pageNumber, int pageSize) {
    try {

      // Configurar la paginación
      Pageable pageable = PageRequest.of(
          pageNumber, pageSize, Sort.by("sessionTime").descending());
      // Buscar las sesiones de usuario en la base de datos por dirección IP y estado
      Page<UserLoginActivity> userSessions = userLoginActivityRepository.findByUserId(
          userId, pageable);
      // Transformar las entidades UserLoginActivity en sus correspondientes DTO
      Page<UserLoginActivityResponseDTO> uResponseDTOs = transformToDTOPage(userSessions);
      return uResponseDTOs;
    } catch (Exception e) {
      throw new IllegalArgumentException("215, Error al buscar la sesión por userId y página");
    }
  }

  /**
   * Método que busca sesiones de usuario basadas en la dirección IP y un rango de
   * fechas de inicio, luego las transforma en su correspondiente DTO (Data
   * Transfer Object) para la exposición.
   *
   * @param ipAddress La dirección IP desde la cual se iniciaron las sesiones.
   * @param startDate La fecha y hora de inicio del rango.
   * @param endDate   La fecha y hora de fin del rango.
   * @return Una lista de `UserLoginActivityResponseDTO` que contiene las sesiones
   *         de
   *         usuario que coinciden con la dirección IP y el rango de fechas
   *         especificado.
   * @throws IllegalArgumentException Si ocurre algún error durante la búsqueda o
   *                                  transformación de las sesiones de usuario.
   */
  public Page<UserLoginActivityResponseDTO> findByIpAddressAndSessionTimeBetween(
      String ipAddress, Instant startDate, Instant endDate, int pageNumber, int pageSize) {
    try {
      // Configurar la paginación
      Pageable pageable = PageRequest.of(
          pageNumber, pageSize, Sort.by("sessionTime").descending());
      // Buscar las sesiones de usuario en la base de datos por dirección IP y rango
      // de fechas
      Page<UserLoginActivity> userSessions = userLoginActivityRepository.findByIpAddressAndSessionTimeBetween(
          ipAddress, startDate, endDate, pageable);
      // Transformar las entidades UserLoginActivity en sus correspondientes DTO
      Page<UserLoginActivityResponseDTO> uResponseDTOs = transformToDTOPage(userSessions);
      return uResponseDTOs;
    } catch (Exception e) {
      throw new IllegalArgumentException("216, Error al buscar la sesión por ipAddress y rango fechas");
    }
  }

  /**
   * Busca actividades de inicio de sesión por agente de usuario (User-Agent) y
   * rango de fechas, y retorna los resultados paginados.
   *
   * Este método se utiliza para obtener un listado paginado de actividades de
   * inicio de sesión en función del User-Agent y un rango de fechas específico,
   * ordenado de forma descendente por la fecha de intento de inicio de sesión.
   *
   * @param userAgent  El agente de usuario (User-Agent) asociado con el intento
   *                   de inicio de sesión. Generalmente describe el navegador o
   *                   dispositivo utilizado por el usuario.
   * @param startDate  La fecha de inicio del rango de búsqueda (inclusive). Solo
   *                   se incluirán actividades que hayan ocurrido en o después de
   *                   esta fecha.
   * @param endDate    La fecha de fin del rango de búsqueda (inclusive). Solo se
   *                   incluirán actividades que hayan ocurrido en o antes de esta
   *                   fecha.
   * @param pageNumber El número de página que se desea obtener, basado en la
   *                   paginación.
   * @param pageSize   El número de registros por página que se desea obtener.
   * @return Una página de objetos `UserLoginActivityResponseDTO` que contienen la
   *         información de las actividades de inicio de sesión que coinciden con
   *         los criterios de búsqueda.
   * @throws IllegalArgumentException Si ocurre algún error durante la consulta o
   *                                  transformación de los datos.
   */
  public Page<UserLoginActivityResponseDTO> findByUserAgentAndSessionTimeBetween(
      String userAgent, Instant startDate, Instant endDate, int pageNumber, int pageSize) {
    try {
      // Configurar la paginación
      Pageable pageable = PageRequest.of(
          pageNumber, pageSize, Sort.by("sessionTime").descending());
      // Buscar las sesiones de usuario en la base de datos por User-Agent y rango de
      // fechas
      Page<UserLoginActivity> userSessions = userLoginActivityRepository.findByUserAgentAndSessionTimeBetween(
          userAgent, startDate, endDate, pageable);
      // Transformar las entidades UserLoginActivity en sus correspondientes DTO
      Page<UserLoginActivityResponseDTO> uResponseDTOs = transformToDTOPage(userSessions);
      return uResponseDTOs;
    } catch (Exception e) {
      throw new IllegalArgumentException("217, Error al buscar la sesión por useAgent y rango fechas");
    }
  }

  /**
   * Método que cuenta el número de sesiones de usuario basadas en el ID de
   * usuario y el estado de la sesión.
   *
   * @param userId        El ID del usuario cuyas sesiones se están contando.
   * @param sessionStatus El estado de la sesión que puede ser, por ejemplo,
   *                      'SUCCESS' o 'FAILURE'.
   * @return Un valor `Long` que representa el número de sesiones que coinciden
   *         con el ID de usuario y el estado de sesión proporcionado.
   * @throws IllegalArgumentException Si ocurre algún error durante el proceso de
   *                                  conteo.
   */
  public Long countByUserIdAndSessionStatus(Long userId, String sessionStatus) {
    try {
      // Contar las sesiones en la base de datos que coinciden con el ID del usuario y
      // el estado de sesión
      Long countSession = userLoginActivityRepository.countByUserIdAndSessionStatus(userId, sessionStatus);
      return countSession;
    } catch (Exception e) {
      throw new IllegalArgumentException("218, Error contar las sesiones según estatus por idUser");
    }
  }

  /**
   * Método que elimina las sesiones de usuario basadas en el ID de usuario y el
   * estado de la sesión.
   * 
   * @param userId        El ID del usuario cuyas sesiones se desean eliminar.
   * @param sessionStatus El estado de la sesión que se desea eliminar (por
   *                      ejemplo, 'SUCCESS' o 'FAILURE').
   * @throws IllegalArgumentException Si ocurre algún error durante la eliminación
   *                                  de las sesiones.
   */
  @Transactional
  public void deleteByUserIdAndSessionStatus(Long userId, String sessionStatus) {
    try {
      // Elimina las sesiones del usuario con el estado especificado
      userLoginActivityRepository.deleteByUserIdAndSessionStatus(userId, sessionStatus);
    } catch (Exception e) {
      throw new IllegalArgumentException("219, Error al borrar sesión por userId y estatus");
    }
  }

  /**
   * Método que transforma una lista de entidades `UserLoginActivity` en una lista
   * de DTOs `UserLoginActivityResponseDTO`.
   * 
   * @param UserLoginActivity Lista de sesiones de usuario (`UserLoginActivity`)
   *                          que se desea convertir a DTOs.
   * @return Una lista de objetos `UserLoginActivityResponseDTO` que representan
   *         las sesiones de usuario.
   * @throws IllegalArgumentException Si ocurre algún error durante la
   *                                  conversiónde las sesiones.
   */
  private List<UserLoginActivityResponseDTO> transformToDTO(
      List<UserLoginActivity> userLoginActivities) {
    try {
      List<UserLoginActivityResponseDTO> uList = userLoginActivities.stream()
          .map(UserLoginActivityMapper::convertUserLoginActivityToUserLoginActivityResponseDTO)
          .collect(Collectors.toList());
      return uList;
    } catch (Exception e) {
      throw new IllegalArgumentException("205, Error al buscar el loginAttempt con el email");
    }
  }

  /**
   * Transforma una página de objetos UserLoginActivity en una página de objetos
   * UserLoginActivityResponseDTO.
   * 
   * Este método convierte cada objeto UserLoginActivity de la página
   * proporcionada en su correspondiente objeto UserLoginActivityResponseDTO
   * utilizando el UserSessionMapper.
   * 
   * @param userSessions La página de objetos UserLoginActivity a convertir.
   * @return Una página de objetos UserLoginActivityResponseDTO con los datos
   *         mapeados.
   * @throws IllegalArgumentException Si ocurre algún error durante el proceso de
   *                                  mapeo.
   */
  private Page<UserLoginActivityResponseDTO> transformToDTOPage(Page<UserLoginActivity> userSessions) {
    try {
      // Convertir la lista de UserLoginActivity a UserLoginActivityResponseDTO
      // utilizando el
      // UserSessionMapper
      Page<UserLoginActivityResponseDTO> uResponseDTOs = userSessions
          .map(UserLoginActivityMapper::convertUserLoginActivityToUserLoginActivityResponseDTO);
      return uResponseDTOs;
    } catch (Exception e) {
      throw new IllegalArgumentException("219, Error mappear las sesiones");
    }
  }

  public void registerSuccessfulLogin(
      Long userId, String newToken, Instant expiryDate, String ipAddress,
      String userAgent, String idSession, Instant sessionTime, String sessionStatus) {
    try {
      userLoginActivityRepository.spRegisterSuccessfulLogin(
          userId, newToken, expiryDate, ipAddress, userAgent, idSession, sessionTime, sessionStatus);
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

}
