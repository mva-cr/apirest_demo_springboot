package com.mvanalytic.apirest_demo_springboot.repositories.user;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mvanalytic.apirest_demo_springboot.domain.user.UserKey;

@Repository
public interface UserKeyRepository extends JpaRepository<UserKey, Long> {

  /**
   * Invoca el procedimiento almacenado 'sp_activate_user' para activar la cuenta
   * de un usuario.
   * <p>
   * Este método llama al procedimiento almacenado que verifica la validez de una
   * clave de activación,
   * comprueba su expiración y activa al usuario si todas las condiciones se
   * cumplen.
   * </p>
   *
   * @param id          El ID del usuario a activar.
   * @param keyValue    La clave de activación proporcionada.
   * @param expiryHours El número de horas que la clave de activación es válida
   *                    antes de expirar.
   * @throws DataAccessException Si hay un error en la ejecución del procedimiento
   *                             almacenado.
   */
  @Modifying
  @Procedure("sp_activate_account")
  void spActivateAccount(
      @Param("id") Long id,
      @Param("key_value") String keyValue,
      @Param("expiry_hours") int expiryHours);

  /**
   * Busca un registro de `UserKey` junto con la entidad `User` asociada, basado
   * en el ID de la clave. Este método utiliza una consulta personalizada con
   * `JOIN FETCH` para asegurar que la entidad `User` se cargue inmediatamente
   * junto con el `UserKey`. Esto evita problemas de carga perezosa (lazy loading)
   * y excepciones como `LazyInitializationException` cuando se accede a la
   * relación `User` fuera de un contexto transaccional o de sesión activa.
   *
   * @param id El identificador único del registro de `UserKey` en la base de
   *           datos.
   * @return Un `Optional<UserKey>` que contiene el registro de `UserKey` con la
   *         entidad `User` cargada, si se encuentra un registro con el ID
   *         proporcionado; de lo contrario, retorna un `Optional.empty()`.
   *
   * @throws IllegalArgumentException si el ID proporcionado es nulo.
   *
   *                                  Ejemplo de uso:
   * 
   *                                  <pre>
   * {@code
   * Optional<UserKey> userKey = userKeyRepository.findByIdWithUser(100L);
   * if (userKey.isPresent()) {
   *     // Procesar el UserKey y acceder a la entidad User asociada
   *     User user = userKey.get().getUser();
   * }
   * }
   * </pre>
   */
  @Query("SELECT uk FROM UserKey uk JOIN FETCH uk.user WHERE uk.id = :id")
  Optional<UserKey> findByIdWithUser(@Param("id") Long id);
}
