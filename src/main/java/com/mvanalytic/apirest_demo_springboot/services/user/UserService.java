package com.mvanalytic.apirest_demo_springboot.services.user;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.repositories.user.UserRepository;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;

import org.apache.logging.log4j.Logger;

/**
 * Servicio para gestionar operaciones CRUD relacionadas con los usuarios.
 * Proporciona métodos para registrar, actualizar, eliminar y obtener usuarios.
 */
@Service
public class UserService {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(UserService.class);

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
      throw new IllegalArgumentException("El email ya está en uso");
    }

    if (existingUserByNickname.isPresent()) {
      throw new IllegalArgumentException("El nickname ya está en uso.");
    }

    // Log para verificar el valor de la contraseña
    if (user.getPassword() == null) {
      throw new IllegalArgumentException("La contraseña no puede ser nula");
    }
    // Codifica la contraseña antes de guardarla
    String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword); // Establece el hash de la contraseña
    return userRepository.save(user);
  }

  /**
   * Actualiza la información de un usuario existente.
   * Codifica la contraseña solo si ha sido modificada.
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  // FIXME: hacer que solo se actulice lo que ha cambiado
  public User updateUser(User user) {
    Optional<User> existingUserOptional = userRepository.findByNickname(user.getNickname());
    if (existingUserOptional.isPresent()) {
      User existingUser = existingUserOptional.get();

      // Solo se actualiza la contraseña si se ha modificado
      if (!user.getPassword().equals(existingUser.getPassword())) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
      }

      existingUser.setFirstName(user.getFirstName());
      existingUser.setLastName(user.getLastName());
      existingUser.setEmail(user.getEmail());
      existingUser.setNickname(user.getNickname());
      existingUser.setActivated(user.isActivated());
      existingUser.setStatus(user.isStatus());
      existingUser.setLanguageKey(user.getLanguageKey());
      existingUser.setAuthorities(user.getAuthoritySet());

      return userRepository.save(existingUser);
    } else {
      throw new IllegalArgumentException("Usuario no encontrado para actualización");
    }
  }

  /**
   * Obtiene un usuario por su email.
   *
   * @param nickname El nickname del usuario a buscar.
   * @return El usuario encontrado.
   */
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));
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
        throw new UsernameNotFoundException("Usuario no encontrado con el nickname: " + nickname);
      }

    } catch (UsernameNotFoundException e) {
      logger.error("Error al buscar usuario por nickname: {}", e.getMessage());
      throw e; // Relanza la excepción para que sea manejada por los manejadores de excepciones
    } catch (Exception e) {
      logger.error("Error inesperado al buscar usuario por nickname: {}", e.getMessage());
      throw new RuntimeException("Error inesperado al buscar usuario por nickname: " + nickname, e);
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
}
