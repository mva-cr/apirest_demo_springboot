package com.mvanalytic.apirest_demo_springboot.exceptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase que maneja excepciones de autorización en la aplicación.
 * 
 * Implementa {@link AccessDeniedHandler} de Spring Security y se encarga de
 * devolver una respuesta personalizada cuando un usuario autenticado intenta
 * acceder a un recurso sin los permisos necesarios.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  /**
   * Método que se invoca cuando un usuario autenticado intenta acceder a un
   * recurso protegido sin los permisos adecuados.
   * 
   * @param request               El objeto HttpServletRequest que contiene la
   *                              solicitud realizada por el cliente.
   * @param response              El objeto HttpServletResponse que contiene la
   *                              respuesta que se enviará al cliente.
   * @param accessDeniedException La excepción de acceso denegado que se lanzó.
   * @throws IOException      Si ocurre un error de entrada/salida.
   * @throws ServletException Si ocurre un error relacionado con el servlet.
   */
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    // Configura el código de respuesta
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");

    // Define el mensaje de error
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("error", "501, Permisos insuficientes");
    errorDetails.put("message", accessDeniedException.getMessage());
    errorDetails.put("timestamp", System.currentTimeMillis());

    // Serializa el mensaje de error en JSON y lo envía en la respuesta
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.writeValue(response.getOutputStream(), errorDetails);
  }

}
