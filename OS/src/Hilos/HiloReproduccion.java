/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Hilos;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.SwingUtilities;

/**
 *
 * @author andre
 */
public class HiloReproduccion extends Thread {
    private final File archivo;
    private final Runnable alTerminar;
    private PlayerConAcceso player;
    private volatile boolean pausado;
    private volatile boolean detener;
    private volatile int frameObjetivo = -1;
    private volatile int framesDecodificados;
    private volatile boolean buscando;
    
    public HiloReproduccion(File archivo, Runnable alTerminar){
        this.archivo = archivo;
        this.alTerminar = alTerminar;
    }
    
    
    public void pausar(){
        pausado = true;
    }
    
    public void reanudar(){
        pausado = false;
    }
    
    public boolean estaPausado(){
        return pausado;
    }
    
    public void detener(){
        detener = true;
        pausado = false;
        buscando = false;
        interrupt();
    }
    
    public void saltarA(int frame){
        frameObjetivo = frame;
        buscando = true;
    }
    
    public int posicionActual(){
        return framesDecodificados;
    }
    
    public boolean estaBuscando(){
        return buscando;
    }
    
    public void run(){
        try{
        player = new PlayerConAcceso(new BufferedInputStream(new FileInputStream(archivo)));
        while(!detener){
            if(pausado){
                try{
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    break;
                }
              continue;  
            }
            if (frameObjetivo >= 0){
                while (framesDecodificados < frameObjetivo && player.saltarUnFrame()){
                    framesDecodificados++;
                }
                frameObjetivo = -1;
                buscando = false;
            }
            if(!player.decodeFrame()){
                break;
            }
            framesDecodificados++;
        }
    }catch(Exception e){
        
    } finally{
        if (player != null){
            player.close();
        }
        if(alTerminar!= null){
            SwingUtilities.invokeLater(alTerminar);
        }
    }
    }
    
    
}
