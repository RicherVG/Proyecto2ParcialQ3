/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 *
 * @author riche
 */
public class Notificacion implements Serializable {
   private static final long serialVersionUID = 1L;
   public enum Tipo {
      SOLICITUD, LIKE, COMENTARIO, SEGUIDOR
   }
   private final Tipo tipo;
   private final String autorUsername;  
   private final String texto;          
   private final LocalDateTime timestamp;
   private boolean leida = false;
   public Notificacion(Tipo tipo, String autorUsername, String texto) {
      this.tipo = tipo;
      this.autorUsername = autorUsername;
      this.texto = texto;
      this.timestamp = LocalDateTime.now();
   }
   public Tipo getTipo()             { return tipo; }
   public String getAutorUsername()  { return autorUsername; }
   public String getTexto()          { return texto; }
   public LocalDateTime getTimestamp() { return timestamp; }
   public boolean isLeida()          { return leida; }
   public void setLeida(boolean l)    { this.leida = l; }
}
