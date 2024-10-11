# API REST Demo en Spring Boot

Esta proyecto contiene los elementos necesarios para la construcción de una [`ARIRest`](./docs/api/README.md).

La conexión a una única base de datos, la base de datos soporta que un usuario tenga más de un `Role` pero por reglas de negocio en este proyecto solo se permite un rol, de forma que por código en el backend se deshabilita esta posibilidad. Se implementa el `RefresToken` como medida alternativa para el manejo de los tiempos en la sesiones de los usuarios ya que se utiliza `JWT (Jason Web Token)` que es `stateless`, se agregan tablas de auditoría de cambios en las tablas de usuario así como el control de sesiones por usuario e intentos de login de no usuarios.

Está implementada con las siguientes versiones:

1. `Spring Boot`: 3.3.3 (versión de Spring Framework: 6.1.12)
2. `java.version`: 17
3. `Database`: SQL Server 2022
4. `Lenguage`: java

## Resumen

Este proyecto corresponde a la construcción de una APIRest que incorpora la autenticación de los usuarios, manteniendo algunas rutas libres.

Las inserciones o modificaciones a la base de datos que implica más de una tabla, se realiza por medio de procedimientos almacenados de forma que asegura que si una parte de la transacción falla toda se reverse (Rollback).

El ROLE_USER es el único que puede cargar la información de todos los usuarios, con excepción del password, el cual es encriptado antes de ser insertado a la base de datos, de forma que este ni el ROLE_USER lo puede conocer.

Cuando el ROLE_USER crea un usuario, se crea una contraseña temporal (aleatoria) la cual el único que la conoce es el usuario a quien le llega el correo

## Implementaciones:

1. implementación de autenticación [autenticación](./docs/autentication/README.md).
2. manejo de [perfiles](./docs/perfiles/README.md).
3. [capas de seguridad de la aplicación](./docs/securtyAcces/README.md).
4. [Reglas de negocio](./docs/busines-rules/README.md)
5. variables de: [`activationKey`, `resetKey`, `resetDate` y `verificación de tiempo`](./docs/createUser/README.md)
6. [base de datos](./asset/database.sql), se incluyen los [roles a utilizar](./asset/data.sql) y se definen sus [aspectos técnicos](./docs/dababases/README.md)
7. [serialización](./docs/dto-serializable/README.md).
8. manejo de [excepciones personalizadas](./docs/exceptions/README.md) de forma que al `frontend` se retorne un `String` que contiene un numero y un texto separado por una coma (,).
9. uso de [logg](./docs/logger/README.md).
10. [mapper](./docs/mapper/README.md).
11. uso de dos ambientes definidos en los [propiedades](./docs/properties/README.md).
12. [Envío de correo](./docs/mail/README.md).
13. [Validaciones de endpoint en Postman](./docs/pruebas-endpoint/README.md).
14. Incorpora el envío para descarga de archivos PDF.
15. [Paginación](./docs/paginacion/README.md) en las consultas a la Base de Datos
16. ver [definición correcto de los métodos en el Repository](./docs/repository/README.md)

## Estructura del proyecto

Su estructura es la siguiente:

```
src/main/java/com/mvanalitic/sugef_test_springboot_b
│
├── controller
│   ├── busines/
│   │   └── TODO:                                       # TODO:
│   └── files/
│   │   └── FileController.java                         # endpoint para administrar la descarga de archivos
│   └── user/
│       ├── AdminFailedLoginAttemptController.java      # gestiona los Intentos de login fallidos
│       ├── AdminRefreshTokenController.java            # gestiona los RefreshToken de los usuarios con sesión activa
│       ├── AdminUserController.java                    # Endpoint - administrar usuarios de uso exclusivo para el ROLE_ADMIN
│       ├── AdminUserLoginActivityController.java       # gestiona las sesiones de los usuarios
│       ├── AuthController.java                         # Endpoint para activar la cuenta del usuario
│       ├── PublicUserController.java                   # Recibe la solicitud de inicio de sesión
│       └── UserController.java                         # Endpoind para administrar variables del usuario con sesión activa, cualquier rol
│
├── domain
│   ├── busines/
│   │   ├── TODO:                                       # TODO:
│   └── user/
│       ├── Authority.java                              # Representa la autoridad o roles en el sistema
│       ├── FailedLoginAttempt.java                     # Representa los intentos de sesión fallidos de no usuarios
│       ├── RefreshToken.java                           # Representa los Refresh Token
│       ├── User.java                                   # Representa un usuario en el sistema
│       ├── UserAuthority.java                          # Representa la tabla de autoridad de usuario
│       ├── UserAuthorityId.java                        # Representa la clave compuesta para la entidad 'UserAuthority'
│       ├── UserKey.java                                # Representa la tabla 'user_key' en la base de datos
│       └── UserLoginActivity.java                      # Representa los intentos de sesión fallidos de usuarios
│
├── dto
│   ├── busines/
│   │   └── TODO:                                       # TODO:
│   ├── mail/
│   │   └── Mail:                                       # Representa un correo electrónico
│   └── user/
│       ├── ActiveAccountRequesDTO.java                 # Solicitud de activación de una cuenta
│       └── AdminUserResponseDTO.java                   # Contiene la información que el ROLE_ADMIN puede ver
│       └── AuthorityDTO.java                           # Representa la el rol del usuario
│       └── ChangePasswordByResetRequestDTO.java        # DTO utilizado para restablecer la contraseña que fue autorizada por ROLE_ADMIN
│       └── FailedLoginAttemptResponseDTO.java          # Tabla de auditoría de los intentos de inicio de sesión fallidos de no usuarios
│       └── JwtResponseDTO.java                         # DTO que contiene el token JWT cuando inicia sesión exitosamente
│       └── LoginAttemptResponseDTO.java                # tabla de auditoría de los intentos de login exitosos y fallidos
│       └── LoginRequestDTO.java                        # DTO que recibe la solicitud de inicio de sesión
│       └── PasswordResetRequestDTO.java                # DTO utilizado para gestionar restablecimiento de contraseña
│       └── ResendActivationRequestDTO.java             # DTO para gestionar el reenvío de contraseña por el ROLE_ADMIN
│       └── UserAuditResponseDTO.java                   # Representa la información de la tabla user_audit
│       └── UserAuthorityRequestDTO.java                # DTO utilizado la auditoría de los cambios en la tabla user-mva
│       └── UserEmailRequestDTO.java                    # Solicitud de cambio de email del usuaro con sesión activa
│       └── UserLoginActivityResponseDTO.java           # Detalles de una sesión de usuario fallidos y exitosos
│       └── UserNicknameRequestDTO.java                 # Solicitud de cambio de nickname del usuario
│       └── UerPasswordRequestDTO.java                  # Solicitud de cambio de contraseña del usuario
│       └── UserProfileRequestDTO.java                  # Solicitud de cambio de información del usuario activo
│       └── UserProfileResponseDTO.java                 # Información que el usario puede ver
│       └── UserRegistrationByAdminRequestDTO.java      # Registro de un nuevo usuario por el ROLE_ADMIN
│       └── UserRegistrationRequestDTO.java             # Solicitud de cambio de role de un usuario
│       └── UserSessionResponseDTO.java                 # Detalles de una sesión de usuario
│       └── UserStatusRequestDTO.java                   # Cambio de los atributos: status y activated
│
├── exception
│   ├── CustomAccessDeniedHandler.java          # Clase para manejar errores de autorización
│   ├── GlobalExceptionHandler.java             # Manejador global de excepciones
│   ├── MailSendException.java                  # Excepción personalizada para envío de correo
│   └── UserAlreadyActivatedException.java      # Excepción personalizada
│
├── mapper
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   └── user/
│   │   ├── FailedLoginAttemptMapper            # Realiza conversiones entre UserLoginActivity y su DTO
│   │   ├── RefresTokenMapper                   # Realiza conversiones entre RefresTokenMapper y su DTO
│   │   ├── UserLoginActivityMapper             # Realiza conversiones entre UserLoginActivity y su DTO                             #
│       └── UserMapper.java                     # Realiza conversiones entre User y sus DTO's
│
├── repositories
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   └── user/
│       ├── FailedLoginAttemptRepository.java   # Interfaz de la entidad FailedLoginAttempt
│       ├── RefreshTokenRepository.java         # Interfaz de la entidad RefreshToken
│       ├── UserAuthorityRepository.java        # Interfaz de la entidad UserAuthority
│       ├── UserKeyRepository.java              # Interfaz de la entidad UserKey
│       ├── UserLoginActivityRepository.java    # Interfaz de la entidad UserLoginActivity
│       └── UserRepository.java                 # Interfaz que define las operaciones CRUD y métodos personalizados
│
└── security/
│   ├── config/                                 # Configuración general de seguridad
│   │   └── SecurityConfig.java                 # Clase de configuración principal
│   ├── handlers/ (opcional)                    # Manejadores de excepciones de seguridad
│   │   ├── AuthEntryPointJwt.java              # Clase para manejar errores de autenticación
│   ├── jwt/
│   │   └── AuthTokenFilter.java                # Filtro para comprobar la existencia de un token JWT válido
│   └── providers/
│       └── CustomAuthenticationProvider.java   # Manejo centralizado de las excepciones de la aplicación
│
├── services
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   ├── files/
│   │   ├── FileService.java                    # Interfaz para el servicio de manejo de archivos
│       └── FileServiceImpl.java                # Servicio que implementa la interfaz FileService
│   ├── mail/
│   │   └── MailService.java                    # Servicio encargado de gestionar el envío de correos electrónicos
│   └── user/
│       ├── AuthService.java                    # Autentica al usuario y genera un token JWT
│       ├── FailedLoginAttemptService.java      # Implementación de la interfaz FailedLoginAttemptRepository
│       ├── RefreshTokenService.java            # Implementación de la interfaz RefreshTokenRepository
│       ├── UserAuthorityService.java           # Implementación de la Interfaz UserAuthorityRepository
│       ├── UserDetailsServiceImpl.java         # Maneja logica de autenticación
│       ├── UserKeyServiceImpl.java             # Implementación de la Interfaz UserKeyRepository
│       ├── UserLoginActivityService.java       # Implementación de la Interfaz UserLoginActivityRepository
│       └── UserService.java                    # Encargado del CRUD de los usuarios del sistema
│
├── utility
│   ├── AppUtility.java                         # Componente con métodos utilizados en varios servicios
│   ├── JwtUtils.java                           # Gestión de JWT (JSON Web Tokens)
│   ├── LoggerConfig.java                       # Configuración para el sistema de logging de la aplicación
│   ├── LoggerSingleton.java                    # Manejo la instancia única de Logger en toda la aplicación
│   ├── LogPathConfig.java                      # Configurar y obtener la ruta del directorio de logs de la aplicación
│   ├── RandomKeyGenerator.java                 # Generador de claves temporales aleatorias
│   └── UserValidationService.java              # Validaciones de objetos utilizados con la administración del user
│
└── resources /
    ├── i18n/                                   # Internacionalización
    │   ├── messages_en.properties              # textos para idioma ingles
    │   └── messages.properties                 # textos para idioma español (default)
    ├── static/
    │   └── img/                                # imagenes
    ├── templates/
    │   ├── activationAccount.html              # template para activación de cuenta creada por el mismo user
    │   ├── activationAccountCreateUser.html    # template para activacion de cuenta creada por el ROLE_ADMIN
    │   ├── activationAccountResend.html        # template para reenviar un activación de cuenta (ROLE_ADMIN)
    │   ├── activationReportToAdmin.html        # template para enviar aviso a ROLE_ADMIN que se activó una cuenta
    │   ├── changePasswordByReset.html          # template para enviar correo con reset de contraseña
    │   ├── failedLoginAttemptnofication.html   # template para enviar correo de notificación de intento fallido de inicio de sesión
    │   └── successfulAttempt.html              # template para enviar correo de notificación de intento exitoso de inicio de sesión
    ├── application-dev.properties              # Gestión de configuraciones en el entorno de desarrollo
    ├── application-prod.properties             # Gestión de configuraciones en el entorno de producción
    └── application.properties                  # Gestión de configuraciones globales y de inicio.
```

## Comandos base

1. limpiar proyecto

```
mvn clean
```

2. Limpia y reconstruye el proyecto

```
./mvnw clean install;
```

3. Ejecutar el proyecto

```
./mvnw spring-boot:run
```

4. Ver dependencias

```
mvn dependency:tree
```
