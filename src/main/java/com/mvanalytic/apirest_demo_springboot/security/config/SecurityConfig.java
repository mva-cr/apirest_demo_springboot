package com.mvanalytic.apirest_demo_springboot.security.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.mvanalytic.apirest_demo_springboot.exceptions.CustomAccessDeniedHandler;
import com.mvanalytic.apirest_demo_springboot.security.handlers.AuthEntryPointJwt;
import com.mvanalytic.apirest_demo_springboot.security.jwt.AuthTokenFilter;
import com.mvanalytic.apirest_demo_springboot.services.user.UserDetailsServiceImpl;

/**
 * Configura la seguridad web utilizando Spring Security.
 * Esta configuración incluye CORS, CSRF, manejo de sesiones y rutas protegidas.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita el uso de @PreAuthorize
public class SecurityConfig {

  @Autowired
  private UserDetailsServiceImpl userDetailsServiceImpl;

  // public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl) {
  // this.userDetailsServiceImpl = userDetailsServiceImpl;
  // }

  /**
   * Configura la cadena de seguridad global para la aplicación.
   * 
   * @param http              Objeto HttpSecurity para configurar aspectos de la
   *                          seguridad web.
   * @param authEntryPointJwt Manejador personalizado para los errores de
   *                          autenticación.
   * @param authTokenFilter   Filtro que verifica la presencia de un JWT válido en
   *                          las solicitudes.
   * @return SecurityFilterChain La cadena de filtros de seguridad configurada.
   * @throws Exception Si ocurre un error en la configuración de la seguridad.
   */
  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      AuthEntryPointJwt authEntryPointJwt,
      AuthTokenFilter authTokenFilter,
      CustomAccessDeniedHandler accessDeniedHandler) throws Exception {
    http
        // Usar la fuente de configuración CORS personalizada
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // Deshabilitar CSRF si es necesario
        .csrf(csrf -> csrf.disable())
        // Configuración predeterminada de CORS
        .cors(Customizer.withDefaults())
        // Manejador personalizado para puntos de entrada no autenticados
        .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
        // Sin estado para la gestión de sesiones
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Establecimiento de las reglas de autorización a nivel de URL para la
        // aplicación web
        .authorizeHttpRequests(authz -> authz
            // Permitir acceso sin autenticación a rutas publicas
            .requestMatchers("/api/public/**", "/favicon.ico").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            // Solo usuarios con ROLE_ADMIN pueden acceder a /admin/**
            .requestMatchers(("/api/admin/**")).hasAnyAuthority("ROLE_ADMIN")
            // Solo usuarios con ROLE_USER pueden acceder a /user/**
            .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
            // Requerir autenticación para todas las demás solicitudes
            .anyRequest().authenticated())
        // Filtro JWT antes del filtro de autenticación
        .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Configura el AuthenticationManager para utilizar el UserDetailsService y el
   * PasswordEncoder.
   * Configura y expone el AuthenticationManager como un bean.
   * Este bean es crucial para el proceso de autenticación, permitiendo a Spring
   * Security
   * manejar la validación de las credenciales de usuario de manera centralizada.
   *
   * @param http El objeto HttpSecurity que es parte de la configuración de
   *             seguridad
   *             de Spring. Se utiliza para configurar aspectos como CORS, CSRF,
   *             manejo de sesiones y rutas de autorización.
   * @return AuthenticationManager El gestor de autenticación configurado, listo
   *         para
   *         ser utilizado en otros componentes o servicios que requieran
   *         autenticación.
   * @throws Exception Si hay un error en la configuración del
   *                   AuthenticationManager.
   */
  @Bean
  public AuthenticationManager authManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
    auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
    return auth.build();
  }

  /**
   * Crea y configura un {@link CorsConfigurationSource} que se utiliza para
   * definir la política CORS de la aplicación.
   * Este método especifica qué orígenes, métodos y cabezales están permitidos en
   * las peticiones cross-origin.
   *
   * @return CorsConfigurationSource El objeto de configuración CORS que define
   *         las políticas de cross-origin para la aplicación.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Lista de orígenes permitidos. Debes sustituir estas URL por las URL reales
    // desde donde esperas recibir peticiones.
    configuration.setAllowedOrigins(Arrays.asList("https//example.com"));
    // Métodos HTTP permitidos. Puedes ajustar esto según las necesidades de tu API.
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    // Cabezales permitidos en las peticiones. Estos cabezales son comunes en
    // peticiones que incluyen autenticación o información de control de caché.
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
    // Exponer ciertos cabezales a la respuesta. Útil para aplicaciones que
    // necesitan leer cabezales específicos en el lado del cliente.
    configuration.setExposedHeaders(Arrays.asList("Some-Exposed-Header"));
    // Opciones adicionales como permitir credenciales en las peticiones CORS.
    // Cambiar a `true` si tu API necesita cookies o encabezados de autorización en
    // solicitudes cross-origin.
    configuration.setAllowCredentials(true);

    // La configuración de CORS se aplica a todas las rutas de la aplicación.
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  /**
   * Proporciona el codificador de contraseñas utilizado para codificar las
   * contraseñas de los usuarios.
   * 
   * @return PasswordEncoder El codificador de contraseñas.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
