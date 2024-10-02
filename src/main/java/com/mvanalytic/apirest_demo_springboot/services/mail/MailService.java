package com.mvanalytic.apirest_demo_springboot.services.mail;

import java.util.Locale;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserKey;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileResponseDTO;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Instant;

/**
 * Servicio encargado de gestionar el envío de correos electrónicos dentro de la
 * aplicación. Proporciona métodos para enviar correos de activación de cuentas,
 * notificaciones al administrador, y otros tipos de correos electrónicos con o
 * sin archivos adjuntos.
 * 
 * <p>
 * Este servicio utiliza {@link JavaMailSender} para enviar correos
 * electrónicos, y Thymeleaf como motor de plantillas para generar contenido
 * dinámico en HTML.
 * </p>
 * 
 * <p>
 * El servicio maneja excepciones relacionadas con el envío de correos, como
 * {@link MailSendException} y otras excepciones inesperadas, y registra estos
 * errores en el log utilizando {@link LoggerSingleton}.
 * </p>
 * 
 * <p>
 * El servicio está configurado para funcionar de manera asíncrona, permitiendo
 * enviar correos en segundo plano sin bloquear el hilo principal de la
 * aplicación.
 * </p>
 */
@Service
public class MailService {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(MailService.class);

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private SpringTemplateEngine templateEngine;

  @Autowired
  private AppUtility appUtility;

  // Inyecta MessageSource para obtener los textos internacionalizados
  @Autowired
  private MessageSource messageSource;

  @Value("${spring.mail.username}")
  private String sender;

  /**
   * Envía un correo electrónico de activación de cuenta al usuario proporcionado.
   * Este método construye un mensaje de activación con un enlace único para que
   * el usuario pueda activar su cuenta.
   *
   * @param user    El objeto User que representa al usuario destinatario del
   *                correo. Debe contener los detalles del usuario, como el nombre
   *                y el correo electrónico.
   * @param userKey El objeto UserKey que contiene la clave de activación
   *                (key_value) y su propósito (key_purpose) para ser utilizado en
   *                el mensaje de activación.
   * 
   * @throws MailSendException si ocurre un error durante el envío del correo
   *                           electrónico. Esta excepción se utiliza para manejar
   *                           tanto errores específicos de envío de correos como
   *                           errores inesperados.
   */
  @Async
  public void sendActivationAccount(User user, UserKey userKey) {
    try {
      // Crear el nombre completo del usuario
      String fullName = user.getFirstName() + " " + user.getLastName();
      if (user.getSecondLastName() != null) {
        fullName += " " + user.getSecondLastName();
      }

      // Configurar el Locale en función del idioma del usuario
      Locale locale = new Locale(user.getLanguageKey());

      // Forzar el Locale en el contexto actual
      LocaleContextHolder.setLocale(locale);

      // Crear el contexto de Thymeleaf y agregar variables dinámicas
      Context context = new Context(locale);
      context.setVariable("fullName", fullName);
      context.setVariable("baseUrl", appUtility.getBaseUrl());
      context.setVariable("userKey", userKey); // Pasamos el userKey completo
      // context.setVariable("user", user); // Pasamos el userKey completo

      // Procesar la plantilla HTML usando Thymeleaf
      String htmlContent = templateEngine.process("activationAccount", context);

      // Crear MimeMessage
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();

      // Usar MimeMessageHelper para facilitar la creación del correo HTML
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      helper.setFrom(sender);
      helper.setTo(user.getEmail());

      // Define el subject utilizando el MessageSource y el idioma actual
      String subject = messageSource.getMessage("email.activation.subject", null, LocaleContextHolder.getLocale());
      helper.setSubject(subject);

      // Configurar el contenido HTML del correo
      helper.setText(htmlContent, true); // true indica que es HTML
      // Adjuntar la imagen SVG como recurso embebido
      ClassPathResource imagResource = new ClassPathResource("static/img/mvanalytic_color.png");
      // FileSystemResource resource = new FileSystemResource(new
      // File("src/main/resources/static/img/mvanalytic_color.png"));
      helper.addInline("logo_mv_analytic", imagResource, "image/png");

      // Enviar el correo
      javaMailSender.send(mimeMessage);

      logger.info("Correo de activación enviado a: {}", user.getEmail());
    } catch (MessagingException e) {
      logger.error("Error al enviar el correo de activación: {}", e.getMessage());
      throw new MailSendException("140, Error al enviar el correo de activación",
          e);
    } catch (Exception e) {
      logger.error("Error inesperado al enviar el correo de activación: {}",
          e.getMessage());
      throw new MailSendException("141, Error inesperado al enviar el correo de activación", e);
    }
  }

  /**
   * Envía un correo de activación de cuenta con una contraseña temporal al
   * usuario.
   *
   * El correo contiene un enlace de activación y la contraseña temporal generada.
   * El usuario debe utilizar la contraseña temporal para acceder a un formulario
   * donde ingresará una nueva contraseña.
   *
   * @param user              El usuario al que se le enviará el correo.
   * @param userKey           Clave de activación generada para el usuario.
   * @param temporaryPassword Contraseña temporal asignada al usuario.
   * @throws MailSendException Si ocurre un error durante el envío del correo.
   */
  @Async
  public void sendActivationAccountWithTemporaryPassword(
      User user,
      UserKey userKey,
      String temporaryPassword,
      boolean isNew) {
    try {
      // Configurar el Locale en función del idioma del usuario
      Locale locale = new Locale(user.getLanguageKey());

      // Forzar el Locale en el contexto actual
      LocaleContextHolder.setLocale(locale);

      // Crear el contexto de Thymeleaf y agregar variables dinámicas
      Context context = new Context(locale);

      context.setVariable("fullName", user.getFirstName() + " " + user.getLastName() +
          (user.getSecondLastName() != null ? " " + user.getSecondLastName() : ""));
      context.setVariable("temporaryPassword", temporaryPassword);
      context.setVariable("userKey", userKey);
      context.setVariable("baseUrl", appUtility.getBaseUrl());

      // Determinar el tipo de plantilla
      String template = isNew ? "activationAccountCreateUser" : "activationAccountResend";

      // Procesar la plantilla
      String htmlContent = templateEngine.process(template, context);

      // Crear el mensaje de correo HTML
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
      helper.setFrom(sender);
      helper.setTo(user.getEmail());

      // Define el subject utilizando el MessageSource y el idioma actual
      String subject = messageSource.getMessage("email.activation.new.subject", null, LocaleContextHolder.getLocale());
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      // Adjuntar la imagen SVG como recurso embebido
      ClassPathResource imagResource = new ClassPathResource("static/img/mvanalytic_color.png");
      // FileSystemResource resource = new FileSystemResource(new
      // File("src/main/resources/static/img/mvanalytic_color.png"));
      helper.addInline("logo_mv_analytic", imagResource, "image/png");

      // Enviar el correo
      javaMailSender.send(mimeMessage);
      logger.info("Correo de activación enviado a: {}", user.getEmail());
    } catch (Exception e) {
      logger.error("Error al enviar correo: {}", e.getMessage());
      throw new MailSendException("156, Error inesperado al enviar el correo de restablecimiento de contraseña", e);
    }
  }

  /**
   * Envía un correo electrónico de restablecimiento de contraseña al usuario
   * proporcionado.
   * 
   * Este método construye un mensaje de restablecimiento de contraseña con un
   * enlace único para que el usuario pueda restablecer su contraseña.
   *
   * @param user    El objeto User que representa al usuario destinatario del
   *                correo. Debe contener los detalles del usuario, como el nombre
   *                y el correo electrónico.
   * @param userKey El objeto UserKey que contiene la clave de restablecimiento
   *                (key_value) y su propósito (key_purpose) para ser utilizado en
   *                el mensaje de restablecimiento.
   * 
   * @throws MailSendException si ocurre un error durante el envío del correo
   *                           electrónico. Esta excepción se utiliza para manejar
   *                           tanto errores específicos de envío de correos como
   *                           errores inesperados.
   */
  @Async
  public void sendPasswordReset(User user, UserKey userKey) {
    try {
      // Configurar el Locale en función del idioma del usuario
      Locale locale = new Locale(user.getLanguageKey());

      // Forzar el Locale en el contexto actual
      LocaleContextHolder.setLocale(locale);

      // Crear el contexto de Thymeleaf y agregar variables dinámicas
      Context context = new Context(locale);

      context.setVariable("fullName", user.getFirstName() + " " + user.getLastName() +
          (user.getSecondLastName() != null ? " " + user.getSecondLastName() : ""));
      context.setVariable("userKey", userKey);
      context.setVariable("baseUrl", appUtility.getBaseUrl());

      // Determinar el tipo de plantilla
      String template = "changePasswordByReset";

      // Procesar la plantilla
      String htmlContent = templateEngine.process(template, context);

      // Crear el mensaje de correo HTML
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
      helper.setFrom(sender);
      helper.setTo(user.getEmail());

      // Define el subject utilizando el MessageSource y el idioma actual
      String subject = messageSource.getMessage("email.password.reset.subject", null, LocaleContextHolder.getLocale());
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      // Adjuntar la imagen SVG como recurso embebido
      ClassPathResource imagResource = new ClassPathResource("static/img/mvanalytic_color.png");
      // FileSystemResource resource = new FileSystemResource(new
      // File("src/main/resources/static/img/mvanalytic_color.png"));
      helper.addInline("logo_mv_analytic", imagResource, "image/png");

      // Enviar el correo
      javaMailSender.send(mimeMessage);
      logger.info("Correo de activación enviado a: {}", user.getEmail());

    } catch (MailException e) {
      logger.error("Error específico al enviar el correo: {}", e.getMessage());
      throw new MailSendException("155, Error al enviar el correo de restablecimiento de contraseña", e);
    } catch (Exception e) {
      logger.error("Error inesperado al enviar el correo de restablecimiento de contraseña: {}", e.getMessage());
      throw new MailSendException("156, Error inesperado al enviar el correo de restablecimiento de contraseña", e);
    }
  }

  /**
   * Envía un correo electrónico al administrador cuando un nuevo usuario activa
   * su cuenta.
   * 
   * Este método genera un reporte de activación y lo envía al administrador
   * correspondiente con el rol 'ROLE_ADMIN'. El correo incluye detalles del
   * usuario recién activado, como su nombre completo, correo electrónico y
   * nickname, utilizando una plantilla HTML con Thymeleaf.
   * 
   * @param user      El objeto `User` que representa al usuario que ha activado
   *                  su cuenta. Este objeto contiene detalles como el nombre,
   *                  apellido, segundo apellido, email y nickname.
   * @param userAdmin El objeto `UserProfileResponseDTO` que representa al
   *                  administrador de la aplicación. Se utiliza para definir el
   *                  idioma y el correo electrónico de destino del mensaje.
   * 
   *                  Funcionalidad:
   *                  1. **Configurar el Locale**:
   *                  - Se establece el idioma que se utilizará para el correo en
   *                  función de `userAdmin.getLanguageKey()`,
   *                  que corresponde al idioma preferido del administrador.
   *                  - Se ajusta el contexto del Locale para que todas las
   *                  cadenas de texto se localicen correctamente.
   * 
   *                  2. **Crear el contexto de Thymeleaf**:
   *                  - Se genera un `Context` para Thymeleaf, al cual se le
   *                  asignan diversas variables dinámicas:
   *                  - `userFullName`: El nombre completo del usuario recién
   *                  activado.
   *                  - `adminFullName`: El nombre completo del administrador que
   *                  recibirá el correo.
   *                  - `userMail`: El correo electrónico del usuario activado.
   *                  - `userNickname`: El nickname del usuario activado.
   *                  - `baseUrl`: La URL base de la aplicación, obtenida de
   *                  `appUtility`.
   * 
   *                  3. **Procesar la plantilla**:
   *                  - Se selecciona la plantilla Thymeleaf llamada
   *                  `activationReportToAdmin` y se genera el HTML procesado con
   *                  las variables dinámicas.
   * 
   *                  4. **Preparar el correo**:
   *                  - Se utiliza `MimeMessage` para crear un correo HTML.
   *                  - El asunto (subject) del correo se localiza utilizando
   *                  `messageSource` con la clave
   *                  `email.admin.activation.subject`, que dependerá del idioma
   *                  actual del administrador.
   *                  - Se adjunta la imagen del logo de MV Analytic como recurso
   *                  embebido en el correo mediante la función `addInline`.
   * 
   *                  5. **Enviar el correo**:
   *                  - El correo HTML se envía al administrador utilizando el
   *                  correo configurado en `userAdmin.getEmail()`.
   * 
   *                  6. **Manejo de excepciones**:
   *                  - En caso de errores específicos durante el envío del
   *                  correo, se captura la excepción `MailException` y se
   *                  registra el mensaje de error, incluyendo el stack trace para
   *                  una mejor depuración.
   *                  - En caso de otros errores no previstos, se lanza una
   *                  excepción general.
   * 
   * @throws MailSendException Si ocurre algún error durante el envío del correo,
   *                           se lanza esta excepción personalizada con un código
   *                           de error y la excepción original.
   */
  @Async
  public void sendActivationReportToAdmin(User user, UserProfileResponseDTO userAdmin) {
    try {
      // Configurar el Locale en función del idioma del usuario
      Locale locale = new Locale(userAdmin.getLanguageKey());

      // Forzar el Locale en el contexto actual
      LocaleContextHolder.setLocale(locale);

      // Crear el contexto de Thymeleaf y agregar variables dinámicas
      Context context = new Context(locale);

      // nombre completo del usuario
      context.setVariable("userFullName", user.getFirstName() + " " + user.getLastName() +
          (user.getSecondLastName() != null ? " " + user.getSecondLastName() : ""));

      // nombre completo de ROLE_ADMIN
      context.setVariable("adminFullName", userAdmin.getFirstName() + " " + userAdmin.getLastName() +
          (userAdmin.getSecondLastName() != null ? " " + userAdmin.getSecondLastName() : ""));

      // definir correo del usuario para
      context.setVariable("userMail", user.getEmail());

      // definir nickname del usuario para
      context.setVariable("userNickname", user.getNickname());
      context.setVariable("baseUrl", appUtility.getBaseUrl());

      // Determinar el tipo de plantilla
      String template = "activationReportToAdmin";

      // Procesar la plantilla
      String htmlContent = templateEngine.process(template, context);

      // Crear el mensaje de correo HTML
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
      helper.setFrom(sender);
      helper.setTo(userAdmin.getEmail());

      // Define el subject utilizando el MessageSource y el idioma actual
      String subject = messageSource.getMessage("email.admin.activation.subject", null,
          LocaleContextHolder.getLocale());
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      // Adjuntar la imagen SVG como recurso embebido
      ClassPathResource imagResource = new ClassPathResource("static/img/mvanalytic_color.png");
      // FileSystemResource resource = new FileSystemResource(new
      // File("src/main/resources/static/img/mvanalytic_color.png"));
      helper.addInline("logo_mv_analytic", imagResource, "image/png");

      // Enviar el correo
      javaMailSender.send(mimeMessage);
    } catch (MailException e) {
      logger.error("Error específico al enviar el correo: {}", e.getMessage());
      logger.error("Stack Trace:", e);
      throw new MailSendException("160, Error inesperado al enviar correo de aviso", e);
    } catch (Exception e) {
      logger.error("Error inesperado al enviar el correo de aviso: {}", e.getMessage());
      logger.error("Stack Trace:", e);
      throw new MailSendException("160, Error inesperado al enviar correo de aviso", e);
    }
  }

  /**
   * Envía un correo electrónico de notificación al usuario tras un intento
   * fallido de inicio de sesión. El correo incluye detalles como la fecha, hora,
   * dirección IP y plataforma desde la que se intentó iniciar sesión.
   * 
   * @param user      El objeto {@link User} que contiene los datos del usuario al
   *                  que se le enviará el correo.
   * @param ipAddress La dirección IP desde la que se realizó el intento fallido
   *                  de inicio de sesión.
   * @param userAgent El User-Agent que describe la plataforma o navegador desde
   *                  el cual se realizó el intento de inicio de sesión.
   * @param date      el instante que se registró el intento de login
   * 
   * @throws MailSendException Si ocurre algún error durante el envío del correo.
   */
  public void sendFailedLoginAttempt(
      User user,
      String ipAddress,
      String userAgent,
      Instant date) {
    try {
      // Configurar el Locale en función del idioma del usuario
      Locale locale = new Locale(user.getLanguageKey());

      // Forzar el Locale en el contexto actual
      LocaleContextHolder.setLocale(locale);

      // Crear el contexto de Thymeleaf y agregar variables dinámicas
      Context context = new Context(locale);

      context.setVariable("fullName", user.getFirstName() + " " + user.getLastName() +
          (user.getSecondLastName() != null ? " " + user.getSecondLastName() : ""));
      context.setVariable("ipAddress", ipAddress); // Dirección IP
      context.setVariable("userAgent", userAgent); // Información del agente (plataforma)

      // dar formato a la fecha y hora
      String timeString = appUtility.formatAttemptTime(date);
      context.setVariable("date", timeString);

      // Determinar el tipo de plantilla
      String template = "failedLoginAttemptNotification";

      // Procesar la plantilla
      String htmlContent = templateEngine.process(template, context);

      // Crear el mensaje de correo HTML
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
      helper.setFrom(sender);
      helper.setTo(user.getEmail());

      // Define el subject utilizando el MessageSource y el idioma actual
      String subject = messageSource.getMessage("email.failed.login.subject", null, LocaleContextHolder.getLocale());
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      // Adjuntar la imagen PNG como recurso embebido
      ClassPathResource imagResource = new ClassPathResource("static/img/mvanalytic_color.png");

      helper.addInline("logo_mv_analytic", imagResource, "image/png");

      // Enviar el correo
      javaMailSender.send(mimeMessage);

    } catch (MailException e) {
      logger.error("Error al enviar el correo de intento fallido de login: {}", e.getMessage());
      throw new MailSendException("167, Error al enviar el correo de intento fallido de login", e);
    } catch (Exception e) {
      logger.error("Error inesperado al enviar el correo de intento fallido de login: {}", e.getMessage());
      throw new MailSendException("168, Error inesperado al enviar el correo de intento fallido de login", e);
    }
  }

  /**
   * Envía un correo electrónico notificando al usuario sobre un inicio de sesión
   * exitoso.
   * 
   * @param user      El objeto User que contiene la información del usuario que
   *                  inició sesión.
   * @param ipAddress La dirección IP desde la cual se realizó el intento de
   *                  inicio de sesión.
   * @param userAgent La información del agente de usuario (plataforma/dispositivo
   *                  desde el cual se realizó el intento).
   * @param startTime El instante en que se inició la sesión, utilizado para dar
   *                  formato a la fecha y hora.
   * 
   *                  Este método genera un correo electrónico HTML,
   *                  personalizando los detalles del inicio de sesión exitoso,
   *                  como el nombre completo del usuario, la dirección IP, la
   *                  plataforma utilizada y la fecha y hora del inicio de sesión.
   *                  El correo se envía utilizando Thymeleaf para procesar una
   *                  plantilla HTML. Si ocurre un error al enviar el correo, se
   *                  lanza una excepción `MailSendException`.
   */
  public void sendSuccessfulLoginAttempt(
      User user,
      String ipAddress,
      String userAgent,
      Instant startTime) {
    try {
      // Configurar el Locale en función del idioma del usuario
      Locale locale = new Locale(user.getLanguageKey());

      // Forzar el Locale en el contexto actual
      LocaleContextHolder.setLocale(locale);

      // Crear el contexto de Thymeleaf y agregar variables dinámicas
      Context context = new Context(locale);

      context.setVariable("fullName", user.getFirstName() + " " + user.getLastName() +
          (user.getSecondLastName() != null ? " " + user.getSecondLastName() : ""));
      context.setVariable("ipAddress", ipAddress); // Dirección IP
      context.setVariable("userAgent", userAgent); // Información del agente (plataforma)

      // dar formato a la fecha y hora
      String timeString = appUtility.formatAttemptTime(startTime);
      context.setVariable("date", timeString);

      // Determinar el tipo de plantilla
      String template = "successfulAttempt";

      // Procesar la plantilla
      String htmlContent = templateEngine.process(template, context);

      // Crear el mensaje de correo HTML
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
      helper.setFrom(sender);
      helper.setTo(user.getEmail());

      // Define el subject utilizando el MessageSource y el idioma actual
      String subject = messageSource.getMessage("email.successful.login.subject", null,
          LocaleContextHolder.getLocale());
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      // Adjuntar la imagen PNG como recurso embebido
      ClassPathResource imagResource = new ClassPathResource("static/img/mvanalytic_color.png");

      helper.addInline("logo_mv_analytic", imagResource, "image/png");

      // Enviar el correo
      javaMailSender.send(mimeMessage);

    } catch (MailException e) {
      logger.error("Error al enviar el correo de intento fallido de login: {}", e.getMessage());
      throw new MailSendException("167, Error al enviar el correo de intento fallido de login", e);
    } catch (Exception e) {
      logger.error("Error inesperado al enviar el correo de intento fallido de login: {}", e.getMessage());
      throw new MailSendException("168, Error inesperado al enviar el correo de intento fallido de login", e);
    }
  }

}
