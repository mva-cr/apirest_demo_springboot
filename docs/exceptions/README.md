# Implementación de manejo de errores

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
