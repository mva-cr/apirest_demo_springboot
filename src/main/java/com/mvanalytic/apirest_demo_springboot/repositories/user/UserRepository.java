package com.mvanalytic.apirest_demo_springboot.repositories.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario en la base de datos por su correo electrónico.
     *
     * @param email el correo electrónico del usuario que se desea encontrar.
     * @return un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario en la base de datos por su nombre de usuario (nickname).
     *
     * @param nickname el nombre de usuario del usuario que se desea encontrar.
     * @return un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    Optional<User> findByNickname(String nickname);

    /**
     * Llama a un procedimiento almacenado para actualizar parámetros de
     * un usuario. Los parámetros que son `null` no actualizarán sus
     * correspondientes
     * columnas gracias al uso de COALESCE en el procedimiento almacenado.
     * Este método solo puede ser accedido por el 'ROLE_ADMIN'
     *
     * @param id        ID del usuario
     * @param activated Estado de activación del usuario (puede ser null)
     * @param status    Estado del usuario (puede ser null)
     */
    @Modifying
    @Procedure("sp_update_user_by_role_admin")
    void spUpdateUserByRoleAdmin(
            @Param("id_user") Long id,
            @Param("activated") Boolean activated,
            @Param("status") Boolean status);

    /**
     * Llama a un procedimiento almacenado para actualizar el role de un usuario.
     * El parámetro no debe ser `null`.
     * Este método solo puede ser accedido por el "ROLE_ADMIN"
     *
     * @param id            ID del usuario
     * @param authorityName Rol del User (no puede ser null)
     */
    @Modifying
    @Procedure("sp_update_user_role_with_status_check")
    void spUdateUserRoleWithStatusCheck(
            @Param("id_user") Long id,
            @Param("Role") String authorityName);

    /**
     * Llama a un procedimiento almacenado para actualizar parámetros de
     * un usuario. Los parámetros que son `null` no actualizarán sus
     * correspondientes
     * columnas gracias al uso de COALESCE en el procedimiento almacenado.
     * Este método solo puede ser accedido por el "ROLE_USER"
     *
     * @param id             ID del usuario
     * @param firstName      Primer nombre del usuario (puede ser null)
     * @param lastName       Apellido del usuario (puede ser null)
     * @param secondLastName Segundo apellido del usuario (puede ser null)
     * @param languageKey    Clave del idioma del usuario (puede ser null)
     */
    @Modifying
    @Procedure("sp_update_user_by_role_user")
    void spUpdateUserByRoleUser(
            @Param("id_user") Long id,
            @Param("first_name") String firstName,
            @Param("last_name") String lastName,
            @Param("second_last_name") String secondLastName,
            @Param("language_key") String lenguageKey);

    /**
     * Llama a un procedimiento almacenado para actualizar el nickname de un
     * usuario.
     * El parámetro no debe ser `null`
     * Este método solo puede ser accedido por el "ROLE_USER"
     *
     * @param id       ID del usuario
     * @param nickname Nuevo nickname (no puede ser null)
     */
    @Modifying
    @Procedure("sp_change_nickname")
    void spChangeNickname(
            @Param("id_user") Long id,
            @Param("NewNickname") String nickname);

    /**
     * Llama a un procedimiento almacenado para actualizar el email de un usuario.
     * El parámetro no debe ser `null`
     * Este método solo puede ser accedido por el "ROLE_USER"
     *
     * @param id    ID del usuario
     * @param email Nuevo email (no puede ser null)
     */
    @Modifying
    @Procedure("sp_change_email")
    void spChangeEmail(
            @Param("id_user") Long id,
            @Param("NewEmail") String email);

    /**
     * Llama a un procedimiento almacenado para actualizar el email de un usuario.
     * El parámetro no debe ser `null`
     * Este método solo puede ser accedido por el "ROLE_USER"
     *
     * @param id          ID del usuario
     * @param newPassword Nuevo password en hash (no puede ser null)
     * @param oldPassword Actual password en hash (no puede ser null)
     */
    @Modifying
    @Procedure("sp_change_password")
    void spChangePassword(
            @Param("id_user") Long id,
            @Param("NewPassword") String newPassword,
            @Param("CurrentPassword") String oldPassword);

}
