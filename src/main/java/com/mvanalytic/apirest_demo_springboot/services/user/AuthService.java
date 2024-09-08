package com.mvanalytic.apirest_demo_springboot.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mvanalytic.apirest_demo_springboot.dto.user.JwtResponse;
import com.mvanalytic.apirest_demo_springboot.utility.JwtUtils;


/**
 * Servicio para gestionar la autenticación de usuarios.
 * Utiliza AuthenticationManager para validar las credenciales del usuario y
 * JwtUtils para generar tokens JWT.
 */
@Service
public class AuthService {

  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtils jwtUtils;

  // private UserService userService;

  // @Autowired
  public AuthService(@Lazy AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
  }

  /**
   * Método privado para autenticar al usuario ya sea por nickname o email.
   *
   * @param identifier El identificador del usuario (nickname o email).
   * @param password   La contraseña del usuario.
   * @return Respuesta JWT que contiene el token y el nombre de usuario.
   */
  public JwtResponse authenticateUser(String identifier, String password) {
    try {
      // Intenta autenticar usando el AuthenticationManager
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(identifier, password));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      // Se obtiene el UserDetails desde la autenticación
      // UserDetails userDetails = (UserDetails) authentication.getPrincipal();

      // Genera el token JWT
      String jwt = jwtUtils.generateJwtToken(authentication);

      // Devuelve la respuesta del JWT con el username del UserDetails
      JwtResponse jwtResponse = new JwtResponse();
      // Se crea y se carga el User según su identifier
      // User user = new User();
      // if (identifier.contains("@")) {
      // user = userService.getUserByEmail(identifier);
      // } else {
      // user = userService.getUserByNickName(identifier);
      // }
      // Se transforma el User a JwtResponse
      // jwtResponse = UserMapper.convertUserToJwtResponse(user);
      // Se carga el token
      jwtResponse.setToken(jwt);
      // jwtResponse.setNickName(userDetails.getUsername());
      // jwtResponse.setStatusCode(200);
      // jwtResponse.set(jwt);
      // return jwtResponse;
      return jwtResponse;

    } catch (UsernameNotFoundException e) {
      // Manejo de excepción cuando el nickname no existe
      throw new UsernameNotFoundException("El identificador no existe.");
    } catch (BadCredentialsException e) {
      // Maneja el caso cuando el password es incorrecto
      throw new BadCredentialsException("El password ingresado es incorrecto.");

    } catch (CredentialsExpiredException e) {
      // Maneja el caso cuando las credenciales han expirado
      throw new CredentialsExpiredException("Las credenciales han expirado. Por favor, inicia sesión nuevamente.");

    } catch (DisabledException e) {
      // Maneja el caso cuando la cuenta está deshabilitada
      throw new DisabledException("Cuenta deshabilitada, contacte al administrador.");
    } catch (Exception e) {
      // Manejo de cualquier otra excepción de autenticación
      throw new RuntimeException("Error en la autenticación: " + e.getMessage(), e);
    }

  }

}
