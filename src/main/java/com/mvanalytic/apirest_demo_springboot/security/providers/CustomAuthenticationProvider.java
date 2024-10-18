package com.mvanalytic.apirest_demo_springboot.security.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;



/**
 * Proveedor personalizado de autenticación para la aplicación.
 * Este proveedor recibe las excepciones y las retorna al frontent
 * 'password'.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder; // Inyecta el PasswordEncoder

  /**
   * Realiza la autenticación del usuario con el nickname y password
   * proporcionados.
   *
   * @param authentication el objeto de autenticación que contiene el nickname y
   *                       el password.
   * @return el objeto de autenticación completamente autenticado si las
   *         credenciales son válidas.
   * @throws AuthenticationException si la autenticación falla (credenciales
   *                                 incorrectas, usuario deshabilitado, etc.).
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    try {
      // Obtiene el nickname y el password proporcionados por el usuario
      String identifier = authentication.getName();
      String rawPassword = authentication.getCredentials().toString();

      UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);

      // Verifica si el usuario está habilitado
      if (!userDetails.isEnabled()) {
        throw new DisabledException("110, Cuenta deshabilitada");
      }

      // Verifica si las credenciales (password) son correctas
      if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
        throw new BadCredentialsException("108, La contraseña no coincide con la registrada");
      }

      // Devuelve un token de autenticación completamente autenticado si la validación
      // es exitosa
      return new UsernamePasswordAuthenticationToken(userDetails, rawPassword, userDetails.getAuthorities());
    } catch (UsernameNotFoundException | DisabledException | BadCredentialsException e) {
      throw e;
    } catch (Exception e) {
      throw new AuthenticationException("155, Error en tiempo de ejecución " + e.getMessage(), e) {
      };
    }
  }

  /**
   * Indica si este proveedor de autenticación soporta el tipo de autenticación
   * proporcionado.
   *
   * @param authentication el tipo de autenticación
   * @return true si este proveedor soporta la autenticación proporcionada, false
   *         en caso contrario.
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
