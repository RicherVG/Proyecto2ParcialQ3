/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Excepciones;

/**
 *
 * @author user
 */
public class UsernameDuplicadoException extends Exception {
    
    public UsernameDuplicadoException (String username){
        super("Nombre de usuario: " +username +" ya existe. Porfavor elegir otro nombre");
    }
}
