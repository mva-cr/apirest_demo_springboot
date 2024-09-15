package com.mvanalytic.apirest_demo_springboot.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

/**
 * Clase de utilidad para configurar y obtener la ruta del directorio de logs de la aplicación.
 * 
 * Esta clase proporciona un método para determinar la ruta de un directorio de logs
 * basado en el sistema operativo y el nombre de la aplicación proporcionado.
 * Además, crea el directorio de logs si no existe.
 * 
 * Uso típico:
 * <pre>
 * {@code
 * String logPath = LogPathConfig.getLogDirectoryPath("MyApplication");
 * System.out.println("Los logs se almacenarán en: " + logPath);
 * }
 * </pre>
 * 
 * Comportamiento:
 * <ul>
 *   <li>Para sistemas Windows, los logs se almacenarán en "C:\Logs\<nombreAplicacion>\".</li>
 *   <li>Para sistemas basados en Unix/Linux/MacOS, los logs se almacenarán en 
 *       "<user.home>/logs/<nombreAplicacion>/".</li>
 * </ul>
 * 
 * El directorio de logs se crea automáticamente si no existe.
 * 
 * @author Mario Martínez Lanuza
 */
public class LogPathConfig {

  public static String getLogDirectoryPath(String applicationName) {
    String os = System.getProperty("os.name").toLowerCase();
    String baseDir = os.contains("win") ? "C:" + File.separator + "Logs" : System.getProperty("user.home") + "/logs";
    String finalPath = baseDir + File.separator + applicationName + File.separator;

    // crear el directorio si no existe
    try {
      Path path = Paths.get(finalPath);
      if (!Files.exists(path)) {
        Files.createDirectories(path);
      } else {
        System.out.println("el direcorio ya existe");
      }
    } catch (Exception e) {
      System.err.println("Error creando directorio de logs: " + e.getMessage());
    }
    return finalPath;
  }
}
