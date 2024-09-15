# Aspectos técnicos de la base de datos

[Retornar a la principal](../../README.md)

## Lógica de negocio implementada en la base de datos

### Asignación del role

#### Rol default

Mediente el trigger `trg_add_user_role` que es de tipo `AFTER INSERT` en la tabla `user_mva` agregar el rol `ROLE_USER`, rol que debe estar en la tabla `authority`.

Por lo tanto, en caso de querer asignar un rol diferente, este debe cambiado directamente por el usuario con `ROLE_ADMIN`.

#### Cambio de Roles

El cambio de roles se realiza por medio del cambio en el atributo `status` de la tabla `user_mva`.

Cuando un user se le cambia el status de `true` a `false` o la inversa mediante un trigger (`trg_update_user_authority`) de tipo `AFTER UPDATE` en la tabla `user_mva` se modifica el rol asignado a este user, si el nuevo `status` es `true` o `1` hace un update al rol pasando a `ROLE_USER`.

En cambio si se cambia a `false` o `0` el `status` se cambia a `ROLE_UNAUTHORIZE`, el cual no tiene privilegios, lo que equivale a deshabilitar y quitar los roles.

### Auditorias

Se han creado las tablas de auditoria

| Tabla origen   | Tabla para auditoría |    Trigger que se aplica |
| :------------- | :------------------- | -----------------------: |
| user_mva       | user_audit           |       trg_user_mva_audit |
| user_authority | user_authority_audit | trg_user_authority_audit |

**Otras tablas de auditoría**

Adicionalmente se han creado tablas para otro tipo de seguimiento, estas son:

| Nombre de tabla | Propósito                                                |
| :-------------- | :------------------------------------------------------- |
| login_attempt   | rastrea intentos de inicio de sesión fallidos y exitosos |
| user_session    | controla y audita sesiones activas y pasadas             |

### Actualizaciones parciales

Se crearon dos procedimientos almacenados (`Store Procedure`) que contiene parámetros opcionales con valores predeterminados de `NULL`. Esto permite que la función `COALESCE` actualice el campo solo si se proporciona un valor nuevo: de lo contrario, mantiene el actual. En realidad reescribe el que ya está en la base de datos, de forma que esto provoca que se active un trigger que esté vinculado con `UPDADE` de esta tabla, por lo que se han incorporados en estos, un condición de ejecutar el contenido del `trigger` solo si el valor ha cambiado, evitando así sobre carga de la base de datos.

Estos `Procedimiento` son:

| Nombre del procedimiento     | Qué modifica                                                               | Rol que lo puede hacer |
| :--------------------------- | :------------------------------------------------------------------------- | :--------------------- |
| sp_update_user_by_role_admin | Los parámetros: `activated` y `status`                                     | Administrador          |
| sp_update_user_by_role_user  | Parámetros: `first_name`, `last_name`, `second_last_name` y `language_key` | Usuario                |

**Ventajas**

1. **Flexibilidad**: Permite actualizaciones muy flexibles sin necesidad de escribir múltiples declaraciones UPDATE para diferentes combinaciones de campos.
2. **Mantenimiento**: Facilita la mantenimiento del código al tener un único punto de actualización.
   Este enfoque simplifica la gestión de actualizaciones en aplicaciones complejas donde los usuarios pueden necesitar actualizar diferentes conjuntos de campos en diferentes momentos.

ejemplo de la forma en que se debe enviar del frontend.

```
EXEC sp_update_user_by_role_admin 1, null, true
```

## Otros

Para el idioma se utiliza el código de idiomas según ISO-639-1 (2 letras). Por ejemplo:

1. Español: es
2. Inglés: en

## Tamaño Común de activationKey

UUID (32 caracteres hexadecimales + 4 guiones):

Un UUID estándar tiene 36 caracteres en total (32 hexadecimales y 4 guiones).

Ejemplo: 123e4567-e89b-12d3-a456-426614174000

Si usas UUID para activationKey, un tamaño de NVARCHAR(36) es adecuado.

## Manejo de errores en los procedimientos para una Base `SQL Server`

Se uitiliza `RAISERROR` esta es una función de `SQL Server` que se utiliza para generar un error y enviar un mensaje al cliente. Los parámetros 16 y 1 tienen significados específicos:

- `16`: Es el nivel de severidad del error. En `SQL Server`, los niveles de severidad del 11 al 16 son errores generados por el usuario y pueden ser corregidos por el usuario. El nivel 16 indica un error general de usuario o un error de solicitud incorrecta.
- `1`: Es el estado del error, que puede ser utilizado para categorizar los errores dentro de cada nivel de severidad. El estado puede ayudar a identificar exactamente en qué punto del procedimiento se encontraba cuando ocurrió el error, especialmente útil si se lanzan varios errores diferentes dentro del mismo procedimiento almacenado.

## Manejo de procedimiento antes de crearlo

Se utiliza un código como el siguiente ejemplo:

```
IF EXISTS (SELECT *
FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[sp_change_password]') AND type in (N'P', N'PC'))
BEGIN
  DROP PROCEDURE [dbo].[sp_change_password]
END
GO
```

El fragmento de SQL que has anterior verifica la existencia de un objeto en la base de datos antes de intentar eliminarlo, y especifica ciertos tipos de objeto mediante los códigos `(N'P', N'PC')`. Aquí está el significado de estos valores:

- `P`: Representa un procedimiento almacenado regular. Es el tipo más común de procedimiento que contiene código T-SQL compilado y optimizado que se puede ejecutar repetidamente.

- `PC`: Representa un procedimiento almacenado compilado nativamente. Estos procedimientos son específicos de SQL Server y son compilados a código máquina, lo que significa que se ejecutan mucho más rápido que los procedimientos almacenados regulares. Se utilizan principalmente en aplicaciones de alto rendimiento y son parte de la funcionalidad de In-Memory OLTP de SQL Server.

El uso de `N` antes de las comillas indica que la cadena está siendo tratada como NVARCHAR, una representación de cadena que puede almacenar caracteres Unicode, lo cual es útil para la compatibilidad con múltiples idiomas y configuraciones regionales.

## Manejo de Trigger antes de crealo

Similar el caso anterior, en este ejemplo:

```
IF EXISTS (SELECT 1 FROM sys.triggers WHERE object_id = OBJECT_ID(N'[dbo].[trg_user_authority_audit]'))
BEGIN
    DROP TRIGGER [dbo].[trg_user_authority_audit];
END
GO
```

**Explicación**:

- `IF EXISTS`: Esta condición verifica la existencia del trigger especificado en la base de datos antes de intentar eliminarlo.
- `sys.triggers`: Es el catálogo de vistas en SQL Server que contiene información sobre los triggers. Se usa para buscar el trigger por su nombre.
- `OBJECT_ID`: Función que obtiene el ID del objeto para el nombre del trigger dado, facilitando la búsqueda exacta sin ambigüedad.
- `DROP TRIGGER`: Comando SQL que elimina el trigger especificado.

Este método es útil para evitar errores durante scripts de despliegue o mantenimiento que pueden ejecutarse en entornos donde el estado actual de la base de datos puede ser desconocido. Asegura que los scripts no fallarán si el trigger ya ha sido eliminado o si nunca fue creado.

[Retornar a la principal](../../README.md)
