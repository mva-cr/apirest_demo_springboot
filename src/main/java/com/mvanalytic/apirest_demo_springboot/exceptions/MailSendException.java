package com.mvanalytic.apirest_demo_springboot.exceptions;

/**
 * Excepción personalizada para manejar errores específicos relacionados con el
 * envío de correos electrónicos.
 * 
 * Esta excepción se utiliza cuando ocurre un problema al enviar un correo
 * electrónico, ya sea por problemas de
 * conexión con el servidor SMTP, credenciales incorrectas, dirección de correo
 * no válida, entre otros.
 * Extiende RuntimeException, lo que permite que esta excepción sea lanzada en
 * cualquier lugar del código sin la necesidad
 * de declararla explícitamente en la firma del método.
 */
public class MailSendException extends RuntimeException {

  /**
   * Constructor que crea una nueva instancia de MailSendException con un mensaje
   * de error detallado
   * y la causa original del error.
   * 
   * @param message El mensaje detallado que describe el error ocurrido.
   * @param cause   La excepción original (causa) que produjo este error, útil
   *                para el seguimiento del problema.
   */
  public MailSendException(String message, Throwable cause) {
    super(message, cause);
  }

}
