package com.mvanalytic.apirest_demo_springboot.mapper.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.mvanalytic.apirest_demo_springboot.domain.user.Authority;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserAuthority;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserAuthorityId;
import com.mvanalytic.apirest_demo_springboot.dto.user.AdminUserResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.AuthorityDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.JwtResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserAuthorityRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRegistrationByAdminRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRegistrationRequestDTO;

// import org.springframework.security.core.GrantedAuthority;

/**
 * Clase que realiza conversiones entre User y sus DTO's
 */
public class UserMapper {

 
  // User - AdminUserDTO y viceversa

  /**
   * Convierte un objeto `User` en un objeto `AdminUserResponseDTO`.
   * 
   * Este método toma un objeto `User` y lo transforma en un DTO adecuado para la
   * respuesta que requiere el rol de administrador, con la información relevante
   * sobre el usuario.
   * 
   * @param user El objeto `User` que se va a convertir en `AdminUserResponseDTO`.
   * @return Un objeto `AdminUserResponseDTO` que contiene la información del
   *         usuario, como su nombre, correo electrónico, autoridades, entre
   *         otros.
   * @throws IllegalArgumentException Si ocurre algún error al mappear el objeto
   *                                  `User` a su DTO.
   */
  public static AdminUserResponseDTO convertUserToAdminUserDTO(User user) {
    try {
      AdminUserResponseDTO adminUserDTO = new AdminUserResponseDTO();
      adminUserDTO.setId(user.getId());
      adminUserDTO.setFirstName(user.getFirstName());
      adminUserDTO.setLastName(user.getLastName());
      adminUserDTO.setSecondLastName(user.getSecondLastName());
      adminUserDTO.setNickname(user.getNickname());
      adminUserDTO.setEmail(user.getEmail());
      adminUserDTO.setLanguageKey(user.getLanguageKey());
      adminUserDTO.setStatus(user.isStatus());
      adminUserDTO.setActivated(user.isActivated());
      adminUserDTO.setAuthorities(
          user.getAuthorities().stream()
              .map(authority -> new Authority(((SimpleGrantedAuthority) authority).getAuthority()))
              .map(authority -> convertAuthorityToAuthorityDTO(authority))
              .collect(Collectors.toSet()));
      return adminUserDTO;
    } catch (Exception e) {
      throw new IllegalArgumentException("188, Error al mappear el User a AdminUserResponseDTO");
    }
  }

  /**
   * Convierte un objeto `Authority` en un objeto `AuthorityDTO`.
   * 
   * Este método toma un objeto `Authority` y lo transforma en un DTO adecuado
   * para representar la autoridad en el contexto de un DTO.
   * 
   * @param authority El objeto `Authority` que se va a convertir en
   *                  `AuthorityDTO`.
   * @return Un objeto `AuthorityDTO` que contiene el nombre de la autoridad.
   * @throws IllegalArgumentException Si ocurre algún error al mappear el objeto
   *                                  `Authority` a su DTO.
   */
  public static AuthorityDTO convertAuthorityToAuthorityDTO(Authority authority) {
    try {
      return new AuthorityDTO(authority.getName());
    } catch (Exception e) {
      throw new IllegalArgumentException("189, Error al mappear el Authority a AuthorityDTO");
    }
  }

  /**
   * Convierte un objeto `AdminUserResponseDTO` en un objeto `User`.
   * 
   * Este método toma un objeto `AdminUserResponseDTO` y lo transforma en un
   * objeto `User`. Se asegura de que todos los campos relevantes del DTO se
   * transfieran correctamente al objeto `User`. También realiza la conversión de
   * las autoridades si están presentes.
   * 
   * @param adminUserDTO El objeto `AdminUserResponseDTO` que se va a convertir en
   *                     `User`.
   * @return Un objeto `User` con los valores del DTO mapeados.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión
   *                                  del DTO a la entidad `User`.
   */
  public static User convertAdminUserDTOToUser(AdminUserResponseDTO adminUserDTO) {
    try {
      User user = new User();
      user.setId(adminUserDTO.getId());
      user.setFirstName(adminUserDTO.getFirstName());
      user.setLastName(adminUserDTO.getLastName());
      user.setSecondLastName(
          adminUserDTO.getSecondLastName() == null ? null : adminUserDTO.getSecondLastName());
      user.setNickname(adminUserDTO.getNickname());
      user.setEmail(adminUserDTO.getEmail());
      user.setLanguageKey(adminUserDTO.getLanguageKey());
      user.setStatus(adminUserDTO.isStatus());
      user.setActivated(adminUserDTO.isActivated());
      user.setAuthorities(
          // Verifica si el set es nulo antes de intentar mapearlo
          adminUserDTO.getAuthorities() == null ? new HashSet<>()
              : adminUserDTO.getAuthorities()
                  .stream()
                  .map(authorityDTO -> convertAuthorityDTOToAuthority(authorityDTO))
                  .collect(Collectors.toSet()));

      return user;
    } catch (Exception e) {
      throw new IllegalArgumentException("192, Error al mappear el AdminUserResponseDTO a User");
    }
  }

  /**
   * Convierte un objeto `AuthorityDTO` en un objeto `Authority`.
   * 
   * Este método toma un objeto `AuthorityDTO` y lo transforma en un objeto
   * `Authority`, que puede ser utilizado en las entidades del sistema
   * relacionadas con la seguridad y permisos.
   * 
   * @param authorityDTO El objeto `AuthorityDTO` que se va a convertir en
   *                     `Authority`.
   * @return Un objeto `Authority` con el nombre del rol mapeado desde el DTO.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión
   *                                  del DTO a la entidad `Authority`.
   */
  public static Authority convertAuthorityDTOToAuthority(AuthorityDTO authorityDTO) {
    try {
      return new Authority(authorityDTO.getName());
    } catch (Exception e) {
      throw new IllegalArgumentException("191, Error al mappear el AuthorityDTO a Authority");
    }
  }

  /**
   * Convierte un objeto `User` en un objeto `UserProfileResponseDTO`.
   * 
   * Este método se encarga de tomar una entidad `User` y convertirla en un
   * `UserProfileResponseDTO`, que representa un perfil de usuario con información
   * relevante para la visualización o respuesta.
   * 
   * @param user El objeto `User` que se va a convertir.
   * @return Un objeto `UserProfileResponseDTO` con los detalles del perfil del
   *         usuario.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión
   *                                  de `User` a `UserProfileResponseDTO`.
   */
  public static UserProfileResponseDTO convertUserToUserProfileDTO(User user) {
    try {
      UserProfileResponseDTO userProfileDTO = new UserProfileResponseDTO();
      userProfileDTO.setId(user.getId());
      userProfileDTO.setFirstName(user.getFirstName());
      userProfileDTO.setLastName(user.getLastName());
      userProfileDTO.setSecondLastName(
          user.getSecondLastName() == null ? null : user.getSecondLastName());
      userProfileDTO.setNickname(user.getNickname());
      userProfileDTO.setEmail(user.getEmail());
      userProfileDTO.setLanguageKey(user.getLanguageKey());

      return userProfileDTO;
    } catch (Exception e) {
      throw new IllegalArgumentException("193, Error al mappear el User en UserProfileResponseDTO");
    }
  }

  /**
   * Convierte un objeto `UserProfileResponseDTO` en un objeto `User`.
   * 
   * Este método toma un `UserProfileResponseDTO` y lo convierte en una entidad
   * `User`, transfiriendo los datos del perfil del usuario desde el DTO a la
   * entidad correspondiente.
   * 
   * @param userProfileDTO El objeto `UserProfileResponseDTO` que se va a
   *                       convertir en un `User`.
   * @return Un objeto `User` con los detalles correspondientes transferidos desde
   *         el DTO.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión
   *                                  del DTO a `User`.
   */
  public static User convertUserProfileDTOToUser(UserProfileResponseDTO userProfileDTO) {
    try {
      User user = new User();
      user.setId(userProfileDTO.getId());
      user.setFirstName(userProfileDTO.getFirstName());
      user.setLastName(userProfileDTO.getLastName());
      user.setSecondLastName(userProfileDTO.getSecondLastName());
      user.setNickname(userProfileDTO.getNickname());
      user.setEmail(userProfileDTO.getEmail());
      user.setLanguageKey(userProfileDTO.getLanguageKey());

      return user;
    } catch (Exception e) {
      throw new IllegalArgumentException("194, Error al mappear el UserProfileResponseDTO a User");
    }
  }

  /**
   * Convierte un objeto `User` en un `UserRegistrationRequestDTO`.
   * 
   * Este método toma un objeto `User` y lo convierte en un
   * `UserRegistrationRequestDTO`,
   * transfiriendo los datos del usuario a un DTO adecuado para la solicitud de
   * registro de usuario.
   * 
   * @param user El objeto `User` que se va a convertir en un
   *             `UserRegistrationRequestDTO`.
   * @return Un objeto `UserRegistrationRequestDTO` con los detalles
   *         correspondientes del usuario.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión
   *                                  del `User` a `UserRegistrationRequestDTO`.
   */
  public static UserRegistrationRequestDTO convertUserToUserRegistrationDTO(User user) {
    try {
      UserRegistrationRequestDTO userRegistrationDTO = new UserRegistrationRequestDTO();
      userRegistrationDTO.setFirstName(user.getFirstName());
      userRegistrationDTO.setLastName(user.getLastName());
      userRegistrationDTO.setSecondLastName(
          user.getSecondLastName() == null ? null : user.getSecondLastName());
      userRegistrationDTO.setNickname(user.getNickname());
      userRegistrationDTO.setEmail(user.getEmail());
      userRegistrationDTO.setPassword(user.getPassword());
      userRegistrationDTO.setLanguageKey(user.getLanguageKey());

      return userRegistrationDTO;
    } catch (Exception e) {
      throw new IllegalArgumentException("195, Error al mappear el User a UserRegistrationRequestDTO");
    }
  }

  /**
   * Convierte un objeto `UserRegistrationRequestDTO` en un objeto `User`.
   * 
   * Este método toma un objeto `UserRegistrationRequestDTO` que contiene la
   * información de registro del usuario y lo convierte en un objeto `User` listo
   * para ser utilizado en la lógica del sistema.
   * 
   * @param userRegistrationDTO El DTO de solicitud de registro de usuario que
   *                            contiene los datos del usuario.
   * @return Un objeto `User` con los detalles correspondientes del registro del
   *         usuario.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión
   *                                  del `UserRegistrationRequestDTO` a `User`.
   */
  public static User convertUserRegistrationDTOToUser(
      UserRegistrationRequestDTO userRegistrationDTO) {
    try {
      User user = new User();
      user.setFirstName(userRegistrationDTO.getFirstName());
      user.setLastName(userRegistrationDTO.getLastName());
      user.setSecondLastName(userRegistrationDTO.getSecondLastName());
      user.setNickname(userRegistrationDTO.getNickname());
      user.setEmail(userRegistrationDTO.getEmail());
      user.setLanguageKey(userRegistrationDTO.getLanguageKey());
      user.setPassword(userRegistrationDTO.getPassword());

      return user;
    } catch (Exception e) {
      throw new IllegalArgumentException("196, Error al mappear el UserRegistrationRequestDTO a User");
    }
  }

  /**
   * Convierte un objeto `UserRegistrationByAdminRequestDTO` en un objeto `User`.
   * 
   * Este método toma un objeto `UserRegistrationByAdminRequestDTO`, que contiene
   * los datos proporcionados por un administrador para registrar un nuevo
   * usuario, y lo convierte en un objeto `User` listo para ser utilizado en el
   * sistema.
   * 
   * @param userRegistrationByAdminRequestDTO El DTO de solicitud de registro de
   *                                          usuario creado por el administrador.
   * @return Un objeto `User` con los detalles correspondientes al registro
   *         realizado por el administrador.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión
   *                                  del `UserRegistrationByAdminRequestDTO` a
   *                                  `User`.
   */
  public static User convertUserRegistrationByAdminDTOToUser(
      UserRegistrationByAdminRequestDTO userRegistrationByAdminRequestDTO) {
    try {
      User user = new User();
      user.setFirstName(userRegistrationByAdminRequestDTO.getFirstName());
      user.setLastName(userRegistrationByAdminRequestDTO.getLastName());
      user.setSecondLastName(userRegistrationByAdminRequestDTO.getSecondLastName());
      user.setNickname(userRegistrationByAdminRequestDTO.getNickname());
      user.setEmail(userRegistrationByAdminRequestDTO.getEmail());
      user.setLanguageKey(userRegistrationByAdminRequestDTO.getLanguageKey());

      return user;
    } catch (Exception e) {
      throw new IllegalArgumentException("197, Error al mappear el UserRegistrationByAdminRequestDTO a User");
    }
  }

  /**
   * Convierte un objeto `User` en un objeto `JwtResponseDTO`.
   * 
   * Este método toma un objeto `User`, que contiene los detalles del usuario, y
   * lo convierte en un objeto `JwtResponseDTO` que se utiliza para enviar la
   * respuesta JWT (Token Web JSON) tras un inicio de sesión exitoso o en la
   * creación de tokens.
   * 
   * @param user El objeto `User` que contiene la información del usuario
   *             autenticado.
   * @return Un objeto `JwtResponseDTO` con los detalles del usuario y listo para
   *         ser usado en la respuesta de autenticación.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión
   *                                  del `User` a `JwtResponseDTO`.
   */
  public static JwtResponseDTO convertUserToJwtResponse(User user) {
    try {
      JwtResponseDTO jwtResponse = new JwtResponseDTO();
      jwtResponse.setId(user.getId());
      jwtResponse.setNickname(user.getNickname());
      jwtResponse.setFirstname(user.getFirstName());
      jwtResponse.setLastName(user.getLastName());
      jwtResponse.setSecondLastName(user.getSecondLastName());
      jwtResponse.setEmail(user.getEmail());
      jwtResponse.setLanguageKey(user.getLanguageKey());

      return jwtResponse;
    } catch (Exception e) {
      throw new IllegalArgumentException("198, Error al mappear el User a JwtResponseDTO");
    }
  }

  /**
   * Convierte un objeto `UserAuthorityRequestDTO` en una lista de objetos
   * `UserAuthority`.
   * 
   * Este método toma un objeto `UserAuthorityRequestDTO`, que contiene el ID del
   * usuario y una lista de autoridades (roles), y convierte estos datos en una
   * lista de objetos `UserAuthority`. Cada `UserAuthority` representa la relación
   * entre un usuario y una autoridad (rol).
   * 
   * @param userAuthorityRequestDTO El objeto `UserAuthorityRequestDTO` que
   *                                contiene el ID del usuario y los roles
   *                                asociados.
   * @return Una lista de objetos `UserAuthority` que representan las autoridades
   *         asignadas al usuario.
   * @throws IllegalArgumentException Si ocurre algún error durante la conversión
   *                                  de `UserAuthorityRequestDTO` a
   *                                  `UserAuthority`.
   */
  public static List<UserAuthority> convertUserAuthorityRequestDTOToUserAuthority(
      UserAuthorityRequestDTO userAuthorityRequestDTO) {
    try {
      List<UserAuthority> userAuthorities = new ArrayList<>();

      // Convertir cada AuthorityDTO en un UserAuthority
      for (AuthorityDTO authorityDTO : userAuthorityRequestDTO.getAuthorities()) {
        // Crear una nueva instancia de UserAuthorityId usando el ID del usuario y el
        // nombre de la autoridad
        UserAuthorityId userAuthorityId = new UserAuthorityId(userAuthorityRequestDTO.getUserId(),
            authorityDTO.getName());

        // Crear una nueva instancia de UserAuthority
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setId(userAuthorityId);

        // Establecer el User y Authority

        User user = new User();
        user.setId(userAuthorityRequestDTO.getUserId());
        userAuthority.setUser(user);
        userAuthority.setAuthority(new Authority(authorityDTO.getName()));

        userAuthorities.add(userAuthority);
      }

      return userAuthorities;
    } catch (Exception e) {
      throw new IllegalArgumentException("199, Error al mappear el UserAuthorityRequestDTO a su DTO");
    }
  }

}
