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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import modelo.Usuarios;
import Hilos.HiloReproduccion;
/**
 *
 * @author andre
 */
public class ReproductorMusica extends JInternalFrame {
    
    private final Usuarios usuarioActual;
    private File carpetaMusica;
    private final List<File> canciones;
    private DefaultListModel<String> modeloLista;
    private JList<String> listaCanciones;
    private JLabel lblCaratula;
    private JLabel lblDescripcion;
    private JButton btnPlay;
    private JButton btnPausa;
    private JButton btnStop;
    private JButton btnAnadirImagen;
    private JSlider sliderTiempo;
    private JLabel lblTiempo;
    private javax.swing.Timer timerTiempo;
    private int duracionSegundos;
    private double framesPorSegundo = 38.28;
    private boolean actualizandoDesdeTimer;
    private HiloReproduccion hilo;
    private int generacion;
    private File archivoActual;
    private JButton btnAnterior;
  private JButton btnSiguiente;
    
    public ReproductorMusica(Usuarios usuario){
        super("Reproductor de música", true, true, true, true);
        this.usuarioActual = usuario;
        this.canciones = new ArrayList<>();
        this.carpetaMusica = new File("Z" + File.separator + usuario.getUsername()
                + File.separator + "Musica");
        setSize(580, 500);
        construirVentana();
        cargarCanciones(carpetaMusica);
    }
    
    public ReproductorMusica(Usuarios usuario, File archivoInicial){
        this(usuario);
        if (archivoInicial != null && archivoInicial.isFile()
                && archivoInicial.getName().toLowerCase().endsWith(".mp3")){
            agregarCancion(archivoInicial);
        }
    }
    
    private void construirVentana(){
        setLayout(new BorderLayout());
        
    
        JPanel izquierdo = new JPanel(new BorderLayout(0, 8));
        izquierdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblCaratula = new JLabel("Sin carátula", SwingConstants.CENTER);
        lblCaratula.setPreferredSize(new Dimension(200, 200));
        lblCaratula.setOpaque(true);
        lblCaratula.setBackground(new Color(45, 45, 50));
        lblCaratula.setForeground(Color.WHITE);
        lblCaratula.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCaratula.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 130)));
        
        lblDescripcion = new JLabel("<html>Selecciona una canción</html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        btnAnadirImagen = new JButton("🖼 Añadir imagen");
        btnAnadirImagen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAnadirImagen.addActionListener(e -> anadirImagen());
        
        izquierdo.add(lblCaratula, BorderLayout.NORTH);
        izquierdo.add(lblDescripcion, BorderLayout.CENTER);
        izquierdo.add(btnAnadirImagen, BorderLayout.SOUTH);
        
      
        modeloLista = new DefaultListModel<>();
        listaCanciones = new JList<>(modeloLista);
        listaCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCanciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaCanciones.addListSelectionListener(this::onSeleccionCancion);
        listaCanciones.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e){
                if (e.getClickCount() == 2){
                    reproducirSeleccion();
                }
            }
        });
        JScrollPane scrollLista = new JScrollPane(listaCanciones);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Lista de canciones"));
        
        JPanel centro = new JPanel(new BorderLayout(8, 0));
        centro.add(izquierdo, BorderLayout.WEST);
        centro.add(scrollLista, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        
   
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnAnterior = new JButton("⏮ Anterior");
        btnAnterior.addActionListener(e -> reproducirAnterior());
        btnPlay = new JButton("▶ Play");
        btnPlay.addActionListener(e -> reproducirSeleccion());
        btnPausa = new JButton("⏸ Pausar");
        btnPausa.setEnabled(false);
        btnPausa.addActionListener(e -> pausar());
        btnStop = new JButton("⏹ Stop");
        btnStop.setEnabled(false);
        btnStop.addActionListener(e -> detener());
        btnSiguiente = new JButton("⏭ Siguiente");
        btnSiguiente.addActionListener(e -> reproducirSiguiente());
        JButton btnCargar = new JButton("Cargar canción");
        btnCargar.addActionListener(e -> cargarCancion());
        controles.add(btnAnterior);
        controles.add(btnPlay);
        controles.add(btnPausa);
        controles.add(btnStop);
        controles.add(btnSiguiente);
        controles.add(btnCargar);
        
        JPanel barraTiempo = new JPanel(new BorderLayout(8, 0));
        barraTiempo.setBorder(BorderFactory.createEmptyBorder(4, 10, 0, 10));
        sliderTiempo = new JSlider(0, 1000, 0);
        sliderTiempo.setEnabled(false);
        sliderTiempo.addChangeListener(e -> onCambioSlider());
        lblTiempo = new JLabel("0:00 / 0:00", SwingConstants.CENTER);
        lblTiempo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        barraTiempo.add(sliderTiempo, BorderLayout.CENTER);
        barraTiempo.add(lblTiempo, BorderLayout.EAST);
        
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(barraTiempo, BorderLayout.NORTH);
        sur.add(controles, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);
        
        timerTiempo = new javax.swing.Timer(250, e -> actualizarSlider());
        timerTiempo.start();
        
        addInternalFrameListener(new javax.swing.event.InternalFrameAdapter(){
            @Override
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent e){
                detener();
            }
        });
    }
    
    private void cargarCanciones(File carpeta){
        canciones.clear();
        modeloLista.clear();
        File[] hijos = carpeta.listFiles();
        if (hijos != null){
            for (File f : hijos){
                if (f.isFile() && f.getName().toLowerCase().endsWith(".mp3")){
                    canciones.add(f);
                    modeloLista.addElement(f.getName());
                }
            }
        }
        actualizarCaratula(null);
    }
    
    private void agregarCancion(File archivo){
        if (!canciones.contains(archivo)){
            canciones.add(archivo);
            modeloLista.addElement(archivo.getName());
        }
        int indice = canciones.indexOf(archivo);
        listaCanciones.setSelectedIndex(indice);
        listaCanciones.ensureIndexIsVisible(indice);
    }
    
    private void cargarCancion(){
        JFileChooser selector = new JFileChooser(carpetaMusica);
        selector.setFileFilter(new FileNameExtensionFilter("Archivos MP3", "mp3"));
        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION){
            File archivo = selector.getSelectedFile();
            if (!archivo.getName().toLowerCase().endsWith(".mp3")){
                JOptionPane.showMessageDialog(this, "Solo se admiten archivos .mp3");
                return;
            }
            File destino = archivo;
            try{
                if (!archivo.getParentFile().equals(carpetaMusica)){
                    if (!carpetaMusica.exists()){
                        carpetaMusica.mkdirs();
                    }
                    destino = new File(carpetaMusica, archivo.getName());
                    Files.copy(archivo.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e){
                JOptionPane.showMessageDialog(this, "No se pudo guardar la canción: " + e.getMessage());
                return;
            }
            agregarCancion(destino);
        }
    }
    
    private void anadirImagen(){
        int indice = listaCanciones.getSelectedIndex();
        if (indice < 0 || indice >= canciones.size()){
            JOptionPane.showMessageDialog(this, "Selecciona una canción primero");
            return;
        }
        File cancion = canciones.get(indice);
        JFileChooser selector = new JFileChooser(carpetaMusica);
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "gif", "bmp"));
        int resultado = selector.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION)
            return;
        File imagen = selector.getSelectedFile();
        String base = cancion.getName();
        int punto = base.lastIndexOf(".");
        if (punto != -1) base = base.substring(0, punto);
        String ext = imagen.getName();
        int pe = ext.lastIndexOf(".");
        if (pe != -1) ext = ext.substring(pe);
        File destino = new File(carpetaMusica, "cover_" + base + ext);
        try{
            if (!carpetaMusica.exists()){
                carpetaMusica.mkdirs();
            }
            Files.copy(imagen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            eliminarCaratulasViejas(base,destino);
            actualizarCaratula(cancion);
        } catch (IOException e){
            JOptionPane.showMessageDialog(this, "No se pudo guardar la imagen: " + e.getMessage());
        }
    }
    
    
    private void eliminarCaratulasViejas(String base, File nuevaCaratula){
    File[] archivos = carpetaMusica.listFiles();
    if (archivos == null) return;
    String baseMin = base.toLowerCase();
    for (File f : archivos){
        if (f.isFile() && esImagen(f) && !f.equals(nuevaCaratula)){
            String nombre = f.getName().toLowerCase();
            if (nombre.startsWith("cover_" + baseMin)
                    || nombre.startsWith(baseMin + ".")){
                f.delete();
            }
        }
    }
}
    
    
    
    private void onSeleccionCancion(ListSelectionEvent evt){
        if (evt.getValueIsAdjusting()) return;
        int indice = listaCanciones.getSelectedIndex();
        if (indice >= 0 && indice < canciones.size()){
            actualizarDescripcion(canciones.get(indice));
            actualizarCaratula(canciones.get(indice));
        }
    }
    
    private void reproducirSeleccion(){
        int indice = listaCanciones.getSelectedIndex();
        if (indice < 0 || indice >= canciones.size()){
            JOptionPane.showMessageDialog(this, "Selecciona una canción de la lista");
            return;
        }
        reproducir(canciones.get(indice));
    }
    
    private void reproducirAnterior(){
        int indice = listaCanciones.getSelectedIndex();
        if(indice <= 0)
            return;
        listaCanciones.setSelectedIndex(indice - 1);
        reproducirSeleccion();
    }
    
    private void reproducirSiguiente(){
    int indice = listaCanciones.getSelectedIndex();
    if (indice < 0 || indice >= canciones.size() - 1)
        return;
    listaCanciones.setSelectedIndex(indice + 1);
    reproducirSeleccion();
}
    
    
    
    private void reproducir(File archivo){
     archivoActual = archivo;
    reproducirDesde(archivo, 0);
    }
    
    private void reproducirDesde(File archivo, int frameInicial){
    detener();
    final int miGeneracion = ++generacion;
    double[] info = leerInfoMp3(archivo);
    if (info != null){
       framesPorSegundo = 1000.0 / info[0];
       duracionSegundos = (int) (info[1] / framesPorSegundo);
    } else {
        duracionSegundos = (int) (archivo.length() * 8 / 128000.0);
        framesPorSegundo = 38.28;
    }
    sliderTiempo.setValue(0);
    sliderTiempo.setEnabled(true);
    lblTiempo.setText("0:00 / " + formatearTiempo(duracionSegundos));
    actualizarCaratula(archivo);
    hilo = new HiloReproduccion(archivo,()-> {
       if (generacion == miGeneracion) {
           hilo = null;
           btnPlay.setEnabled(true);
           btnPausa.setEnabled(false);
           btnPausa.setText("⏸ Pausar");
           sliderTiempo.setValue(0);
           sliderTiempo.setEnabled(false);
           lblTiempo.setText("0:00 / 0:00");
       }
    });
    if (frameInicial > 0){
        hilo.saltarA(frameInicial);
    }
    hilo.start();
    btnPlay.setEnabled(false);
    btnPausa.setEnabled(true);
    btnStop.setEnabled(true);
    btnPausa.setText("⏸ Pausar");
    if (frameInicial > 0){
        int segInicial = (int) (frameInicial / framesPorSegundo);
        sliderTiempo.setValue(Math.min(1000, segInicial * 1000 / duracionSegundos));
        lblTiempo.setText(formatearTiempo(segInicial) + " / " + formatearTiempo(duracionSegundos));
    }
}
    
    
    
    private void pausar(){
        if (hilo == null)
            return;
        if (hilo.estaPausado()){
            hilo.reanudar();
            btnPausa.setText("⏸ Pausar");
        }else{
            hilo.pausar();
            btnPausa.setText("▶ Reanudar");
        }
    }
    
    private void detener(){
        if (hilo != null){
            hilo.detener();
            hilo = null;
        }
        btnPlay.setEnabled(true);
        btnPausa.setEnabled(false);
        btnStop.setEnabled(false);
        btnPausa.setText("⏸ Pausar");
        sliderTiempo.setValue(0);
        sliderTiempo.setEnabled(false);
        lblTiempo.setText("0:00 / 0:00");
        
    }
    
    private void onCambioSlider(){
       if (actualizandoDesdeTimer)
        return;
    if (hilo == null || duracionSegundos <= 0)
        return;
        int seg = sliderTiempo.getValue() * duracionSegundos / 1000;
         if (sliderTiempo.getValueIsAdjusting()){
             lblTiempo.setText(formatearTiempo(seg) + " / " + formatearTiempo(duracionSegundos));
        return;
         }
           int frameDestino = (int) (seg * framesPorSegundo);
    if (frameDestino < hilo.posicionActual()){
        reproducirDesde(archivoActual, frameDestino);
    }
    else{
        hilo.saltarA(frameDestino);
    }
    lblTiempo.setText(formatearTiempo(seg) + " / " + formatearTiempo(duracionSegundos));
    }
    
    private void actualizarSlider(){
        if (hilo == null || duracionSegundos <= 0)
            return;
        
        if(hilo.estaBuscando())
            return;
        
        
        if (sliderTiempo.getValueIsAdjusting())
            return; 
        int seg = (int) (hilo.posicionActual() / framesPorSegundo);
        actualizandoDesdeTimer = true;
        sliderTiempo.setValue(Math.min(1000, seg * 1000 / duracionSegundos));
        actualizandoDesdeTimer = false;
        lblTiempo.setText(formatearTiempo(seg) + " / " + formatearTiempo(duracionSegundos));
    }
    
   private double[] leerInfoMp3(File archivo){
    try (java.io.FileInputStream fis = new java.io.FileInputStream(archivo);
         java.io.BufferedInputStream bis = new java.io.BufferedInputStream(fis)){
        javazoom.jl.decoder.Bitstream bitstream = new javazoom.jl.decoder.Bitstream(bis);
        javazoom.jl.decoder.Header header = bitstream.readFrame();
        if (header == null) return null;
        float msPorFrame = header.ms_per_frame();
        int frames = 1;
        while (bitstream.readFrame() != null){
            frames++;
            bitstream.closeFrame();
        }
        bitstream.close();
        return new double[]{msPorFrame, frames};
    } catch (Exception e){
        return null;
    }
}
    
    private String formatearTiempo(int segundos){
        int min = segundos / 60;
        int seg = segundos % 60;
        return min + ":" + (seg < 10 ? "0" : "") + seg;
    }
    
    private void actualizarCaratula(File cancion){
        ImageIcon caratula = buscarCaratula(cancion);
        if (caratula != null){
            lblCaratula.setIcon(caratula);
            lblCaratula.setText("");
        } else {
            lblCaratula.setIcon(null);
            lblCaratula.setText("Sin carátula");
        }
    }
    
    private ImageIcon buscarCaratula(File cancion){
        File[] archivos = carpetaMusica.listFiles();
        if (archivos == null) return null;
        
        if (cancion != null){
            
            String base = cancion.getName();
            int punto = base.lastIndexOf(".");
            if (punto != -1) base = base.substring(0, punto);
            String baseMin = base.toLowerCase();
            for (File f : archivos){
                if (f.isFile() && esImagen(f)){
                    String nombre = f.getName().toLowerCase();
                    if (nombre.startsWith("cover_" + baseMin)
                            || nombre.startsWith(baseMin + ".")){
                        return cargarCaratula(f);
                    }
                }
            }
            return null; 
        }
        
    
        String[] preferidos = {"cover", "folder", "caratula", "portada", "album", "front"};
        for (String nombre : preferidos){
            for (File f : archivos){
                if (f.isFile() && esImagen(f)
                        && f.getName().toLowerCase().startsWith(nombre)){
                    return cargarCaratula(f);
                }
            }
        }
        for (File f : archivos){
            if (f.isFile() && esImagen(f)){
                return cargarCaratula(f);
            }
        }
        return null;
    }
    
    private ImageIcon cargarCaratula(File archivo){
        ImageIcon icono = new ImageIcon(archivo.getAbsolutePath());
        Image img = icono.getImage();
        int anchoImg = icono.getIconWidth();
        int altoImg = icono.getIconHeight();
        if (anchoImg <= 0 || altoImg <= 0) return null;
        int ancho = 190;
        int alto = 190;
        double escala = Math.min((double) ancho / anchoImg,
                (double) alto / altoImg);
        int w = Math.max(1, (int) (anchoImg * escala));
        int h = Math.max(1, (int) (altoImg * escala));
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
    
    private boolean esImagen(File archivo){
        String nombre = archivo.getName().toLowerCase();
        return nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")
                || nombre.endsWith(".png") || nombre.endsWith(".gif")
                || nombre.endsWith(".bmp");
    }
    
    private void actualizarDescripcion(File archivo){
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<b>").append(archivo.getName()).append("</b><br>");
        sb.append("Tamaño: ").append(formatearTamaño(archivo.length())).append("<br>");
        
        String[] id3 = leerId3v1(archivo);
        if (id3 != null){
            if (!id3[0].isEmpty()) sb.append("Título: ").append(id3[0]).append("<br>");
            if (!id3[1].isEmpty()) sb.append("Artista: ").append(id3[1]).append("<br>");
            if (!id3[2].isEmpty()) sb.append("Álbum: ").append(id3[2]).append("<br>");
            if (!id3[3].isEmpty()) sb.append("Año: ").append(id3[3]).append("<br>");
        } else {
            sb.append("Sin etiquetas ID3.<br>");
        }
        sb.append("</html>");
        lblDescripcion.setText(sb.toString());
    }
    
    private String[] leerId3v1(File archivo){
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")){
            long largo = raf.length();
            if (largo < 128) return null;
            raf.seek(largo - 128);
            byte[] tag = new byte[128];
            raf.readFully(tag);
            if (tag[0] != 'T' || tag[1] != 'A' || tag[2] != 'G') return null;
            String titulo = new String(tag, 3, 30, "ISO-8859-1").trim();
            String artista = new String(tag, 33, 30, "ISO-8859-1").trim();
            String album = new String(tag, 63, 30, "ISO-8859-1").trim();
            String anio = new String(tag, 93, 4, "ISO-8859-1").trim();
            return new String[]{titulo, artista, album, anio};
        } catch (Exception ex){
            return null;
        }
    }
    
    private String formatearTamaño(long bytes){
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
    

   
}