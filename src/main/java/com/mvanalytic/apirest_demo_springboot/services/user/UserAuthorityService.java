package com.mvanalytic.apirest_demo_springboot.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.AuthorityDTO;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserAuthorityRequestDTO;
import com.mvanalytic.apirest_demo_springboot.repositories.user.UserAuthorityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class UserAuthorityService {

  @Autowired
  private UserService userService;

  @Autowired
  private UserAuthorityRepository userAuthorityRepository;

  /**
   * Para cuando se necesita manejar procedimientos almacenados que no se ajustan
   * bien a los métodos simples
   * del repositorio.
   * Permite manejar la sincronización con la base de datos.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Actualiza el rol de un usuario existente.
   * Solo ROLE_ADMIN puede acceder a este método
   *
   * @param user El usuario con la información actualizada.
   * @return El usuario actualizado.
   */
  @Transactional
  public User updateUserAuthority(UserAuthorityRequestDTO userAuthorityRequestDTO) {
    try {
      // Obtener el usuario por ID, esto lanzará DataAccessException si no se
      // encuentra
      User user = userService.getUserById(userAuthorityRequestDTO.getUserId());

      // Lista de autoridades del usuario
      Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

      // obtener el primer elemento de la colección de autoridaded
      GrantedAuthority firsAuthority = authorities
          .stream()
          .findFirst()
          .orElse(null);

      if (firsAuthority != null) {

      }
      String currentAuthority = null;

      if (firsAuthority != null) {
        currentAuthority = firsAuthority.getAuthority();
      }

      // Obtén el primer nombre de autoridad incluso carga null si este es su valor
      String authorityName = userAuthorityRequestDTO.getAuthorities()
          // Crea un flujo a partir del conjunto
          .stream()
          // Mapea cada AuthorityDTO a su propiedad 'name'
          .map(AuthorityDTO::getName)
          // Obtiene el primer elemento, si existe
          .findFirst()
          // Devuelve null si el flujo está vacío
          .orElse(null);

      // Verificar si la autoridad (Role) ya está asignado al usuario
      if (currentAuthority != null &&
          currentAuthority.equals(authorityName)) {
        throw new IllegalArgumentException("103, El nuevo valor no puede ser igual al valor actual");
      }

      // Llamar al procedimiento almacenado para actualizar el usuario
      userAuthorityRepository.spUserAuthorityUdateUser(
          userAuthorityRequestDTO.getUserId(),
          authorityName);

      /**
       * Limpiar la caché de primer nivel de Hibernate: Puedes usar EntityManager
       * para limpiar la caché de primer nivel antes de volver a cargar el usuario.
       */
      entityManager.flush();
      /**
       * Refrescar la entidad: Usar el método refresh de EntityManager para forzar
       * la sincronización con la base de datos.
       */
      entityManager.clear();

      // Retornar el usuario actualizado
      return userService.getUserById(userAuthorityRequestDTO.getUserId());

    } catch (DataAccessException e) {
      // Lanza una excepción de tiempo de ejecución con un mensaje más específico
      throw new RuntimeException(e.getMostSpecificCause().getMessage());
    } catch (IllegalArgumentException e) {
      throw e; // Relanzar la excepción para que sea manejada por un controlador global de
               // excepciones
    }
  }

}
