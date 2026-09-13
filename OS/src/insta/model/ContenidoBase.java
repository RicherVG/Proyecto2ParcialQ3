/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
/**
 *
 * @author riche
 */
public abstract class ContenidoBase implements Serializable, Publicable {
   private static final long serialVersionUID = 1L;
   protected String    username;
   protected LocalDate fecha;
   protected LocalTime hora;
   protected String    contenido; 
   protected ContenidoBase(String username, String contenido) {
      this.username  = username;
      this.contenido = contenido.length() > 220
                   ? contenido.substring(0, 220) : contenido;
      this.fecha     = LocalDate.now();
      this.hora      = LocalTime.now().withNano(0);
   }
   public String getInfo() {
      return "@" + username + " — " + fecha + " " + hora;
   }
   @Override
   public String getUsername() { 
       return username;
   }
   @Override
   public String getContenido() { 
       return contenido; 
   }
   public LocalDate getFecha() { 
       return fecha; 
   }
   public LocalTime getHora()  {
       return hora;  
   }
   @Override
   public abstract String getResumen();
}
