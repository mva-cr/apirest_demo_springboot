# Proceso de creación de un usuario

[Retornar a la principal](../../README.md)

## Ciclo de vida del usuario relacionado con la activación y recuparación de la cuenta

### `activationKey`

**Uso**: Activación de la cuenta de usuario.

**Objetivo**: La variable activationKey se utiliza para almacenar un código o token único que se genera cuando un nuevo usuario se registra. Este código se envía al correo electrónico del usuario como parte del proceso de activación de la cuenta.

**Proceso de activación**:

1. **Registro del usuario**: Cuando un usuario se registra, el sistema genera un activationKey único (generalmente un UUID o una cadena de caracteres aleatoria).
2. **Envío de email**: El activationKey se envía al correo electrónico del usuario recién registrado en un enlace de activación.
   #. **Click en el enlace**: El usuario debe hacer clic en el enlace de activación que contiene el `activationKey`. Este enlace generalmente apunta a un endpoint de la API que verifica el `activationKey`.
3. **Verificación del código**: Cuando el usuario hace clic en el enlace, el sistema verifica el activationKey proporcionado contra el almacenado en la base de datos.
4. **Activación de la cuenta**: Si el `activationKey` es válido, el sistema marca la cuenta del usuario como activada `(activated = true)`. Después de la activación, el activationKey puede ser eliminado o dejarse como referencia histórica.

## `resetKey`

**Uso**: Restablecimiento de la contraseña del usuario.

**Objetivo**: La variable resetKey se utiliza cuando un usuario solicita restablecer su contraseña. Este campo almacena un código o token único generado por el sistema cuando se solicita el restablecimiento de la contraseña.

**Proceso de restablecimiento de contraseña:**

1. **Solicitud de restablecimiento**: El usuario inicia el proceso de restablecimiento de contraseña proporcionando su correo electrónico o nombre de usuario.
2. **Generación del resetKey**: El sistema genera un `resetKey` único (generalmente un UUID o una cadena de caracteres aleatoria).
3. **Envío del correo de restablecimiento**: Este `resetKey` se envía al correo electrónico del usuario junto con un enlace para restablecer la contraseña.
4. **Click en el enlace de restablecimiento**: El usuario hace clic en el enlace, que apunta a un endpoint de la API donde se verifica el `resetKey`.
5. **Verificación del código**: El sistema verifica que el `resetKey` proporcionado coincide con el que se generó y almacenó para el usuario.
6. **Restablecimiento de la contraseña**: Si el `resetKey` es válido, el usuario puede ingresar una nueva contraseña. Una vez completado, el `resetKey` puede ser eliminado o dejarse como referencia para evitar usos múltiples.

## `resetDate`

**Uso**: Control del tiempo para el restablecimiento de contraseña.

**Objetivo**: La variable `resetDate` almacena la fecha y hora en la que se generó el `resetKey` para el restablecimiento de la contraseña. Esto se utiliza para establecer un límite de tiempo para que el usuario pueda usar el `resetKey`.

**Proceso de verificación de tiempo:**

1. **Registro de la fecha**: Cuando se genera un `resetKey` para restablecer la contraseña, la fecha y hora actuales se almacenan en `resetDate`.
2. **Límite de tiempo**: El sistema establece un límite de tiempo (por ejemplo, 24 horas) durante el cual el `resetKey` es válido.
3. **Verificación de tiempo**: Cuando el usuario intenta restablecer su contraseña usando el `resetKey`, el sistema verifica que la diferencia entre la hora actual y `resetDate` esté dentro del límite de tiempo permitido.
4. **Expiración**: Si el `resetKey` ha expirado (es decir, el tiempo permitido ha pasado), el usuario no podrá usar ese código para restablecer la contraseña y deberá solicitar un nuevo restablecimiento.

[Retornar a la principal](../../README.md)
