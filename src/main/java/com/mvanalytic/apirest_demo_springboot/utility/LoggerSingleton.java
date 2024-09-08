package com.mvanalytic.apirest_demo_springboot.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Clase Singleton para manejar la instancia única de Logger en toda la
 * aplicación.
 * Proporciona un punto de acceso global al Logger configurado, asegurando la
 * consistencia en todo el registro de la aplicación.
 *
 * El uso de Singleton garantiza que solo se crea una instancia del Logger,
 * reduciendo la sobrecarga y centralizando la configuración del logging.
 */
public class LoggerSingleton {

  // Variable estática que almacena la referencia al logger.
  private static Logger logger = null;

  /**
   * Constructor privado para prevenir la instanciación de la clase.
   * La clase no debe ser instanciada desde fuera para mantener el patrón
   * Singleton.
   */
  private LoggerSingleton() {
  }

  /**
   * Proporciona la instancia del logger para la clase solicitada.
   * Si el logger no ha sido creado, inicializa una nueva instancia.
   *
   * @param clazz La clase para la cual se solicita el logger.
   *              Usa el nombre de esta clase para configurar el logger.
   * @return La instancia de Logger configurada para la clase especificada.
   */
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
