package com.mvanalytic.apirest_demo_springboot.security.handlers;

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

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    // Configura el código de respuesta
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");

    // Define el mensaje de error
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("error", "Insufficient permissions");
    errorDetails.put("message", accessDeniedException.getMessage());
    errorDetails.put("timestamp", System.currentTimeMillis());

    // Serializa el mensaje de error en JSON y lo envía en la respuesta
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.writeValue(response.getOutputStream(), errorDetails);
  }

}
