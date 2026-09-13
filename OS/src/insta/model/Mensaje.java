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
public class Mensaje implements Serializable {
   private static final long serialVersionUID = 1L;
   private String      emisor;
   private String      receptor;
   private LocalDate   fecha;
   private LocalTime   hora;
   private String      contenido;  
   private TipoMensaje tipo;       
   private boolean     leido;
   public Mensaje(String emisor, String receptor,
               String contenido, String tipoStr) {
      this.emisor   = emisor;
      this.receptor = receptor;
      this.contenido= contenido.length() > 300
                  ? contenido.substring(0, 300) : contenido;
      this.tipo     = parseTipo(tipoStr);
      this.fecha    = LocalDate.now();
      this.hora     = LocalTime.now().withNano(0);
      this.leido    = false;
   }
   private static TipoMensaje parseTipo(String valor) {
      if (valor == null) return TipoMensaje.TEXTO;
      try {
         return TipoMensaje.valueOf(valor.toUpperCase());
      } catch (IllegalArgumentException e) {
         return TipoMensaje.TEXTO; 
      }
   }
   private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
      java.io.ObjectInputStream.GetField fields = ois.readFields();
      emisor   = (String)    fields.get("emisor",   null);
      receptor = (String)    fields.get("receptor", null);
      fecha    = (LocalDate) fields.get("fecha",    null);
      hora     = (LocalTime) fields.get("hora",     null);
      contenido= (String)    fields.get("contenido",null);
      leido    =             fields.get("leido",    false);
      Object tm = fields.get("tipo", null);
      if (tm instanceof TipoMensaje) {
         tipo = (TipoMensaje) tm;
      } else if (tm instanceof String) {
         tipo = parseTipo((String) tm);
      } else {
         tipo = TipoMensaje.TEXTO;
      }
   }
   private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
      oos.defaultWriteObject();
   }
   public String    getEmisor()   { return emisor;   }
   public String    getReceptor() { return receptor; }
   public LocalDate getFecha()    { return fecha;    }
   public LocalTime getHora()     { return hora;     }
   public String    getContenido(){ return contenido;}
   public boolean   isLeido()     { return leido;    }
   public TipoMensaje getTipoEnum() { return tipo; }
   public String getTipo() {
      return tipo != null ? tipo.name() : "TEXTO";
   }
   public void setLeido(boolean leido) { this.leido = leido; }
   @Override
   public String toString() {
      return "[" + getTipo() + "] " + emisor + " → " + receptor + ": " + contenido;
   }
}
