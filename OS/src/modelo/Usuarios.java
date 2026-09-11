/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author user
 */
public class Usuarios implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String nombreCompleto,username,password;
    private int edad;
    private char genero;
    private LocalDate fechaRegistro;
    private boolean activo,esAdmin;
    
    public Usuarios(String nombreCompleto, String username, String password, int edad, char genero, boolean esAdmin){
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.password = password;
        this.edad = edad;
        this.genero = genero;
        this.fechaRegistro = LocalDate.now();
        this.activo = true;
        this.esAdmin= esAdmin;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getEdad() {
        return edad;
    }

    public char getGenero() {
        return genero;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setGenero(char genero) {
        this.genero = genero;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public String toString(){
        return username + " (" + nombreCompleto + ")";
    }
    
}
