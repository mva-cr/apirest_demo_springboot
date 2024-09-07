# Flujo de Logger

Configuración del Logger (Inicio de la Aplicación)

La clase LoggerConfig configura los detalles del logger al inicio de la aplicación.
Este paso define los "appenders" (destinos de los logs), los "layouts" (formato de los logs), y las políticas de rotación.
java
Copiar código
public class LoggerConfig {
    public static void configureLogger(String applicationName) {
        // Obtiene el directorio de logs
        String logDir = LogPathConfig.getLogDirectoryPath(applicationName);

        // Construye la configuración del logger
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.ERROR);
        builder.setConfigurationName("RollingBuilder");

        // Configura el appender para la consola
        AppenderComponentBuilder consoleAppender = builder.newAppender("Stdout", "CONSOLE")
            .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        consoleAppender.add(builder.newLayout("PatternLayout")
            .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} %-5level - %msg%n"));
        builder.add(consoleAppender);

        // Configura el appender para archivo de log con rotación
        AppenderComponentBuilder fileAppender = builder.newAppender("rolling", "RollingFile")
            .addAttribute("fileName", logDir + "test_sugef.log")
            .addAttribute("filePattern", logDir + "app-%d{yyyy-MM-dd}.log.gz");
        fileAppender.add(builder.newLayout("PatternLayout")
            .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n"));

        // Política de rotación por tiempo
        ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
            .addComponent(builder.newComponent("TimeBasedTriggeringPolicy").addAttribute("interval", "1")
                .addAttribute("modulate", true));
        fileAppender.addComponent(triggeringPolicy);
        builder.add(fileAppender);

        // Configura loggers específicos y el logger raíz
        builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG)
            .add(builder.newAppenderRef("rolling"))
            .addAttribute("additivity", false));

        builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("rolling")));

        // Inicializa el logger
        Configurator.initialize(builder.build());
    }
}
Obtención de la Ruta del Directorio de Logs

LogPathConfig se asegura de que el directorio donde se almacenarán los logs exista.
Si el directorio no existe, lo crea.
java
Copiar código
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
Inicialización del Logger Singleton

LoggerSingleton proporciona una instancia única del logger configurado.
Esta clase se asegura de que el logger se inicialice una sola vez y se reutilice en toda la aplicación.
java
Copiar código
public class LoggerSingleton {
    private static Logger logger = null;

    private LoggerSingleton() {}

    public static Logger getLogger(Class<?> clazz) {
        if (logger == null) {
            synchronized (LoggerSingleton.class) {
                if (logger == null) {
                    logger = LogManager.getLogger(clazz);
                }
            }
        }
        return logger;
    }
}
Uso del Logger en la Aplicación

En cualquier parte de tu aplicación, puedes utilizar el logger para registrar eventos o mensajes de depuración.
Por ejemplo, en la clase AuthTokenFilter:
java
Copiar código
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerSingleton.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {
            // Obtiene el token JWT del encabezado de la solicitud
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String userName = jwtUtils.getNicknameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userName);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("No se puede configurar la autenticación del usuario: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
Salida de Logs a la Consola y a los Archivos

Basado en la configuración de LoggerConfig, los mensajes de log se envían tanto a la consola (Stdout) como a los archivos de log configurados (RollingFile).
El archivo de log principal es test-sugef.log, y los archivos rotados seguirán el patrón app-YYYY-MM-DD.log.gz.
Verificación de Logs

Los logs deben estar presentes en el directorio especificado por LogPathConfig.
Puedes verificar si los archivos se están generando correctamente y si contienen los mensajes esperados.
Posibles Problemas y Soluciones
Archivo de Log no Creado:

Verifica que LoggerConfig.configureLogger se llame correctamente al inicio de la aplicación.
Asegúrate de que la aplicación tenga permisos para escribir en el directorio de logs.
Revisa la consola de salida estándar para cualquier mensaje de error relacionado con la configuración de Log4j2.
Logs no Aparecen en Consola:

Confirma que el appender de consola (Stdout) esté configurado correctamente.
Verifica que el nivel de log configurado (Level.DEBUG, Level.INFO, etc.) sea apropiado para los mensajes que deseas ver.
Logs no Aparecen en Archivos:

Asegúrate de que la política de rotación esté configurada correctamente y que la ruta del archivo de log sea válida.
Revisa los permisos del sistema de archivos para el directorio de logs.
Resumen
El flujo completo del logger comienza con la configuración inicial en LoggerConfig, seguido por la creación de la ruta de logs en LogPathConfig, la obtención de una instancia única del logger en LoggerSingleton, y finalmente el uso del logger en la aplicación para registrar eventos, errores, y mensajes de depuración.

