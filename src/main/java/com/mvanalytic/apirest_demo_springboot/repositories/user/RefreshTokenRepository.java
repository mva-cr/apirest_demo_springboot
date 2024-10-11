package com.mvanalytic.apirest_demo_springboot.repositories.user;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mvanalytic.apirest_demo_springboot.domain.user.RefreshToken;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import java.util.Optional;

/**
 * Interfaz que extiende JpaRepository para manejar operaciones de CRUD
 * relacionadas con la entidad RefreshToken. Proporciona métodos para buscar,
 * eliminar y manipular tokens de refresco en la base de datos.
 * 
 * Extiende JpaRepository, por lo que hereda métodos estándar como save(),
 * findById(), findAll(), delete(), entre otros.
 * 
 * @author [Tu nombre]
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * Busca un refresh token en la base de datos basado en su valor de token.
   * 
   * @param token El valor del token a buscar.
   * @return Optional<RefreshToken> Un Optional que contiene el RefreshToken si se
   *         encuentra, o vacío si no existe.
   */
  Optional<RefreshToken> findByToken(String token);

  /**
   * Elimina todos los refresh tokens asociados a un usuario específico.
   * 
   * @param user El usuario del que se eliminarán todos los refresh tokens.
   */
  void deleteByUser(User user);

  /**
   * Elimina todos los refresh tokens que hayan expirado antes de una fecha
   * determinada.
   * 
   * @param now La fecha y hora actual usada para comparar con la fecha de
   *            expiración de los tokens.
   */
  void deleteByExpiryDateBefore(Instant now);

  /**
   * Busca un RefreshToken asociado a un usuario específico.
   *
   * @param user El usuario para el cual se desea encontrar el token de
   *             actualización.
   * @return Un Optional que contiene el RefreshToken si existe.
   */
  Optional<RefreshToken> findByUser(User user);

  /**
   * Busca un refresh token asociado a un usuario específico utilizando el ID del
   * usuario.
   * 
   * @param userId El ID del usuario para el cual se busca el refresh token.
   * @return Un Optional que contiene el refresh token si se encuentra; de lo
   *         contrario, un Optional vacío.
   */
  Optional<RefreshToken> findByUserId(Long userId);

  /**
 * Encuentra todos los RefreshTokens cuya fecha de expiración está entre un rango de fechas específico.
 * 
 * @param startDate La fecha y hora de inicio del rango (inclusive) como un objeto Instant.
 * @param endDate La fecha y hora de finalización del rango (inclusive) como un objeto Instant.
 * @return Una lista de objetos RefreshToken cuya fecha de expiración esté entre las fechas proporcionadas.
 */
List<RefreshToken> findByExpiryDateBetween(Instant startDate, Instant endDate);


}
