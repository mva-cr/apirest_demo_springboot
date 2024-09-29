package com.mvanalytic.apirest_demo_springboot.controllers.user;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserEmailRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserNicknameRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserPasswordRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileRequestDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserMapper;
import com.mvanalytic.apirest_demo_springboot.services.user.UserService;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;
import com.mvanalytic.apirest_demo_springboot.utility.UserValidationService;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controlador para gestionar operaciones CRUD de usuarios.
 * Este controlador solo puede ser accedido por usuarios con el rol
 * "ROLE_ADMIN".
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(UserController.class);

  @Autowired
  private UserValidationService userValidationService;

  @Autowired
  private UserService userService;

  private void validateUserAccess(Long id) {
    // Obtener el usuario autenticado
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // Obtener el nickname del usario autenticado
    String currentUserName = authentication.getName();

    // Obtener el usuario autenticado desde el servicio
    User authenticatedUser = userService.getUserByNickName(currentUserName);

    // Verifica que el id del usuario recibido en el endpoint coincida con el del
    // usuario autenticado
    if (!authenticatedUser.getId().equals(id)) {
      logger.error("");
      throw new AccessDeniedException("119, No puede acceder a información de otro usuario");
    }
  }

  /**
   * Actualiza la información de un usuario existente.
   * Solo permite modificar los parámetros fistName, lastName, secondLastName y
   * LanguageKey
   * Solo usuarios con el rol ROLE_USER pueden acceder a este método.
   * Permite actualizaciones parciales del User
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @PatchMapping("/update/profile")
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<UserProfileResponseDTO> updateProfileUser(
      @RequestBody UserProfileRequestDTO userProfileUpdateRequestDTO) {

    // Valida el acceso del usuario autenticado
    validateUserAccess(userProfileUpdateRequestDTO.getId());

    // Valida la entrada de datos
    userValidationService.validateUserProfileUpdateRequestDTO(userProfileUpdateRequestDTO);

    // Realiza la actualización del usuario
    User updateUser = userService.updateProfileUser(userProfileUpdateRequestDTO);

    UserProfileResponseDTO userDTOResponse = UserMapper.convertUserToUserProfileDTO(updateUser);

    return ResponseEntity.ok(userDTOResponse);
  }

  /**
   * Actualiza el nickname de un usuario existente.
   * Solo usuarios con el rol ROLE_USER pueden acceder a este método.
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @PatchMapping("/update/nickname")
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<UserProfileResponseDTO> updateNickname(
      @RequestBody UserNicknameRequestDTO userNicknameUpdateRequestDTO) {

    // Valida el acceso del usuario autenticado
    validateUserAccess(userNicknameUpdateRequestDTO.getId());

    // Validar entrada
    userValidationService.validateUserNicknameUpdateRequestDTO(userNicknameUpdateRequestDTO);

    // Realiza la actualización del usuario
    User updateUser = userService.updateNickname(userNicknameUpdateRequestDTO);

    UserProfileResponseDTO userDTOResponse = UserMapper.convertUserToUserProfileDTO(updateUser);

    return ResponseEntity.ok(userDTOResponse);
  }

  /**
   * Actualiza el email de un usuario existente.
   * Solo usuarios con el rol ROLE_USER pueden acceder a este método.
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @PatchMapping("/update/email")
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<UserProfileResponseDTO> updateEmail(
      @RequestBody UserEmailRequestDTO userEmailUpdateRequestDTO) {

    // Valida el acceso del usuario autenticado
    validateUserAccess(userEmailUpdateRequestDTO.getId());

    // Validar el Request DTO
    userValidationService.validateUserMailRequestDTO(userEmailUpdateRequestDTO);

    // Realiza la actualización del usuario
    User updateUser = userService.updateEmail(userEmailUpdateRequestDTO);

    UserProfileResponseDTO userDTOResponse = UserMapper.convertUserToUserProfileDTO(updateUser);

    return ResponseEntity.ok(userDTOResponse);
  }

  /**
   * Actualiza el password de un usuario existente.
   * Solo usuarios con el rol ROLE_USER pueden acceder a este método.
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @PatchMapping("/update/password")
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<String> updatPassword(
      @RequestBody UserPasswordRequestDTO userPasswordUpdateRequestDTO) {

    // Valida el acceso del usuario autenticado
    validateUserAccess(userPasswordUpdateRequestDTO.getId());

    // Validar entrada
    userValidationService.validateUserPasswordUpdateRequestDTO(userPasswordUpdateRequestDTO);

    // Realiza la actualización del usuario
    userService.updatePassword(userPasswordUpdateRequestDTO);

    // UserProfileResponseDTO userDTOResponse =
    // UserMapper.convertUserToUserProfileDTO(updateUser);

    return ResponseEntity.ok("El password se ha cambiado satisfactoriamente");
  }

}
