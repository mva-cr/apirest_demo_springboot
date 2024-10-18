package com.mvanalytic.apirest_demo_springboot.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Clase de utilidad para configurar y obtener la ruta del directorio de logs de
 * la aplicación.
 * 
 * Esta clase proporciona un método para determinar la ruta de un directorio de
 * logs basado en el sistema operativo y el nombre de la aplicación
 * proporcionado. Además, crea el directorio de logs si no existe.
 * 
 * @author Mario Martínez Lanuza
 */
@Component
public class LogPathConfig {

  @Autowired
  private static AppUtility appUtility;

  public static String getLogDirectoryPath(String applicationName, String fileName) {
    String os = System.getProperty("os.name").toLowerCase();
    String baseDir = os.contains("win") ? "C:" + File.separator + "Logs" : System.getProperty("user.home") + "/logs";
    String finalPath = baseDir + File.separator + applicationName + File.separator;

    // crear el directorio si no existe
    try {
      // Crear el directorio si no existe
      Path dirPath = Paths.get(finalPath);
      if (!Files.exists(dirPath)) {
        Files.createDirectories(dirPath);
      }

      // Crear el archivo de log si no existe
      File logFile = new File(finalPath + fileName);
      if (!logFile.exists()) {
        logFile.createNewFile();

        // Files.createFile(logFilePath);
        // appUtility.sendLog("Archivo de log", "creado satisfactoriamente");
      }
    } catch (IOException e) {
      appUtility.sendLog("513, Error creando directorio de logs", e.getMessage());
    }
    return finalPath;
  }
}
