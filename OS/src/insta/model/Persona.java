/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;

import java.io.Serializable;

/**
 * 
 * 
 * @author riche
 */
public class Persona implements Serializable {
   private static final long serialVersionUID = 1L;
   protected String nombreCompleto;
   protected String genero;
   protected int edad;
   public Persona() {
   }

   public Persona(String nombreCompleto, String genero, int edad) {
      this.nombreCompleto = nombreCompleto;
      this.genero = genero;
      this.edad = edad;
   }
   public String getNombreCompleto() {
       return nombreCompleto; 
   }
   public void setNombreCompleto(String nombre) { 
       this.nombreCompleto = nombre; 
   }
   public String getGenero() {
       return genero; 
   }
   public void setGenero(String genero) {
       this.genero = genero;
   }
   public int getEdad() { 
       return edad; 
   }
   public void setEdad(int edad) { 
       this.edad = edad; 
   }
}
