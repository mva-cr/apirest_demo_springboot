package com.mvanalytic.apirest_demo_springboot.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserKey;
import com.mvanalytic.apirest_demo_springboot.dto.user.ActivateAccountRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.PasswordResetRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.ChangePasswordByResetRequestDTO;
import com.mvanalytic.apirest_demo_springboot.services.user.UserKeyServiceImpl;
import com.mvanalytic.apirest_demo_springboot.services.user.UserService;
import com.mvanalytic.apirest_demo_springboot.utility.UserValidationService;

@RestController
@RequestMapping("/api/public")
public class PublicUserController {

  @Autowired
  private UserKeyServiceImpl userKeyServiceImpl;

  @Autowired
  private UserValidationService userValidationService;

  @Autowired
  private UserService userService;

  /**
   * Endpoint para activar la cuenta del usuario cuando hace clic en el enlace
   * de activación recibido por correo.
   * 
   * @param id            El identificador único del registro de activación.
   * @param activationKey La clave de activación generada para activar la cuenta.
   * @return Un ResponseEntity con un mensaje de éxito o error según el resultado.
   */
  @GetMapping("/activate-account/{id}/{activationKey}")
  public ResponseEntity<String> activateUser(
      @PathVariable Long id,
      @PathVariable String activationKey) {
    try {
      // Validar la solicitud
      userValidationService.validateActivationAccount(id, activationKey);

      // Llama al servicio para activar procesar la solicitud
      UserKey userKey = userKeyServiceImpl.activateAccount(id, activationKey, null);

      // notificar a ROLE_ADMIN
      userService.activationReportToAdmin(userKey);

      return ResponseEntity.ok("Cuenta activada exitosamente.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  /**
   * Activa la cuenta de un usuario utilizando una clave temporal recibida por
   * correo del administrador.
   *
   * Este endpoint se utiliza cuando un administrador envía una clave temporal al
   * usuario para que éste pueda activar su cuenta. El usuario debe proporcionar
   * la clave temporal y una nueva contraseña, la cual será actualizada en el
   * sistema si la validación es exitosa.
   *
   * Proceso:
   * 1. Se valida el ID del usuario, el `keyValue` y la contraseña temporal.
   * 2. Si la validación es correcta, se actualiza la contraseña del usuario con
   * la nueva proporcionada.
   * 3. La cuenta del usuario queda activada.
   *
   * @param id                        El ID del usuario que se va a activar.
   * @param keyValue                  La clave temporal enviada por el
   *                                  administrador.
   * @param activateAccountRequestDTO Objeto que contiene la contraseña temporal y
   *                                  la nueva contraseña.
   * @return Respuesta HTTP con mensaje de éxito o error.
   */
  @PostMapping("/activate-account-with-temporaty-password/{id}/{keyValue}")
  public ResponseEntity<String> activateUserUsingResendKey(
      @PathVariable Long id,
      @PathVariable String keyValue,
      @RequestBody ActivateAccountRequestDTO activateAccountRequestDTO) {
    try {

      // Valida el id, keyValue y el objeto que se recibe
      userValidationService.validateActivateAccountRequestDTO(activateAccountRequestDTO, id, keyValue);

      // UserKey userKey = userKeyServiceImpl.findByIdWithUser(id);
      // User user = userService.getUserById(userKey.getUser().getId());

      // verificar si el password temporal coincide
      // userService.validateTemporaryPassword(id,
      // activateAccountRequestDTO.getTempPassword());

      // Activa el user
      UserKey userKey = userKeyServiceImpl.activateAccount(id, keyValue, activateAccountRequestDTO.getTempPassword());

      // notificar a ROLE_ADMIN
      userService.activationReportToAdmin(userKey);

      return ResponseEntity.ok("Contraseña restablecida exitosamente. Inicie sesión con su nueva contraseña");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  /**
   * Endpoint para solicitar un restablecimiento de contraseña (el propio user).
   *
   * Este método permite que un usuario solicite restablecer su contraseña. Recibe
   * un `PasswordResetRequestDTO` que contiene los datos del usuario (correo
   * electrónico o nombre de usuario), valida la solicitud y luego envía un correo
   * con un enlace para que el usuario pueda restablecer su contraseña.
   * 
   * Este proceso inicia el flujo de restablecimiento de contraseña generando una
   * nueva clave de restablecimiento (`keyValue`) que será enviada al usuario.
   *
   * @param passwordResetRequestDTO Objeto que contiene el correo electrónico o el
   *                                nombre de usuario del usuario.
   * @return ResponseEntity<String> Un mensaje indicando que el correo con las
   *         instrucciones ha sido enviado.
   */
  @PostMapping("/password-reset-request")
  public ResponseEntity<String> passwordReset(
      @RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
    // Validar la solicitud
    userValidationService.validatePasswordResetRequestDTO(passwordResetRequestDTO);

    // Procesar la solicitud de restablecimiento
    userService.processPasswordReset(passwordResetRequestDTO);

    // Retornar una respuesta indicando que el proceso ha comenzado
    return ResponseEntity.ok(
        "Se ha enviado un correo con las instrucciones para restablecer la contraseña.");
  }

  /**
   * Endpoint para restablecer la contraseña de un usuario, solicitud hecha por el
   * propio user previamente y sobre la que recibió un correo
   *
   * Este método permite que un usuario restablezca su contraseña mediante un `id`
   * y una clave de restablecimiento (`keyValue`) que se proporcionan en la URL.
   * El `keyValue` debe haber sido enviado previamente por correo electrónico al
   * usuario.
   * 
   * Si la validación del ID, la clave de restablecimiento y el nuevo password son
   * exitosos, e procede a actualizar la contraseña del usuario. En caso
   * contrario, se devuelve un mensaje de error.
   *
   * @param id                      El identificador del usuario que está
   *                                restableciendo la contraseña.
   * @param keyValue                La clave de restablecimiento proporcionada por
   *                                el usuario.
   * @param resetPasswordRequestDTO Objeto que contiene la nueva contraseña que el
   *                                usuario desea establecer.
   * @return ResponseEntity<String> Un mensaje de éxito o un mensaje de error en
   *         caso de fallar.
   */
  @PostMapping("/change-password-by-reset/{id}/{keyValue}")
  public ResponseEntity<String> changePasswordByReset(
      @PathVariable Long id,
      @PathVariable String keyValue,
      @RequestBody ChangePasswordByResetRequestDTO changePasswordByResetRequestDTO) {
    try {
      // Valida el id y keyValue
      userValidationService.validateResetPasswordDTO(changePasswordByResetRequestDTO, id, keyValue);

      // Envío a procesar la solicitud de cambio de contraseña
      userService.changePasswordByReset(
          id,
          changePasswordByResetRequestDTO.getNewPassword(),
          keyValue);

      return ResponseEntity.ok("Contraseña restablecida exitosamente. Inicie sesión con su nueva contraseña");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

}
