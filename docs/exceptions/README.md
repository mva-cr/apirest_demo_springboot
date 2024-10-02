# Implementación de manejo de errores

[Retornar a la principal](../../README.md)

## `GlobalExceptionHandler`

Es un manejador global de excepciones que se encarga de capturar excepciones que se lanzan en cualquier parte de la aplicación, la cuales por supuesto se deben agregar los respectivos método

Utilizar la anotación `@ControllerAdvice`, lo que le permite manejar excepciones globalmente.

De esta forma cuando una excepción, como se muestra en el sigueinte ejemplo, de tipo `EntityNotFoundException` se lanza el el siguiente método de la clase `UserService` es capturada por el método `handleEntityNotFoundException` de la clase `GlobalExceptionHandler`. Ej.

**UserService**

```{UserService}
public User getUserById(Long id) {
    try {
      // Intenta encontrar al usuario por ID
      return userRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado en el id: " + id));
    } catch (EntityNotFoundException e) {
      // Loguea el error con el mensaje de excepción
      logger.error("Usuario no encontrado en el id: {}", id, e);
      // Relanza la excepción para que sea manejada por otros manejadores de excepciones
      throw e;
    }
  }
```

**GlobalExceptionHandler**

```{GlobalExceptionHandler}
@ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
    // Maneja la excepción de entidad no encontrada
    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getLocalizedMessage());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }
```

Cada tipo de excepción se debe agregar a la clase `GlobalExceptionHandler`

Con esto se retorna como parte de la respuesta al frontend el mansaje personalizado

**Motivo**

- **Claridad y Organización**: Al colocar GlobalExceptionHandler en un paquete de exception, tienes una separación clara de las responsabilidades en tu código. Las clases en security/handlers están enfocadas en el manejo de excepciones relacionadas con la seguridad (como problemas de autenticación o autorización), mientras que las clases en el paquete exception están orientadas a manejar errores de la aplicación de manera más general.

- **Flexibilidad**: Tener un paquete dedicado para excepciones permite una mayor flexibilidad en la adición de nuevos manejadores o clases de excepciones específicas sin mezclar lógica de seguridad y lógica de manejo de excepciones generales.

**¿Qué hace `@ControllerAdvice`?**

1. **Manejo Global de Excepciones**: `@ControllerAdvice` se utiliza comúnmente para manejar excepciones de manera global en toda la aplicación. Esto significa que en lugar de manejar excepciones en cada método de controlador individualmente, puedes definir una lógica centralizada que se aplique a todos los controladores de la aplicación.

2. `Intercepción de Solicitudes HTTP`: También permite interceptar solicitudes HTTP antes o después de que sean procesadas por un controlador. Puedes usarlo para preprocesar datos, aplicar lógica de validación, manipular los datos de entrada o salida, o realizar tareas comunes antes de que una solicitud sea manejada por un controlador.

**Beneficios de Usar `@ControllerAdvice`**

1. `Centralización`: Facilita la centralización del manejo de excepciones, reduciendo la repetición de código en tus controladores.

2. `Reutilización`: Promueve la reutilización de la lógica de manejo de excepciones en toda la aplicación.

3. `Mantenimiento`: Mejora la mantenibilidad del código, ya que todas las reglas de manejo de excepciones están en un solo lugar.

**Flujo del Manejo de Excepción**

1. **Excepción Lanzada**: En el método getUserById de UserService, cuando no se encuentra un usuario con el ID especificado, se lanza una EntityNotFoundException con el mensaje "Usuario no encontrado en el id: " + id.

2. **Excepción Capturada**: El catch en el método getUserById captura la excepción, la registra en el logger, y luego la vuelve a lanzar (throw e;).

3. **Intercepción por `@ControllerAdvice`**: La excepción lanzada es capturada por el método handleEntityNotFoundException en la clase anotada con `@ControllerAdvice`. Este método construye una respuesta JSON que contiene el mensaje de error (ex.getMessage()), que en este caso sería:

**¿De dónde viene ex.getMessage()?**

En el contexto del método, por ejmemplo: `getUserById` de `UserService`, este mensaje proviene de la excepción que tú mismo lanzaste en esta línea:

```
.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado en el id: " + id));
```

## Manejo de errores en la autenticación

`AuthEntryPointJwt`: Esta clase maneja los errores de autenticación, devolviendo un mensaje de error específico cuando un usuario no autenticado intenta acceder a un recurso protegido.

`CustomAccessDeniedHandler`: Esta clase maneja los errores de autorización, proporcionando un mensaje detallado cuando un usuario autenticado intenta acceder a un recurso sin tener los permisos necesarios.

Las clases (`AuthEntryPointJwt` y `CustomAccessDeniedHandler`) están específicamente diseñadas para manejar excepciones relacionadas con la autenticación y la autorización en una aplicación Spring Security, sus funciones son:

1. `AuthEntryPointJwt`

**Función Principal**: Manejar excepciones de autenticación.

**Detalles**:

- **Clase**: AuthEntryPointJwt implementa AuthenticationEntryPoint, que es una interfaz proporcionada por Spring Security.
- **Uso**: Se utiliza cuando un usuario no autenticado intenta acceder a un recurso protegido.

**Excepciones Manejadas**:

1. `UsernameNotFoundException`: Usuario no encontrado.
2. `BadCredentialsException`: Credenciales incorrectas.
3. `DisabledException`: Cuenta de usuario deshabilitada.
4. `CredentialsExpiredException`: Credenciales (como el token) han expirado.
5. `InsufficientAuthenticationException`: Falta de autenticación o permisos insuficientes.
6. Otros casos no especificados se marcan como "Unauthorized".

- **Respuesta**: Devuelve un error HTTP 401 (UNAUTHORIZED) con un mensaje de error en formato JSON.

**¿Cuándo se utiliza?**

Cuando un usuario no autenticado intenta acceder a un endpoint protegido sin un token válido, o cuando hay problemas con las credenciales proporcionadas.

2. `CustomAccessDeniedHandler`

**Función Principal**: Manejar excepciones de autorización.

**Detalles**:

- **Clase**: `CustomAccessDeniedHandler` implementa AccessDeniedHandler, otra interfaz de Spring Security.
- **Uso**: Se invoca cuando un usuario autenticado no tiene los permisos necesarios para acceder a un recurso.
- **Excepciones Manejadas**:
  - `AccessDeniedException`: Se lanza cuando un usuario autenticado intenta acceder a un recurso sin la autorización necesaria.
- **Respuesta**: Devuelve un error HTTP 403 (FORBIDDEN) con detalles sobre la excepción y un mensaje de que no se tienen permisos suficientes.

**¿Cuándo se utiliza?**

Cuando un usuario autenticado intenta acceder a un recurso para el cual no tiene los permisos adecuados.

## Diferenciación de Excepciones en Spring

Spring distingue a dónde debe ir una excepción basándose en el contexto en el que ocurre:

1. Excepciones durante la autenticación:

- Excepciones como `UsernameNotFoundException` que se lanzan durante el proceso de autenticación (por ejemplo, cuando un usuario intenta iniciar sesión y el sistema no encuentra un usuario con el nombre proporcionado) son capturadas por `AuthEntryPointJwt`.
- En este caso, `AuthEntryPointJwt` se activa porque implementa AuthenticationEntryPoint, que Spring Security invoca automáticamente cuando un usuario no autenticado intenta acceder a un recurso protegido. Aquí es donde se manejan errores relacionados con la autenticación, como credenciales incorrectas o usuario no encontrado al intentar autenticarse.

2. Excepciones fuera del contexto de autenticación:

- Cuando UsernameNotFoundException se lanza en otros contextos, como en el método getUserByEmail de UserService, es manejada por cualquier manejador de excepciones global, como el `GlobalExceptionHandler` que implementa `@ControllerAdvice`.
- Este `@ControllerAdvice` interceptará cualquier excepción de `UsernameNotFoundException` que se lance fuera del contexto de la autenticación y devolverá una respuesta personalizada al cliente.

**Cómo distingue Spring entre ambos contextos**

Spring Security se basa en la configuración de seguridad y el contexto en el que se lanza la excepción:

1. Contexto de autenticación (`AuthEntryPointJwt`):

- Se utiliza `AuthEntryPointJwt` cuando ocurre un problema de autenticación durante un intento de acceso a un recurso protegido.
- Esto es administrado por Spring Security en el contexto de autenticación.

2. Contexto de aplicación (`GlobalExceptionHandler`):

- Se utiliza `GlobalExceptionHandler` para manejar excepciones lanzadas en otros contextos de la aplicación que no están relacionados con la autenticación.
- Por ejemplo, si un método del servicio (UserService.getUserByEmail) lanza `UsernameNotFoundException`, esta será capturada por el manejador global porque no ocurre durante el proceso de autenticación.

## Estructura de los mensajes de error enviados por el servidor

Los códigos de error detallados se definen con base en la siguiente tabla:

| Código | Detalle                                                                | Enviado por          |
| :----- | :--------------------------------------------------------------------- | :------------------- |
| 100    | Al menos una variable no debe ser nula                                 | Base Datos           |
| 101    | El valor no puede ser nulo                                             | Base Datos           |
| 102    | El usuario no existe                                                   | Base Datos           |
| 103    | El nuevo valor no puede ser igual al valor actual                      | Base Datos           |
| 104    | El rol asignado no existe                                              | Base Datos           |
| 105    | El nickname ingresado ya existe                                        | Base Datos           |
| 106    | El correo ingresado ya existe                                          | Base Datos y Backend |
| 107    | La contraseña es nula                                                  | Base Datos           |
| 108    | La contraseña no coincide con la registrada                            | Base Datos           |
| 109    | La nueva contraseña es igual a la contraseña vigente                   | Base Datos y Backend |
| 110    | Cuenta deshabilitada                                                   | Backend              |
| 111    | El identificador no existe                                             | Backend              |
| 112    | Las credenciales han expirado                                          | Backend              |
| 113    | La contraseña temporal no coindice con la enviada                      | Backend              |
| 114    | Error inesperado al buscar el usuario                                  | Backend              |
| 115    | Error al actualizar el usuario                                         | Backend              |
| 116    | Error al actualizar el nickname                                        | Backend              |
| 117    | Error al actualizar el correo                                          | Backend              |
| 118    | Error al actualizar la contraseña                                      | Backend              |
| 119    | No puede acceder a información de otro usuario                         | Backend              |
| 120    | El id ingresado es nulo                                                | Backend              |
| 121    | El nombre ingresado es nulo                                            | Backend              |
| 122    | El apellido ingresado es nulo                                          | Backend              |
| 123    | El correo no cumple el formato definido                                | Backend              |
| 124    | El nickname no cumple el formato definido                              | Backend              |
| 125    | La contraseña no cumple el formato definido                            | Backend              |
| 126    | El idioma es nulo                                                      | Backend              |
| 127    | El status ingresado es nulo                                            | Backend              |
| 128    | La activación ingresada es nula                                        | Backend              |
| 129    | El nombre del rol es nulo                                              | Backend              |
| 130    | El nombre excede los 50 caracteres                                     | Backend              |
| 131    | El apellido excede los 50 caracteres                                   | Backend              |
| 132    | El segundo apellido excede los 50 caracteres                           | Backend              |
| 133    | El nombre del rol excede los 50 caracteres                             | Backend              |
| 134    | El idioma debe ser de 2 caracteres                                     | Backend              |
| 135    | El nombre no cumple el formato definido                                | Backend              |
| 136    | El apellido no cumple el formato definido                              | Backend              |
| 137    | El segundo apellido no cumple el formato definido                      | Backend              |
| 138    | El idioma no cumple el formato definido                                | Backend              |
| 139    | El rol no cumple el formato definido                                   | Backend              |
| 140    | Error al enviar el correo de activación                                | Backend              |
| 141    | Error inesperado al enviar el correo de activación                     | Backend              |
| 142    | Error al activar cuenta                                                | Backend              |
| 143    | No existe usuario asociado con la clave de activación aportada         | Backend              |
| 144    | Debe proporcionar solo un identificador: correo o nickname             | Backend              |
| 145    | No existe usuario asociado con la clave de restablecimiento aportada   | Backend              |
| 146    | Parámetros nulos                                                       | Backend              |
| 147    | La clave de activación ha expirado                                     | Backend              |
| 148    | La cuenta ya ha sido activada                                          | Backend              |
| 149    | Debe proporcionar un identificador: correo o nickname                  | Backend              |
| 150    | No existe usuario asociado con el correo proporcionado                 | Backend              |
| 151    | No existe usuario asociado con el nickname proporcionado               | Backend              |
| 152    | Ya existe una solicitud de restablecimiento activa para este usuario   | Backend              |
| 153    | La clave de restablecimiento ha expirado                               | Backend              |
| 154    | La clave de restablecimiento no cumple el formato definido             | Backend              |
| 155    | Error al enviar el correo de restablecimiento de contraseña            | Backend              |
| 156    | Error inesperado al enviar el correo de restablecimiento de contraseña | Backend              |
| 157    | Error al actualizar contraseña                                         | Backend              |
| 158    | Error al cargar el usuario                                             | Backend              |
| 159    | Error al cargar el tiempo de expiración                                | Backend              |
| 160    | Error inesperado al enviar correo de aviso                             | Backend              |
| 161    | Error contiene espacio en blanco                                       | Backend              |
| 162    | Error correo no válido                                                 | Backend              |
| 163    | Tipo de archivo no permitido                                           | Backend              |
| 164    | Archivo no encontrado                                                  | Backend              |
| 165    | Acceso no autorizado al archivo                                        | Backend              |
| 166    | Error al intentar guardar el registro loginAttempt                     | Backend              |
| 167    | Error al enviar el correo de intento fallido de login                  | Backend              |
| 168    | Error inesperado al enviar el correo de intento fallido de login       | Backend              |
| 169    | Error al cargar la url base                                            | Backend              |
| 170    | Error al cargar la ruta de los archivos para descarga                  | Backend              |
| 171    | Error al al formatear la fecha y hora del intento                      | Backend              |
| 172    | Error al intentar guardar sesión del usuario                           | Backend              |
| 173    | Error al cargar el refreshTokenDurationMs                              | Backend              |
| 174    | Error al crear el refreshTokenDurationMs                               | Backend              |
| 175    | Error al verificar Expiration del token                                | Backend              |
| 176    | Error al eliminar los refresh token del id (del user)                  | Backend              |
| 177    | Refresh token no encontrado                                            | Backend              |
| 178    | Error al guardar el RefreshToken                                       | Backend              |
| 179    | Error al eliminar el RefreshToken                                      | Backend              |
| 180    | Error al verificar la expiración del refresh token                     | Backend              |
| 181    | No existe RefreshToken del Usuario                                     | Backend              |
| 182    | Error al intentar eliminar los RefreshToken previos a una fecha        | Backend              |
| 183    | Error al cargar todos los refreshToken                                 | Backend              |
| 184    | Error en el formato de la fecha y hora                                 | Backend              |
| 185    | Error al convertir la fecha a UTC                                      | Backend              |
| 505    | No autorizado                                                          | Backend              |
| 501    | Permisos insuficientes                                                 | Backend              |
| 502    | Token expirado                                                         | Backend              |
| 503    | Token JWT nulo o inválido                                              | Backend              |
| 504    | No se pudo configurar autenticación                                    | Backend              |
| 505    | Error de autenticación inesperado                                      | Backend              |
| 506    | Error en tiempo de ejecución                                           | Backend              |
| 507    | Autenticación insuficiente                                             | Backend              |
| 508    | Error al cargar el registro de llaves                                  | Backend              |

El mensaje estará compuesto por el código separado una coma (,) y el detalle, de forma que al `Frontend` el mesaje de error llegue como el siguiente ejemplo:

```
{
    "error": "501, Permisos insuficientes"
}
```

De esta forma se puede separa el mensaje en dos elmentos, el código y una descripción del mismo de forma que con el código se pueda personalizar y generar el idioma que se desee.

[Retornar a la principal](../../README.md)
