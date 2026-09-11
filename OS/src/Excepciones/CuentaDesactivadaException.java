/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Excepciones;

/**
 *
 * @author user
 */
public class CuentaDesactivadaException extends Exception{
    
    public CuentaDesactivadaException(String username){
        super("Cuenta: " + username + " esta desactivada. Contactar con el administrador");
    }
    
}
