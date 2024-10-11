package com.mvanalytic.apirest_demo_springboot.controllers.user;

import java.util.ArrayList;
import java.util.List;
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
import com.mvanalytic.apirest_demo_springboot.dto.user.ResendActivationRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserAuthorityRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRegistrationByAdminRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserStatusRequestDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserMapper;
import com.mvanalytic.apirest_demo_springboot.services.user.UserAuthorityService;
import com.mvanalytic.apirest_demo_springboot.services.user.UserService;
import com.mvanalytic.apirest_demo_springboot.utility.UserValidationService;

@RestController
@RequestMapping("api/admin/users")
public class AdminUserController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserAuthorityService userAuthorityService;

  @Autowired
  private UserValidationService userValidationService;

  /**
   * Registra un nuevo usuario en el sistema.
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   *
   * @param user El usuario a registrar.
   * @return Una respuesta indicando el éxito del registro.
   */
  @PostMapping("/register")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<String> registerUser(
      @RequestBody UserRegistrationByAdminRequestDTO userRegistrationByAdminRequestDTO) {

    // validar los datos de la solicitud
    userValidationService.validateUserRegistrationByAdminRequestDTO(userRegistrationByAdminRequestDTO);

    // Mapear el DTO a la entidad User
    User user = UserMapper.convertUserRegistrationByAdminDTOToUser(userRegistrationByAdminRequestDTO);

    // Envio a procesar solicitud
    userService.registerUser(user, true);

    return ResponseEntity.ok("Usuario registrado exitosamente");
  }

  /**
   * Controlador para reenviar un enlace de activación a un usuario.
   *
   * Este método maneja solicitudes POST en la ruta "/resend-activation". Recibe
   * un objeto DTO que contiene el correo electrónico del usuario y, después de
   * validar el formato del correo, envía una solicitud al servicio para reenviar
   * el enlace de activación.
   * 
   * @param resendActivationRequestDTO DTO que contiene la información del correo
   *                                   electrónico del usuario.
   * 
   * @return ResponseEntity<String> con un mensaje de éxito si el correo es válido
   *         y el proceso de reenvío se completa sin errores.
   *         - Si el correo es válido, se envía un código HTTP 200 (OK) y un
   *         mensaje indicando que el nuevo enlace ha sido enviado.
   *         - Si el correo no es válido, se lanza una excepción con un código de
   *         error específico.
   * 
   * @throws IllegalArgumentException Si el correo proporcionado no cumple con el
   *                                  formato válido.
   */
  @PostMapping("/resend-activation")
  public ResponseEntity<String> resendActivationLink(
      @RequestBody ResendActivationRequestDTO resendActivationRequestDTO) {

    // Validar el formato del correo electrónico usando un servicio de validación
    if (!userValidationService.isValidEmail(resendActivationRequestDTO.getEmail(), 5, 254)) {
      throw new IllegalArgumentException("123, El correo no cumple el formato definido");
    }

    // Llama al servicio de usuario para reenviar el enlace de activación al correo
    // validado
    userService.resendActivation(resendActivationRequestDTO.getEmail());

    // Devuelve una respuesta HTTP 200 (OK) con un mensaje de éxito
    return ResponseEntity.ok("Nuevo enlace de activación enviado a tu correo.");
  }

  /**
   * Obtiene un usuario por su correo.
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   *
   * @param nickname El nickname del usuario a buscar.
   * @return El usuario encontrado.
   */
  @GetMapping("/get-by-id/{id}")
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
  @GetMapping("/get-by-email/{email}")
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
  @GetMapping("/get-by-nickname/{nickname}")
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
  @GetMapping("/all")
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
      @RequestBody UserStatusRequestDTO userStatusUpdateRequestDTO) {

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
  public ResponseEntity<AdminUserResponseDTO> updateUserAuthority(
      @RequestBody UserAuthorityRequestDTO userAuthorityRequestDTO) {

    // validar datos de entrada
    userValidationService.validateUserRoleUpdateRequestDTO(userAuthorityRequestDTO);

    // User user = UserMapper.convertAdminUserDTOToUser(adminUserDTO);
    User updateUser = userAuthorityService.updateUserAuthority(userAuthorityRequestDTO);

    AdminUserResponseDTO adminUserDTOResponse = UserMapper.convertUserToAdminUserDTO(updateUser);

    return ResponseEntity.ok(adminUserDTOResponse);
  }

}
