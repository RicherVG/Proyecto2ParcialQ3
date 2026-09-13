/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author riche
 */
public class ListaEnlazadaPublicaciones implements Serializable {
   private static final long serialVersionUID = 1L;
   private NodoPublicacion cabeza; 
   private int tamanio;
   public ListaEnlazadaPublicaciones() {
      this.cabeza   = null;
      this.tamanio  = 0;
   }
   public void agregar(Publicacion p) {
      NodoPublicacion nuevo = new NodoPublicacion(p);
      if (cabeza == null) {
         cabeza = nuevo;
      } else {
         NodoPublicacion actual = cabeza;
         while (actual.getSiguiente() != null) {
            actual = actual.getSiguiente();
         }
         actual.setSiguiente(nuevo);
      }
      tamanio++;
   }
   public boolean eliminar(Publicacion p) {
      if (cabeza == null) return false;
      if (cabeza.getDato().equals(p)){
         cabeza = cabeza.getSiguiente();
         tamanio--;
         return true;
      }
      NodoPublicacion actual = cabeza;
      while (actual.getSiguiente() != null) {
         if (actual.getSiguiente().getDato() == p) {
            actual.setSiguiente(actual.getSiguiente().getSiguiente());
            tamanio--;
            return true;
         }
         actual = actual.getSiguiente();
      }
      return false;
   }
   public List<Publicacion> toList() {
      List<Publicacion> lista = new ArrayList<>();
      NodoPublicacion actual = cabeza;
      while (actual != null) {
         lista.add(actual.getDato());
         actual = actual.getSiguiente();
      }
      return lista;
   }
   public static ListaEnlazadaPublicaciones fromList(List<Publicacion> lista) {
      ListaEnlazadaPublicaciones lep = new ListaEnlazadaPublicaciones();
      for (Publicacion p : lista) {
         lep.agregar(p);
      }
      return lep;
   }
   public int size() {
      return tamanio;
   }
   
   public int sizeRecursivo() {
      return contarNodos(cabeza);
   }
   
   private int contarNodos(NodoPublicacion actual) {
      if (actual == null) return 0;
      return 1 + contarNodos(actual.getSiguiente());
   }
   
   public boolean isEmpty() {
      return tamanio == 0;
   }
   public NodoPublicacion getCabeza() {
      return cabeza;
   }
   @Override
   public String toString() {
      return "ListaEnlazadaPublicaciones{tamanio=" + tamanio + "}";
   }
}
