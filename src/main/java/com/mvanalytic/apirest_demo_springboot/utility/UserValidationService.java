package com.mvanalytic.apirest_demo_springboot.utility;

import org.springframework.stereotype.Service;
import com.mvanalytic.apirest_demo_springboot.dto.user.ActivateAccountRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.AuthorityDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.PasswordResetRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.ChangePasswordByResetRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserEmailRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserNicknameRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserPasswordRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRegistrationByAdminRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserRegistrationRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserAuthorityRequestDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserStatusRequestDTO;

/**
 * Clase que realiza las validaciones de los diferentes objetos utilizados con
 * la administración del user.
 */
@Service
public class UserValidationService {

  /**
   * Valida los parámetros de la clase UserRegistrationRequestDTO
   * 
   * @param userRegistrationRequestDTO Objeto a validar
   */
  public void validateUserRegistrationRequestDTO(
      UserRegistrationRequestDTO userRegistrationRequestDTO) {
    // Null
    if (userRegistrationRequestDTO.getFirstName() == null) {
      throw new IllegalArgumentException("121, El nombre ingresado es nulo");
    }

    if (userRegistrationRequestDTO.getLastName() == null) {
      throw new IllegalArgumentException("122, El apellido ingresado es nulo");
    }

    if (userRegistrationRequestDTO.getLanguageKey() == null) {
      throw new IllegalArgumentException("126, El idioma es nulo");
    }
    // espacios vacío
    if (!isValidText(userRegistrationRequestDTO.getFirstName())) {
      throw new IllegalArgumentException("135, El nombre no cumple el formato definido");
    }

    if (!isValidText(userRegistrationRequestDTO.getLastName())) {
      throw new IllegalArgumentException("136, El apellido no cumple el formato definido");
    }

    if (userRegistrationRequestDTO.getSecondLastName() != null &&
        !isValidText(userRegistrationRequestDTO.getSecondLastName())) {
      throw new IllegalArgumentException("137, El segundo apellido no cumple el formato definido");
    }

    if (!isValidText(userRegistrationRequestDTO.getLanguageKey())) {
      throw new IllegalArgumentException("138, El idioma no cumple el formato definido");
    }

    // Length
    if (!isValidateMaxLength(userRegistrationRequestDTO.getFirstName(), 50)) {
      throw new IllegalArgumentException("130, El nombre excede los 50 caracteres");
    }

    if (!isValidateMaxLength(userRegistrationRequestDTO.getLastName(), 50)) {
      throw new IllegalArgumentException("131, El apellido excede los 50 caracteres");
    }

    if (userRegistrationRequestDTO.getSecondLastName() != null &&
        !isValidateMaxLength(userRegistrationRequestDTO.getSecondLastName(), 50)) {
      throw new IllegalArgumentException("132, El segundo apellido excede los 50 caracteres");
    }

    if (userRegistrationRequestDTO.getLanguageKey().length() != 2) {
      throw new IllegalArgumentException("137, El idioma debe ser de 2 caracteres");
    }

    // Formato

    // Longitud mínima: 1 (nombre de usuario) + 1 (@) + 1 (dominio de segundo nivel)
    // + 2 (TLD) = 5 caracteres
    if (!isValidEmail(userRegistrationRequestDTO.getEmail(), 5, 254)) {
      throw new IllegalArgumentException("123, El correo no cumple el formato definido");
    }

    if (!isValidNickname(userRegistrationRequestDTO.getNickname(), 1, 50)) {
      throw new IllegalArgumentException("124, El nickname no cumple el formato definido");
    }
    // Longitud máxima de la contraseña: 72 caracteres (para compatibilidad completa
    // con BCrypt).
    if (!isValidPassword(userRegistrationRequestDTO.getPassword(), 72)) {
      throw new IllegalArgumentException("125, La contraseña no cumple el formato definido");
    }
  }

  /**
   * Valida los parámetros de la clase UserRegistrationRequestDTO
   * 
   * @param userRegistrationRequestDTO Objeto a validar
   */
  public void validateUserRegistrationByAdminRequestDTO(
      UserRegistrationByAdminRequestDTO userRegistrationByAdminRequestDTO) {
    // Null
    if (userRegistrationByAdminRequestDTO.getFirstName() == null) {
      throw new IllegalArgumentException("121, El nombre ingresado es nulo");
    }

    if (userRegistrationByAdminRequestDTO.getLastName() == null) {
      throw new IllegalArgumentException("122, El apellido ingresado es nulo");
    }

    if (userRegistrationByAdminRequestDTO.getLanguageKey() == null) {
      throw new IllegalArgumentException("126, El idioma es nulo");
    }
    // espacios vacío
    if (!isValidText(userRegistrationByAdminRequestDTO.getFirstName())) {
      throw new IllegalArgumentException("135, El nombre no cumple el formato definido");
    }

    if (!isValidText(userRegistrationByAdminRequestDTO.getLastName())) {
      throw new IllegalArgumentException("136, El apellido no cumple el formato definido");
    }

    if (userRegistrationByAdminRequestDTO.getSecondLastName() != null &&
        !isValidText(userRegistrationByAdminRequestDTO.getSecondLastName())) {
      throw new IllegalArgumentException("137, El segundo apellido no cumple el formato definido");
    }

    if (!isValidText(userRegistrationByAdminRequestDTO.getLanguageKey())) {
      throw new IllegalArgumentException("138, El idioma no cumple el formato definido");
    }

    // Length
    if (!isValidateMaxLength(userRegistrationByAdminRequestDTO.getFirstName(), 50)) {
      throw new IllegalArgumentException("130, El nombre excede los 50 caracteres");
    }

    if (!isValidateMaxLength(userRegistrationByAdminRequestDTO.getLastName(), 50)) {
      throw new IllegalArgumentException("131, El apellido excede los 50 caracteres");
    }

    if (userRegistrationByAdminRequestDTO.getSecondLastName() != null &&
        !isValidateMaxLength(userRegistrationByAdminRequestDTO.getSecondLastName(), 50)) {
      throw new IllegalArgumentException("132, El segundo apellido excede los 50 caracteres");
    }

    if (userRegistrationByAdminRequestDTO.getLanguageKey().length() != 2) {
      throw new IllegalArgumentException("137, El idioma debe ser de 2 caracteres");
    }

    // Formato

    // Longitud mínima: 1 (nombre de usuario) + 1 (@) + 1 (dominio de segundo nivel)
    // + 2 (TLD) = 5 caracteres
    if (!isValidEmail(userRegistrationByAdminRequestDTO.getEmail(), 5, 254)) {
      throw new IllegalArgumentException("123, El correo no cumple el formato definido");
    }

    if (!isValidNickname(userRegistrationByAdminRequestDTO.getNickname(), 1, 50)) {
      throw new IllegalArgumentException("124, El nickname no cumple el formato definido");
    }
  }

  /**
   * Valida los parámetros de la clase UserProfileUpdateRequestDTO
   * 
   * @param userProfileUpdateRequestDTO Objeto a validar
   */
  public void validateUserProfileUpdateRequestDTO(UserProfileRequestDTO userProfileUpdateRequestDTO) {

    if (userProfileUpdateRequestDTO.getId() == null) {
      throw new IllegalArgumentException("120, El id ingresado es nulo");
    }

    // Espacios vacíos

    if (userProfileUpdateRequestDTO.getFirstName() != null &&
        !isValidText(userProfileUpdateRequestDTO.getFirstName())) {
      throw new IllegalArgumentException("135, El nombre no cumple el formato definido");
    }

    if (userProfileUpdateRequestDTO.getLastName() != null &&
        !isValidText(userProfileUpdateRequestDTO.getLastName())) {
      throw new IllegalArgumentException("136, El apellido no cumple el formato definido");
    }

    if (userProfileUpdateRequestDTO.getSecondLastName() != null &&
        !isValidText(userProfileUpdateRequestDTO.getSecondLastName())) {
      throw new IllegalArgumentException("137, El segundo apellido no cumple el formato definido");
    }

    if (userProfileUpdateRequestDTO.getLanguageKey() != null &&
        !isValidText(userProfileUpdateRequestDTO.getLanguageKey())) {
      throw new IllegalArgumentException("138, El idioma no cumple el formato definido");
    }

    // Length
    if (userProfileUpdateRequestDTO.getFirstName() != null &&
        !isValidateMaxLength(userProfileUpdateRequestDTO.getFirstName(), 50)) {
      throw new IllegalArgumentException("130, El nombre excede los 50 caracteres");
    }

    if (userProfileUpdateRequestDTO.getLastName() != null &&
        !isValidateMaxLength(userProfileUpdateRequestDTO.getLastName(), 50)) {
      throw new IllegalArgumentException("131, El apellido excede los 50 caracteres");
    }

    if (userProfileUpdateRequestDTO.getSecondLastName() != null &&
        !isValidateMaxLength(userProfileUpdateRequestDTO.getSecondLastName(), 50)) {
      throw new IllegalArgumentException("132, El segundo apellido excede los 50 caracteres");
    }

    if (userProfileUpdateRequestDTO.getLanguageKey() != null &&
        userProfileUpdateRequestDTO.getLanguageKey().length() != 2) {
      throw new IllegalArgumentException("134, El idioma debe ser de 2 caracteres");
    }
  }

  /**
   * Valida los parámetros de la clase UserEmailUpdateRequestDTO
   * 
   * @param UserEmailRequestDTO Objeto a validar
   */
  public void validateUserMailRequestDTO(
      UserEmailRequestDTO userEmailUpdateRequestDTO) {

    // Vericar si el id en null
    if (userEmailUpdateRequestDTO.getId() == null) {
      throw new IllegalArgumentException("120, El id ingresado es nulo");
    }
    // Verificar si es null el email

    if (!isValidEmail(userEmailUpdateRequestDTO.getEmail(), 5, 254)) {
      throw new IllegalArgumentException("123, El correo no cumple el formato definido");
    }

  }

  /**
   * Valida los parámetros de la clase UserNicknameUpdateRequestDTO
   * 
   * @param UserNicknameRequestDTO Objeto a validar
   */
  public void validateUserNicknameUpdateRequestDTO(UserNicknameRequestDTO userNicknameUpdateRequestDTO) {

    if (userNicknameUpdateRequestDTO.getId() == null) {
      throw new IllegalArgumentException("120, El id ingresado es nulo");
    }

    if (!isValidNickname(userNicknameUpdateRequestDTO.getNickname(), 1, 50)) {
      throw new IllegalArgumentException("124, El nickname no cumple el formato definido");
    }
  }

  /**
   * Valida los parámetros de la clase UserStatusUpdateRequestDTO
   * 
   * @param UserStatusRequestDTO Objeto a validar
   */
  public void validateUserStatusUpdateRequestDTO(UserStatusRequestDTO userStatusUpdateRequestDTO) {

    if (userStatusUpdateRequestDTO.getId() == null) {
      throw new IllegalArgumentException("120, El id ingresado es nulo");
    }

  }

  /**
   * Valida los parámetros de la clase UserPasswordUpdateRequestDTO
   * 
   * @param UserPasswordRequestDTO Objeto a validar
   */
  public void validateUserPasswordUpdateRequestDTO(UserPasswordRequestDTO userPasswordUpdateRequestDTO) {

    if (userPasswordUpdateRequestDTO.getId() == null) {
      throw new IllegalArgumentException("120, El id ingresado es nulo");
    }

    if (!isValidPassword(userPasswordUpdateRequestDTO.getNewPassword(), 72)) {
      throw new IllegalArgumentException("125, La contraseña no cumple el formato definido");
    }
  }

  /**
   * Valida los parámetros de la clase UserRoleUpdateRequestDTO
   * 
   * @param UserAuthorityRequestDTO Objeto a validar
   */
  public void validateUserRoleUpdateRequestDTO(UserAuthorityRequestDTO userAuthorityUpdateRequestDTO) {

    if (userAuthorityUpdateRequestDTO.getUserId() == null) {
      throw new IllegalArgumentException("120, El id ingresado es nulo");
    }

    // Obtén el primer nombre de autoridad incluso carga null si este es su valor
    String authorityName = userAuthorityUpdateRequestDTO.getAuthorities()
        .stream() // Crea un flujo a partir del conjunto
        .map(AuthorityDTO::getName) // Mapea cada AuthorityDTO a su propiedad 'name'
        .findFirst() // Obtiene el primer elemento, si existe
        .orElse(null); // Devuelve null si el flujo está vacío

    if (authorityName == null) {
      throw new IllegalArgumentException("129, El nombre del rol es nulo");
    }

    if (!isValidateMaxLength(authorityName, 50)) {
      throw new IllegalArgumentException("129, El nombre del rol excede los 50 caracteres");
    }

    if (!isValidText(authorityName)) {
      throw new IllegalArgumentException("139, El rol no cumple el formato definido");
    }
  }

  /**
   * Servicio que valida los datos proporcionados en el DTO de solicitud de
   * restablecimiento de contraseña. Se asegura de que el usuario proporcione solo
   * el correo electrónico o el nickname, pero no ambos.
   * 
   * @param passwordResetRequestDTO DTO que contiene los datos para la solicitud
   *                                de restablecimiento de contraseña.
   * @throws IllegalArgumentException Si ambos campos son nulos o si ambos están
   *                                  presentes.
   */
  public void validatePasswordResetRequestDTO(PasswordResetRequestDTO passwordResetRequestDTO) {

    if (passwordResetRequestDTO.getEmail() == null && passwordResetRequestDTO.getNickname() == null) {
      throw new IllegalArgumentException("144, Debe proporcionar solo un identificador: correo o nickname");
    }

    if (passwordResetRequestDTO.getEmail() != null &&
        !isValidEmail(passwordResetRequestDTO.getEmail(), 5, 254)) {
      throw new IllegalArgumentException("123, El correo no cumple el formato definido");
    }

    if (passwordResetRequestDTO.getNickname() != null &&
        !isValidNickname(passwordResetRequestDTO.getNickname(), 1, 50)) {
      throw new IllegalArgumentException("124, El nickname no cumple el formato definido");
    }
  }

  public void validateActivationAccount(Long id, String activationKey) {
    if (id == null) {
      throw new IllegalArgumentException("120, El id ingresado es nulo");
    }

    if (!isValidKeyValueFormat(activationKey)) {
      throw new IllegalArgumentException("154, La clave de restablecimiento no cumple el formato definido");
    }
  }

  public void validateResetPasswordDTO(ChangePasswordByResetRequestDTO changePasswordByResetRequestDTO, Long id,
      String keyValue) {

    if (id == null) {
      throw new IllegalArgumentException("120, El id ingresado es nulo");
    }

    if (!isValidPassword(changePasswordByResetRequestDTO.getNewPassword(), 72)) {
      throw new IllegalArgumentException("125, La contraseña no cumple el formato definido");
    }

    if (!isValidKeyValueFormat(keyValue)) {
      throw new IllegalArgumentException("154, La clave de restablecimiento no cumple el formato definido");
    }
  }

  /**
   * Valida los parámetros de la clase ActivateAccountRequestDTO
   * 
   * @param activateAccountRequestDTO Objeto a validar
   */
  public void validateActivateAccountRequestDTO(ActivateAccountRequestDTO activateAccountRequestDTO, Long id,
      String keyValue) {

    if (id == null) {
      throw new IllegalArgumentException("120, El id ingresado es nulo");
    }

    if (!isValidPassword(activateAccountRequestDTO.getNewPassword(), 72)) {
      throw new IllegalArgumentException("125, La contraseña no cumple el formato definido");
    }

    if (!isValidKeyValueFormat(keyValue)) {
      throw new IllegalArgumentException("154, La clave de restablecimiento no cumple el formato definido");
    }
  }

  /**
   * Valida que el texto no contiene espacios vacíos
   * 
   * @param text
   * @return true si no contiene, false lo contrario
   */
  private boolean isValidText(String text) {
    return !text.contains(" ") &&
        text.length() > 0;
  }

  /**
   * Valida que un texto no sea mayor al valor pasado por parámetro (maxLength)
   * 
   * @param data      String a validar
   * @param maxLength largo a evaluar
   * @return true o false si es mayor o menor respectivamente
   */
  private boolean isValidateMaxLength(String data, int maxLength) {
    return data.length() <= maxLength;
  }

  /**
   * Verifica si una contraseña cumple con los requisitos de longitud mínima,
   * máxima caracteres y largo máximo, y no contenga espacios vacíos.
   * Las restricciones son:
   * (?=.*[A-Z]): Asegura al menos una letra mayúscula
   * (?=.*[a-z]): Asegura al menos una letra minúscula
   * (?=.*\d): Asegura al menos un dígito.
   * (?=.*[@$!%*?&.,/]) Asegura al menos un carácter especial
   * [A-Za-z\\d@$!%*?&.,/]{12,} Asegura que la cadena tenga al menos 12 caracteres
   * caracteres permitidos son:
   * Los caracteres especiales permitidos son:
   * @ (Arroba), $ (Símbolo de dólar), ! (Signo de exclamación), % (Signo de
   * porcentaje), * (Asterisco),
   * ? (Signo de interrogación), & (Ampersand), . (Punto), , (Coma) y / (Slash)
   * Adicionalmente valida que al menos sean 12 caracteres y máximo 72
   * 
   * @param password  La contraseña a validar.
   * @param maxLength La longitud máxima permitida
   * @return true si la contraseña cumple con los requisitos; de lo contrario,
   *         false.
   */
  public boolean isValidPassword(String password, int maxLength) {
    // Expresión regular que requiere al menos 12 caracteres, incluyendo letras
    // mayúsculas, letras minúsculas, dígitos,
    // y caracteres especiales:
    //
    String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&.,/])[A-Za-z\\d@$!%*?&.,/]{12,}$";

    // Verifica si la contraseña coincide con la expresión regular;
    return password != null
        && password.length() <= maxLength
        && !password.contains(" ")
        && password.matches(regex);
  }

  /**
   * Verifica si un correo electrónico cumple con el formato estándar de un
   * correo. que tenga un tamaño mínimo uno máximo y no contenga espacio vacíos
   *
   * El correo debe seguir el patrón de tener un conjunto de caracteres
   * alfanuméricos antes del símbolo '@', seguido por un dominio que también
   * contiene caracteres alfanuméricos y al menos un punto '.' seguido de una
   * extensión de dominio.
   * 
   * Los parámetros son:
   * A-Za-z: Permite letras mayúsculas y minúsculas.
   * 0-9: Permite dígitos del 0 al 9.
   * Caracteres especiales:
   * (Punto) .
   * (Raya baja) _
   * (Signo de porcentaje) %
   * (Simbolo de suma) +
   * (Guion) -
   * 
   * Ejemplo de formato válido: usuario@dominio.com
   * 
   * @param email     El correo electrónico a validar.
   * @param minLength La longitud mínima permitida para el correo.
   * @param maxLength La longitud máxima permitida para el correo.
   * @return true si el correo electrónico cumple con el formato y la longitud; de
   *         lo contrario, false.
   */
  public boolean isValidEmail(String email, int minLength, int maxLength) {
    // Expresión regular que verifica el formato del correo electrónico.
    String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    // Verifica si el correo no es nulo, cumple con el formato de regex (ignorando
    // mayúsculas/minúsculas), y tiene una longitud válida
    return email != null &&
        email.length() >= minLength &&
        email.length() <= maxLength &&
        !email.contains(" ") &&
        email.matches(regex);
  }

  /**
   * Valida si el formato de un correo electrónico es válido.
   * Este método está diseñado para validar cualquier dirección de correo
   * electrónico
   * sin tener en cuenta la longitud mínima o máxima.
   *
   * @param email El correo electrónico que se va a validar.
   * @return true si el correo electrónico es válido según la expresión regular,
   *         false en caso contrario.
   */
  public boolean isValidGeneralEmail(String email) {
    // Expresión regular que verifica el formato del correo electrónico.
    String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    // Verifica si el correo no es nulo, cumple con el formato de regex (ignorando
    // mayúsculas/minúsculas)
    // y no contiene espacios.
    return email != null &&
        !email.contains(" ") &&
        email.matches(regex);
  }

  /**
   * Verifica si un nickname cumple con el formato permitido, la longitud
   * especificada y no contiene espacios.
   *
   * El nickname puede contener letras (mayúsculas y minúsculas), dígitos, guiones
   * bajos (_),
   * puntos (.), arrobas (@) y guiones (-). No se permiten otros caracteres ni
   * espacios en blanco.
   *
   * Ejemplo de nickname válido: "user_name-123"
   * 
   * @param nickname  El nickname a validar.
   * @param minLength La longitud mínima permitida para el nickname.
   * @param maxLength La longitud máxima permitida para el nickname.
   * @return true si el nickname cumple con el formato, la longitud y no contiene
   *         espacios; de lo contrario, false.
   */
  public boolean isValidNickname(String nickname, int minLength, int maxLength) {
    // Expresión regular que verifica el formato del nickname
    String regex = "^[_.@A-Za-z0-9-]*$";

    // Verifica si el nickname no es nulo, cumple con el formato de regex, tiene una
    // longitud válida y no contiene espacios
    return nickname != null &&
        nickname.length() >= minLength &&
        nickname.length() <= maxLength &&
        !nickname.contains(" ") &&
        nickname.matches(regex);
  }

  /**
   * Valida si el keyValue tiene el formato correcto de un UUID.
   * El formato debe ser 8-4-4-4-12 caracteres, como
   * "123e4567-e89b-12d3-a456-426614174000".
   *
   * @param keyValue La clave a validar.
   * @return true si el keyValue tiene el formato correcto, false en caso
   *         contrario.
   */
  private boolean isValidKeyValueFormat(String keyValue) {
    // Expresión regular que valida el formato de un UUID estándar.
    String uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    // Compara el keyValue con el patrón de UUID.
    return keyValue != null && keyValue.matches(uuidRegex);
  }

}
