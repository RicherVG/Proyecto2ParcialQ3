/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;

import java.io.File;

/**
 *
 * @author andre
 */
public class NodoCarpeta {
    
    
    public final File carpeta;
    
    public NodoCarpeta(File carpeta){
        this.carpeta = carpeta;
    }
    
    public File getCarpeta(){
        return carpeta;
        
    }
    
    public String toString(){
        return carpeta.getName();
    }
    
    
}
