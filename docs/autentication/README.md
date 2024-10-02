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

<!-- TODO: verificar la continuidad -->

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

## Control del tiempo por sesión

### Use de `server.servlet.session.timeout` no utilizan `JWT` (JSON Web Token)

**¿Qué es `server.servlet.session.timeout`?**

Esta variable define cuánto tiempo una sesión HTTP permanecerá activa en el servidor antes de caducar por inactividad. Si un usuario no realiza ninguna actividad en la aplicación dentro de ese tiempo, el servidor invalidará la sesión.

- **Sesión HTTP**: Es un mecanismo en el que el servidor almacena información sobre cada usuario en un objeto HttpSession. Cada vez que un usuario hace una solicitud, el servidor usa una cookie (o URL de sesión) para identificar a la sesión correspondiente en el servidor.

- **Ejemplo de uso**: Si tienes una aplicación donde manejas sesiones de usuario mediante HttpSession, esta variable indica cuánto tiempo la sesión puede estar inactiva antes de que el servidor la invalide. En este caso, 15m significa que, si el usuario no interactúa con la aplicación durante 15 minutos, el servidor terminará la sesión.

**¿Cuándo se utiliza?**

- **Aplicaciones basadas en sesiones HTTP**: Si tu aplicación utiliza el manejo tradicional de sesiones HTTP (almacenadas en el servidor), server.servlet.session.timeout establece el tiempo de vida de la sesión por inactividad.

- **Ejemplo práctico**: Supongamos que un usuario inicia sesión en tu aplicación, y no interactúa con la misma durante 15 minutos. Si `server.servlet.session.timeout=15m`, cuando el usuario intente hacer una nueva acción después de este tiempo (15 minutos), su sesión habrá expirado, y necesitará volver a iniciar sesión.

**Comparación con JWT:**

- **JWT (JSON Web Token)**: La sesión no se mantiene en el servidor. El token contiene toda la información necesaria y tiene una duración específica (controlada por app.jwtExpirationMs). Cuando el token expira, el cliente debe solicitar uno nuevo.

- **Sesión HTTP tradicional**: La información de la sesión (usuario autenticado, etc.) se mantiene en el servidor y está vinculada a un HttpSession. El cliente solo mantiene un identificador de sesión (normalmente en una cookie) y, cuando el tiempo de inactividad supera el tiempo configurado (en este caso, 15 minutos), la sesión expira en el servidor.

**¿Deberías usarla?**
Si tu aplicación solo usa JWT para manejar autenticación, no necesitas preocuparte por esta variable.
Si usas sesiones HTTP tradicionales (por ejemplo, en combinación con autenticación basada en formularios o Spring Security sin JWT), esta variable es relevante para definir el tiempo de vida de las sesiones.

**¿Cómo debería ser el cierre de sesión (logout) con JWT?**
Con JWT, el concepto de "cerrar sesión" no se maneja del mismo modo que con las sesiones basadas en el servidor. Al usar JWT:

- **El servidor no almacena información del token**. El token reside en el cliente y, por lo tanto, al cerrar sesión no hay sesión para invalidar en el servidor.
- **El cierre de sesión** simplemente significa eliminar el token del cliente. Esto se puede lograr eliminando el token almacenado en el frontend (localStorage, sessionStorage, cookies, etc.).
- **El token sigue siendo válido hasta que expira**. El backend no puede invalidar directamente un token sin un mecanismo extra como una lista de revocación (Blacklist) o invalidación de token por la modificación del secret.

### Tokens de corta duración con refresh tokens

Es un patrón común utilizado para mejorar la seguridad en sistemas de autenticación basados en `JWT` (`JSON Web Tokens`). Este patrón permite que los access tokens (tokens de acceso) tengan una corta vida útil, pero ofrece una manera segura de renovarlos sin que el usuario tenga que autenticarse nuevamente, mediante el uso de refresh tokens.

**Conceptos clave**:

1. **Access Token (Token de Acceso)**:

- Es el JWT principal que contiene la información sobre la autenticación del usuario, como su identidad y los permisos (claims).
- Tiene una vida útil **corta** (normalmente entre 15 minutos y 1 hora).
- Se incluye en cada petición del cliente al servidor para autenticar y autorizar las solicitudes.
- **Es vulnerable** si se compromete, pero al tener una duración corta, se reduce el impacto de un posible ataque.

2. **Refresh Token (Token de Actualización)**:

- Es un token que se utiliza exclusivamente para obtener un nuevo access token cuando el anterior ha expirado.
- Se emite junto con el access token al momento de autenticación y se almacena de forma segura en el cliente (pero no debe ser accesible desde el JavaScript del frontend para mayor seguridad).
- Tiene una vida útil más larga que el access token, por ejemplo, 1 semana o más, dependiendo de la política de seguridad.
- No se envía con cada petición, solo cuando se necesita obtener un nuevo access token.
- En algunos casos, el refresh token también puede ser revocado en el servidor, lo que permite un mayor control sobre la sesión del usuario.

**Flujo de trabajo con access token y refresh token**:

1. **Inicio de sesión (login)**:

- El usuario se autentica y recibe dos tokens: un **`access token`** de corta duración y un **`refresh token`** de larga duración.
- El `access token` se usa para autenticar cada petición del cliente al servidor, mientras que el refresh token se guarda de forma segura en el cliente (usualmente en cookies http-only o almacenamiento seguro).

2. Uso del `access token`:

- Mientras el `access token` esté vigente, se envía con cada solicitud al servidor.
- Si el token expira, el usuario no puede seguir accediendo a los recursos protegidos con ese token.

3. Renovación con el refresh token:

- Cuando el `access token` expira, el cliente envía una solicitud al servidor para obtener un nuevo `access token`, incluyendo el refresh token.
- El servidor verifica el refresh token:
  - Si es válido y no ha sido revocado, emite un nuevo `access token`.
  - Si el refresh token no es válido o ha expirado, el usuario tendrá que autenticarse de nuevo.
- El ciclo se repite hasta que el refresh token expira o es revocado.

4. Revocación del refresh token:

- Si el usuario cierra sesión (logout) o si el refresh token es comprometido, se puede revocar el refresh token en el servidor.
- Esto invalida la posibilidad de obtener nuevos `access tokens`, obligando al usuario a autenticarse de nuevo.

**Ventajas del uso de tokens de corta duración con refresh token**:

1. Mayor seguridad:

- **`Access tokens` de corta duración** limitan el tiempo de exposición en caso de que un token sea robado o comprometido.
- **Refresh tokens** pueden ser revocados en el servidor, proporcionando un mayor control sobre la sesión.

2. **Mejora en la experiencia de usuario**:

- El usuario solo tiene que autenticarse una vez (login), y mientras el refresh token sea válido, se pueden emitir nuevos `access tokens` sin necesidad de que el usuario vuelva a iniciar sesión.
- Ofrece una sesión persistente más segura, incluso en sistemas distribuidos donde no se desea almacenar sesiones en el servidor.

3. **Balance entre seguridad y usabilidad**:

- Los `access tokens` cortos reducen el riesgo de comprometer una sesión, mientras que los refresh tokens permiten una experiencia sin interrupciones para el usuario.

**Implementación típica**:

1. **Login**:

- El usuario ingresa sus credenciales y recibe tanto un `access token` (corto) como un refresh token (largo).

2. **Uso del `access token`**:

- El cliente incluye el `access token` en el Authorization header de cada solicitud (Bearer <token>).
- Si el token es válido, el servidor procesa la solicitud.

3. **Renovación del token**:

- Cuando el `access token` expira, el cliente envía una solicitud al endpoint de "refresh token" junto con el refresh token.
- El servidor verifica el refresh token, emite un nuevo `access token` y, opcionalmente, un nuevo refresh token.

4. **Logout o revocación del refresh token**:

- Cuando el usuario cierra sesión o el refresh token se compromete, el servidor invalida el refresh token. El cliente ya no puede obtener nuevos `access tokens` hasta que el usuario se autentique de nuevo.

La tabla `refresh_token` de la base de datos **Solo** podrá tener una entrada por usuario, si bien la tabla lo permite y se podría justificar ya que el usuario puede tener `sesiones múltiples` por reglas de negocio se limita esta opción. Así si un usuario inicia sesión desde un navegador diferente, la sesión que haya tenido en otro se elimina y se crea la nueva.

[Retornar a la principal](../../README.md)
