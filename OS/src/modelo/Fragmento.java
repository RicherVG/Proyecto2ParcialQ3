/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author andre
 */
public class Fragmento implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String texto,fuente;
    private int tamano,colorRGB;
    
    
    public Fragmento(String texto, String fuente, int tamano, int colorRGB){
        this.texto = texto;
        this.fuente = fuente;
        this.tamano = tamano;
        this.colorRGB = colorRGB;
    }

    public String getTexto() {
        return texto;
    }

    public String getFuente() {
        return fuente;
    }

    public int getTamano() {
        return tamano;
    }

    public int getColorRGB() {
        return colorRGB;
    }
    
    
    
    
    
}
