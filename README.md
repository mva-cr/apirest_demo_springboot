# API REST Demo en Spring Boot

Esta proyecto contiene los elementos necesarios para la construcción de una [`ARIRest`](./docs/api/README.md).

La conexión a una única base de datos.

Está implementada con las siguientes versiones:

1. `Spring Boot`: 3.3.3
2. `java.version`: 17
3. `Database`: SQL Server 2022
4. `Lenguage`: java

## Implementaciones:

1. inicio de sesión implementando la [autenticación](./docs/login/README.md).
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

## Estructura del proyecto

Su estructura es la siguiente:

```
src/main/java/com/mvanalitic/sugef_test_springboot_b
│
├── controller
│   ├── busines/
│   │   └── TODO:                               # TODO:
│   └── user/
│       ├── AdminUserController.java            # Endpoint - administrar usuarios de uso exclusivo para el ROLE_ADMIN
│       ├── AuthController.java                 # Recibe la solicitud de inicio de sesión
│       └── UserController.java                 # Endpoind para administrar variables del usuario con sesión activa, cualquier rol
│
├── domain
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   └── user/
│       ├── Authority.java                      # Representa la autoridad o roles en el sistema
│       └── User.java                           # Representa un usuario en el sistema
│
├── dto
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   └── user/
│       ├── AdminUserResponseDTO.java           # Contiene la información que el ROLE_ADMIN puede ver
│       └── AuthorityDTO.java                   # Representa la el rol del usuario
│       └── JwtResponseDTO.java                 # DTO que contiene el token JWT cuando inicia sesión exitosamente
│       └── LoginAttemptResponseDTO.java        # tabla de auditoría de los intentos de login exitosos y fallidos
│       └── LoginRequestDTO.java                # DTO que recibe la solicitud de inicio de sesión
│       └── UserAuditresponseDTO.java           # Representa la información de la tabla user_audit
│       └── UserEmailUpdateRequestDTO.java      # Solicitud de cambio de email del usuaro con sesión activa
│       └── UserNicknameUpdateRequestDTO.java   # Solicitud de cambio de nickname del usuario
│       └── UerPasswordUpdateRequestDTO.java    # Solicitud de cambio de contraseña del usuario
│       └── UserProfileResponseDTO.java         # Información que el usario puede ver
│       └── UserProfileUpdateRequestDTO.java    # Solicitud de cambio de información del usuario activo
│       └── UserRegistrationRequestDTO.java     # Datos del registro de un nuevo usuario
│       └── UserRoleUpdateRequestDTO.java       # Solicitud de cambio de role de un usuario
│       └── UserStatusUpdateRequestDTO.java     # Cambio de los atributos: status y activated
│
├── exception
│   └── GlobalExceptionHandler.java             # Manejador global de excepciones
│
├── mapper
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   └── user/
│       └── UserMapper.java                     # Realiza conversiones entre User y sus DTO's
│
├── repositories
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   └── user/
│       └── UserRepository.java                 # Interfaz que define las operaciones CRUD y métodos personalizados
│
└── security/
│   ├── config/                                 # Configuración general de seguridad
│   │   └── SecurityConfig.java                 # Clase de configuración principal
│   ├── handlers/ (opcional)                    # Manejadores de excepciones de seguridad
│   │   ├── AuthEntryPointJwt.java              # Clase para manejar errores de autenticación
│   │   └── CustomAccessDeniedHandler.java      # Clase para manejar errores de autorización
│   ├── jwt/
│   │   └── AuthTokenFilter.java                # Filtro para comprobar la existencia de un token JWT válido
│   └── providers/
│       └── CustomAuthenticationProvider.java   # Manejo centralizado de las excepciones de la aplicación
│
├── services
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   └── user/
│       ├── AuthService.java                    # Autentica al usuario y genera un token JWT
│       ├── UserDetailsServiceImpl.java         # Maneja logica de autenticación
│       └── UserService.java                    # Encargado del CRUD de los usuarios del sistema
│
├── utility
│   ├── JwtUtils.java                           # Gestión de JWT (JSON Web Tokens)
│   ├── LoggerConfig.java                       # Configuración para el sistema de logging de la aplicación
│   ├── LoggerSingleton.java                    # Manejo la instancia única de Logger en toda la aplicación
│   ├── LogPathConfig.java                      # Configurar y obtener la ruta del directorio de logs de la aplicación
│   └── UserValidationService.java              # Validaciones de objetos utilizados con la administración del user
│
└── resources /
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
