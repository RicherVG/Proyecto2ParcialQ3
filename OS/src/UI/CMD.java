/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import modelo.Usuarios;

/**
 *
 * @author andre
 */
public class CMD extends JInternalFrame {
    private JTextArea area;
    private int inicioEntrada = 0;
    private File carpetaActual;
    private final File carpetaRaiz;
    
    
   public CMD(Usuarios usuario){
       super("CMD",true,true,true,true);
       this.carpetaRaiz = new File("Z" + File.separator + usuario.getUsername());
       this.carpetaActual = carpetaRaiz;
       setSize(700,450);
       construirVentana();
   }
    
    
    
    private void construirVentana(){
        setLayout(new BorderLayout());
        
        area = new JTextArea();
        area.setEditable(true);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font("Consolas", Font.PLAIN,14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(area);
        add(scroll,BorderLayout.CENTER);
        
        appendText("Mini-windows [Version 1.0.B]\n");
        appendText("(c) Andres y Richer corpo. Todos los derechos no reservados.\n" );
        
        escribirPrompt();
        
        area.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                int caretPos = area.getCaretPosition();
                
                if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_HOME) && caretPos <= inicioEntrada){
                    e.consume();
                    area.setCaretPosition(inicioEntrada);
                    return;
                }
                
                if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) && caretPos <= inicioEntrada){
                    e.consume();
                    return;
                }
                
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    e.consume();
                    try{
                        int len = area.getDocument().getLength();
                        String comando = area.getText(inicioEntrada, len - inicioEntrada).trim();
                        appendText("\n");
                        procesar(comando);
                    } catch(BadLocationException ex){
                        appendText("\nError: " + ex.getMessage() + "\n");
                    }
                    escribirPrompt();
                }
                
                
                
                
            }
        });
        
        
    }
    
    private void appendText(String s){
        area.append(s);
        area.setCaretPosition(area.getDocument().getLength());
    }
    
    
    private void escribirPrompt(){
        appendText(rutaMostrable(carpetaActual) + ">");
        inicioEntrada = area.getDocument().getLength();
    }
    
    private String rutaMostrable(File carpeta){
        String raiz = carpetaRaiz.getAbsolutePath();
        String actual = carpeta.getAbsolutePath();
        if(actual.equals(raiz))
            return "Z:\\";
        
        if (actual.startsWith(raiz))
            return "Z:" +actual.substring(raiz.length()).replace(File.separatorChar, '\\');
        return actual;
        
    }
    
    private void procesar(String raw){
        if (raw == null || raw.isEmpty())
            return;
        String[] partes = raw.split("\\s+");
        String cmd = partes[0].toLowerCase();
        
        
        switch(cmd){
            case "help":
            appendText("MKDIR <nombre>     Crea una nueva carpeta\n");
            appendText("RM <nombre>        Elimina una carpeta o archivo\n");
            appendText("CD <carpeta        Cambia a la carpeta indicada\n");
            appendText("CD..               Regresa a la carpeta anterior\n");
            appendText("DIR                Lista las carpetas y archivos de la carpeta actual\n");
            appendText("DATE               Muestra la fecha actual\n");
            appendText("TIME               Muestra la hora actual\n");
            appendText("CLS                Limpia la pantalla\n");
            appendText("EXIT               Cierra la consola\n");
            appendText("COPY NUL <nombre   Crea un archivo vacío\n");
            break;
            
            case "mkdir":
                if(partes.length < 2 ){
                    appendText("Sintaxis: mkdir <nombre>\n");
                    return;
                    
                }
                String nombreMk = raw.substring(raw.indexOf(" ") + 1).trim();
                File nueva = new File(carpetaActual, nombreMk);
                if(nueva.exists()){
                    appendText("Ya existe un archivo o carpeta con ese nombre en concreto.\n");
                    
                }
                else if(nueva.mkdir()){
                    appendText("Carpeta creada: " +nombreMk + "\n");
                }
                else{
                    appendText("No se pudo crear la carpeta. \n");
                }
                break;
                
                
            case "rm":
                if (partes.length < 2){
                    appendText("Sintaxis: rm<nombre>\n");
                    return;
                }
                String nombreRm = raw.substring(raw.indexOf(" ") + 1).trim();
                File eliminar = new File(carpetaActual, nombreRm);
                if(!eliminar.exists()){
                    appendText("No se encontró: " +nombreRm + "\n");
                }
                else if (eliminar.isDirectory()){
                    if (eliminar(eliminar)){
                        appendText("Carpeta eliminada: " +nombreRm + "\n");
                    }
                    else{
                        appendText("No se pudo eliminar la carpeta.\n");
                    }
                }else{
                    if (eliminar.delete()){
                        appendText("Archivo eliminado: " +nombreRm + "\n");
                    }
                    else{
                        appendText("No se pudo eliminar el archivo. \n");
                    }
                }
                break;
            
                case "cd":
                if (partes.length < 2){
                    appendText(rutaMostrable(carpetaActual) + "\n");
                    return;
                }
                String ruta = raw.substring(raw.indexOf(" ")+ 1).trim();
                File destino;
                if (ruta.equals("..")){
                    destino = carpetaActual.getParentFile();
                }
                else{
                    destino = new File(carpetaActual, ruta);
                }
                cambiarCarpeta(destino);
                break;
                
                case"cd..":
                    cambiarCarpeta(carpetaActual.getParentFile());
                    break;
                
                
                
                case "dir":
                File[] contenido = carpetaActual.listFiles();
                if (contenido == null || contenido.length == 0) {
                    appendText("No hay archivos ni carpetas.\n");
                    return;
                }
                for (File f : contenido) {
                    if (f.isDirectory()) {
                        appendText("<DIR>    " + f.getName() + "\n");
                    } else {
                        appendText("         " + f.getName() + "\n");
                    }
                }
                break;
                
            case "date":
                appendText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
                break;
                
            case "time":
                appendText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                break;
                
            case "cls":
                area.setText("");
                appendText("Microsoft Windows [Versión 10.0.22621.521]\n");
                appendText("(c) Microsoft Corporation. Todos los derechos reservados.\n");
                appendText("Escriba 'help' para ver los comandos disponibles.\n");
                break;
                
            case "exit":
                dispose();
                break;
                
            case "copy":
                if (partes.length < 3 || !partes[1].equalsIgnoreCase("nul")){
                    appendText("Sintaxis: copy nul <nombre>\n");
                    return;
                }
                String nombreCopy = raw.substring(raw.indexOf(partes[2])).trim();
                File nuevoCopy = new File(carpetaActual,nombreCopy);
                
                try{
                    if (nuevoCopy.createNewFile()){
                        appendText("Archivo creado: " + nombreCopy + "\n");
                    }
                    else{
                        appendText("Ya existe un archivo o carpeta con ese nombre.\n");
                    }
                } catch(Exception e){
                    appendText("No se pudo crear el archivo: " + e.getMessage() + "\n");

                }
                
                break;
                
            default:
                appendText("'" + cmd + "' no se reconoce como un comando interno o externo.\n");
                
                
                
        }
        
    }
    
    
    
    private boolean eliminar(File carpeta){
        File[] contenido = carpeta.listFiles();
        if(contenido != null){
            for (File f : contenido){
                if (f.isDirectory()){
                    eliminar(f);
                }
                else{
                    f.delete();
                }
            }
        }
        return carpeta.delete();
    }
    
    private void cambiarCarpeta(File destino){
        if (destino == null || !destino.isDirectory()){
            appendText("El sistema no puede encontrar la ruta especificada.\n");
            return;
        }
        
        String raiz = carpetaRaiz.getAbsolutePath();
        String destinoAbs = destino.getAbsolutePath();
        
        boolean dentroDeRaiz = destinoAbs.equals(raiz) || destinoAbs.startsWith(raiz + File.separator);
        
        if (!dentroDeRaiz){
            appendText("Ya estas en la raiz Z:\\, no puedes salir de ella. \n");
            return;
        }
        carpetaActual = destino;
        appendText(rutaMostrable(carpetaActual) + "\n");
        
        
        
    }
    
    
}
