/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import Estructuras.NodoCarpeta;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import modelo.Usuarios;

/**
 *
 * @author andre
 */
public class ExploradorArchivos extends JInternalFrame {
    private Usuarios usuarioActual;
    private File carpetaRaiz, carpetaSeleccionada, archivoCopiado;
    private JTree arbolCarpetas;
    private DefaultTreeModel modeloArbol;
    private JTable tablaArchivos;
    private DefaultTableModel modeloTabla;
    private JLabel lblRutaActual;
    private JList<String> listaArchivos;
    private DefaultListModel<String>modeloLista;
    private java.util.function.Consumer<File> alAbrirMusica;
   
    
    
    
    public ExploradorArchivos(Usuarios usuario){
        super("Explorador de archivos",true,true,true,true);
        this.usuarioActual = usuario;
        this.carpetaRaiz = new File("Z" + File.separator + usuario.getUsername());
        setSize(800,480);
        construirVentana();
    }
    
    
    private void construirVentana(){
      setLayout(new BorderLayout());

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(crearBarraHerramientas(), BorderLayout.NORTH);

        lblRutaActual = new JLabel("Ruta actual: ");
        lblRutaActual.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        norte.add(lblRutaActual, BorderLayout.SOUTH);

        add(norte, BorderLayout.NORTH);
        add(crearDivisor(), BorderLayout.CENTER);

        arbolCarpetas.setSelectionRow(0);
        aplicarEstiloWindows();
        

        
    }

    private JPanel crearBarraHerramientas(){
        JPanel barra = new JPanel (new FlowLayout(FlowLayout.LEFT,6,6));
        
        barra.add(crearBoton("Nueva carpeta", this::onNuevaCarpeta ));
        barra.add(crearBoton("Nuevo Archivo", this::onNuevoArchivo));
        barra.add(crearBoton("Importar" , this::onImportar));
        barra.add(crearBoton("Organizar", this::onOrganizar));
        barra.add(crearBoton("Copiar", this::onCopiar));
        barra.add(crearBoton("Pegar", this::onPegar));
        barra.add(crearBoton("Eliminar", this::onEliminar));
        barra.add(crearBoton("Renombrar", this::onRenombrar));
        barra.add(crearBoton("Abrir", this::onAbrir));
        barra.add(crearBoton("Refrescar", this::refrescarArbol));
        
        return barra;
        
        
    }
    
    private JButton crearBoton(String texto, Runnable accion){
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        return boton;
    }
    
    private JSplitPane crearDivisor(){
        DefaultMutableTreeNode nodoRaiz = crearNodoRaizMiPC();
        modeloArbol = new DefaultTreeModel(nodoRaiz);
        arbolCarpetas = new JTree (modeloArbol);
        arbolCarpetas.addTreeSelectionListener(this::onSeleccionCarpeta);
        JScrollPane scrollArbol = new JScrollPane(arbolCarpetas);
        aplicarIconosArbol();
        scrollArbol.setPreferredSize(new Dimension(220,0));
        String [] columnas = {"" ,"Nombre" , "Ultima modificacion", "Tipo", "Tamaño"};
        modeloTabla = new DefaultTableModel(columnas, 0){
            public boolean isCellEditable(int fila, int columna){
                return false;
            }
            
            public Class<?> getColumnClass (int columnIndex){
                return (columnIndex == 0 ) ? ImageIcon.class : String.class;
            }
            
        };
        tablaArchivos = new JTable(modeloTabla);
        tablaArchivos.setAutoCreateRowSorter(true);
       tablaArchivos.getColumnModel().getColumn(0).setMaxWidth(28);
       tablaArchivos.getColumnModel().getColumn(0).setMinWidth(28);
       tablaArchivos.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("");
       JScrollPane scrollTabla = new JScrollPane(tablaArchivos);

    JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollArbol, scrollTabla);
    divisor.setDividerLocation(0.25);
    divisor.setResizeWeight(0.25);
        return divisor;
    }
    
    
    private DefaultMutableTreeNode crearNodoRaizMiPC(){
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("MI PC");
        File[] unidades = File.listRoots();
        if(unidades != null){
            for(File unidad : unidades){
                raiz.add(construirNodo(unidad,false));
                
            }
        }
        
        raiz.add(construirNodo(carpetaRaiz,true));
        return raiz;
        
    }
    
    
    
    private DefaultMutableTreeNode construirNodo(File carpeta, boolean recursivo){
    
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(new NodoCarpeta(carpeta));
        
        if(recursivo){
            File[] hijos = carpeta.listFiles(File::isDirectory);
            if(hijos!= null){
                for (File hijo : hijos){
                    nodo.add(construirNodo(hijo,true));
                }
            }
        }
        
        return nodo;
    }
    
    private void cargarHijosSiNecesario(DefaultMutableTreeNode nodo){
         if (nodo.getChildCount() > 0)
        return;
    Object obj = nodo.getUserObject();
    if (!(obj instanceof NodoCarpeta))
        return;
    File carpeta = ((NodoCarpeta) obj).carpeta;
    File[] hijos = carpeta.listFiles(File::isDirectory);
    if (hijos != null){
        for (File hijo : hijos){
            nodo.add(new DefaultMutableTreeNode(new NodoCarpeta(hijo)));
        }
        modeloArbol.reload(nodo);
    }
        
    }
    
    
    
    
    private void onSeleccionCarpeta(TreeSelectionEvent evt){
    DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolCarpetas.getLastSelectedPathComponent();

    if (nodoSeleccionado == null){
        return;
    }

    Object obj = nodoSeleccionado.getUserObject();
    if (!(obj instanceof NodoCarpeta)){
        return;
    }
    
    
     NodoCarpeta envoltura = (NodoCarpeta) obj;
    carpetaSeleccionada = envoltura.carpeta;

    lblRutaActual.setText("Ruta actual " + carpetaSeleccionada.getAbsolutePath());
    cargarHijosSiNecesario(nodoSeleccionado);
    actualizarTabla();
        
    }
    
    
    
    
    private void actualizarTabla(){
        modeloTabla.setRowCount(0);
        
       File[] contenido = carpetaSeleccionada.listFiles();
       if(contenido == null) return;
       
       SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
       ImageIcon iconoCarpeta = cargarIconoEscalado("carpeta.png", 18);
       ImageIcon iconoArchivo = cargarIconoEscalado("editor.png", 18);
       
       for(File item : contenido){
           String nombre = item.getName();
           String fecha = formato.format(new Date(item.lastModified()));
           String tipo = item.isDirectory() ? "Carpeta de Archivos" : obtenerExtension(item);
           String tamano = item.isDirectory() ? "--" : formatearTamaño(item.length());
           ImageIcon icono =  obtenerIconoPorTipo(item);
           modeloTabla.addRow(new Object[]{icono,nombre,fecha,tipo,tamano});
       }
       
        
    }
    
    private ImageIcon obtenerIconoPorTipo(File archivo){
        if (archivo.isDirectory()){
            return cargarIconoEscalado("carpeta.png", 18);
        }
        
        String ext = obtenerExtension(archivo).toLowerCase();
        
        if(ext.contains("jpg")|| ext.contains("png") || ext.contains("jpeg") || ext.contains("gif") ){
            return cargarIconoEscalado("imagen.png" , 18);
        }
        
        if (ext.contains("mp3") || ext.contains("wav")){
            return cargarIconoEscalado("musica.png", 18);
        }
        return cargarIconoEscalado("editor.png", 18);
    }
    
    
    
    private String obtenerExtension(File archivo){
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf(".");
        return (punto == -1) ? "Archivo" : "Archivo " + nombre.substring(punto + 1).toUpperCase();
    }
    
    private String formatearTamaño(long bytes){
        if (bytes < 1024 ) return bytes + " B";
        if (bytes < 1024 * 1024 ) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
    
    
    private void refrescarArbol(){
        DefaultMutableTreeNode nuevaRaiz = crearNodoRaizMiPC();
        modeloArbol.setRoot(nuevaRaiz);
         actualizarTabla();
    }
    
    
    
    private File obtenerSeleccionEnTabla(){
        int fila = tablaArchivos.getSelectedRow();
        if (fila == -1 || carpetaSeleccionada == null){
            return null;
        }
        int filaModelo = tablaArchivos.convertRowIndexToModel(fila);
        String nombre = (String) modeloTabla.getValueAt(filaModelo, 1);
        return new File (carpetaSeleccionada, nombre);
        
        
    }
    
    
 private void aplicarIconosArbol() {
    ImageIcon iconoCarpeta = cargarIconoEscalado("carpeta.png", 16);

    arbolCarpetas.setCellRenderer(new javax.swing.tree.DefaultTreeCellRenderer() {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (iconoCarpeta != null) {
                setIcon(iconoCarpeta); // todas las carpetas usan el mismo ícono, sin importar si tienen hijos
            }

            return this;
        }
    });
}
    
    private ImageIcon cargarIconoEscalado (String nombreArchivo, int tamano){
        java.net.URL  ruta = getClass().getResource("/ImagenesOS/" + nombreArchivo);
        if (ruta == null)
            return null;
        Image img = new ImageIcon(ruta).getImage();
        Image escalada = img.getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
        return new ImageIcon(escalada);
    }
    
    
    private void onNuevaCarpeta(){
        if (carpetaSeleccionada == null)
            return;
        String nombre = JOptionPane.showInternalInputDialog(this, "Nombre de la nueva carpeta:");
        if (nombre == null || nombre.trim().isEmpty())
            return;
        
        File nueva = new File (carpetaSeleccionada, nombre.trim());
        if (nueva.mkdir()){
            refrescarArbol();
        }
        else{
            JOptionPane.showInternalMessageDialog(this, "No se completo la tarea de crear la carpeta");
        }
    }
    
    private void onNuevoArchivo(){
        if (carpetaSeleccionada == null)
            return;
        String nombre = JOptionPane.showInternalInputDialog(this, "Nombre del nuevo archivo (Incluir extension");
        if (nombre == null || nombre.trim().isEmpty())
            return;
        
        try{
            File nuevo = new File (carpetaSeleccionada, nombre.trim());
            if (nuevo.createNewFile()){
                actualizarTabla();
            }
            else{
                JOptionPane.showInternalMessageDialog(this, "Archivo ya existe");
            }
        }catch (IOException e){
             JOptionPane.showInternalMessageDialog(this, "Error al crear el archivo: " + e.getMessage());
        }
    }
    
    
    private void onImportar(){
        if (carpetaSeleccionada == null)
            return;
        JFileChooser selector = new JFileChooser();
        int resultado = selector.showOpenDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION){
            File origen = selector.getSelectedFile();
            File destino = new File (carpetaSeleccionada, origen.getName());
            try{
              Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                actualizarTabla();
            }catch(IOException e ){
                JOptionPane.showInternalMessageDialog(this, "Error al importar: " + e.getMessage());
                
            }
        }
        
        
    }   
    
    
    private void onOrganizar(){
        if (carpetaSeleccionada == null)
            return;
        int confirmar = JOptionPane.showInternalConfirmDialog (this,
            "¿Organizar los archivos de \"" + carpetaSeleccionada.getName() + "\" en subcarpetas por tipo?",
            "Organizar", JOptionPane.YES_NO_OPTION);
        
        if (confirmar != JOptionPane.YES_OPTION) 
            return;
        
        Hilos.OrganizadorHilos hilo = new Hilos.OrganizadorHilos(carpetaSeleccionada, () -> {
        refrescarArbol();
        JOptionPane.showInternalMessageDialog(this, "Organización completada.");
        
        
    });
     hilo.start();
    }
    
    
    private void onCopiar(){
        archivoCopiado = obtenerSeleccionEnTabla();
        if(archivoCopiado == null){
            JOptionPane.showInternalMessageDialog(this, "Selecciona un archivo primero");
        }
    }
    
    
    private void onPegar(){
        if (archivoCopiado == null || carpetaSeleccionada == null)
            return;
        if (archivoCopiado.isDirectory()){
            JOptionPane.showInternalMessageDialog(this, "Solo un archivo singular, no multiples");
            return;
        }
        
        File destino = new File (carpetaSeleccionada, archivoCopiado.getName());
        try{
            Files.copy(archivoCopiado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            actualizarTabla();
        } catch(IOException e){
            JOptionPane.showInternalMessageDialog(this, "Error al pegar: " +e.getMessage());
        }
        
        
    }
    
    
    private void onEliminar(){
        File seleccionado = obtenerSeleccionEnTabla();
        if (seleccionado == null){
            JOptionPane.showInternalMessageDialog(this, "Selecciona un archivo o carpeta primero.");
            return;
        }
        
        int confirmar = JOptionPane.showInternalConfirmDialog (this, "¿Eliminar \"" + seleccionado.getName() + "\"?", "Confirmar",JOptionPane.YES_NO_OPTION);
        
        if (confirmar == JOptionPane.YES_OPTION){
            eliminar(seleccionado);
            refrescarArbol();
        }
        
    }
    
    private void eliminar(File archivo){
        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                for (File hijo : hijos) {
                    eliminar(hijo);
                }
            }
        }
        archivo.delete();
    }
    
    
    private void onRenombrar(){
        File seleccionado = obtenerSeleccionEnTabla();
        if(seleccionado == null){
            JOptionPane.showInternalMessageDialog(this, "Selecciona un archivo o carpeta primero");
            return;
        }
        
        String nuevoNombre = (String) JOptionPane.showInternalInputDialog(this, "Nuevo nombre:", "Renombrar", JOptionPane.PLAIN_MESSAGE, null, null, seleccionado.getName());
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty())
            return;
        File renombrado = new File(seleccionado.getParentFile(), nuevoNombre.trim());
        if (seleccionado.renameTo(renombrado)){
            refrescarArbol();
        }
        else{
            JOptionPane.showInternalMessageDialog(this, "Renombrado fallido");
        }
        
    }
    
    
    
    private void onAbrir(){
        File seleccionado = obtenerSeleccionEnTabla();
        if(seleccionado == null)
            return;
        if (seleccionado.isDirectory()){
            carpetaSeleccionada = seleccionado;
            lblRutaActual.setText("Ruta actual: " +carpetaSeleccionada.getAbsolutePath());
            actualizarTabla();
        }
        else{
            try{
                String ext = obtenerExtension(seleccionado).toLowerCase();
                if(ext.contains("mp3") && alAbrirMusica != null){
                    alAbrirMusica.accept(seleccionado);
                    return;
                }
                Desktop.getDesktop().open(seleccionado);
            } catch (IOException e ){
                JOptionPane.showInternalMessageDialog(this, "No se pudo abrir el archivo: " +e.getMessage());
            }
        }
    }
    
    
    
    private void actualizarListaArchivos(){
        modeloLista.clear();
        
        File[] contenido = carpetaSeleccionada.listFiles();
        if(contenido != null){
            for(File archivo : contenido){
                modeloLista.addElement(archivo.getName());
            }
        }
    }
    
    private void aplicarEstiloWindows(){
        Color azulSeleccion = new Color(0,120,215);
        Color grisClaro = new Color(240, 240, 240);
        
        arbolCarpetas.setBackground(Color.WHITE);
        arbolCarpetas.setRowHeight(22);
        
        tablaArchivos.setBackground(Color.WHITE);
        tablaArchivos.setGridColor(new Color(230,230,230));
        tablaArchivos.setShowGrid(true);
        tablaArchivos.setRowHeight(24);
        tablaArchivos.setSelectionBackground(azulSeleccion);
        tablaArchivos.setSelectionForeground(Color.WHITE);
        tablaArchivos.setIntercellSpacing(new Dimension(1,1));
        
        tablaArchivos.getTableHeader().setBackground(new Color(240,240,240));
        tablaArchivos.getTableHeader().setForeground(Color.BLACK);
        tablaArchivos.getTableHeader().setFont(tablaArchivos.getTableHeader().getFont().deriveFont(Font.BOLD,12f));
        
        tablaArchivos.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer(){
           @Override
           public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean esSeleccionado, boolean focuseado, int row, int column){
               Component c = super.getTableCellRendererComponent(tabla, valor, esSeleccionado, focuseado, row, column);
               
               if (!esSeleccionado){
                  c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
 
               }
               return c;
           } 
        });
        
        estilizarPanel(getContentPane(),grisClaro);
        getContentPane().setBackground(Color.WHITE);
    }
    
    private void estilizarPanel(Container contenedor, Color color) {
    for (Component comp : contenedor.getComponents()) {
        if (comp instanceof JPanel) {
            comp.setBackground(color);
        }
        if (comp instanceof JLabel) {
            comp.setBackground(color);
            ((JLabel) comp).setOpaque(true);
        }
        if (comp instanceof Container) {
            estilizarPanel((Container) comp, color);
        }
    }
}
    
    
    
    public void setAlAbrirMusica(java.util.function.Consumer<File> accion){
    this.alAbrirMusica = accion;
}
    
}
