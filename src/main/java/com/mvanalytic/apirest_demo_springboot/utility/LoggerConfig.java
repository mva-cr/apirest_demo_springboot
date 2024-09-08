package com.mvanalytic.apirest_demo_springboot.utility;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.appender.ConsoleAppender;

/**
 * Clase de configuración para el sistema de logging de la aplicación.
 * Esta configuración permite registrar eventos tanto en consola como en
 * archivos de log rotativos.
 */
public class LoggerConfig {

    /**
     * Configura los agregadores y políticas de log para la aplicación.
     * 
     * @param applicationName El nombre de la aplicación, utilizado para definir la
     *                        ruta del archivo de log.
     */
    public static void configureLogger(String applicationName) {
        // Determina la ruta del directorio de log basada en el sistema operativo y la
        // existencia del directorio.
        String logDir = LogPathConfig.getLogDirectoryPath(applicationName);

        // Inicia la construcción de la configuración de Log4j2.
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.ERROR);
        builder.setConfigurationName("RollingBuilder");

        // Configura un agregador de consola para la salida estándar.
        AppenderComponentBuilder consoleAppender = builder.newAppender("Stdout", "CONSOLE")
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        consoleAppender.add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} %-5level - %msg%n"));
        builder.add(consoleAppender);

        // Configura un agregador de archivo con rotación basada en tiempo.
        AppenderComponentBuilder fileAppender = builder.newAppender("rolling", "RollingFile")
                // Archivo principal
                .addAttribute("fileName", logDir + "test_sugef.log")
                // Archivos rotados
                .addAttribute("filePattern", logDir + "test_sugef-%d{yyyy-MM-dd}.log.gz");
        fileAppender.add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n"));

        // Configura la política de rotación basada en tiempo para el agregador de
        // archivo.
        ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
                .addComponent(builder.newComponent("TimeBasedTriggeringPolicy")
                        .addAttribute("interval", "1")
                        .addAttribute("modulate", true));
        fileAppender.addComponent(triggeringPolicy);
        builder.add(fileAppender);

        // Configura los niveles de log y la aditividad para el logger principal y
        // específicos.
        builder.add(builder.newLogger("com.mvanalytic", Level.DEBUG)
                .add(builder.newAppenderRef("rolling"))
                .addAttribute("additivity", false));

        // Configura el logger raíz para capturar todos los mensajes de nivel DEBUG.
        builder.add(builder.newRootLogger(Level.DEBUG).add(builder.newAppenderRef("rolling")));

        // Inicializa la configuración de log.
        Configurator.initialize(builder.build());
    }
}
