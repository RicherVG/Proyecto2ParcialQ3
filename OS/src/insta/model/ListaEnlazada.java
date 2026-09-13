package insta.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Estructura de datos propia: Lista Enlazada Simple genérica.
 * Requisito académico obligatorio (Sección 2.4).
 * 
 * @author riche
 * @param <T> tipo de elemento en la lista
 */
public class ListaEnlazada<T> implements Serializable, Iterable<T> {
   private static final long serialVersionUID = 1L;
   private Nodo<T> cabeza;
   private int tamanio;

   public ListaEnlazada() {
      this.cabeza = null;
      this.tamanio = 0;
   }

   public void agregar(T dato) {
      Nodo<T> nuevo = new Nodo<>(dato);
      if (cabeza == null) {
         cabeza = nuevo;
      } else {
         Nodo<T> actual = cabeza;
         while (actual.getSiguiente() != null) {
            actual = actual.getSiguiente();
         }
         actual.setSiguiente(nuevo);
      }
      tamanio++;
   }

   public void agregarAlInicio(T dato) {
      Nodo<T> nuevo = new Nodo<>(dato, cabeza);
      cabeza = nuevo;
      tamanio++;
   }

   public boolean eliminar(T dato) {
      if (cabeza == null) return false;
      if (sonIguales(cabeza.getDato(), dato)) {
         cabeza = cabeza.getSiguiente();
         tamanio--;
         return true;
      }
      Nodo<T> actual = cabeza;
      while (actual.getSiguiente() != null) {
         if (sonIguales(actual.getSiguiente().getDato(), dato)) {
            actual.setSiguiente(actual.getSiguiente().getSiguiente());
            tamanio--;
            return true;
         }
         actual = actual.getSiguiente();
      }
      return false;
   }

   public boolean eliminarIgnoreCase(String valor) {
      if (cabeza == null) return false;
      if (cabeza.getDato() instanceof String && ((String) cabeza.getDato()).equalsIgnoreCase(valor)) {
         cabeza = cabeza.getSiguiente();
         tamanio--;
         return true;
      }
      Nodo<T> actual = cabeza;
      while (actual.getSiguiente() != null) {
         if (actual.getSiguiente().getDato() instanceof String &&
               ((String) actual.getSiguiente().getDato()).equalsIgnoreCase(valor)) {
            actual.setSiguiente(actual.getSiguiente().getSiguiente());
            tamanio--;
            return true;
         }
         actual = actual.getSiguiente();
      }
      return false;
   }

   public boolean contiene(T dato) {
      Nodo<T> actual = cabeza;
      while (actual != null) {
         if (sonIguales(actual.getDato(), dato)) {
            return true;
         }
         actual = actual.getSiguiente();
      }
      return false;
   }

   public boolean contieneIgnoreCase(String valor) {
      Nodo<T> actual = cabeza;
      while (actual != null) {
         if (actual.getDato() instanceof String && ((String) actual.getDato()).equalsIgnoreCase(valor)) {
            return true;
         }
         actual = actual.getSiguiente();
      }
      return false;
   }

   public T get(int index) {
      if (index < 0 || index >= tamanio) {
         throw new IndexOutOfBoundsException("Índice fuera de rango: " + index + " (tamaño: " + tamanio + ")");
      }
      Nodo<T> actual = cabeza;
      for (int i = 0; i < index; i++) {
         actual = actual.getSiguiente();
      }
      return actual.getDato();
   }

   public List<T> toList() {
      List<T> lista = new ArrayList<>();
      Nodo<T> actual = cabeza;
      while (actual != null) {
         lista.add(actual.getDato());
         actual = actual.getSiguiente();
      }
      return lista;
   }

   public static <E> ListaEnlazada<E> fromList(List<E> lista) {
      ListaEnlazada<E> le = new ListaEnlazada<>();
      if (lista != null) {
         for (E item : lista) {
            le.agregar(item);
         }
      }
      return le;
   }

   public int size() {
      return tamanio;
   }

   public boolean isEmpty() {
      return tamanio == 0;
   }

   public void limpiar() {
      cabeza = null;
      tamanio = 0;
   }

   public Nodo<T> getCabeza() {
      return cabeza;
   }

   private boolean sonIguales(T a, T b) {
      if (a == null && b == null) return true;
      if (a == null || b == null) return false;
      return a.equals(b);
   }

   @Override
   public Iterator<T> iterator() {
      return new Iterator<T>() {
         private Nodo<T> actual = cabeza;

         @Override
         public boolean hasNext() {
            return actual != null;
         }

         @Override
         public T next() {
            if (actual == null) throw new NoSuchElementException();
            T dato = actual.getDato();
            actual = actual.getSiguiente();
            return dato;
         }
      };
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("[");
      Nodo<T> actual = cabeza;
      while (actual != null) {
         sb.append(actual.getDato());
         if (actual.getSiguiente() != null) sb.append(", ");
         actual = actual.getSiguiente();
      }
      sb.append("]");
      return sb.toString();
   }
}
