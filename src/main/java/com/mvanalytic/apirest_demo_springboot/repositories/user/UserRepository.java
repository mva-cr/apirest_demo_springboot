package com.mvanalytic.apirest_demo_springboot.repositories.user;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repositorio para la entidad {@link User}.
 * 
 * Esta interfaz extiende de {@link JpaRepository} para proporcionar operaciones
 * CRUD
 * (crear, leer, actualizar y eliminar) sobre la entidad {@link User}.
 * 
 * Además, permite la definición de métodos personalizados para realizar
 * consultas
 * específicas a la base de datos. La implementación de esta interfaz es
 * manejada automáticamente
 * por Spring Data JPA.
 * 
 * @author Mario Martínez Lanuza
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        /**
         * Busca un usuario por su clave de activación.
         * 
         * Spring Data JPA genera automáticamente consultas a partir de los nombres de
         * los métodos definidos en los repositorios. En este caso, findByActivationKey
         * generará una consulta SQL como SELECT * FROM user WHERE activation_key = ?.
         * 
         * @param activationKey La clave de activación del usuario.
         * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
         */
        // Optional<User> findByActivationKey(String activationKey);

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
         * Invoca el procedimiento almacenado `sp_create_user_and_key` para crear un
         * nuevo usuario en la tabla `user_mva` y generar una clave de activación en la
         * tabla `user_key`.
         *
         * El procedimiento realiza las siguientes acciones:
         * 1. Verifica si el correo o el nickname ya existen. Si alguno de ellos ya
         * existe, el procedimiento lanza una excepción con un código y mensaje de error
         * específico:
         * - "106, El correo ingresado ya existe" si el correo ya está registrado.
         * - "105, El nickname ingresado ya existe" si el nickname ya está registrado.
         * 2. Inserta el nuevo usuario en la tabla `user_mva` con la información
         * proporcionada.
         * 3. Genera una nueva clave de activación y la inserta en la tabla `user_key`,
         * asociando la clave con el usuario recién creado.
         * 4. Utiliza una transacción para asegurar que tanto la creación del usuario
         * como el registro de la clave se realicen de manera atómica.
         *
         * @param firstName       El primer nombre del usuario.
         * @param lastName        El primer apellido del usuario.
         * @param secondLasttName El segundo apellido del usuario (puede ser NULL).
         * @param email           El correo electrónico del usuario (debe ser único).
         * @param nickname        El apodo o nombre de usuario (debe ser único).
         * @param passwordHash    La contraseña del usuario en formato hash.
         * @param languageKey     La clave del idioma preferido del usuario.
         * @param keyValue        El valor de la clave de activación generada.
         * @param keyPurpose      El propósito de la clave, como 'ACCOUNT_ACTIVATION' o
         *                        'PASSWORD_RESET'.
         * @param created_at      El instante de la creación
         *
         * @throws IllegalArgumentException Si el correo o nickname ya existen, o si
         *                                  ocurre algún error durante el proceso.
         */
        @Modifying
        @Procedure("sp_create_user_and_key")
        Long spCreateUserAndKey(
                        @Param("first_name") String firstName,
                        @Param("last_name") String lastName,
                        @Param("second_last_name") String secondLasttName,
                        @Param("email") String email,
                        @Param("nickname") String nickname,
                        @Param("password_hash") String passwordHash,
                        @Param("language_key") String languageKey,
                        @Param("key_value") String keyValue,
                        @Param("key_purpose") String keyPurpose,
                        @Param("created_at") Instant createdAt);

        /**
         * Llama al procedimiento almacenado `sp_update_password_and_insert_key` para
         * actualizar la contraseña de un usuario en la tabla `user_mva` y registrar una
         * nueva clave en la tabla `user_key`.
         *
         * Este método realiza las siguientes acciones:
         * 1. Actualiza la columna `password_hash` en la tabla `user_mva` con una nueva
         * contraseña temporal.
         * 2. Inserta una nueva entrada en la tabla `user_key` para el mismo usuario,
         * utilizando los valores proporcionados de `key_value`, `key_purpose` y
         * `created_at`.
         * 3. Ambas operaciones se ejecutan dentro de una transacción en el
         * procedimiento almacenado.
         *
         * Parámetros:
         * - `id`: El ID del usuario en la tabla `user_mva`. Este valor es utilizado
         * tanto para la actualización de la contraseña como para asociar la clave de
         * activación en `user_key`.
         * - `tempPassword`: La nueva contraseña temporal en formato hash que será
         * almacenada en la columna `password_hash` en la tabla `user_mva`.
         * - `keyValue`: El valor de la clave de activación que será registrado en la
         * tabla `user_key`.
         * - `keyPurpose`: El propósito de la clave, como 'ACCOUNT_ACTIVATION' o
         * 'PASSWORD_RESET', que será almacenado en la tabla `user_key`.
         * - `createdAt`: La fecha y hora de creación de la clave, que será insertada en
         * la tabla `user_key`.
         *
         * @param id           El ID del usuario cuyo password se actualizará.
         * @param tempPassword La nueva contraseña temporal en formato hash.
         * @param keyValue     El valor de la clave de activación.
         * @param keyPurpose   El propósito de la clave ('ACCOUNT_ACTIVATION',
         *                     'PASSWORD_RESET', etc.).
         * @param createdAt    La fecha y hora en que la clave fue creada.
         */
        @Modifying
        @Procedure("sp_update_password_and_insert_key")
        Long spUpdatePasswordAndInsertUserKey(
                        @Param("id") Long id,
                        @Param("temp_password") String tempPassword,
                        @Param("key_value") String keyValue,
                        @Param("key_purpose") String keyPurpose,
                        @Param("created_at") Instant createdAt

        );

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
        // @Modifying
        // @Procedure("sp_user_authority_update")
        // void spUserAuthorityUdateUser(
        // @Param("id_user") Long id,
        // @Param("Role") String authorityName);

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

        /**
         * Llama al procedimiento almacenado para restablecer la contraseña de un
         * usuario.
         * 
         * Este método utiliza el identificador del usuario, la clave de
         * restablecimiento, la nueva contraseña y un límite de horas para determinar si
         * la clave ha expirado.
         * 
         * @param id          ID del usuario, correspondiente al registro en la tabla
         *                    'user_mva'.
         * @param keyValue    La clave de restablecimiento generada para el usuario.
         * @param newPassword El nuevo password (en formato hash) que reemplazará el
         *                    existente.
         * @param expiryHours El tiempo límite en horas para que la clave de
         *                    restablecimiento sea válida.
         * 
         * @throws IllegalArgumentException Si algún parámetro es inválido o la clave de
         *                                  restablecimiento ha expirado.
         */
        @Modifying
        @Procedure("sp_change_password_by_reset")
        void spChangePasswordByReset(
                        @Param("id") Long id,
                        @Param("key_value") String keyValue,
                        @Param("new_password") String newPassword,
                        @Param("expiry_hours") int expiryHours);

        /**
         * Consulta que obtiene el primer usuario con el rol de 'ROLE_ADMIN'.
         * 
         * Este query realiza las siguientes operaciones:
         * 
         * 1. **SELECT TOP 1**: Selecciona la primera fila que cumple con los criterios
         * de búsqueda.
         * El uso de `TOP 1` asegura que solo se devolverá un único resultado (el primer
         * usuario con el rol 'ROLE_ADMIN').
         * 
         * 2. **Columnas seleccionadas**:
         * - `u.id`: El ID del usuario en la tabla `user_mva`.
         * - `u.first_name`: El primer nombre del usuario.
         * - `u.last_name`: El primer apellido del usuario.
         * - `u.second_last_name`: El segundo apellido del usuario (puede ser `NULL`).
         * - `u.nickname`: El apodo o nombre de usuario del sistema.
         * - `u.email`: La dirección de correo electrónico del usuario.
         * - `u.language_key`: El código de idioma preferido del usuario (e.g., 'es'
         * para español, 'en' para inglés).
         * 
         * 3. **FROM user_authority ua**: La consulta empieza a partir de la tabla
         * `user_authority`, que relaciona usuarios y roles.
         * 
         * 4. **INNER JOIN user_mva u**: Realiza una unión interna entre la tabla
         * `user_authority` y `user_mva` para obtener los detalles completos del
         * usuario. La condición de la unión es que el campo `user_id` de la tabla
         * `user_authority` coincida con el campo `id` de la tabla `user_mva`.
         * 
         * 5. **WHERE ua.authority_name = 'ROLE_ADMIN'**: Filtra los resultados para que
         * solo se devuelvan los usuarios que tienen el rol de 'ROLE_ADMIN'.
         * 
         * 6. **ORDER BY ua.user_id ASC**: Ordena los resultados en orden ascendente
         * basado en el `user_id` para asegurarse de que se devuelva el usuario con el
         * ID más bajo (el primer usuario registrado con ese rol).
         * 
         * 7. **Resultado**: El query devolverá un conjunto de resultados con las
         * columnas seleccionadas para el primer usuario que tenga el rol de
         * 'ROLE_ADMIN'.
         * 
         * @return Lista de objetos con los detalles del primer usuario con el rol
         *         'ROLE_ADMIN'. Cada objeto es un array de objetos donde cada posición
         *         corresponde a una columna seleccionada en el query.
         */
        @Query(value = """
                        SELECT TOP 1
                            u.id,
                            u.first_name,
                            u.last_name,
                            u.second_last_name,
                            u.nickname,
                            u.email,
                            u.language_key
                        FROM
                            user_authority ua
                        INNER JOIN
                            user_mva u ON u.id = ua.user_id
                        WHERE
                            ua.authority_name = 'ROLE_ADMIN'
                        ORDER BY
                            ua.user_id ASC
                        """, nativeQuery = true)
        List<Object[]> findFirstAdminUser();

        /**
         * Actualiza el password_hash de un usuario en la base de datos.
         *
         * @param id       El ID del usuario.
         * @param password El nuevo password hasheado.
         */
        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
        void updatePasswordById(@Param("id") Long id, @Param("password") String password);

}
