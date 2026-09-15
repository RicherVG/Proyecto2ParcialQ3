/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andre
 */
public class DocumentoTexto implements Serializable {
    private static final long serialVersionUID = 1L;
    private List <Fragmento> fragmentos;
    
    
    public DocumentoTexto(){
        this.fragmentos = new ArrayList<>();
    }
    
    public void agregarFragmento(Fragmento f){
        fragmentos.add(f);
    }
    
    public List<Fragmento> getFragmentos(){
        return fragmentos;
    }
    
}
