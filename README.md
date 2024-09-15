# API REST Demo en Spring Boot

Esta proyecto contiene los elementos necesarios para la construcción de una [`ARIRest`](./docs/api/README.md).

Está implementada con las siguientes versiones:

1. `Spring Boot`: 3.3.3
2. `java.version`: 17
3. `Database`: SQL Server 2022
4. `Lenguage`: java

## Estructura del proyecto

Su estructura es la siguiente:

```
src/main/java/com/mvanalitic/sugef_test_springboot_b
│
├── controller
│   ├── busines/
│   │   └── TODO:                         # TODO:
│   └── user/
│       ├── AdminUserController.java      # Endpoint para administrar los usuarios de uso exclusivo para el ROLE_ADMIN
│       ├── AuthController.java           # Recibe la solicitud de inicio de sesión
│       └── UserController.java           # Endpoind para administrar variables del usuario con sesión activa, cualquier rol
│
├── domain
│   ├── busines/
│   │   ├── TODO:                         # TODO:
│   └── user/
│       ├── Authority.java                # Representa la autoridad o roles en el sistema
│       └── User.java                     # Representa un usuario en el sistema
│
├── dto
│   ├── busines/
│   │   ├── TODO:                         # TODO:
│   └── user/
│       ├── AdminUserResponseDTO.java         #
│       └── AuthorityDTO.java                 #
│       └── JwtResponseDTO.java               # DTO que contiene el token JWT cuando inicia sesión exitosamente
│       └── LoginAttemptResponseDTO.java      #
│       └── LoginRequestDTO.java              # DTO que recibe la solicitud de inicio de sesión
│       └── UserAuditresponseDTO.java         # DTO que contiene el
│       └── UserEmailUpdateRequestDTO.java      # DTO que contiene el
│       └── UserNicknameUpdateRequestDTO.java   # DTO que contiene el
│       └── UerPasswordUpdateRequestDTO.java    # DTO que contiene el
│       └── UserProfileResponseDTO.java         # DTO que contiene el
│       └── UserProfileUpdateRequestDTO.java    # DTO que contiene el
│       └── UserRegistrationRequestDTO.java     # DTO que contiene el
│       └── UserRoleUpdateRequestDTO.java       # DTO que contiene el
│       └── UserStatusUpdateRequestDTO.java     # DTO que contiene el
│
├── exception
│   └── GlobalExceptionHandler.java             # Manejador global de excepciones
│
├── mapper
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   └── user/
│       └── UserMapper.java                     # DTO que contiene el
│
├── repositories
│   ├── busines/
│   │   ├── TODO:                               # TODO:
│   └── user/
│       └── UserRepository.java                 # Interfaz que define los
│
└── security/
│   ├── config/                                 # Configuración general de seguridad
│   │   └── SecurityConfig.java                 # Clase de configuración principal
│   ├── handlers/ (opcional)                    # Manejadores de excepciones de seguridad
│   │   ├── AuthEntryPointJwt.java              # Clase para manejar errores de autenticación
│   │   └── CustomAccessDeniedHandler.java      # Clase para manejar errores de autorización
│   ├── jwt/
│   │   └── AuthTokenFilter.java                #
│   └── providers/
│       └── CustomAuthenticationProvider.java   #
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
│   ├── JwtUtils.java                           #
│   ├── LoggerConfig.java                       #
│   ├── LoggerSingleton.java                    #
│   ├── LogPathConfig.java                      #
│   └── UserValidationService.java              #
│
└── resources /
    ├── application-dev.properties              #
    ├── application-prod.properties             #
    └── application.properties                  #
```

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
