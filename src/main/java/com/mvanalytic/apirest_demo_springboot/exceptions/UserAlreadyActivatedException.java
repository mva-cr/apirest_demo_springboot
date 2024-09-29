package com.mvanalytic.apirest_demo_springboot.exceptions;

/**
 * Excepción personalizada lanzada cuando se intenta activar un usuario que ya
 * está activado.
 * 
 * Esta excepción se utiliza para proporcionar un manejo más específico y
 * controlado de este tipo de error.
 */
public class UserAlreadyActivatedException extends RuntimeException {

  /**
   * Crea una nueva instancia de UserAlreadyActivatedException con el mensaje
   * proporcionado.
   * 
   * @param message El mensaje que describe el error.
   */
  public UserAlreadyActivatedException(String message) {
    super(message);
  }
}
