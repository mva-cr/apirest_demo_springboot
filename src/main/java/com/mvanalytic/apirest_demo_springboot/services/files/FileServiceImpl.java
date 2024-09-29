package com.mvanalytic.apirest_demo_springboot.services.files;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Servicio que implementa la interfaz {@link FileService} para la gestión de
 * archivos. Proporciona métodos para cargar archivos de forma segura desde una
 * ubicación de almacenamiento. Esta clase maneja excepciones relacionadas con
 * la manipulación de archivos y controla el acceso a ellos.
 * 
 * <p>
 * La clase usa el componente {@link AppUtility} para obtener la ubicación de
 * almacenamiento de los archivos y maneja solo archivos PDF como medida de
 * seguridad.
 * </p>
 * 
 * <p>
 * Excepciones manejadas:
 * <ul>
 * <li>{@link FileNotFoundException} si el archivo solicitado no se encuentra o
 * no es legible.</li>
 * <li>{@link MalformedURLException} si la URL generada para acceder al archivo
 * no es válida.</li>
 * <li>{@link SecurityException} si se intenta acceder a un archivo fuera del
 * directorio permitido o si se trata de un tipo de archivo no permitido.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * El servicio usa el logger proporcionado por {@link LoggerSingleton} para
 * registrar errores y advertencias.
 * </p>
 */
@Service
public class FileServiceImpl implements FileService {

  // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(FileServiceImpl.class);

  @Autowired
  private AppUtility appUtility;

  /**
   * Carga un archivo como recurso a partir de su nombre de archivo.
   * <p>
   * Este método localiza el archivo en el sistema de archivos utilizando la ruta
   * de almacenamiento definida y devuelve un recurso legible si el archivo existe
   * y es accesible.
   * Solo se permiten archivos de tipo PDF, y se aplica una validación de
   * seguridad para evitar accesos no autorizados fuera de la carpeta de
   * almacenamiento permitida.
   * 
   * @param fileName El nombre del archivo que se desea cargar.
   * @return Un objeto {@link Resource} que representa el archivo solicitado si
   *         existe y es legible.
   * @throws RuntimeException Si el archivo no se encuentra, tiene una URL
   *                          malformada o hay una violación de seguridad.
   */
  @Override
  public Resource loadFileAResource(String fileName) {
    try {
      // Obtener la ruta base desde appUtility
      Path storageLocation = Paths.get(appUtility.getFileStorageLocation())
          .toAbsolutePath().normalize();

      // Resolver la ruta del archivo solicitado a partir de la ruta base
      Path filePath = storageLocation.resolve(fileName).normalize();

      // Validar que el archivo solicitado esté dentro de la carpeta de almacenamiento
      // permitida
      if (!filePath.startsWith(storageLocation)) {
        throw new SecurityException("165, Acceso no autorizado al archivo");
      }

      // Limitar unicamente a archivos de tipo .pdf
      if (!fileName.endsWith(".pdf")) {
        throw new SecurityException("163, Tipo de archivo no permitido");
      }

      // Crear el recurso del archivo
      Resource resource = new UrlResource(filePath.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new FileNotFoundException("164, Archivo no encontrado");
      }
    } catch (FileNotFoundException e) {
      logger.error("Error: Archivo no encontrado", e);
      throw new RuntimeException(e.getMessage()); // Maneja la excepción
    } catch (MalformedURLException e) {
      logger.error("Error de URL", e);
      throw new RuntimeException(e.getMessage());
    }
  }

}
