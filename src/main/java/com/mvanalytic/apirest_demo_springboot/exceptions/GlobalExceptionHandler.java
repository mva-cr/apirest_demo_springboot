package com.mvanalytic.apirest_demo_springboot.exceptions;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

/**
 * Manejador global de excepciones para toda la aplicación.
 * 
 * Esta clase captura y maneja excepciones que pueden ocurrir en cualquier parte
 * de la aplicación
 * y proporciona respuestas HTTP adecuadas con mensajes de error informativos.
 * 
 * Las excepciones manejadas aquí incluyen:
 * - EntityNotFoundException: cuando una entidad no se encuentra en la base de
 * datos.
 * - RuntimeException: para manejar errores de ejecución generales.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Maneja la excepción EntityNotFoundException lanzada en cualquier parte de la
   * aplicación.
   * 
   * @param ex La excepción EntityNotFoundException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error y un
   *         estado HTTP 404 (NOT_FOUND).
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
    // Maneja la excepción de entidad no encontrada
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getLocalizedMessage());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  /**
   * Maneja excepciones de tipo RuntimeException lanzadas en cualquier parte de la
   * aplicación.
   * 
   * @param ex La excepción RuntimeException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error genérico
   *         y un estado HTTP 500 (INTERNAL_SERVER_ERROR).
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
    // Maneja excepciones de tipo Runtime
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Maneja excepciones de tipo UsernameNotFoundException lanzadas en cualquier
   * parte de la aplicación.
   * 
   * @param ex La excepción RuntimeException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error genérico
   *         y un estado HTTP 500 (INTERNAL_SERVER_ERROR).
   */
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
    // Maneja excepciones de tipo Runtime
    Map<String, String> response = new HashMap<>();
    response.put("erro", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Maneja excepciones de tipo IllegalArgumentException lanzadas en cualquier
   * parte de la aplicación.
   * 
   * @param ex La excepción IllegalArgumentException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error genérico
   *         y un estado HTTP 400 (BAD_REQUEST).
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    // Maneja excepciones de tipo IllegalArgumentException
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Maneja excepciones de tipo CredentialsExpiredException lanzadas en cualquier
   * parte de la aplicación.
   * 
   * @param ex La excepción CredentialsExpiredException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error genérico
   *         y un estado HTTP 401 (UNAUTHORIZED).
   */
  @ExceptionHandler(CredentialsExpiredException.class)
  public ResponseEntity<Object> handleCredentialsExpiredException(CredentialsExpiredException ex) {
    // Maneja excepciones de tipo CredentialsExpiredException
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  /**
   * Maneja excepciones de tipo InsufficientAuthenticationException lanzadas en
   * cualquier
   * parte de la aplicación.
   * 
   * @param ex La excepción InsufficientAuthenticationException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error genérico
   *         y un estado HTTP 401 (UNAUTHORIZED).
   */
  @ExceptionHandler(InsufficientAuthenticationException.class)
  public ResponseEntity<Object> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
    // Maneja excepciones de tipo CredentialsExpiredException
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  /**
   * Maneja excepciones de tipo DisabledException lanzadas en
   * cualquier
   * parte de la aplicación.
   * 
   * @param ex La excepción DisabledException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error genérico
   *         y un estado HTTP 401 (UNAUTHORIZED).
   */
  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<Object> handleDisabledException(DisabledException ex) {
    // Maneja excepciones de tipo DisabledException
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  /**
   * Maneja excepciones de tipo DataAccessException lanzadas en
   * cualquier
   * parte de la aplicación.
   * 
   * @param ex La excepción DataAccessException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error genérico
   *         y un estado HTTP 500 (INTERNAL_SERVER_ERROR).
   */
  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<Object> handleDataAccessException(DataAccessException ex) {
    // Maneja excepciones de tipo DataAccessException
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Maneja excepciones de tipo AccessDeniedException lanzadas en
   * cualquier
   * parte de la aplicación.
   * 
   * @param ex La excepción AccessDeniedException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error genérico
   *         y un estado HTTP 403 (FORBIDDEN).
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
    // Maneja excepciones de tipo AccessDeniedException
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }

  /**
   * Maneja excepciones de tipo MailSendException lanzadas en cualquier parte de
   * la aplicación.
   * 
   * @param ex La excepción MailSendException capturada.
   * @return ResponseEntity que contiene un mapa con el mensaje de error y un
   *         estado HTTP 500 (INTERNAL_SERVER_ERROR).
   */
  @ExceptionHandler(MailSendException.class)
  public ResponseEntity<Object> handleMailSendException(MailSendException ex) {
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Maneja la excepción UserAlreadyActivatedException lanzada cuando se
   * intenta activar una cuenta que ya está activada.
   * 
   * Este método captura la excepción y devuelve una respuesta HTTP con el
   * código de estado 400 (BAD_REQUEST) junto con el mensaje de error
   * proporcionado en la excepción.
   *
   * @param ex La excepción UserAlreadyActivatedException capturada.
   * @return ResponseEntity que contiene el mensaje de error y el estado HTTP 400.
   */
  @ExceptionHandler(UserAlreadyActivatedException.class)
  public ResponseEntity<String> handleUserAlreadyActivatedException(UserAlreadyActivatedException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * Maneja excepciones del tipo FileNotFoundException.
   * <p>
   * Este método se invoca cuando se lanza una excepción de tipo
   * {@link FileNotFoundException} en cualquier parte de la aplicación. Crea una
   * respuesta JSON personalizada con un mensaje de error y una descripción
   * detallada del problema, devolviendo un código de estado HTTP 400 (Bad
   * Request).
   * 
   * @param ex la excepción {@link FileNotFoundException} capturada.
   * @return un objeto {@link ResponseEntity} que contiene la respuesta de error
   *         en formato JSON con el código de estado HTTP 400.
   */
  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException ex) {
    Map<String, String> response = new HashMap<>();
    response.put("error", "archivo no encontrado");
    response.put("details", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Maneja excepciones del tipo SecurityException.
   * <p>
   * Este método se invoca cuando se lanza una excepción de tipo
   * {@link SecurityException} en cualquier parte de la aplicación.
   * Crea una respuesta JSON personalizada con un mensaje de error y una
   * descripción detallada del problema, devolviendo un código de estado HTTP 403
   * (Forbidden).
   * 
   * @param ex la excepción {@link SecurityException} capturada.
   * @return un objeto {@link ResponseEntity} que contiene la respuesta de error
   *         en formato JSON con el código de estado HTTP 403.
   */
  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<Object> handleSecurityException(SecurityException ex) {
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // Devolver un código 403 Forbidden
  }

}
