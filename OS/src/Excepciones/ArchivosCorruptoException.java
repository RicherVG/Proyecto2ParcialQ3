/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Excepciones;

/**
 *
 * @author user
 */
public class ArchivosCorruptoException extends Exception {
    
    public ArchivosCorruptoException (String nombreArchivo){
        super("El archivo: " + nombreArchivo + " esta dañado o no se pudo leer correctamente");
    }
    
    public ArchivosCorruptoException(String nombreArchivo, Throwable causa){
      super("El archivo: " + nombreArchivo + " esta dañado o no se pudo acceder de manera correcta", causa);  
    }
    
}
