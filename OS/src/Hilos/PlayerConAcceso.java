/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Hilos;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

/**
 *
 * @author andre
 */
public class PlayerConAcceso extends AdvancedPlayer {
    public PlayerConAcceso(java.io.InputStream stream) throws JavaLayerException{
        super(stream);
    }
    
    
    public boolean decodeFrame() throws JavaLayerException{
        return super.decodeFrame();
    }
    
    public boolean saltarUnFrame() throws JavaLayerException{
    return super.skipFrame();
}
    
    
    
}
