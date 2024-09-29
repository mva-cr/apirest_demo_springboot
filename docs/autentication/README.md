# Autenticación

[Retornar a la principal](../../README.md)

Este cubre varios procesos:

1. creación de cuanta
2. inicio de sesión
3. Restablecimiento de contraseña

## creación de cuenta

Se implementa la creación de usuario por parte del administrador `ROLE_ADMIN`, al cearlo se le envía un correo al usuario con un link de autenticación, que contiene el `id` y el `keyVaue` que se generan en la creación, solo requiere dar clic a ese link y el proceso activición de la cuenta se realiza.

Una vez creada la cuenta y activada, es necesario que el administrador le asigne un rol de lo contrario las solicitudes de este último serán denegadas.

Al iniciar sesión se le envía un `token` de tipo `Bearer` para que se incluya en el encabezado de cada solicitud en el `frontend`.

## Inicio de sesión

### Flujo de Inicio de Sesión (Login)

1. **Solicitud de Autenticación desde el Cliente**: El proceso comienza cuando el cliente (puede ser el frontend o Postman) envía una solicitud HTTP POST al endpoint de login. En tu caso, la URL es:

```
POST http://localhost:8080/api/auth/login
```

La solicitud incluye un cuerpo JSON con el nickname y password del usuario:

```
{
  "nickname": "String",
  "password": "String"
}
```

2. **Controlador de Autenticación**:

El Método: `authenticateUser(LoginRequest loginRequest)` de la clase `AuthController` Recibe la solicitud de inicio de sesión, delega la autenticación al servicio de autenticación (AuthService), y devuelve un JwtResponse con el token JWT si la autenticación es exitosa.

3. **Servicio de Autenticación**: La clase `AuthService` contiene el método authenticateUser el cual es responsable de manejar la lógica de autenticación. Autentica al usuario y genera un token JWT basado en las credenciales proporcionadas (nickname y password) :

Si las credenciales son válidas, se genera un token JWT usando JwtUtils.

4. **Gestor de Autenticación**: AuthenticationManager es un componente central de Spring Security que se utiliza para autenticar las credenciales del usuario. La autenticación es gestionada internamente por el `AuthenticationManager` utilizando el método `authenticate()`, que invoca a `UserDetailsService` para cargar los detalles del usuario.

5. **Servicio de Detalles de Usuario**: `UserDetailsServiceImpl` carga los detalles del usuario desde la base de datos utilizando el UserRepository.

   - Verifica que el usuario exista y que su cuenta esté activa
   - Devuelve un objeto UserDetails que contiene el nombre de usuario, la contraseña y los roles del usuario.

6. **Filtro de Autenticación JWT: AuthTokenFilter** Este filtro intercepta todas las solicitudes HTTP y verifica la validez del token JWT utilizando el método `doFilterInternal()`. Verifica la presencia de un token JWT en el encabezado Authorization, valida el token y establece el contexto de seguridad para la solicitud actual.

7. **Utilidad JWT: JwtUtils** Por medio del método `generateJwtToken(Authentication authentication)` Genera un token JWT para el usuario autenticado utilizando una clave secreta y una fecha de expiración definida.

## Tiempo de expiración de la sesión HTTP

`server.servlet.session.timeout` se refiere al tiempo de inactividad desde la última petición del usuario al servidor (backend) antes de que la sesión HTTP caduque.

**Detalles**

1. Tiempo de Inactividad: Esta propiedad mide el tiempo de inactividad, es decir, el tiempo que ha transcurrido desde la última solicitud HTTP del usuario al servidor. Cada vez que el usuario hace una nueva solicitud al servidor (como cargar una nueva página, hacer clic en un botón, enviar un formulario, etc.), este contador de tiempo se reinicia.

2. Expiración de la Sesión: Si el usuario no realiza ninguna solicitud al servidor durante el período especificado (en este caso, 15 minutos), la sesión del usuario caducará. Esto significa que cualquier intento posterior de interactuar con la aplicación puede resultar en la necesidad de volver a iniciar sesión o en una respuesta que indique que la sesión ha expirado.

En `application.properties`se ha definido así:

```
server.servlet.session.timeout=15m
```

Que son 15 minutos.

### Tiempo de expiración del token JWT

El tiempo de expiración del token JWT comienza a contar a partir del momento en que el token es emitido. Esto se especifica en el método generateJwtToken al usar la función setIssuedAt(new Date()). Es decir, desde el momento en que se genera y se emite el token, la cuenta regresiva para la expiración comienza.

En `application.properties`se ha definido así:

```
app.jwtExpirationMs=3600000
```

Tiempo de vida del token de autenticación en milisegundos: 1 hora = 1000 _ 60 _ 60

## Restablecimiento de contraseña

Para implementar el proceso de restablecimiento de contraseña (reset password), se deben seguir varios pasos que incluyen la generación de un token temporal, el envío de un correo electrónico con el enlace para cambiar la contraseña y la actualización de la misma en la base de datos una vez que el usuario proporcione una nueva contraseña.

**Proceso Completo para Reset Password**

1. Solicitud de restablecimiento de contraseña:

- El usuario proporciona su email en una página del frontend.
- El sistema genera un token temporal (similar a cómo se hace con la activación de cuenta) y lo almacena en la base de datos.
- Se envía un correo electrónico al usuario con un enlace para restablecer su contraseña. Este enlace incluye el token temporal.

2. Generación de token de restablecimiento:

- Similar al token de activación, se genera un UUID que será utilizado como resetKey y se asocia al usuario.
- Este token tiene una fecha de expiración que limita el tiempo que el enlace es válido.

3. Envio del correo:

- El correo contiene un enlace con el formato http://localhost:8080/password-change/{id}/{resetKey}, donde id es el ID del usuario y resetKey es el token generado.
- El correo explica al usuario que haga clic en el enlace para restablecer su contraseña.

4. Pantalla de restablecimiento de contraseña:

- El usuario hace clic en el enlace, lo que lo lleva a una pantalla donde puede ingresar su nueva contraseña.
- En esta pantalla, el token (resetKey) ya está incluido en la URL y el backend lo validará.

5. Verificación y cambio de contraseña:

- El sistema verifica el token, el ID del usuario y que el token no haya expirado.
- Si todo es correcto, el sistema acepta la nueva contraseña, la hashea (usando BCrypt o un algoritmo similar) y la almacena en la base de datos.

6. Confirmación:

- El sistema envía una confirmación al usuario de que su contraseña ha sido cambiada.

[Retornar a la principal](../../README.md)
