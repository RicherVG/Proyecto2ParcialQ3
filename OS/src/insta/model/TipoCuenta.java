/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;
/**
 *
 * @author riche
 */
public enum TipoCuenta {
   PUBLICA("Pública"),
   PRIVADA("Privada");
   private final String etiqueta;
   TipoCuenta(String etiqueta) {
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
