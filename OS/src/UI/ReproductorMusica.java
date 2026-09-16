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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import modelo.Usuarios;

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
    private HiloReproduccion hilo;
    
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
        
        izquierdo.add(lblCaratula, BorderLayout.NORTH);
        izquierdo.add(lblDescripcion, BorderLayout.CENTER);
        
      
        modeloLista = new DefaultListModel<>();
        listaCanciones = new JList<>(modeloLista);
        listaCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCanciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaCanciones.addListSelectionListener(this::onSeleccionCancion);
        JScrollPane scrollLista = new JScrollPane(listaCanciones);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Lista de canciones"));
        
        JPanel centro = new JPanel(new BorderLayout(8, 0));
        centro.add(izquierdo, BorderLayout.WEST);
        centro.add(scrollLista, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        
   
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnPlay = new JButton("▶ Play");
        btnPlay.addActionListener(e -> reproducirSeleccion());
        btnPausa = new JButton("⏸ Pausar");
        btnPausa.setEnabled(false);
        btnPausa.addActionListener(e -> pausar());
        btnStop = new JButton("⏹ Stop");
        btnStop.setEnabled(false);
        btnStop.addActionListener(e -> detener());
        JButton btnCargar = new JButton("Cargar canción");
        btnCargar.addActionListener(e -> cargarCancion());
        controles.add(btnPlay);
        controles.add(btnPausa);
        controles.add(btnStop);
        controles.add(btnCargar);
        add(controles, BorderLayout.SOUTH);
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
        actualizarCaratula();
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
        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION){
            File archivo = selector.getSelectedFile();
            if (archivo.getName().toLowerCase().endsWith(".mp3")){
                agregarCancion(archivo);
            } else {
                JOptionPane.showMessageDialog(this, "Solo se admiten archivos .mp3");
            }
        }
    }
    
    private void onSeleccionCancion(ListSelectionEvent evt){
        if (evt.getValueIsAdjusting()) return;
        int indice = listaCanciones.getSelectedIndex();
        if (indice >= 0 && indice < canciones.size()){
            actualizarDescripcion(canciones.get(indice));
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
    
    private void reproducir(File archivo){
        detener();
        hilo = new HiloReproduccion(archivo);
        hilo.start();
        btnPlay.setEnabled(false);
        btnPausa.setEnabled(true);
        btnStop.setEnabled(true);
        btnPausa.setText("⏸ Pausar");
    }
    
    private void pausar(){
        if (hilo == null) return;
        if (hilo.pausado){
            hilo.pausado = false;
            btnPausa.setText("⏸ Pausar");
        } else {
            hilo.pausado = true;
            btnPausa.setText("▶ Reanudar");
        }
    }
    
    private void detener(){
        if (hilo != null){
            hilo.detener = true;
            hilo.pausado = false;
            hilo.interrupt();
            hilo = null;
        }
        btnPlay.setEnabled(true);
        btnPausa.setEnabled(false);
        btnStop.setEnabled(false);
        btnPausa.setText("⏸ Pausar");
    }
    
    private void actualizarCaratula(){
        ImageIcon caratula = buscarCaratula();
        if (caratula != null){
            lblCaratula.setIcon(caratula);
            lblCaratula.setText("");
        } else {
            lblCaratula.setIcon(null);
            lblCaratula.setText("Sin carátula");
        }
    }
    
    private ImageIcon buscarCaratula(){
        File[] archivos = carpetaMusica.listFiles();
        if (archivos == null) return null;
        
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
        Image img = new ImageIcon(archivo.getAbsolutePath()).getImage();
        int ancho = 190;
        int alto = 190;
        double escala = Math.min((double) ancho / img.getWidth(null),
                (double) alto / img.getHeight(null));
        int w = Math.max(1, (int) (img.getWidth(null) * escala));
        int h = Math.max(1, (int) (img.getHeight(null) * escala));
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
    

    private class PlayerConAcceso extends AdvancedPlayer {
        PlayerConAcceso(java.io.InputStream stream) throws JavaLayerException {
            super(stream);
        }
        
        @Override
        public boolean decodeFrame() throws JavaLayerException {
            return super.decodeFrame();
        }
    }
    
 
    private class HiloReproduccion extends Thread {
        private final File archivo;
        private PlayerConAcceso player;
        private volatile boolean pausado;
        private volatile boolean detener;
        
        HiloReproduccion(File archivo){
            this.archivo = archivo;
        }
        
        @Override
        public void run(){
            try {
                player = new PlayerConAcceso(new BufferedInputStream(
                        new FileInputStream(archivo)));
                while (!detener){
                    if (pausado){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex){
                            break;
                        }
                        continue;
                    }
                    if (!player.decodeFrame()){
                        break;
                    }
                }
            } catch (Exception ex){
                
            } finally {
                if (player != null){
                    player.close();
                }
                SwingUtilities.invokeLater(() -> {
                    if (hilo == HiloReproduccion.this){
                        hilo = null;
                        btnPlay.setEnabled(true);
                        btnPausa.setEnabled(false);
                        btnStop.setEnabled(false);
                        btnPausa.setText("⏸ Pausar");
                    }
                });
            }
        }
    }
}
