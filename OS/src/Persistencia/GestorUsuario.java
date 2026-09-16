/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistencia;

import Excepciones.ArchivosCorruptoException;
import Excepciones.CuentaDesactivadaException;
import Excepciones.UsernameDuplicadoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuarios;

/**
 *
 * @author user
 */
public class GestorUsuario {
    
    
    private static final String RUTA_RAIZ = "Z";
    private static final String ARCHIVO_USUARIOS = RUTA_RAIZ +File.separator + "Usuarios.sop";
    
    private List<Usuarios> usuarios;
    
    public GestorUsuario(){
        this.usuarios = new ArrayList<>();
        inicializarSistema();
    }
    
    private void inicializarSistema(){
        File raiz = new File (RUTA_RAIZ);
        if (!raiz.exists()){
            raiz.mkdir();
        }
        
        File archivo = new File (ARCHIVO_USUARIOS);
        if (!archivo.exists()){
            Usuarios admin = new Usuarios("Administardo", "ADMIN2", "ADMIN123", 99, 'M' , true);
            usuarios.add(admin);
            crearCarpetasUsuario(admin.getUsername());
            guardarUsuarios();
            
        }else{
            try{
               cargarUsuarios(); 
            } catch (ArchivosCorruptoException e){
                System.out.println(e.getMessage());
                usuarios = new ArrayList<>();
            }
        }
        
    }
    
    
    private void crearCarpetasUsuario(String username){
        File carpetaUsuario = new File (RUTA_RAIZ + File.separator + username);
        carpetaUsuario.mkdir();
        new File (carpetaUsuario, "Documentos").mkdir();
        new File (carpetaUsuario, "Musica").mkdir();
        new File (carpetaUsuario, "Imagenes").mkdir();
    }
    
    
    private void guardarUsuarios(){
        try(ObjectOutputStream oos = new ObjectOutputStream(
             new FileOutputStream (ARCHIVO_USUARIOS))){
            oos.writeObject(usuarios);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void cargarUsuarios() throws ArchivosCorruptoException{
       try (ObjectInputStream ois = new ObjectInputStream(
       new FileInputStream (ARCHIVO_USUARIOS))) {
           usuarios = (List<Usuarios>) ois.readObject();
       }catch (IOException | ClassNotFoundException e){
           throw new ArchivosCorruptoException(ARCHIVO_USUARIOS,e);
       }
    }
    
    public void registrarUsuario(Usuarios nuevo) throws UsernameDuplicadoException{
        for (Usuarios u : usuarios){
            if (u.getUsername().equalsIgnoreCase(nuevo.getUsername())){
                throw new UsernameDuplicadoException(nuevo.getUsername());
            }
        }
        usuarios.add(nuevo);
        crearCarpetasUsuario(nuevo.getUsername());
        guardarUsuarios();
    }
    
    
    public Usuarios autenticar (String username, String contrasena) throws CuentaDesactivadaException{
       for (Usuarios u : usuarios){
           if (u.getUsername().equals(username)){
               if (!u.isActivo()){
                   throw new CuentaDesactivadaException(username);
               }
               if (u.getPassword().equals(contrasena)){
                   return u;
               }
               return null;
           }
       } 
       return null;
    }
    
    public List<Usuarios> getUsuarios(){
        return usuarios;
    }
    
    
    public void eliminarUsuario(Usuarios usuario){
        usuarios.remove(usuario);
        guardarUsuarios();
        File carpeta = new File(RUTA_RAIZ + File.separator + usuario.getUsername());
        borrar(carpeta);
    }
    
    
    private void borrar(File f){
        if (f.isDirectory()){
            File[] hijos = f.listFiles();
            if(hijos != null ){
                for(File h : hijos){
                    borrar(h);
                }
            }
        }
        
        f.delete();
    }
    
    
    
}
