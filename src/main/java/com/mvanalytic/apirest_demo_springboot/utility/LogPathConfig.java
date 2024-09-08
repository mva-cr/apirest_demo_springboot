package com.mvanalytic.apirest_demo_springboot.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

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
