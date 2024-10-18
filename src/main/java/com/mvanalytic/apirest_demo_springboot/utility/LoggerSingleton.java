package com.mvanalytic.apirest_demo_springboot.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase Singleton para manejar instancias únicas de Logger para cada clase en
 * toda la aplicación. Proporciona un punto de acceso global al Logger
 * configurado para cada clase, asegurando la consistencia en todo el registro
 * de la aplicación.
 *
 * Se usa una estructura de caché para almacenar un logger por cada clase que lo
 * solicite.
 * 
 * @author Mario Martínez Lanuza
 */
public class LoggerSingleton {

  // Caché de loggers para cada clase.
  private static final Map<Class<?>, Logger> loggers = new ConcurrentHashMap<>();

  /**
   * Constructor privado para prevenir la instanciación de la clase.
   */
  private LoggerSingleton() {
    // Evitar la creación de instancias
  }

  /**
   * Proporciona la instancia del logger para la clase solicitada. Si el logger no
   * ha sido creado para la clase, se inicializa una nueva instancia.
   *
   * @param clazz La clase para la cual se solicita el logger.
   * @return La instancia de Logger configurada para la clase especificada.
   */
  public static Logger getLogger(Class<?> clazz) {
    return loggers.computeIfAbsent(clazz, LoggerFactory::getLogger);
  }
}
