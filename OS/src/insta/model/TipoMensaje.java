/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;
/**
 *
 * @author riche
 */
public enum TipoMensaje {
   TEXTO("Texto"),
   STICKER("Sticker"),
   IMAGEN("Imagen");
   private final String etiqueta;
   TipoMensaje(String etiqueta) {
      this.etiqueta = etiqueta;
   }
   public String getEtiqueta() {
      return etiqueta;
   }
   @Override
   public String toString() {
      return etiqueta;
   }
}
