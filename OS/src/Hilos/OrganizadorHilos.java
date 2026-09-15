/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Hilos;

import Estructuras.ListaEnlazada;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author andre
 */
public class OrganizadorHilos extends Thread {
    private static final List <String> EXT_IMAGENES = Arrays.asList("jpg","jpeg","png","bmp","gif");
    private static final List <String> EXT_MUSICA = Arrays.asList("mp3" , "wav");
    
    private File carpeta;
    private Runnable alTerminar;
    
    public OrganizadorHilos(File carpeta, Runnable alTerminar){
        this.carpeta = carpeta;
        this.alTerminar = alTerminar;
    }
    
    public void run(){
        ListaEnlazada<File> imagenes = new ListaEnlazada<>();
        ListaEnlazada<File> musica = new ListaEnlazada<>();
        ListaEnlazada<File> documentos = new ListaEnlazada<>();
        
        File[] contenido = carpeta.listFiles(File::isFile);
        if(contenido != null){
            for(File archivo : contenido){
                String ext = obtenerExtension(archivo);
                
                if (EXT_IMAGENES.contains(ext)){
                    imagenes.agregar(archivo);
                } 
                else if (EXT_MUSICA.contains(ext)) {
                    musica.agregar(archivo);
                }
                else{
                    documentos.agregar(archivo);
                }
                
            }
        }
        
        
         moverLista(imagenes, new File(carpeta, "Imagenes"));
        moverLista(musica, new File(carpeta, "Musica"));
        moverLista(documentos, new File(carpeta, "Documentos"));

        if (alTerminar != null) {
            SwingUtilities.invokeLater(alTerminar);
        } 
        
        
    }
    
    private void moverLista(ListaEnlazada<File> lista, File subcarpetaDestino){
        if (lista.estaVacia())
            return;
        if (!subcarpetaDestino.exists())
            subcarpetaDestino.mkdir();
        
        for (File archivo : lista){
            File destino = new File (subcarpetaDestino, archivo.getName());
            try{
                Files.move(archivo.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (Exception e){
             System.err.println("No se pudo mover " + archivo.getName() + ": " + e.getMessage());
            }
        }
        
        
    }
    
    
    
    
    
    
    private String obtenerExtension(File archivo){
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');
        return (punto == -1) ? "" : nombre.substring(punto + 1).toLowerCase();
    }
    
    
    
    
    
    
}
