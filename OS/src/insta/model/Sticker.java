/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;
import java.io.Serializable;
/**
 *
 * @author riche
 */
public class Sticker implements Serializable {
   private static final long serialVersionUID = 1L;
   private String nombre;      
   private String rutaImagen;  
   public Sticker(String nombre, String rutaImagen) {
      this.nombre     = nombre;
      this.rutaImagen = rutaImagen;
   }
   public String getNombre()     { return nombre;     }
   public String getRutaImagen() { return rutaImagen; }
   @Override
   public String toString() { return nombre; }
}
