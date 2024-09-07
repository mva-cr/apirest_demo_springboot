# Seguridad en el acceso a la aplicación

Definiendo dos capas de seguridad en la aplicación.

## Configuración en `filterChain`

La configuración en el método filterChain establece las reglas de autorización a nivel de URL para la aplicación web. En el fragmento de código proporcionado, estás definiendo qué roles pueden acceder a qué rutas de la aplicación.

**proporciona una primera capa de seguridad** al rechazar inmediatamente cualquier solicitud que no cumpla con los criterios básicos de acceso (como un rol específico para una URL específica).

1. Proporciona seguridad básica y global para todas las rutas de la aplicación.
2. Es útil para definir reglas de acceso generales, como permitir acceso sin autenticación a ciertas rutas (/api/auth/\*\*) o restringir acceso a ciertas rutas para usuarios con roles específicos (/api/users/|\*\* para ROLE_ADMIN). Ej.

```
  .authorizeHttpRequests(authz -> authz
      // Permitir todos los accesos a "/api/auth/**"
      .requestMatchers("/api/auth/**").permitAll()
      // Solo usuarios con ROLE_ADMIN pueden acceder a /admin/**
      .requestMatchers(("/api/admin/**")).hasAnyAuthority("ROLE_ADMIN")
      // Solo usuarios con ROLE_USER pueden acceder a /user/**
      .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
      // Requerir autenticación para todas las demás solicitudes
      .anyRequest().authenticated())
```

En este ejemplo:

- Solo los usuarios con el rol ROLE_ADMIN pueden acceder a cualquier endpoint que comience con /api/users/\*\*.
- Si un usuario sin este rol intenta acceder a cualquier ruta que coincida con /api/users/\*\*, el servidor rechazará la solicitud con un error de "403 Forbidden".

## Anotación `@PreAuthorize` en el Controlador

La anotación `@PreAuthorize` se utiliza para especificar restricciones de seguridad adicionales a nivel de método del controlador (**Añade Seguridad a Nivel de Método**). Permite controlar la autorización en un nivel más granular que `filterChain`.

1. Proporciona una capa adicional de seguridad más específica.
2. Te permite definir reglas de seguridad más detalladas y específicas para cada método en particular.
3. A diferencia de filterChain, puede usar expresiones complejas para personalizar la lógica de autorización, por ejemplo, combinando múltiples roles o verificando condiciones adicionales.

Ajemplo en el Controlador para solo un perfil

```
@RestController
@RequestMapping("api/admin/users")
public class AdminUserController {

  @Autowired
  private UserService userService;

  /**
   * Registra un nuevo usuario en el sistema.
   * Solo usuarios con el rol ROLE_ADMIN pueden acceder a este método.
   *
   * @param user El usuario a registrar.
   * @return Una respuesta indicando el éxito del registro.
   */
  @PostMapping("/register")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<String> registerUser(@RequestBody User user) {
    userService.registerUser(user);

    return ResponseEntity.ok("Usuario registrado exitosamente");
  }
}
```

Ajemplo en el Controlador para dos un perfiles

```
@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;
  @GetMapping("/info/{nickname}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<User> getUserInfo(@PathVariable String nickname) {
        User user = userService.getUserByNickName(nickname);
        return ResponseEntity.ok(user);
    }
}
```

## ¿Cuándo se ejecuta el método `filterChain`?

1. **Inicialización del contexto de la aplicación**:

Cuando la aplicación Spring Boot se inicia, el contenedor de Spring escanea todas las clases de configuración y carga todos los `beans` necesarios, incluidas las configuraciones de seguridad.
Durante esta fase de inicialización, Spring busca cualquier método anotado con `@Bean` dentro de las clases de configuración (`@Configuration`) y los ejecuta para registrar esos beans en el contexto de la aplicación.

2. **Cargando el bean de `SecurityFilterChain`**:

1. El método `filterChain` está anotado con` @Bean`, lo que indica a Spring que debe ejecutar este método durante la inicialización para crear y registrar un bean de tipo SecurityFilterChain.
   Spring Security utiliza este bean para configurar la seguridad global de la aplicación, como las reglas de autorización, los filtros de autenticación, la gestión de sesiones, etc.

1. **Configuración de la cadena de filtros**:

1. Cuando se invoca el método filterChain, este configura la cadena de filtros que Spring Security utilizará para cada solicitud HTTP entrante.
1. La cadena de filtros se define mediante llamadas a métodos de la API fluida de HttpSecurity, como authorizeHttpRequests, addFilterBefore, etc.
1. Esta configuración establece qué rutas deben ser protegidas, qué métodos de autenticación utilizar, cómo manejar las excepciones de seguridad, y más.

## ¿Cuándo se utiliza esta configuración?

Una vez que la aplicación se ha inicializado y el bean de SecurityFilterChain se ha creado:

1. Para cada solicitud entrante, la cadena de filtros configurada se aplica automáticamente.
2. El contenedor de servlet de Spring delega todas las solicitudes entrantes al DelegatingFilterProxy, que es el componente de Spring que gestiona la seguridad.
3. Este proxy delega las solicitudes al primer filtro de la cadena (como AuthTokenFilter o cualquier otro filtro de seguridad que hayas definido).
4. Los filtros se aplican en el orden configurado en el método filterChain.

## ¿En qué momento específico se ejecuta filterChain?

En términos específicos de ejecución, el método filterChain se invoca durante el arranque de la aplicación, antes de que la aplicación comience a aceptar solicitudes HTTP. Esto sucede cuando:

1. Spring Context se inicializa:

- Al inicio de la aplicación, Spring crea todos los beans configurados.
- filterChain es ejecutado para registrar la configuración de la seguridad.

2. Antes de procesar solicitudes HTTP:

- Toda esta configuración debe estar en su lugar antes de que la primera solicitud HTTP sea procesada por el servidor.

## ¿Qué hace filterChain en cada solicitud?

Durante el tiempo de ejecución, el método filterChain no se invoca de nuevo. En cambio:

1. La configuración que definiste dentro de filterChain se utiliza cada vez que una solicitud HTTP entra en tu aplicación.
2. Los filtros de seguridad configurados dentro del SecurityFilterChain se ejecutan en el orden en que se definieron para asegurar la aplicación según las reglas establecidas.
