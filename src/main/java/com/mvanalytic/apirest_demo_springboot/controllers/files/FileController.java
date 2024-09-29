package com.mvanalytic.apirest_demo_springboot.controllers.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvanalytic.apirest_demo_springboot.services.files.FileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/public/files")
public class FileController {

  @Autowired
  private FileService fileService;

  /**
   * Controlador para descargar un archivo PDF desde el servidor.
   *
   * Este método responde a las solicitudes GET en la ruta
   * "/download/{fileName:.+}".
   * El archivo solicitado por el nombre de archivo es enviado como un archivo
   * adjunto para su descarga.
   * 
   * @param fileName Nombre del archivo a descargar, capturado desde la URL. El
   *                 parámetro de la ruta "{fileName:.+}" asegura que el nombre
   *                 del archivo capture también extensiones como ".pdf" o ".jpg"
   *                 gracias a la expresión regular `:.+`.
   * 
   * @return ResponseEntity<Resource> con el archivo como cuerpo de la respuesta.
   *         Incluye los encabezados adecuados para forzar la descarga del archivo
   *         como un archivo adjunto.
   * 
   *         - Si el archivo existe y es legible, devuelve un código HTTP 200 (OK)
   *         y el archivo en el cuerpo de la respuesta.
   *         - Si no, arroja una excepción manejada dentro del servicio
   *         `FileService`.
   */
  @GetMapping("/download/{fileName:.+}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
    Resource file = fileService.loadFileAResource(fileName);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + file.getFilename() + "\" ")
        .body(file);
  }

}
