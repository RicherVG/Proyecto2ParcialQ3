/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;
/**
 *
 * @author riche
 */
public class Publicacion extends ContenidoBase implements Comparable<Publicacion> {
   private static final long serialVersionUID = 1L;
   private String  hashtags;       
   private String  menciones;      
   private String  rutaImagen;     
   private String  tipoMultimedia; 
   private java.util.List<String>     likes; 
   private java.util.List<Comentario> comentarios;
   public Publicacion(String username, String contenido,
                  String hashtags, String menciones,
                  String rutaImagen, String tipoMultimedia) {
      super(username, contenido); 
      this.hashtags  = hashtags;
      this.menciones = menciones;
      this.rutaImagen = rutaImagen;
      this.tipoMultimedia = tipoMultimedia;
      this.likes = new java.util.ArrayList<>();
      this.comentarios = new java.util.ArrayList<>();
   }
   @SuppressWarnings("unchecked")
   private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
      java.io.ObjectInputStream.GetField fields = ois.readFields();
      hashtags       = (String) fields.get("hashtags","");
      menciones      = (String) fields.get("menciones","");
      rutaImagen     = (String) fields.get("rutaImagen","");
      tipoMultimedia = (String) fields.get("tipoMultimedia", "NINGUNA");
      try {
         likes = (java.util.List<String>) fields.get("likes", new java.util.ArrayList<String>());
         comentarios = (java.util.List<Comentario>) fields.get("comentarios", new java.util.ArrayList<Comentario>());
      } catch (IllegalArgumentException e) {
         likes = new java.util.ArrayList<>();
         comentarios = new java.util.ArrayList<>();
      }
      try {
         String oldUser = (String) fields.get("username", null);
         if (oldUser != null) this.username = oldUser;
         java.time.LocalDate oldDate = (java.time.LocalDate) fields.get("fecha", null);
         if (oldDate != null) this.fecha = oldDate;
         java.time.LocalTime oldTime = (java.time.LocalTime) fields.get("hora", null);
         if (oldTime != null) this.hora = oldTime;
         String oldCont = (String) fields.get("contenido", null);
         if (oldCont != null) this.contenido = oldCont;
      } catch (IllegalArgumentException e) {
      }
   }
   private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
      oos.defaultWriteObject();
   }
   @Override
   public String getHashtags() { 
       return hashtags;      
   }
   public String getMenciones() { 
       return menciones;  
   }
   public String getRutaImagen() { 
       return rutaImagen;   
   }
   public String getTipoMultimedia() {
       return tipoMultimedia; 
   }
   public java.util.List<String> getLikes() { return likes; }
   public java.util.List<Comentario> getComentarios() { return comentarios; }
   public void addLike(String username) {
      if (!likes.contains(username)) likes.add(username);
   }
   public void removeLike(String username) {
      likes.remove(username);
   }
   public void addComentario(Comentario c) {
      comentarios.add(c);
   }
   @Override
   public String getResumen() {
      String preview = contenido.length() > 40
                   ? contenido.substring(0, 40) + "..." : contenido;
      return "📷 " + preview;
   }
   @Override
   public int compareTo(Publicacion otra) {
      int cmpFecha = otra.getFecha().compareTo(this.getFecha());
      if (cmpFecha != 0) return cmpFecha;
      return otra.getHora().compareTo(this.getHora());
   }
   @Override
   public String toString() {
      return username + " | " + fecha + " " + hora + " | " + contenido;
   }
}
