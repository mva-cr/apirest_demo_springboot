package com.mvanalytic.apirest_demo_springboot.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    response.put("erro", "Error en el servidor< " + ex.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Maneja excepciones de tipo UsernameNotFoundException lanzadas en cualquier
   * parte de la
   * aplicación.
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

}
