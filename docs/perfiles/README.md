# Atributos según el rol asignado

## Atributos que puede modificar el Administrador:

1. `activated`: El administrador debería poder activar o desactivar cuentas de usuario para gestionar el acceso al sistema.
2. `status`: Similar a activated, el estado puede ser utilizado para habilitar o deshabilitar cuentas basado en criterios administrativos.
3. `authorities` (roles y permisos): Solo el administrador debería modificar los roles o permisos de un usuario para asegurar que los cambios en los privilegios sean controlados centralizadamente.
   reset_key, activation_key: Estos atributos están relacionados con la seguridad y el proceso de recuperación de cuentas, y su manipulación debería ser restringida al administrador si es necesario para asistir en procesos de soporte o restablecimiento de cuenta.
4. `reset_date`: Administrar cuándo fue la última vez que se reseteó la cuenta puede ser también tarea administrativa para seguimientos de seguridad.

## Atributos que puede modificar el Propio Usuario:

1. `firstName`, `lastName`, `secondLastName`: Información básica del perfil que el usuario debería poder actualizar.
2. `email`: Los usuarios deberían poder actualizar su correo electrónico, aunque esto a menudo requiere una verificación posterior para confirmar la propiedad del nuevo correo.
3. `nickname`: Permitir a los usuarios cambiar su nombre de usuario puede estar permitido, aunque podría requerir comprobaciones adicionales para asegurar que el nuevo nombre no esté ya en uso.
4. `password`: Los usuarios siempre deben poder cambiar su propia contraseña.
5. `languageKey`: Permitir a los usuarios seleccionar su preferencia de idioma mejora la usabilidad y accesibilidad del sistema.

## Consideraciones Adicionales:

1. **Validaciones y auditorías**: Cualquier cambio en la información crítica, especialmente realizada por el administrador, debe ser auditada y validada. Esto incluye mantener registros de cuándo y quién realizó cambios en atributos sensibles.
2. **Seguridad y privacidad**: Asegurar que los cambios en los datos del usuario se hagan de manera segura y que se respete la privacidad del usuario es fundamental.
3. **Políticas de la empresa**: Las políticas internas de la empresa pueden dictar restricciones adicionales o proporcionar directrices sobre cómo y quién puede modificar ciertos datos del usuario.
