package com.mvanalytic.apirest_demo_springboot.controllers.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.AdminUserResponseDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRegistrationRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRoleUpdateRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserStatusUpdateRequestDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserMapper;
import com.mvanalytic.apirest_demo_springboot.services.user.UserService;
import com.mvanalytic.apirest_demo_springboot.utility.UserValidationService;

@RestController
@RequestMapping("api/admin/users")
public class AdminUserController {

  @Autowired
  private UserService userService;

  private final UserValidationService userValidationService;

  public AdminUserController() {
    this.userValidationService = new UserValidationService();
  }

  /**
   * Registra un nuevo usuario en el sistema.
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   *
   * @param user El usuario a registrar.
   * @return Una respuesta indicando el éxito del registro.
   */
  @PostMapping("/register")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequestDTO userRegistrationDTO) {

    // validar
    userValidationService.validateUserRegistrationRequestDTO(userRegistrationDTO);
    String activationKey = UUID.randomUUID().toString();
    User user = new User();

    user = UserMapper.convertUserRegistrationDTOToUser(userRegistrationDTO);
    user.setActivationKey(activationKey);

    userService.registerUser(user);
    return ResponseEntity.ok("Usuario registrado exitosamente");
  }

  /**
   * Obtiene un usuario por su correo.
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   *
   * @param nickname El nickname del usuario a buscar.
   * @return El usuario encontrado.
   */
  @GetMapping("get/by-id/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<AdminUserResponseDTO> getUserById(@PathVariable Long id) {
    User user = userService.getUserById(id);
    AdminUserResponseDTO adminUserDTO = new AdminUserResponseDTO();
    adminUserDTO = UserMapper.convertUserToAdminUserDTO(user);
    return ResponseEntity.ok(adminUserDTO);
  }

  /**
   * Obtiene un usuario por su correo.
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   *
   * @param nickname El nickname del usuario a buscar.
   * @return El usuario encontrado.
   */
  @GetMapping("get/by-email/{email}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<AdminUserResponseDTO> getUserByEmail(@PathVariable String email) {
    User user = userService.getUserByEmail(email);
    AdminUserResponseDTO adminUserDTO = new AdminUserResponseDTO();
    adminUserDTO = UserMapper.convertUserToAdminUserDTO(user);
    return ResponseEntity.ok(adminUserDTO);
  }

  /**
   * Obtiene un usuario por su nickname.
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   *
   * @param nickname El nickname del usuario a buscar.
   * @return El usuario encontrado.
   */
  @GetMapping("get/by-nickname/{nickname}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<AdminUserResponseDTO> getUserByNickName(@PathVariable String nickname) {
    User user = userService.getUserByNickName(nickname);
    AdminUserResponseDTO adminUserDTO = new AdminUserResponseDTO();
    adminUserDTO = UserMapper.convertUserToAdminUserDTO(user);
    return ResponseEntity.ok(adminUserDTO);
  }

  /**
   * Endpoint para obtener todos los usuarios registrados en el sistema.
   * 
   * @return Lista de todos los usuarios.
   */
  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<List<AdminUserResponseDTO>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    AdminUserResponseDTO adminUserDTO = new AdminUserResponseDTO();
    List<AdminUserResponseDTO> adminUserDTOs = new ArrayList<AdminUserResponseDTO>();
    for (User user : users) {
      adminUserDTO = UserMapper.convertUserToAdminUserDTO(user);
      adminUserDTOs.add(adminUserDTO);
    }
    return ResponseEntity.ok(adminUserDTOs);
  }

  /**
   * Actualiza la información de un usuario existente.
   * Solo permite modificar los parámetros activated y status
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   * Permite actualizaciones parciales del User
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @PatchMapping("/update")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<AdminUserResponseDTO> updateStatusUser(
      @RequestBody UserStatusUpdateRequestDTO userStatusUpdateRequestDTO) {

    // validar datos de entrada
    userValidationService.validateUserStatusUpdateRequestDTO(userStatusUpdateRequestDTO);

    // User user = UserMapper.convertAdminUserDTOToUser(adminUserDTO);
    User updateUser = userService.updateStatusUser(userStatusUpdateRequestDTO);

    AdminUserResponseDTO adminUserDTOResponse = UserMapper.convertUserToAdminUserDTO(updateUser);

    return ResponseEntity.ok(adminUserDTOResponse);
  }

  /**
   * Actualiza la información de un usuario existente.
   * Solo permite modificar los parámetros activated y status
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   * Permite actualizaciones parciales del User
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @PatchMapping("/update/role")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<AdminUserResponseDTO> updateRolUser(
      @RequestBody UserRoleUpdateRequestDTO userRoleUpdateRequestDTO) {

    // validar datos de entrada
    userValidationService.validateUserRoleUpdateRequestDTO(userRoleUpdateRequestDTO);

    // User user = UserMapper.convertAdminUserDTOToUser(adminUserDTO);
    User updateUser = userService.updateRoleUser(userRoleUpdateRequestDTO);

    AdminUserResponseDTO adminUserDTOResponse = UserMapper.convertUserToAdminUserDTO(updateUser);

    return ResponseEntity.ok(adminUserDTOResponse);
  }

}
