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

// import com.mvanalytic.sugef_test_springboot_b.services.UserDetailsServiceImpl;

/**
 * Proveedor personalizado de autenticación para la aplicación.
 * Este proveedor autentica a los usuarios en función de su 'nickname' y
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

      // Verifica si el identificador contiene un '@', indicando que es un email
      // if (identifier.contains("@")) {
      //   userDetails = ((UserDetailsServiceImpl) userDetailsService).loadUserByEmail(identifier);
      // } else {
      //   userDetails = userDetailsService.loadUserByUsername(identifier);
      // }

      // Carga los detalles del usuario en función del nickname
      // UserDetails userDetails = userDetailsService.loadUserByUsername(nickname);

      // Verifica si el usuario está habilitado
      if (!userDetails.isEnabled()) {
        throw new DisabledException("Cuenta deshabilitada, contacte al administrador.");
      }

      // Verifica si las credenciales (password) son correctas
      if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
        throw new BadCredentialsException("El password ingresado es incorrecto.");
      }

      // Devuelve un token de autenticación completamente autenticado si la validación
      // es exitosa
      return new UsernamePasswordAuthenticationToken(userDetails, rawPassword, userDetails.getAuthorities());
    } catch (UsernameNotFoundException | DisabledException | BadCredentialsException e) {
      throw e;
    } catch (Exception e) {
      throw new AuthenticationException("Error de autenticación inesperado: " + e.getMessage(), e) {
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
