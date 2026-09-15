/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import modelo.Usuarios;

/**
 *
 * @author andre
 */
public class Galeria extends JInternalFrame {
    private final Usuarios usuarioActual;
    private File carpetaImagenes;
    private List<File> imagenes;
    private int indice;
    
    private JLabel lblImagen,lblNombre, lblContador;
    
    public Galeria(Usuarios usuario){
        super("Galería de imágenes",true,true,true,true);
        this.usuarioActual = usuario;
        this.imagenes = new ArrayList<>();
        this.carpetaImagenes = new File("Z" + File.separator + usuario.getUsername() + File.separator + "Imagenes");
        setSize(640,520);
        construirVentana();
        cargarCarpeta(carpetaImagenes);
    }
    
    
    private void construirVentana(){
        setLayout(new BorderLayout());
        
        lblImagen = new JLabel("Cargando imágenes..." , SwingConstants.CENTER);
        lblImagen.setOpaque(true);
        lblImagen.setBackground(new Color(30,30,35));
        lblImagen.setForeground(Color.WHITE);
        lblImagen.setFont(new Font("Segoe UI", Font.PLAIN,14));
        lblImagen.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(lblImagen,BorderLayout.CENTER);
        
        JPanel norte = new JPanel (new FlowLayout(FlowLayout.LEFT,8,6));
        JButton btnCarpeta = new JButton("Elegir carpeta");
        btnCarpeta.addActionListener( e -> elegirCarpeta());
        norte.add(btnCarpeta);
        add(norte,BorderLayout.NORTH);
        
        JPanel sur = new JPanel(new BorderLayout());
        
        JPanel botones = new JPanel (new FlowLayout(FlowLayout.CENTER,16,8));
        JButton btnAnterior = new JButton("◀ Anterior");
        btnAnterior.addActionListener(e -> mover(-1));
        JButton btnSiguiente = new JButton("Siguiente  ▶");
        btnSiguiente.addActionListener(e -> mover(1));
        botones.add(btnAnterior);
        botones.add(btnSiguiente);
        
        lblContador = new JLabel ("0 de 0", SwingConstants.CENTER);
        lblNombre = new JLabel ("", SwingConstants.CENTER);
        lblNombre.setPreferredSize(new Dimension(0,22));
        
        sur.add(botones, BorderLayout.CENTER);
        sur.add(lblContador, BorderLayout.NORTH);
        sur.add(lblNombre, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);
    }
    
    private void elegirCarpeta(){
        JFileChooser selector = new JFileChooser(carpetaImagenes);
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int resultado = selector.showOpenDialog(this);
        if(resultado == JFileChooser.APPROVE_OPTION){
            carpetaImagenes = selector.getSelectedFile();
            cargarCarpeta(carpetaImagenes);
        }
    }
    
    
    private void cargarCarpeta(File carpeta){
        lblImagen.setIcon(null);
        lblImagen.setText("Cargando imagenes....");
        
        new SwingWorker<List<File>, Void>() {
            @Override
            protected List<File> doInBackground(){
                List<File> encontradas = new ArrayList<>();
                File[] hijos = carpeta.listFiles();
                if (hijos != null){
                    for (File f : hijos){
                        if (f.isFile() && esImagen(f)){
                            encontradas.add(f);
                        }
                    }
                }
                return encontradas;
            }
        
        protected void done(){
            try{
                imagenes = get();
                indice = 0;
                if(imagenes.isEmpty()){
                    lblImagen.setIcon(null);
                    lblImagen.setText("No hay imágenes en esta carpeta");
                    lblContador.setText("0 de 0");
                    lblNombre.setText("");
                }
                else{
                    mostrarImagen();
                }
            } catch(Exception e){
                lblImagen.setText("Error al cargar la carpeta");
            }
        }
    }.execute();
    
    
    
    
    
}
    
    private void mover(int direccion){
        if (imagenes.isEmpty())
            return;
        indice = (indice + direccion + imagenes.size()) % imagenes.size();
        mostrarImagen();
    }
    
    private void mostrarImagen(){
        File archivo = imagenes.get(indice);
        lblNombre.setText(archivo.getName());
        lblContador.setText((indice + 1) + " de " + imagenes.size());
        lblImagen.setIcon(null);
        lblImagen.setText("Cargando...");
        
        new SwingWorker<ImageIcon, Void>(){
           protected ImageIcon doInBackground(){
               ImageIcon icono = new ImageIcon(archivo.getAbsolutePath());
               Image img = icono.getImage();
               int anchoImg = icono.getIconWidth();
               int altoImg = icono.getIconHeight();
               if(anchoImg <= 0 || altoImg <= 0)
                   return null;
               
               int anchoMax = Math.max(lblImagen.getWidth(), 200);
               int altoMax = Math.max(lblImagen.getHeight(), 200);
               double escala = Math.min(1.0, Math.min((double) anchoMax / anchoImg, (double ) altoMax/altoImg));
               int ancho = Math.max(1,((int) (anchoImg * escala)));
               int alto = Math.max(1, (int) (altoImg * escala));
               
               return new ImageIcon(img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
           }
           
         protected void done(){
    try{
        ImageIcon icono = get();
        if (icono == null){
            lblImagen.setIcon(null);
            lblImagen.setText("No se pudo cargar: " + archivo.getName());
            return;
        }
        lblImagen.setIcon(icono);
        lblImagen.setText("");
    } catch(Exception e){
        lblImagen.setIcon(null);
        lblImagen.setText("No se pudo cargar: " + archivo.getName());
    }
}
           
           
           
           
        }.execute();
        
        
    }
    
    
    private boolean esImagen(File archivo){
        String nombre = archivo.getName().toLowerCase();
         return nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || nombre.endsWith(".png") || nombre.endsWith(".gif")|| nombre.endsWith(".bmp");
                
    }
    
    
    
    
    
}