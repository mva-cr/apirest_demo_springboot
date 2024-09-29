package com.mvanalytic.apirest_demo_springboot.services.files;

import org.springframework.core.io.Resource;

/**
 * Interfaz para el servicio de manejo de archivos.
 *
 * Esta interfaz define un contrato para la carga de archivos dentro de la aplicación. Su implementación 
 * permitirá cargar archivos desde una ubicación de almacenamiento y devolverlos como recursos.
 */
public interface FileService {
  Resource loadFileAResource(String fileName);
}
