/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author andre
 */
public class ListaEnlazada<T> implements Iterable<T> {
    private Nodo<T> cabeza;
    private int tamano;
    
    public ListaEnlazada(){
        this.cabeza = null;
        this.tamano = 0;
    }
    
    public void agregar(T dato){
        Nodo<T> nuevo = new Nodo<>(dato);
        if(cabeza == null){
            cabeza = nuevo;
        }
        else{
            Nodo<T> actual = cabeza;
            while(actual.getSiguiente()!= null){
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        tamano++;
    }
    
    public boolean eliminar(T dato){
        if(cabeza == null)
           return false;    
        
        if (cabeza.getDato().equals(dato)){
            cabeza = cabeza.getSiguiente();
            tamano--;
            return true;
            
        }
        
        Nodo<T> actual = cabeza;
        while(actual.getSiguiente() != null){
            if (actual.getSiguiente().getDato().equals(dato)){
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamano--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }
    
    public boolean contiene(T dato){
        Nodo<T> actual = cabeza;
        while(actual != null){
            if(actual.getDato().equals(dato)){
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }
    
    public int tamano(){
        return tamano;
    }
    
    public boolean estaVacia(){
        return cabeza == null;
    }
    
    @Override
    public Iterator<T> iterator(){
        return new Iterator<T>(){
            private Nodo<T> actual = cabeza;
            
            public boolean hasNext(){
                return actual != null;
            }
            
           public T next(){
               if(!hasNext()){
                   throw new NoSuchElementException();
               }
               T dato = actual.getDato();
               actual = actual.getSiguiente();
               return dato;
           }
            
         
            
        };
    }
    
    
    public String toString(){
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = cabeza;
        while(actual!= null){
            sb.append(actual.getDato());
          if (actual.getSiguiente() != null) {
                sb.append(", ");
            }
            actual = actual.getSiguiente();
        }
        sb.append("]");
        return sb.toString();
    }
            
        }
    
    
    
    

