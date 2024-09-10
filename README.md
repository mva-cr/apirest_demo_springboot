# API REST Demo en Spring Boot

## Estructura para el inicio de sesión

Para implementar el inicio de sesión se necesita una estructura como la que se muestra seguidament:

1. Carpeta controller
   `AuthController.java`: Manejará las solicitudes de inicio de sesión y devolverá un token JWT al usuario si las credenciales son correctas.
2. Carpeta domain
   Contiene las clases `Authority.java` y `User.java`, que se usarán para la autenticación.
3. Carpeta dto: Contiene las clases
   `LoginRequest.java`: DTO (Data Transfer Object) que contendrá el username y password que el usuario envía para iniciar sesión. Y
   `JwtResponse.java`: DTO que contendrá el token JWT generado y cualquier otra información que desees devolver después del inicio de sesión.
4. Carpeta services: Contiene las clases
   `UserDetailsServiceImpl.java`: Implementación de UserDetailsService que cargará los detalles del usuario desde la base de datos.
   `AuthService.java`: Servicio que manejará la lógica de autenticación, como la validación de credenciales y la generación de tokens JWT.
5. Carpeta utility: Contiene las clases
   `PasswordHashingGenerator.java` que genera el hash a agregar o actualizar el password en hash a la base de datos, `PasswordService.java` que verifica si el password ingresado del usuario es igual al registrado en la base de dato y `JwtUtils.java`: Clase que manejará la generación y validación de tokens JWT.
6. Nueva Carpeta security
   `SecurityConfig.java`: Clase que configurará la seguridad de Spring, especificando las rutas que requieren autenticación y las que no, y también establecerá el proveedor de autenticación y el filtro de JWT.

```
src/main/java/com/mvanalitic/sugef_test_springboot_b
│
├── controller
│   └── AuthController.java               # Recibe la solicitud de inicio de sesión
│
├── domain
│   ├── Authority.java                    # Representa la autoridad o roles en el sistema
│   └── User.java                         # Representa un usuario en el sistema
│
├── dto
│   ├── LoginRequest.java                 # DTO que recibe la solicitud de inicio de sesión
│   └── JwtResponse.java                  # DTO que contiene el token JWT cuando inicia sesión exitosamente
│   └── JwtResponse.java                  # DTO que contiene el
│   └── JwtResponse.java                  # DTO que contiene el
│
├── exception
│   └── GlobalExceptionHandler.java       # Manejador global de excepciones
│
├── mapper
│   ├── UserMapper.java                 # DTO que recibe
│   └── JwtResponse.java                  # DTO que contiene el
│
├── exceptions/   <-- Nueva carpeta para excepciones
│     └── UserNotFoundException.java
│
├── repositories
│   └── UserRepository.java               # Interfaz que define los
│
├── services
│   ├── AuthService.java                  # Autentica al usuario y genera un token JWT
│   ├── UserDetailsServiceImpl.java       # Maneja logica de autenticación
│   └── UserService.java                  # Encargado del CRUD de los usuarios del sistema
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

El orden de creación es:

1. `Autohrity.java`
2. `User.java`
3. `PasswordHashingGenerator.java`
4. `PasswordService.java`
5. `UserRepository.java`
6. `JwtUtils.java`
7. `LoginRequest.java`
8. `JwtResponse.java`
9. `UserDetailsServiceImpl.java`
10. `AuthService.java`
11. `AuthEntryPointJwt`
12. `AuthTokenFilter.java`
13. `AuthController.java`

## Dependencias necesarias para el Login

```
  <!-- JWT API -->
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.2</version>
  </dependency>

  <!-- JWT Implementation -->
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.2</version>
    <scope>runtime</scope>
  </dependency>

  <!-- JWT Jackson Serializer -->
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId> <!-- Si estás utilizando Jackson para la serialización/deserialización -->
    <version>0.11.2</version>
    <scope>runtime</scope>
  </dependency>

  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
```

## Dependencia para logger

```
  <!-- Excluir Logback y agregar Log4j2 -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
      <exclusion>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-logging</artifactId>
      </exclusion>
    </exclusions>
  </dependency>

  <!-- Agregar Log4j2 -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
  </dependency>
```

Se crea en la ruta src/main/resources un archivo con el nombre log4j2.xml, con el siguiente código:

```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n" />
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console" />
        </Root>
    </Loggers>
</Configuration>

```

Cosideraciones para la clase user_app

### Anotaciones de Clase

1. @Entity: Indica que esta clase es una entidad JPA y será mapeada a una tabla en la base de datos.
2. @Table(name = "jhi_user"): Define el nombre de la tabla en la base de datos que corresponde a esta entidad.
3. @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE): Configura la caché de segundo nivel para esta entidad, optimizando las lecturas y escrituras en la base de datos.

### Campos de la Clase

1. id: Identificador único para cada usuario. Es la clave primaria de la tabla y se genera automáticamente.
2. login: Nombre de usuario único utilizado para iniciar sesión. Se valida con una expresión regular definida en Constants.LOGIN_REGEX, y tiene restricciones de tamaño.
3. password: Almacena la contraseña del usuario, que está en formato hash. Es un campo que no se expone en respuestas JSON (@JsonIgnore).
4. firstName y lastName: Nombres del usuario, con restricciones de tamaño.
5. email: Correo electrónico del usuario. Debe cumplir con el formato de correo electrónico y ser único en la base de datos.
6. activated: Indica si el usuario está activado y puede usar el sistema.
7. langKey: Clave del idioma preferido del usuario, utilizada para internacionalización (i18n).
8. imageUrl: URL de la imagen de perfil del usuario.
9. activationKey y resetKey: Claves usadas para la activación del usuario y el reseteo de contraseñas, respectivamente. Estas no se exponen en respuestas JSON.
10. resetDate: Almacena la fecha en que se solicitó el último restablecimiento de contraseña.

### Relaciones con Otras Entidades

1. authorities: Relación ManyToMany con la entidad Authority, que representa los roles o permisos asociados al usuario. Esta relación se mapea a la tabla jhi_user_authority.
2. persistentTokens: Relación OneToMany con la entidad PersistentToken, que gestiona los tokens de autenticación persistentes (como los usados en "remember me").

### Métodos de la Clase

1. Getters y Setters: Métodos de acceso y modificación para cada campo de la clase.
2. setLogin(String login): Convierte el nombre de usuario a minúsculas antes de almacenarlo.
3. equals() y hashCode(): Métodos sobreescritos para comparar objetos User y generar un hashcode basado en la entidad.
4. toString(): Retorna una representación en formato String de los datos más importantes del usuario, útil para depuración.

### Consideraciones de Seguridad

1. Contraseñas Hashadas: El campo password almacena un hash de la contraseña en lugar de la contraseña en texto claro, lo que es crucial para la seguridad.
2. Ocultamiento de Campos Sensibles: A través de @JsonIgnore, se asegura que campos sensibles como contraseñas y claves de activación no se expongan en respuestas JSON.

### Validaciones y Restricciones

@NotNull, @Pattern, @Size, @Email: Anotaciones de validación que garantizan que los datos ingresados cumplan con ciertos criterios antes de ser persistidos en la base de datos.

## Consideraciones para la clase Authority

### Anotaciones de Clase

1. @Entity: Marca esta clase como una entidad JPA, lo que significa que será mapeada a una tabla en la base de datos.
2. @Table(name = "jhi_authority"): Especifica el nombre de la tabla en la base de datos que almacenará las autoridades o roles. En este caso, la tabla se llamará jhi_authority.
3. @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE): Habilita la caché de segundo nivel para esta entidad, lo que mejora el rendimiento al reducir la cantidad de accesos a la base de datos. El uso NONSTRICT_READ_WRITE significa que las lecturas y escrituras pueden ser simultáneas sin bloqueo estricto, pero no garantiza consistencia en todo momento.
4. Campos de la Clase
   name: Este es el único campo de la clase y representa el nombre de la autoridad o rol, como "ROLE_USER" o "ROLE_ADMIN".
5. @NotNull: Indica que este campo no puede ser nulo.
6. @Size(max = 50): Restringe la longitud del nombre a un máximo de 50 caracteres.
7. @Id: Marca este campo como la clave primaria de la entidad.
8. @Column(length = 50): Especifica la longitud de la columna en la base de datos.

### Métodos de la Clase

1. Getters y Setters:
1. getName(): Devuelve el nombre de la autoridad.
1. setName(String name): Establece el nombre de la autoridad.
1. equals(): Sobreescribe el método equals() para comparar dos objetos Authority. Considera dos autoridades como iguales si sus nombres son iguales.
1. hashCode(): Sobreescribe hashCode() para generar un código hash basado en el nombre de la autoridad. Esto es importante para el uso de entidades en colecciones que dependen de códigos hash, como HashSet.
1. toString(): Sobreescribe el método toString() para proporcionar una representación en cadena de la autoridad, mostrando su nombre.

### Propósito General

La clase Authority es fundamental en un sistema de autenticación y autorización, ya que define los roles que se pueden asignar a los usuarios. Por ejemplo, podrías tener roles como "ROLE_USER", "ROLE_ADMIN", etc., que luego se asocian a los usuarios en la aplicación para definir qué operaciones pueden realizar.

refrescar dependencias

```
./mvnw clean install;
```

Ejecutar el proyecto

```
./mvnw spring-boot:run
```

Ver dependencias

```
mvn dependency:tree
```
