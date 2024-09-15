package com.mvanalytic.apirest_demo_springboot.mapper.user;

import java.util.HashSet;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.mvanalytic.apirest_demo_springboot.domain.user.Authority;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.AdminUserResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.AuthorityDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.JwtResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRegistrationRequestDTO;

// import org.springframework.security.core.GrantedAuthority;

/**
 * Clase que realiza conversiones entre User y sus DTO's
 */
public class UserMapper {

  // User - AdminUserDTO y viceversa

  /**
   * Convierte una entidad User a AdminUserDTO
   * 
   * @param user Entidad User a convertir
   * @return DTO AdminUserDTO
   */
  public static AdminUserResponseDTO convertUserToAdminUserDTO(User user) {
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
  }

  /**
   * Convierte una entidad Authority a AuthorityDTO
   * 
   * @param authority
   * @return
   */
  public static AuthorityDTO convertAuthorityToAuthorityDTO(Authority authority) {
    return new AuthorityDTO(authority.getName());
  }

  /**
   * Convierte un Objeto AdminUserDTO a User
   * 
   * @param adminUserDTO DTO objeto AdminUserDTO a convertir
   * @return User Entidad
   */
  public static User convertAdminUserDTOToUser(AdminUserResponseDTO adminUserDTO) {
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
  }

  /**
   * Convierte un objeto AuthorityDTO a un Authority
   * 
   * @param authorityDTO objeto Authority a convertir
   * @return Authority Entidad
   */
  public static Authority convertAuthorityDTOToAuthority(AuthorityDTO authorityDTO) {
    return new Authority(authorityDTO.getName());
  }

  // User - UserProfileDTO y viceversa

  /**
   * Convierte un objeto User a un UserProfileDTO
   * 
   * @param user Entidad a convertir
   * @return UserProfileDTO Entidad
   */
  public static UserProfileResponseDTO convertUserToUserProfileDTO(User user) {
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
  }

  /**
   * Convierte un objeto UserProfileDTO a un User
   * 
   * @param userProfileDTO Entidad a convertir
   * @return User Entidad
   */
  public static User convertUserProfileDTOToUser(UserProfileResponseDTO userProfileDTO) {
    User user = new User();
    user.setId(userProfileDTO.getId());
    user.setFirstName(userProfileDTO.getFirstName());
    user.setLastName(userProfileDTO.getLastName());
    user.setSecondLastName(userProfileDTO.getSecondLastName());
    user.setNickname(userProfileDTO.getNickname());
    user.setEmail(userProfileDTO.getEmail());
    user.setLanguageKey(userProfileDTO.getLanguageKey());

    return user;
  }

  // User - UserRegistrationDTO y viceversa

  /**
   * Convierte un objeto User a un UserRegistrationDTO
   * 
   * @param user Entidad a convertir
   * @return UserRegistrationDTO Entidad
   */
  public static UserRegistrationRequestDTO convertUserToUserRegistrationDTO(User user) {
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
  }

  /**
   * Convierte un objeto UserRegistrationDTO a un User
   * 
   * @param userRegistrationDTO Entidad a convertir
   * @return User entidad
   */
  public static User convertUserRegistrationDTOToUser(UserRegistrationRequestDTO userRegistrationDTO) {
    User user = new User();
    user.setFirstName(userRegistrationDTO.getFirstName());
    user.setLastName(userRegistrationDTO.getLastName());
    user.setSecondLastName(userRegistrationDTO.getSecondLastName());
    user.setNickname(userRegistrationDTO.getNickname());
    user.setEmail(userRegistrationDTO.getEmail());
    user.setLanguageKey(userRegistrationDTO.getLanguageKey());
    user.setPassword(userRegistrationDTO.getPassword());

    return user;
  }

  // User - JwtResponse y viceversa

  public static JwtResponseDTO convertUserToJwtResponse(User user) {
    JwtResponseDTO jwtResponse = new JwtResponseDTO();
    jwtResponse.setId(user.getId());
    jwtResponse.setNickname(user.getNickname());
    jwtResponse.setFirstname(user.getFirstName());
    jwtResponse.setLastName(user.getLastName());
    jwtResponse.setSecondLastName(user.getSecondLastName());
    jwtResponse.setEmail(user.getEmail());
    jwtResponse.setLanguageKey(user.getLanguageKey());

    return jwtResponse;
  }

}
