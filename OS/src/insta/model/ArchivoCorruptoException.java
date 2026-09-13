package insta.model;

import java.io.IOException;

/**
 * Excepción lanzada cuando un archivo binario del sistema no puede leerse o está corrupto.
 * 
 * @author riche
 */
public class ArchivoCorruptoException extends IOException {
   private static final long serialVersionUID = 1L;

   public ArchivoCorruptoException(String rutaArchivo) {
      super("El archivo binario está corrupto o es incompatible: " + rutaArchivo);
   }

   public ArchivoCorruptoException(String rutaArchivo, Throwable cause) {
      super("El archivo binario está corrupto o es incompatible: " + rutaArchivo + " (" + cause.getMessage() + ")", cause);
   }
}
