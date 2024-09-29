package com.mvanalytic.apirest_demo_springboot.security.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase que maneja excepciones de autenticación en la aplicación.
 * 
 * Implementa {@link AuthenticationEntryPoint} de Spring Security y se encarga
 * de
 * devolver una respuesta personalizada cuando un usuario no autenticado intenta
 * acceder a un recurso protegido.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

  /**
   * Método que se invoca cuando un usuario no autenticado intenta acceder a un
   * recurso protegido.
   * 
   * @param request       El objeto HttpServletRequest que contiene la solicitud
   *                      realizada por el cliente.
   * @param response      El objeto HttpServletResponse que contiene la respuesta
   *                      que se enviará al cliente.
   * @param authException La excepción de autenticación que se lanzó.
   * @throws IOException      Si ocurre un error de entrada/salida.
   * @throws ServletException Si ocurre un error relacionado con el servlet.
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {

    // Establece el código de estado HTTP para la respuesta de error de
    // autenticación
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    // Crear un mapa para construir el JSON de respuesta
    Map<String, String> jsonResponse = new HashMap<>();

    if (authException instanceof UsernameNotFoundException) {
      jsonResponse.put("error", "111, El identificador no existe");
    } else if (authException instanceof BadCredentialsException) {
      jsonResponse.put("error", "108, La contraseña no coincide con la registrada");
    } else if (authException instanceof DisabledException) {
      jsonResponse.put("error",
          "110, Cuenta deshabilitada ");
    } else if (authException instanceof CredentialsExpiredException) {
      jsonResponse.put("error", "502, Token expirado");
    } else if (authException instanceof InsufficientAuthenticationException) {
      jsonResponse.put("error", "501, Permisos insuficientes");
    } else {
      jsonResponse.put("error", "500, No autorizado");
    }

    // Convierte el mapa en una cadena JSON
    ObjectMapper objectMapper = new ObjectMapper();
    response.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
  }

}
