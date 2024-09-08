package com.mvanalytic.apirest_demo_springboot.services.user;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mvanalytic.apirest_demo_springboot.domain.user.Authority;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;



/**
 * Servicio para cargar los detalles de los usuarios basado en el nickname.
 * Implementa UserDetailsService que es usado por Spring Security para cargar
 * los detalles de un usuario durante la autenticación.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  // Instancia del servicio
  private final UserService userService;

  public UserDetailsServiceImpl(UserService userService) {
    this.userService = userService;
  }

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(UserDetailsServiceImpl.class);

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    return loadUser(identifier);
  }

  // public UserDetails loadUserByEmail(String email) throws
  // UsernameNotFoundException {
  // return loadUser(email, "email");
  // }

  // @Autowired
  // private UserRepository userRepository;

  /**
   * Carga un usuario por su nickname.
   * 
   * @param identifier Define la forma de cargar el usario, por el nickname o el
   *                   email.
   * @return UserDetails que contiene información del usuario cargado.
   * @throws UsernameNotFoundException Si el usuario no se encuentra en la base de
   *                                   datos o está deshabilitado.
   */

  public UserDetails loadUser(String identifier) throws UsernameNotFoundException {
    // try {
    // Busca el usuario en la base de datos por su nickname
    // Optional<User> optionalUser = userRepository.findByNickname(nickname);

    // // Si no se encuentra el usuario, lanza una excepción
    // if (optionalUser.isEmpty()) {
    // throw new UsernameNotFoundException("El nickname no existe.");
    // }
    User user;

    if (identifier.contains("@")) {
      // Verifica si es un email
      user = userService.getUserByEmail(identifier);
    } else {
      user = userService.getUserByNickName(identifier);
    }

    // Verifica si el usuario está habilitado
    if (!user.isStatus()) {
      logger.error("Cuenta deshabilitada, contacte al administrador de la aplicación.");
      throw new DisabledException("Cuenta deshabilitada, contacte al administrador de la aplicación.");
    }

    // Convierte el stream a un array de GrantedAuthority antes de pasarlo a
    // authorities()
    // UserBuilder builder =
    // org.springframework.security.core.userdetails.User.withUsername(nickname);
    // builder.password(user.getPassword());

    // Convierte las autoridades del usuario a una lista de GrantedAuthority
    List<GrantedAuthority> authoritiesList = user.getAuthorities().stream()
        // Asegura que authority es del tipo correcto
        // Convierte cada Authority a GrantedAuthority
        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
        .collect(Collectors.toList());

    // Devuelve una instancia de UserDetails utilizando la implementación de Spring
    // Security

    // builder.authorities(authoritiesList);
    // Crea una instancia de UserDetails utilizando la implementación de Spring
    // Security
    org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
        user.getNickname(),
        user.getPassword(),
        user.isActivated(), // habilitado
        true, // accountNonExpired
        true, // credentialsNonExpired
        user.isStatus(), // accountNonLocked
        authoritiesList // Lista de autoridades (roles) convertidas a GrantedAuthority
    );

    return userDetails;

    // } catch (IllegalArgumentException e) {
    // logger.error("El identificador no existe {} ", e.getMessage());
    // throw new UsernameNotFoundException("El identificador no existe: " +
    // e.getMessage(), e);
    // }
  }

  /**
   * Convierte el conjunto de autoridades de usuario en una colección de
   * GrantedAuthority, que Spring Security utiliza para la autorización.
   *
   * @param authorities El conjunto de autoridades que posee el usuario.
   * @return Una colección de GrantedAuthority.
   */

  private Collection<? extends GrantedAuthority> mapAuthorities(Set<Authority> authorities) {
    return authorities.stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
        .collect(Collectors.toList());
  }

}
