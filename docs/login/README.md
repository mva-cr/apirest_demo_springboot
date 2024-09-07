# Inicio de sesión

## Flujo de Inicio de Sesión (Login)

1. **Solicitud de Autenticación desde el Cliente**: El proceso comienza cuando el cliente (puede ser el frontend o Postman) envía una solicitud HTTP POST al endpoint de login. En tu caso, la URL es:

```
POST http://localhost:8080/api/auth/login
```

La solicitud incluye un cuerpo JSON con el nickName y password del usuario:

```
{
  "nickName": "String",
  "password": "String"
}
```

2. **Controlador de Autenticación**:

El Método: `authenticateUser(LoginRequest loginRequest)` de la clase `AuthController` Recibe la solicitud de inicio de sesión, delega la autenticación al servicio de autenticación (AuthService), y devuelve un JwtResponse con el token JWT si la autenticación es exitosa.

3. **Servicio de Autenticación**: La clase `AuthService` contiene el método authenticateUser el cual es responsable de manejar la lógica de autenticación. Autentica al usuario y genera un token JWT basado en las credenciales proporcionadas (nickName y password) :

Si las credenciales son válidas, se genera un token JWT usando JwtUtils.

4. **Gestor de Autenticación**: AuthenticationManager es un componente central de Spring Security que se utiliza para autenticar las credenciales del usuario. La autenticación es gestionada internamente por el `AuthenticationManager` utilizando el método `authenticate()`, que invoca a `UserDetailsService` para cargar los detalles del usuario.

5. **Servicio de Detalles de Usuario**: `UserDetailsServiceImpl` carga los detalles del usuario desde la base de datos utilizando el UserRepository.

   - Verifica que el usuario exista y que su cuenta esté activa
   - Devuelve un objeto UserDetails que contiene el nombre de usuario, la contraseña y los roles del usuario.

6. **Filtro de Autenticación JWT: AuthTokenFilter** Este filtro intercepta todas las solicitudes HTTP y verifica la validez del token JWT utilizando el método `doFilterInternal()`. Verifica la presencia de un token JWT en el encabezado Authorization, valida el token y establece el contexto de seguridad para la solicitud actual.

7. **Utilidad JWT: JwtUtils** Por medio del método `generateJwtToken(Authentication authentication)` Genera un token JWT para el usuario autenticado utilizando una clave secreta y una fecha de expiración definida.

## Diagrama del inicio de sesión

```
src/main/java/com/mvanalitic/sugef_test_springboot_b
│
├── controller
│   └── AuthController.java
│
├── domain
│   ├── Authority.java
│   └── User.java
│
├── dto
│   ├── LoginRequest.java
│   └── JwtResponse.java
│
├── repositories
│   └── UserRepository.java
│
├── services
│   ├── AuthService.java
│   └── UserDetailsServiceImpl.java
│
├── utility
│   ├── JwtUtils.java
│   ├── PasswordHashingGenerator.java
│   └── PasswordService.java
│   └── LoggerConfig.java
│   └── LogPathConfig.java
│   └── LoggerSingleton.java
│
└── security/
    ├── jwt/
    │   └── AuthTokenFilter.java
    ├── providers/
    │   └── CustomAuthenticationProvider.java
    └── config/                               # Configuración general de seguridad
    │   └── SecurityConfig.java               # Clase de configuración principal
    └── handlers/ (opcional)                  # Manejadores de excepciones de seguridad
        ├── AuthEntryPointJwt.java            # Clase para manejar errores de autenticación
        └── CustomAccessDeniedHandler.java    # Clase para manejar errores de autorización
```

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

# Tiempo de expiración del token JWT

El tiempo de expiración del token JWT comienza a contar a partir del momento en que el token es emitido. Esto se especifica en el método generateJwtToken al usar la función setIssuedAt(new Date()). Es decir, desde el momento en que se genera y se emite el token, la cuenta regresiva para la expiración comienza.

En `application.properties`se ha definido así:

```
app.jwtExpirationMs=3600000
```

Tiempo de vida del token de autenticación en milisegundos: 1 hora = 1000 _ 60 _ 60
