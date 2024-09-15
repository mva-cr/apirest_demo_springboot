package com.mvanalytic.apirest_demo_springboot.security.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mvanalytic.apirest_demo_springboot.services.user.UserDetailsServiceImpl;
import com.mvanalytic.apirest_demo_springboot.utility.JwtUtils;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

/**
 * Filtro que se ejecuta una vez por solicitud para comprobar la existencia de
 * un token JWT válido.
 * Si existe un token válido, establece la autenticación en el contexto de
 * seguridad.
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(AuthTokenFilter.class);

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsServiceImpl;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    try {
      // Obtiene el token JWT del encabezado de la solicitud
      String jwt = parseJwt(request);

      // Verifica si la URL de la solicitud es una de las que no requiere token
      String requestUri = request.getRequestURI();
      if (requestUri.startsWith("/api/auth")) {
        // Si es una solicitud de login, no se requiere token, así que continúa
        filterChain.doFilter(request, response);
        return;
      }

      // Verifica si el token existe y es válido
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String userName = jwtUtils.getNicknameFromJwtToken(jwt);
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userName);

        // Configura la autenticación en el contexto de seguridad
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        // Si el token es nulo o no es válido, se registra un mensaje de error
        logger.error("Token JWT nulo o inválido para la solicitud a {}", request.getRequestURI());
        throw new InsufficientAuthenticationException(
            "153, Token JWT nulo o inválido " + request.getRequestURI());
      }

    } catch (CredentialsExpiredException e) {
      logger.error("Las credenciales han expirado: {}", e.getMessage());
      // Configura la respuesta con un error 401 y el mensaje correspondiente
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: 152, Token expirado");
      return;
    } catch (InsufficientAuthenticationException e) {
      // Manejo de excepción para autenticación insuficiente
      logger.error("Error de autenticación insuficiente: {}", e.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: 157, Autenticación insuficiente");
    } catch (Exception e) {
      logger.error("No se puede configurar la autenticación del usuario: {}", e.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "Error: 154, No se pudo configurar autenticación " + e.getMessage());
    }

    // Continúa con el siguiente filtro en la cadena

    filterChain.doFilter(request, response);
  }

  /**
   * Método para extraer el token JWT de la cabecera 'Authorization' de la
   * solicitud.
   *
   * @param request La solicitud HTTP.
   * @return El token JWT o null si no está presente.
   */
  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }
    return null;
  }

}
