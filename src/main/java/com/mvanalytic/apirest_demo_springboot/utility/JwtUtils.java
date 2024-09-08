package com.mvanalytic.apirest_demo_springboot.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;

// import com.mvanalytic.sugef_test_springboot_b.domain.User;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

/**
 * Utilidad para la gestión de JWT (JSON Web Tokens), que facilita la creación y
 * verificación de estos tokens.
 */
@Component
public class JwtUtils {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(JwtUtils.class);

  // La clave secreta utilizada para firmar el JWT.
  @Value("${app.jwtSecret}")
  private String jwtSecret;

  // El tiempo de expiración del JWT en milisegundos.
  @Value("${app.jwtExpirationMs}")
  private int jwtExpirationMs;

  /**
   * Obtiene la clave de firma para el token JWT.
   *
   * @return La clave de firma.
   */
  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  /**
   * Genera un token JWT usando la información de autenticación.
   *
   * @param authentication Objeto de autenticación proporcionado por Spring
   *                       Security.
   * @return Un token JWT generado para el usuario autenticado.
   */
  public String generateJwtToken(Authentication authentication) {
    UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

    // Agrega las autoridades del usuario como un "claim" en el token
    String roles = userPrincipal.getAuthorities().stream()
        // Convierte cada GrantedAuthority a su representación de cadena (nombre del
        // rol)
        .map(grantedAuthority -> grantedAuthority.getAuthority())
        // Junta todos los roles en una cadena separada por comas
        .collect(Collectors.joining(","));

    // Construye y devuelve el token JWT
    String token = Jwts.builder()
        // Establece el nombre de usuario como el "subject" del token
        .setSubject((userPrincipal.getUsername()))
        // Añade los roles del usuario como un "claim"
        .claim("roles", roles)
        // Establece la fecha de emisión del token
        .setIssuedAt(new Date())
        // Establece la fecha de expiración del token
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        // Firma el token con la clave secreta usando el algoritmo HS512
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        // Genera el token como una cadena compacta
        .compact();

    return token;
  }

  /**
   * Valida un token JWT recibido.
   *
   * @param authToken El token JWT a validar.
   * @return true si el token es válido, false si es inválido.
   */
  public boolean validateJwtToken(String authToken) {
    // Verifica si el token es nulo o está vacío antes de intentar validarlo.
    if (authToken == null || authToken.isEmpty()) {
      logger.error("El token suministrado es nulo o vacío");
      return false;
    }
    try {
      // Construye el analizador de JWT con la clave de firma
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException e) {
      logger.error("Firma del token JWT no válida: {}", e.getMessage());
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      logger.error("El token JWT ha expirado: {}", e.getMessage());
    } catch (io.jsonwebtoken.MalformedJwtException e) {
      logger.error("El token JWT está mal formado: {}", e.getMessage());
    } catch (io.jsonwebtoken.UnsupportedJwtException e) {
      logger.error("El token JWT no está soportado: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("El token JWT está vacío o tiene una cadena inválida: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("El token suministrado no es válido: {}", e.getMessage());
    }
    return false;
  }

  /**
   * Extrae el nickname del usuario del token JWT.
   *
   * @param token El JWT del que se extraerá el nickname.
   * @return El nickname del usuario (subject) del token.
   */
  public String getNicknameFromJwtToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public String getRolesFromJwtToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("roles", String.class); // Extrae los roles del token
  }
}
