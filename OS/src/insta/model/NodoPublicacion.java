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
public class NodoPublicacion implements Serializable {
   private static final long serialVersionUID = 1L;
   private Publicacion dato;
   private NodoPublicacion siguiente; 
   public NodoPublicacion(Publicacion dato) {
      this.dato = dato;
      this.siguiente = null;
   }
   public Publicacion getDato() {
      return dato;
   }
   public NodoPublicacion getSiguiente() {
      return siguiente;
   }
   public void setSiguiente(NodoPublicacion siguiente) {
      this.siguiente = siguiente;
   }
   @Override
   public String toString() {
      return "Nodo[" + (dato != null ? dato.getUsername() : "null") + "]";
   }
}
