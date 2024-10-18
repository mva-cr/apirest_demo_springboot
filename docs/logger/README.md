# Flujo de Logger

[Retornar a la principal](../../README.md)

`Logback` es una biblioteca de registro de eventos (logging) muy utilizada en aplicaciones Java, especialmente en combinación con el framework Spring Boot. Es el sucesor de Log4j y está diseñado para ser más rápido y tener una configuración más flexible. Logback se compone de tres módulos principales:

1. Logback Core: El módulo básico para manejar la funcionalidad de logging.
2. Logback Classic: Extiende Logback Core e implementa la API SLF4J, lo que lo hace un sustituto directo de Log4j.
3. Logback Access: Proporciona integración con servidores como Tomcat para registrar el tráfico HTTP.

## Implementación

1. Configuración Básica de Logback
   La configuración de Logback se realiza principalmente a través de un archivo XML llamado `logback.xml`, que generalmente se coloca en la carpeta resources de una aplicación Spring Boot.

El archivo logback.xml permite definir cómo y dónde se registran los mensajes de log. Puedes configurarlo para enviar mensajes a la consola, archivos, bases de datos, y más.

## Componentes Principales de logback.xml

1. Configuración (<configuration>): Es el elemento raíz donde se define la configuración del logger, incluyendo appenders, niveles, y loggers específicos.

2. Propiedades (<property>): Puedes definir propiedades reutilizables dentro del archivo para evitar la repetición y hacer más flexible la configuración.

```xml
<property name="LOG_PATH" value="${user.home}/logs/myapp" />
<property name="LOG_FILE_NAME" value="myapp.log" />
```

Los `value` son los valores default, si no se pasa un valor de este parámetro toma estos.

Estas propiedades se utilizan para definir el nombre y la ruta del archivo de log.

3. Appenders (<appender>): Un appender es responsable de enviar los mensajes de log a un destino específico (consola, archivo, base de datos, etc.). Hay varios tipos de appenders:

- ConsoleAppender: Muestra los mensajes de log en la consola.
- RollingFileAppender: Escribe mensajes de log en un archivo y puede crear versiones "archivadas" de los archivos (rotación) basadas en políticas como tiempo o tamaño.

```xml
Copiar código
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

- encoder: Define el formato del mensaje de log.
- pattern: Especifica el formato de cada mensaje de log. Los patrones incluyen elementos como la fecha (%d), el nivel del log (%-5level), el nombre del logger (%logger{36}), y el mensaje (%msg).

4. Root Logger (<root>): El root logger es el logger principal. Todos los mensajes de log que no sean manejados por un logger específico se envían al root logger.

```xml
Copiar código
<root level="INFO">
    <appender-ref ref="CONSOLE" />
    <appender-ref ref="FILE" />
</root>
```

El atributo level determina el nivel mínimo que se registrará (ej. INFO, DEBUG, WARN, ERROR).

4. Loggers Específicos (<logger>): Puedes definir loggers específicos para ciertas clases o paquetes.

```xml
Copiar código
<logger name="org.springframework" level="WARN" />
```

Esto limita la cantidad de información registrada para los paquetes que son más ruidosos, como org.springframework.

## Niveles de Log

1. TRACE: Nivel más detallado, útil para depurar problemas complejos.
2. DEBUG: Información útil para depurar, generalmente se usa durante el desarrollo.
3. INFO: Información general sobre el funcionamiento de la aplicación.
4. WARN: Mensajes que indican posibles problemas.
5. ERROR: Mensajes que indican errores graves.

Los niveles son jerárquicos, es decir, si configuras un logger en nivel WARN, no se registrarán los mensajes INFO ni DEBUG, pero sí WARN y ERROR.

## Flujo de Logging

El flujo del logging con Logback es el siguiente:

1. Mensaje generado: Un componente de la aplicación genera un mensaje de log utilizando logger.info(...), logger.error(...), etc.
2. Filtrado de Nivel: Logback revisa el nivel del mensaje y el nivel del logger para determinar si debe ser registrado o no.
3. Appender: Si el mensaje debe ser registrado, se envía al appender correspondiente, que lo escribe en el destino configurado (por ejemplo, consola o archivo).

## Logging Asíncrono

Logback también soporta logging asíncrono usando un appender especial (AsyncAppender). Esto es útil para evitar que el rendimiento de la aplicación se vea afectado por el proceso de escribir logs en archivos o sistemas externos.

## Clase para asegurar que si la carpeta y el archivo no existe que lo cree

`LogPathConfig` se asegura de que el directorio donde se almacenarán los logs exista.

```java
public class LogPathConfig {
public static String getLogDirectoryPath(String applicationName) {
String os = System.getProperty("os.name").toLowerCase();
String baseDir = os.contains("win") ? "C:" + File.separator + "Logs" : System.getProperty("user.home") + "/logs";
String finalPath = baseDir + File.separator + applicationName + File.separator;

        // Crea el directorio si no existe
        try {
            Path path = Paths.get(finalPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            System.err.println("Error creando directorio de logs: " + e.getMessage());
        }
        return finalPath;
    }

}
```

## Uso de log en aplicaciones modernas

En una aplicación web moderna, el enfoque de logging debe centrarse en mantener la seguridad, la eficiencia y la facilidad de mantenimiento. En el contexto que describes, donde todos los errores están debidamente mapeados y se envían códigos y leyendas claras al frontend, escribir logs directamente en la computadora del usuario no es una práctica recomendada. A continuación, expongo algunas razones y recomendaciones para el manejo de logs en aplicaciones web modernas:

### Escribir Logs en la Computadora del Usuario No es Recomendado

1. **Seguridad**: Escribir archivos de log en la máquina del usuario puede ser riesgoso. Podrías estar exponiendo detalles internos de la aplicación o, peor aún, información sensible del usuario. Además, un atacante podría acceder a estos logs si la máquina del usuario es comprometida.
2. **Inconsistencia**: No se puede garantizar el entorno del usuario. El usuario puede no tener permisos para escribir archivos, el sistema operativo podría no ser compatible con la lógica del archivo de log, o el almacenamiento local podría ser limitado o restringido.
3. **Dificultad de Acceso**: Cuando un problema ocurre, acceder a los logs en las máquinas de los usuarios puede ser complicado, especialmente en aplicaciones que son públicas. Los administradores de sistemas no tienen acceso fácil a esos dispositivos.

### Uso Correcto del Logging en Aplicaciones Web Modernas

1. **Centralizar el Logging en el Servidor**: Los logs deberían almacenarse en el lado del servidor o en un sistema de logging centralizado. Esto permite monitorear y analizar errores de manera eficiente, incluso cuando los usuarios no reporten el problema. Además, los servicios de logging centralizados pueden aplicar mejores prácticas de seguridad.
2. **Sistemas de Monitoreo y Logging en la Nube**:

- Utiliza servicios como AWS CloudWatch, Azure Monitor, Elastic Stack (ELK), o Splunk para centralizar el logging.
- Estos sistemas permiten un monitoreo en tiempo real y cuentan con funcionalidades de búsqueda y análisis, facilitando la identificación de patrones de errores y cuellos de botella.

3. **Enviar Solo Información Necesaria al Frontend**: Dado que ya estás lanzando excepciones como BadCredentialsException("108, La contraseña no coincide con la registrada");, esto es suficiente para el usuario y para el desarrollador del frontend. No es necesario exponer detalles técnicos adicionales que podrían comprometer la seguridad de la aplicación.

- Los códigos de error y las leyendas que describes deberían ser suficientemente claros para ayudar al usuario a comprender lo que está sucediendo, sin exponer información interna.

### Manejo de Logs en Entornos de Producción vs. Desarrollo

1. **Desarrollo**:

- En entornos de desarrollo, puedes utilizar logging más detallado (a nivel DEBUG o INFO). Esto ayuda a los desarrolladores a identificar errores y problemas de lógica durante la fase de implementación.

2. Producción:

- En producción, deberías limitarte principalmente a logs de nivel ERROR o WARN para registrar eventos importantes y errores críticos.
- Toda la información de DEBUG debe ser eliminada, ya que puede contener detalles internos que son innecesarios para el usuario final y potencialmente peligrosos si caen en manos equivocadas.

### Errores Mappeados y Logging Simplificado

1. **Mapping de Errores**: Mapear errores con códigos y mensajes específicos, como haces con BadCredentialsException("108, La contraseña no coincide con la registrada");, es una buena práctica porque:

- Facilita el Manejo de Errores en el frontend.
- Proporciona una experiencia de usuario mejorada al mostrar mensajes claros y concisos sin confundir con información técnica.

2. Logging Simplificado:

- Si los errores ya están mapeados de manera clara y registrada, los logs pueden enfocarse en aspectos relevantes del sistema, como la actividad del usuario (inicio de sesión, transacciones fallidas) y errores críticos que no están asociados a fallos de credenciales, como problemas de conectividad, fallas inesperadas de la base de datos, etc.
- En el servidor deberías registrar estos errores, junto con información adicional como el ID del usuario, la URL de la solicitud, el timestamp, etc., que son útiles para depurar problemas. Ejemplo:

```
logger.error("Error de autenticación: Código {}, Mensaje: {}", "108", "La contraseña no coincide con la registrada");
```

### Uso de Servicios de Logging para el Frontend

1. Para el frontend, puedes optar por utilizar herramientas de monitoreo que permiten rastrear errores del cliente, como `Sentry`, `New Relic`, o `LogRocket`. Estas herramientas facilitan la recopilación de errores de JavaScript, problemas de red, y comportamiento del usuario, enviando la información a un servidor central para su análisis.

2. No escribir archivos de log en el navegador: Es innecesario y no se recomienda porque los navegadores ya proporcionan herramientas de desarrollo para ver errores (`console.error`, etc.). Además, cualquier archivo de log escrito por `JavaScript` sería fácilmente accesible y modificable por el usuario, lo cual no es seguro.

### Mantener un sistema dual entre el mode `dev` y el mode `prod` centralizado

En la clase `AppUtility` se creo carga el `mode`, se crea un instancia Singleton, y un método que según el `mode` que se encuentre va a enviar los log a archivo o se deja la posibilidad de enviarlo a un sistema centralizado de monitoreo.

De esta forma el único logger en la aplicación que envía a imprimir en archivo el del `AppUtility`. de esta forma se logra:

1. **Centralizar el Control del Logging**: Puedes controlar cómo se envían los logs desde un solo lugar (AppUtility), lo cual facilita el cambio y mejora el mantenimiento del código.

2. **Personalización por Entorno**: Podrás decidir si quieres que ciertos logs se impriman solo en desarrollo y evitar su escritura en producción. Incluso podrías usar AppUtility para integrar un sistema de monitoreo de logs en la nube, mientras deshabilitas la escritura en archivos locales en producción.

3. **Preparación para Futuras Integraciones**: Al centralizar la lógica de logging, te preparas para integrar fácilmente sistemas de monitoreo en la nube (como ELK, Splunk o CloudWatch), sin tener que refactorizar cada clase por separado.

```
import org.slf4j.Logger;

@Component
public class AppUtility {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(AppUtility.class);

  @Value("${spring.profiles.active}")
  private String mode;

  public void sendLog(String message, String eMessage) {
    synchronized (logger) {
      if ("dev".equals(mode)) {
        logger.error("{}: {}", message, eMessage);
      } else {
        logger.error("{}", message);
      }
    }
  }

public String getMode() {
  return this.mode;
}

  public void createLogFile(String folderName, String fileName){
    if ("dev".equals(getMode())) {
      String logDirectory = LogPathConfig.getLogDirectoryPath(folderName);
      System.setProperty("LOG_PATH", logDirectory);
			System.setProperty("LOG_FILE_NAME", fileName);
    }
  }
}

```

Al iniciar la applicación se llama el método `createLogFile`

```
@SpringBootApplication
// asegura que Spring escanee todos los componentes dentro del paquete
// com.mvanalytic.apirest_demo_springboot y sus subpaquetes, incluidos los
// repositorios.
@ComponentScan(basePackages = { "com.mvanalytic.apirest_demo_springboot" })
public class ApirestDemoSpringbootApplication implements CommandLineRunner {

	@Autowired
	private AppUtility appUtility;

	public static void main(String[] args) {
		SpringApplication.run(ApirestDemoSpringbootApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/**
		 * Llama al método para crear el archivo de log si el perfil es `dev`. Esto
		 * asegura que el archivo de log y la carpeta correspondiente se creen durante
		 * la ejecución de la aplicación, únicamente en entornos de desarrollo. De esta
		 * manera, se mantiene una buena práctica al separar los entornos y evitar que
		 * en producción se generen logs no deseados.
		 */
		appUtility.createLogFile("sugef_test", "test_sugef.log");
	}

}
```

En los métodos que requieren capturar un error se llama el metodo del `appUtility` y se envía el mensaje ya mapeado de la aplicación y el mensaje del error, ejemplo:

```
@Service
public class AuthService {
  // Instancia singleton de logger
  // private static final Logger logger = LoggerSingleton.getLogger(AuthService.class);


  @Autowired
  private AppUtility appUtility;

  public JwtResponseDTO authenticateUser(
      String identifier,
      String password,
      HttpServletRequest request) {
    try {
      // Intenta autenticar usando el AuthenticationManager
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(identifier, password));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      // Genera el token JWT
      String jwt = jwtUtils.generateJwtToken(authentication);

      // Se carga el user
      User user = getUserByIdentifier(identifier);

      // Crear el refreshToken
      RefreshToken refreshToken = refreshTokenService.createRefreshTokenByUser(user);

      // Transforma el User en JwtResponseDTO
      JwtResponseDTO jwtResponse = UserMapper.convertUserToJwtResponse(user);

      // Se asignar los token al DTO
      jwtResponse.setToken(jwt);
      jwtResponse.setRefreshToken(refreshToken.getToken());

      // Se crea objeto UserLoginActivity
      UserLoginActivity userLoginActivity = createUserLoginActivity(user, request, "SUCCESS");

      // Se llama al SProcedure para cargar de forma atómica toda la transaccion de
      // login exitoso
      uLoginActivityService.registerSuccessfulLogin(
          user.getId(),
          refreshToken.getToken(),
          refreshToken.getExpiryDate(),
          userLoginActivity.getIpAddress(),
          userLoginActivity.getUserAgent(),
          userLoginActivity.getIdSession(),
          userLoginActivity.getSessionTime(),
          userLoginActivity.getSessionStatus());

      // notificación por correo de inicio de sesion
      sendMail(user, userLoginActivity.getIpAddress(), userLoginActivity.getUserAgent(),
          userLoginActivity.getSessionTime(), userLoginActivity.getSessionStatus());

      return jwtResponse;

    } catch (UsernameNotFoundException e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
      throw new UsernameNotFoundException("111, El identificador no existe");
    } catch (BadCredentialsException e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
      // Maneja el caso cuando el password es incorrecto
      throw new BadCredentialsException("108, La contraseña no coincide con la registrada");
    } catch (CredentialsExpiredException e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
      // Maneja el caso cuando las credenciales han expirado
      appUtility.sendLog("112, Las credenciales han expirado", e.getMessage());
      throw new CredentialsExpiredException("112, Las credenciales han expirado");

    } catch (DisabledException e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
      // Maneja el caso cuando la cuenta está deshabilitada
      appUtility.sendLog("110, Cuenta deshabilitada", e.getMessage());
      throw new DisabledException("110, Cuenta deshabilitada");
    } catch (Exception e) {
      // Registro del intento fallido por credenciales incorrectas
      failAuth(identifier, request);
      // Manejo de cualquier otra excepción de autenticación
      appUtility.sendLog("155, Error en tiempo de ejecución ", e.getMessage());
      throw new RuntimeException("155, Error en tiempo de ejecución " + e.getMessage());
    }

  }
}
```

[Retornar a la principal](../../README.md)
