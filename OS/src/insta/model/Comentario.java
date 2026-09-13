/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author riche
 */
public class Comentario implements Serializable {
   private static final long serialVersionUID = 1L;
   private String        autor;
   private String        texto;
   private LocalDateTime fechaHora;
   public Comentario(String autor, String texto) {
      this.autor     = autor;
      this.texto     = texto;
      this.fechaHora = LocalDateTime.now();
   }
   public String getAutor() {
      return autor;
   }
   public String getTexto() {
      return texto;
   }
   public LocalDateTime getFechaHora() {
      return fechaHora;
   }
   public String getFechaHoraFormateada() {
      if (fechaHora == null) return "";
      return fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
   }
}
