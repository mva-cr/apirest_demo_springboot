package com.mvanalytic.apirest_demo_springboot.services.user;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mvanalytic.apirest_demo_springboot.domain.user.RefreshToken;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.JwtResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.RefreshTokenResponseDTO;
import com.mvanalytic.apirest_demo_springboot.repositories.user.RefreshTokenRepository;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;
import com.mvanalytic.apirest_demo_springboot.utility.JwtUtils;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.stream.Collectors;
import com.mvanalytic.apirest_demo_springboot.mapper.user.RefresTokenMapper;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserMapper;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los tokens de
 * actualización (refresh tokens). Provee métodos para crear, verificar y
 * eliminar tokens de actualización.
 */
@Service
public class RefreshTokenService {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(RefreshTokenService.class);

  @Autowired
  private AppUtility appUtility;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private JwtUtils jwtUtils;

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Genera un nuevo token de acceso y refresca el token de refresco asociado al
   * id del usuario.
   * 
   * Este método toma el identificador del refresh token, lo verifica para
   * asegurarse de que no haya expirado, y luego genera un nuevo token de acceso
   * (JWT) y un nuevo refresh token. El refresh token existente es actualizado en
   * la base de datos y se devuelve una respuesta que contiene ambos tokens.
   * 
   * @param refreshTokenId El ID del usuario que debe ser verificado y actualizado
   * @return Un objeto JwtResponseDTO que contiene el nuevo token JWT y el nuevo
   *         refresh token.
   * @throws IllegalArgumentException Si hay un error al crear el refresh token o
   *                                  si el token ha expirado.
   */
  @Transactional
  public JwtResponseDTO recreateRefreshTokenByIdUser(Long idUser) {
    try {
      // carga el RefreshToken del id
      RefreshToken refreshToken = getRefreshTokenByIdUser(idUser);

      // carga el user de este refresToken
      User user = refreshToken.getUser();

      // verificar si el refreshToken ha expirado
      verifyExpiration(refreshToken);

      // Genera un nuevo token de acceso (JWT) y un nuevo RefreshToken (UUID)
      String jwt = jwtUtils.generateJwtTokenFromUsername(user);

      // Definir la fecha de expiracion
      Instant expiration = Instant.now().plusMillis(appUtility.getRefreshTokenDurationMs());
      String token = jwtUtils.generateRefreshToken(user.getNickname(), expiration);

      // Actualiza el Token en la base de datos
      refreshToken.setToken(jwt);
      // Establecer la fecha de expiración
      refreshToken.setExpiryDate(expiration);

      // Actualiza el RefreshToken en la base de datos
      refreshToken = saveRefreshToken(refreshToken);

      // transformar el user en JwtResponseDTO
      JwtResponseDTO jwtResponseDTO = UserMapper.convertUserToJwtResponse(user);
      jwtResponseDTO.setToken(jwt);
      jwtResponseDTO.setRefreshToken(token);

      return jwtResponseDTO;
    } catch (Exception e) {
      logger.error("Error al crear el refreshToken del id_user: {}", e.getMessage());
      throw new IllegalArgumentException("204, Error al crear el refreshToken del id_user");
    }
  }

  /**
   * Crea un nuevo refresh token para un usuario especificado y lo guarda en la
   * base de datos.
   * 
   * @param user El objeto de la entidad `User` para el cual se creará el refresh
   *             token.
   * @return El objeto `RefreshToken` creado y guardado en la base de datos.
   * @throws IllegalArgumentException Si ocurre algún error durante la creación o
   *                                  guardado del refresh token.
   */
  @Transactional
  public RefreshToken createRefreshTokenByUser(User user) {
    try {
      // Crear una nueva instancia de RefreshToken
      RefreshToken refreshToken = new RefreshToken();

      // Asociar el token con el usuario mediante el ID
      refreshToken.setUser(user);

      // Definir la fecha de expiracion
      Instant expiration = Instant.now().plusMillis(appUtility.getRefreshTokenDurationMs());

      // Establecer la fecha de expiración
      refreshToken.setExpiryDate(expiration);

      // Generar el token utilizando el username del usuario
      String token = jwtUtils.generateRefreshToken(user.getUsername(), expiration);
      refreshToken.setToken(token);

      return refreshToken;
    } catch (Exception e) {
      logger.error("Error al crear el refreshToken del user: {}", e.getMessage());
      throw new IllegalArgumentException("203, Error al crear el refreshToken del user");
    }
  }

  /**
   * Guarda un nuevo refresh token en la base de datos para el usuario
   * proporcionado.
   * 
   * Este método primero elimina cualquier refresh token existente asociado al
   * usuario para asegurar que solo haya un refresh token activo por usuario.
   * Luego, guarda el nuevo refresh token.
   * 
   * @param refreshToken El objeto RefreshToken que se va a guardar.
   * @return El refresh token recién guardado.
   * @throws IllegalArgumentException Si ocurre un error al guardar el token.
   */
  @Transactional
  public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
    try {
      // Guardar el token en la base de datos
      deleteRefreshTokenByUser(refreshToken.getUser());

      // Limpiar el estado de persistencia de la sesión para evitar conflictos
      entityManager.flush();
      entityManager.clear();

      return refreshTokenRepository.save(refreshToken);
    } catch (Exception e) {
      logger.error("Error al guardar el RefreshToken: {}", e.getMessage());
      throw new IllegalArgumentException("178, Error al guardar el RefreshToken");
    }
  }

  /**
   * Elimina el refresh token asociado a un usuario específico.
   * 
   * Este método elimina cualquier refresh token existente vinculado al usuario
   * proporcionado. Es utilizado principalmente antes de crear y guardar un nuevo
   * refresh token para el usuario.
   * 
   * @param user El objeto User cuyo refresh token será eliminado.
   * @throws IllegalArgumentException Si ocurre un error al eliminar el token.
   */
  @Transactional
  public void deleteRefreshTokenByUser(User user) {
    try {
      refreshTokenRepository.deleteByUser(user);
    } catch (Exception e) {
      logger.error("Error al eliminar el RefreshToken: {}", e.getMessage());
      throw new IllegalArgumentException("179, Error al eliminar el RefreshToken");
    }
  }

  /**
   * Verifica si el token de actualización (refresh token) ha expirado.
   * Si ha expirado, se elimina el token de la base de datos y se lanza una
   * excepción.
   *
   * @param token El objeto RefreshToken a verificar.
   * @throws IllegalArgumentException Si el refresh token ha expirado.
   */
  public void verifyExpiration(RefreshToken token) {
    try {
      // Verifica si la fecha de expiración del token es anterior al tiempo actual
      if (token.getExpiryDate().isBefore(Instant.now())) {
        // Elimina el token de la base de datos si ha expirado
        refreshTokenRepository.delete(token);
        // Lanza una excepción indicando que el refresh token ha expirado
        throw new IllegalArgumentException("177, Refresh token ha expirado");
      }
    } catch (Exception e) {
      logger.error("Error al verificar la expiración del refresh token: {}", e.getMessage());
      throw new IllegalArgumentException("180, Error al verificar la expiración del refresh token");
    }
  }

  /**
   * Elimina un refresh token de la base de datos. Este método se utiliza para
   * eliminar un refresh token específico del repositorio. Si ocurre algún error
   * durante el proceso de eliminación, se captura la excepción y se lanza un
   * nuevo IllegalArgumentException con un mensaje descriptivo.
   *
   * @param refreshToken El objeto RefreshToken que se desea eliminar de la base
   *                     de datos.
   * @throws IllegalArgumentException Si ocurre algún error al intentar eliminar
   *                                  el token.
   */
  @Transactional
  public void deleteRefreshToken(RefreshToken refreshToken) {
    try {
      refreshTokenRepository.delete(refreshToken);
    } catch (Exception e) {
      logger.error("Error al eliminar el RefreshToken: {}", e.getMessage());
      throw new IllegalArgumentException("179, Error al eliminar el RefreshToken");
    }
  }

  /**
   * Elimina un token de refresco específico de la base de datos por su ID. Este
   * método está dentro de una transacción para asegurar que la operación se
   * realice correctamente.
   * 
   * @Transactional: Se asegura de que la operación de eliminación esté incluida
   *                 en una transacción.
   * 
   * @param id El ID del refresh token que se va a eliminar.
   * @throws IllegalArgumentException Si ocurre un error durante la eliminación
   *                                  del refresh token.
   */
  @Transactional
  public void deleteRefreshTokenByIdToken(long idToken) {
    try {
      refreshTokenRepository.deleteById(idToken);
    } catch (Exception e) {
      logger.error("Error al eliminar el RefreshToken: {}", e.getMessage());
      throw new IllegalArgumentException("179, Error al eliminar el RefreshToken");
    }
  }

  /**
   * Elimina todos los tokens de la tabla `refresh_token` de la base de datos.
   * Este método se utiliza para realizar una limpieza completa de los tokens de
   * refresco.
   * 
   * @Transactional: Se asegura de que la operación de eliminación se realice
   *                 dentro de una transacción, lo que garantiza la consistencia
   *                 de la base de datos.
   *
   * @throws IllegalArgumentException Si ocurre algún error durante la
   *                                  eliminación, se lanza una excepción con un
   *                                  mensaje explicativo.
   */
  @Transactional
  public void deleteAllToken() {
    try {
      refreshTokenRepository.deleteAll();
    } catch (Exception e) {
      logger.error("Error al eliminar los RefreshTokens: {}", e.getMessage());
      throw new IllegalArgumentException("186, Error al eliminar los RefreshTokens");
    }
  }

  /**
   * Elimina todos los refresh tokens asociados a un usuario específico.
   *
   * @param userId El ID del usuario cuyos refresh tokens se eliminarán.
   * @throws IllegalArgumentException Si ocurre un error al eliminar los tokens.
   */
  @Transactional
  public void deleteByUserId(Long userId) {
    try {
      // Obtener el usuario por ID y eliminar sus tokens de actualización
      refreshTokenRepository.deleteByUser(userService.getUserById(userId));
    } catch (Exception e) {
      // Registrar el error y lanzar una excepción con un mensaje
      logger.error("Error al crear el refreshTokenDurationMs: {}", e.getMessage());
      throw new IllegalArgumentException("176, Error al eliminar los refresh token del id_user");
    }
  }

  /**
   * Obtiene el RefreshTokenResponseDTO asociado a un usuario específico.
   *
   * @param user El objeto `User` cuyo RefreshToken se desea obtener.
   * @return El objeto `RefreshToken` asociado al usuario si existe.
   * @throws IllegalArgumentException Si no se encuentra un RefreshToken asociado
   *                                  al usuario o si ocurre algún error durante
   *                                  la búsqueda.
   */
  public RefreshTokenResponseDTO getRefreshTokenByUser(User user) {
    try {
      RefreshToken refreshToken = refreshTokenRepository.findByUser(user).get();
      return RefresTokenMapper.convertRefreshTokenResponseDTO(refreshToken);
    } catch (Exception e) {
      // Registrar el error y lanzar una excepción con un mensaje
      logger.error("Error al crear el refreshTokenDurationMs: {}", e.getMessage());
      throw new IllegalArgumentException("181, No existe RefreshToken del Usuario");
    }
  }

  /**
   * Obtiene el token de refresco (RefreshToken) por su ID.
   * 
   * Este método busca en el repositorio un token de refresco utilizando el ID
   * proporcionado. Si el token no se encuentra o ocurre algún error durante la
   * búsqueda, se lanza una excepción con un mensaje de error claro.
   * 
   * @param id El ID del token de refresco que se desea obtener.
   * @return El objeto RefreshToken correspondiente al ID proporcionado.
   * @throws IllegalArgumentException Si el token de refresco no se encuentra o
   *                                  ocurre un error durante la búsqueda.
   */
  public RefreshToken getRefreshTokenById(Long id) {
    try {
      RefreshToken refreshToken = refreshTokenRepository.findById(id).get();
      // Forzar la inicialización del usuario si es necesario
      if (!Hibernate.isInitialized(refreshToken.getUser())) {
        Hibernate.initialize(refreshToken.getUser());
      }
      return refreshToken;
    } catch (Exception e) {
      // Registrar el error y lanzar una excepción con un mensaje
      logger.error("Refresh token no encontrado: {}", e.getMessage());
      throw new IllegalArgumentException("177, Refresh token no encontrado");
    }
  }

  /**
   * Obtiene un RefreshTokenResponseDTO basado en el ID del refresh token.
   *
   * @param id El ID del RefreshToken que se desea obtener.
   * @return Un objeto RefreshTokenResponseDTO que contiene los datos del refresh
   *         token asociado al ID proporcionado.
   * @throws IllegalArgumentException Si no se encuentra un refresh token con el
   *                                  ID proporcionado.
   */
  public RefreshTokenResponseDTO getRefreshTokenDTOById(Long id) {
    RefreshToken token = getRefreshTokenById(id);
    return RefresTokenMapper.convertRefreshTokenResponseDTO(token);
  }

  /**
   * Obtiene un refresh token asociado a un usuario específico por su ID.
   * 
   * Este método busca el refresh token relacionado con el usuario cuyo ID es
   * proporcionado. Si el token no es encontrado, se registra el error y se lanza
   * una excepción clara.
   *
   * @param id El ID del usuario para el cual se busca el refresh token.
   * @return El refresh token asociado al usuario.
   * @throws IllegalArgumentException Si no se encuentra el refresh token asociado
   *                                  al id del usuario.
   */
  public RefreshToken getRefreshTokenByIdUser(Long id) {
    try {
      return refreshTokenRepository.findByUserId(id).get();
    } catch (Exception e) {
      // Registrar el error y lanzar una excepción
      logger.error("Refresh token no encontrado: {}", e.getMessage());
      throw new IllegalArgumentException("177, Refresh token no encontrado");
    }
  }

  /**
   * Obtiene un refresh token de la base de datos utilizando su valor de token.
   * Este método busca un refresh token específico en la base de datos mediante su
   * valor de token. Si no se encuentra el token, se captura la excepción y se
   * lanza una IllegalArgumentException con un mensaje descriptivo.
   *
   * @param token El valor del refresh token que se desea buscar.
   * @return El objeto RefreshTokenResponseDTO si se encuentra en la base de
   *         datos.
   * @throws IllegalArgumentException Si el token no es encontrado o si ocurre
   *                                  algún error durante la búsqueda.
   */
  public RefreshTokenResponseDTO getRefreshTokenByToken(String token) {
    try {
      RefreshToken refreshToken = refreshTokenRepository.findByToken(token).get();
      RefreshTokenResponseDTO responseDTO = RefresTokenMapper.convertRefreshTokenResponseDTO(refreshToken);
      return responseDTO;
    } catch (Exception e) {
      // Registrar el error y lanzar una excepción con un mensaje
      logger.error("Refresh token no encontrado: {}", e.getMessage());
      throw new IllegalArgumentException("177, Refresh token no encontrado");
    }
  }

  /**
   * Elimina todos los refresh tokens cuya fecha de expiración es anterior a la
   * fecha especificada. Este método elimina todos los refresh tokens que hayan
   * expirado antes de la fecha proporcionada. Si ocurre un error durante el
   * proceso de eliminación, se captura la excepción y se lanza una
   * IllegalArgumentException con un mensaje claro.
   *
   * @param instant La fecha límite; todos los tokens con fecha de expiración
   *                anterior a esta serán eliminados.
   * @throws IllegalArgumentException Si ocurre un error durante el proceso de
   *                                  eliminación.
   */
  @Transactional
  public void deleteRefreshTokenByExpirationDateBefore(Instant instant) {
    try {
      refreshTokenRepository.deleteByExpiryDateBefore(instant);
    } catch (Exception e) {
      // Registrar el error y lanzar una excepción con un mensaje
      logger.error("Error al intentar eliminar los RefreshToken previos a una fecha: {}", e.getMessage());
      throw new IllegalArgumentException("182, Error al intentar eliminar los RefreshToken previos a una fecha");
    }
  }

  /**
   * Encuentra todos los RefreshTokens cuya fecha de expiración esté entre un
   * rango de fechas especificado, los convierte a objetos RefreshTokenResponseDTO
   * y los devuelve.
   * 
   * @param startDate La fecha y hora de inicio del rango como un objeto Instant.
   * @param endDate   La fecha y hora de finalización del rango como un objeto
   *                  Instant.
   * @return Una lista de objetos RefreshTokenResponseDTO que representan los
   *         tokens que expiran dentro del rango especificado.
   * @throws IllegalArgumentException Si ocurre algún error al intentar cargar los
   *                                  RefreshTokens.
   */
  public List<RefreshTokenResponseDTO> findByExpiryDateBetween(Instant startDate, Instant endDate) {
    try {
      // Carga los RefreshTokens cuya fecha de expiración esté dentro del rango dado
      List<RefreshToken> refreshTokens = refreshTokenRepository.findByExpiryDateBetween(startDate, endDate);
      // Transforma la lista de RefreshToken a RefreshTokenResponseDTO usando el
      // mapper
      return refreshTokens.stream()
          .map(RefresTokenMapper::convertRefreshTokenResponseDTO) // Usar el mapper estático
          .collect(Collectors.toList());
    } catch (Exception e) {
      logger.error("Error al intentar eliminar los RefreshToken previos a una fecha: {}", e.getMessage());
      throw new IllegalArgumentException("187, Error al cargar ResfreshTokens que vencen en el rango especificado");
    }
  }

  /**
   * Este método obtiene todos los tokens de actualización (refresh tokens)
   * almacenados en la base de datos y los convierte a una lista de objetos DTO
   * (Data Transfer Objects) de tipo {@link RefreshTokenResponseDTO}. Se utiliza
   * un mapper estático para realizar la conversión de {@link RefreshToken} a
   * {@link RefreshTokenResponseDTO}.
   *
   * @return una lista de objetos {@link RefreshTokenResponseDTO} que contienen la
   *         información del token, el usuario asociado y la fecha de expiración
   *         de cada refresh token.
   * @throws IllegalArgumentException si ocurre algún error durante el proceso de
   *                                  obtención o conversión de los tokens.
   */
  public List<RefreshTokenResponseDTO> getAllTokens() {
    try {
      // Obtener todos los RefreshTokens desde la base de datos
      List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
      // Convertir la lista de RefreshToken a RefreshTokenResponseDTO utilizando el
      // mapper
      return refreshTokens.stream()
          .map(RefresTokenMapper::convertRefreshTokenResponseDTO) // Usar el mapper estático
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Registrar el error y lanzar una excepción con un mensaje
      logger.error("Error al intentar eliminar los RefreshToken previos a una fecha: {}", e.getMessage());
      throw new IllegalArgumentException("183, Error al cargar todos los refreshToken");
    }
  }

}
