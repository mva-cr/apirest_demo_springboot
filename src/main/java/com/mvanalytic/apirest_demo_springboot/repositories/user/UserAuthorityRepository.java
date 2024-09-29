package com.mvanalytic.apirest_demo_springboot.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mvanalytic.apirest_demo_springboot.domain.user.UserAuthority;
import com.mvanalytic.apirest_demo_springboot.domain.user.UserAuthorityId;

/**
 * Repositorio de la entidad UserAuthority que gestiona las operaciones CRUD
 * relacionadas
 * con la relación entre usuarios y autoridades (roles).
 */
@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, UserAuthorityId> {

      /**
       * Llama a un procedimiento almacenado para actualizar el role de un usuario.
       * El parámetro no debe ser `null`.
       * Este método solo puede ser accedido por el "ROLE_ADMIN"
       *
       * @param id            ID del usuario
       * @param authorityName Rol del User (no puede ser null)
       */
      @Modifying
      @Procedure("sp_user_authority_update")
      void spUserAuthorityUdateUser(
                  @Param("id_user") Long id,
                  @Param("Role") String authorityName);


}
