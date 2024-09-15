package com.mvanalytic.apirest_demo_springboot.services.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.AuthorityDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserEmailUpdateRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserNicknameUpdateRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserPasswordUpdateRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileUpdateRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRoleUpdateRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserStatusUpdateRequestDTO;
import com.mvanalytic.apirest_demo_springboot.repositories.user.UserRepository;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio para gestionar operaciones CRUD relacionadas con los usuarios.
 * Proporciona métodos para registrar, actualizar, eliminar y obtener usuarios.
 */
@Service
public class UserService {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(UserService.class);

  /**
   * Para cuando se necesita manejar procedimientos almacenados que no se ajustan
   * bien a los métodos simples
   * del repositorio.
   * Permite manejar la sincronización con la base de datos.
   */
  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  @Lazy
  private PasswordEncoder passwordEncoder;

  /**
   * Registra un nuevo usuario en la base de datos.
   * Verifica si el nickname o email ya existen antes de registrar.
   * Codifica la contraseña del usuario antes de guardarlo.
   *
   * @param user El usuario a registrar.
   * @return El usuario registrado.
   */
  public User registerUser(User user) {
    // Verifica si el email y el nickname ya existen
    Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
    Optional<User> existingUserByNickname = userRepository.findByNickname(user.getNickname());

    if (existingUserByEmail.isPresent()) {
      logger.error("El email ingresado ya existe");
      throw new IllegalArgumentException("106, El email ingresado ya existe");
    }

    if (existingUserByNickname.isPresent()) {
      logger.error("El nickname ingresado ya existe");
      throw new IllegalArgumentException("105, El nickname ingresado ya existe");
    }

    // Codifica la contraseña antes de guardarla
    String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword); // Establece el hash de la contraseña
    return userRepository.save(user);
  }

  /**
   * Obtiene un usuario por su id.
   *
   * @param id El id del usuario a buscar.
   * @return El usuario encontrado.
   */
  public User getUserById(Long id) {
    try {
      // Intenta encontrar al usuario por ID
      return userRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("113, El usuario no existe"));
    } catch (EntityNotFoundException e) {
      // Loguea el error con el mensaje de excepción
      logger.error("El usuario no existe con el id: {}", id, e.getMessage());
      // Relanza la excepción para que sea manejada por otros manejadores de
      // excepciones
      throw e;
    }
  }

  /**
   * Obtiene un usuario por su email.
   *
   * @param nickname El nickname del usuario a buscar.
   * @return El usuario encontrado.
   */
  public User getUserByEmail(String email) {
    try {
      // intenta encontrar el usaurio con el email
      return userRepository.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("113, El usuario no existe"));
    } catch (UsernameNotFoundException e) {
      // Loguea el error con el mensaje de excepción
      logger.error("El usuario no existe con el email: {}", email, e);
      // Relanza la excepción para que sea manejada por otros manejadores de
      // excepciones
      throw e;
    }
  }

  /**
   * Obtiene un usuario por su nickname.
   *
   * @param nickname El nickname del usuario a buscar.
   * @return El usuario encontrado.
   */
  // public User getUserByNickName(String nickname) {
  // return userRepository.findByNickname(nickname)
  // .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con
  // el nickname lolita: " + nickname));
  // }

  public User getUserByNickName(String nickname) {
    try {
      // Intenta encontrar el usuario por su nickname
      Optional<User> optionalUser = userRepository.findByNickname(nickname);

      if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        return user;
      } else {
        // Si el usuario no existe, lanza una excepción
        logger.error("El usuario no existe con el nickname: {}", nickname);
        throw new UsernameNotFoundException("113, El usuario no existe");
      }

    } catch (UsernameNotFoundException e) {
      logger.error("Error al buscar usuario por nickname: {}", e.getMessage());
      throw e; // Relanza la excepción para que sea manejada por los manejadores de excepciones
    } catch (Exception e) {
      logger.error("Error inesperado al buscar usuario por nickname: {}", e.getMessage());
      throw new RuntimeException("114, Error inesperado al buscar el usuario", e);
    }
  }

  /**
   * Obtiene una lista de todos los usuarios en la base de datos.
   * 
   * @return Lista de usuarios.
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Actualiza la información de un usuario existente.
   * correspondiente a los atributos: activated y status.
   * Solo ROLE_ADMIN puede acceder a este método
   *
   * @param adminUserDTO El usuario con la información a actualizar.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateStatusUser(UserStatusUpdateRequestDTO userStatusUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID
      getUserById(userStatusUpdateRequestDTO.getId());

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spUpdateUserByRoleAdmin(
          userStatusUpdateRequestDTO.getId(),
          userStatusUpdateRequestDTO.getActivated(),
          userStatusUpdateRequestDTO.getStatus());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userStatusUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error de acceso a datos al actualizar el usuario: {}", e.getMostSpecificCause().getMessage());
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (Exception e) {
      logger.error("Error inesperado al actualizar el usuario: {}", e.getMessage());
      throw new RuntimeException(
          "115, Error inesperado al actualizar el usuario " + e.getMessage());
    }

  }

  /**
   * Actualiza el rol de un usuario existente.
   * Solo ROLE_ADMIN puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateRoleUser(UserRoleUpdateRequestDTO userRoleUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      User user = getUserById(userRoleUpdateRequestDTO.getId());

      // Lista de autoridades del usuario
      Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

      // obtener el primer elemento de la colección de autoridaded
      GrantedAuthority firsAuthority = authorities
          .stream()
          .findFirst()
          .orElse(null);

      String currentAuthority = firsAuthority.getAuthority();

      // Obtén el primer nombre de autoridad incluso carga null si este es su valor
      String authorityName = userRoleUpdateRequestDTO.getAuthorities()
          // Crea un flujo a partir del conjunto
          .stream()
          // Mapea cada AuthorityDTO a su propiedad 'name'
          .map(AuthorityDTO::getName)
          // Obtiene el primer elemento, si existe
          .findFirst()
          // Devuelve null si el flujo está vacío
          .orElse(null);

      // Verificar si la autoridad (Role) ya está asignado al usuario
      if (currentAuthority.equals(authorityName)) {
        throw new IllegalArgumentException("103, El nuevo valor no puede ser igual al valor actual");
      }

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spUdateUserRoleWithStatusCheck(
          userRoleUpdateRequestDTO.getId(),
          authorityName);

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userRoleUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error al actualizar el rol: {}", e.getMessage());
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("Error de validación: {}", e.getMessage());
      throw e; // Relanzar la excepción para que sea manejada por un controlador global de
               // excepciones
    }
  }

  // Métodos para ROLE_USER

  /**
   * Actualiza la información de un usuario existente.
   * correspondiente a los atributos: firstName, lastName, secondLastName y
   * lenguageKey.
   * Solo ROLE_USER puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateProfileUser(UserProfileUpdateRequestDTO userProfileUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      getUserById(userProfileUpdateRequestDTO.getId());

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spUpdateUserByRoleUser(
          userProfileUpdateRequestDTO.getId(),
          userProfileUpdateRequestDTO.getFirstName(),
          userProfileUpdateRequestDTO.getLastName(),
          userProfileUpdateRequestDTO.getSecondLastName(),
          userProfileUpdateRequestDTO.getLanguageKey());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userProfileUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error inesperado al actualizar el usuario: {}", e.getMessage());
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    }
  }

  /**
   * Actualiza el nickname de un usuario existente.
   * Solo ROLE_USER puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateNickname(UserNicknameUpdateRequestDTO userNicknameUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      getUserById(userNicknameUpdateRequestDTO.getId());

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spChangeNickname(
          userNicknameUpdateRequestDTO.getId(),
          userNicknameUpdateRequestDTO.getNickname());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userNicknameUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error al actualizar el nickname: {}", e.getMessage());
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("Error de validación: {}", e.getMessage());
      throw e; // Relanzar la excepción para que sea manejada por un controlador global de
               // excepciones
    }
  }

  /**
   * Actualiza el email de un usuario existente.
   * Solo ROLE_USER puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateEmail(UserEmailUpdateRequestDTO userEmailUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      getUserById(userEmailUpdateRequestDTO.getId());

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spChangeEmail(
          userEmailUpdateRequestDTO.getId(),
          userEmailUpdateRequestDTO.getEmail());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userEmailUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error al actualizar el correo: {}", e.getMessage());
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("Error de validación del correo: {}", e.getMessage());
      throw e; // Relanzar la excepción para que sea manejada por un controlador global de
               // excepciones
    }
  }

  /**
   * Actualiza el password de un usuario existente.
   * Solo ROLE_USER puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updatePassword(UserPasswordUpdateRequestDTO userPasswordUpdateRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      User user = getUserById(userPasswordUpdateRequestDTO.getId());

      // Verificar que la contraseña actual proporcionada coincida con la que está
      // almacenada
      if (!passwordEncoder.matches(userPasswordUpdateRequestDTO.getOldPassword(), user.getPassword())) {
        throw new IllegalArgumentException("108, La contraseña no coincide con la registrada");
      }

      // Codifica las contraseñas antes de guardarla
      String hashedPassword = passwordEncoder.encode(userPasswordUpdateRequestDTO.getNewPassword());

      // Verifica que la nueva contraseña no sea igual a la actual
      if (passwordEncoder.matches(userPasswordUpdateRequestDTO.getNewPassword(), user.getPassword())) {
        throw new IllegalArgumentException("109, La nueva contraseña es igual a la contraseña vigente");
      }

      // Llamar al procedimiento almacenado para actualizar el usuario
      userRepository.spChangePassword(
          userPasswordUpdateRequestDTO.getId(),
          hashedPassword,
          user.getPassword());

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return getUserById(userPasswordUpdateRequestDTO.getId());

    } catch (DataAccessException e) {
      logger.error("Error al actualizar la contraseña: {}", e.getMessage());
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("Error al actualizar la contraseña: {}", e.getMessage());
      throw e; // Relanzar la excepción para que sea manejada por un controlador global de
               // excepciones
    }
  }

}
