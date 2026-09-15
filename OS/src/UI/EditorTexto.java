/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import java.awt.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import modelo.DocumentoTexto;
import modelo.Fragmento;

/**
 *
 * @author andre
 */
public class EditorTexto extends JInternalFrame {
    
    private JTextPane areaTexto;
    private File carpetaTrabajo,archivoActual;
    
    private static final String[] FUENTES = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    private static final Integer[] TAMANOS = {10,12,14,16,18,24,32};
    
    public EditorTexto(File carpetaInicial){
        
        super("Notepad", true, true, true, true);
        this.carpetaTrabajo = carpetaInicial;
        setSize(650, 450);
        construirVentana();
        
    }
    
    private void construirVentana(){
        setLayout(new BorderLayout());
        
        areaTexto = new JTextPane();
        areaTexto.setEditorKit(new StyledEditorKit(){
             @Override
            public ViewFactory getViewFactory() {
                return new ViewFactory() {
                    @Override
                    public View create(Element elem) {
                        String kind = elem.getName();
                        if (kind != null) {
                            switch (kind) {
                                case AbstractDocument.ContentElementName:
                                    return new LabelView(elem);
                                case AbstractDocument.ParagraphElementName:
                                return new ParrafoConWrap(elem);
                                case AbstractDocument.SectionElementName:
                                    return new BoxView(elem, View.Y_AXIS);
                                case StyleConstants.ComponentElementName:
                                    return new ComponentView(elem);
                                case StyleConstants.IconElementName:
                                    return new IconView(elem);
                            }
                        }
                        return new LabelView(elem);
                    }
                };
            }
        });
        
        JScrollPane scroll = new JScrollPane(areaTexto);
        add(crearBarraHerramientas(), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        aplicarEstilo();
        
    }
    
    private void aplicarEstilo(){
        Color grisClaro = new Color(240,240,240);
        getContentPane().setBackground(Color.WHITE);
        areaTexto.setMargin(new Insets(10,12,10,12));
        
        estilizarPanel(getContentPane(),grisClaro);
        
    }
    
    private void estilizarPanel(Container  contenedor, Color color){
        for(Component compo : contenedor.getComponents()){
            if (compo instanceof JPanel){
                compo.setBackground(color);
            }
            if (compo instanceof JLabel){
                ((JLabel) compo).setOpaque(true);
                compo.setBackground(color);
            }
            if(compo instanceof Container){
                estilizarPanel((Container) compo, color);
            }
        }
    }
    
    
    
    
    private JPanel crearBarraHerramientas(){
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT,6,4));
        
        JButton btnAbrir = new JButton("Abrir");
        btnAbrir.addActionListener(e -> {
            try {
                onAbrir();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(EditorTexto.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> onGuardar());
        
        JComboBox<String> comboFuente = new JComboBox<>(FUENTES);
        comboFuente.setMaximumRowCount(12);
        comboFuente.addActionListener(e-> aplicarFuente((String) comboFuente.getSelectedItem()));
        
        JComboBox<Integer> comboTamano = new JComboBox<>(TAMANOS);
        comboTamano.setSelectedItem(12);
        comboTamano.addActionListener(e -> aplicarTamano((Integer) comboTamano.getSelectedItem()));
        
        JButton btnColor = new JButton("Color");
        btnColor.addActionListener(e -> aplicarColor());
        
        barra.add(btnAbrir);
        barra.add(btnGuardar);
        barra.add(new JLabel ("Fuente:"));
        barra.add(comboFuente);
        barra.add(new JLabel("Tamaño"));
        barra.add(comboTamano);
        barra.add(btnColor);
        
        return barra;
        
    }
    
    
    private void aplicarFuente(String fuente){
      SimpleAttributeSet attrs = new SimpleAttributeSet();
      StyleConstants.setFontFamily(attrs, fuente);
      areaTexto.setCharacterAttributes(attrs, false);
      areaTexto.requestFocusInWindow();
    }
    
    private void aplicarTamano(int tamano){
       SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrs, tamano);
        areaTexto.setCharacterAttributes(attrs, false);
        areaTexto.requestFocusInWindow();
    }
    
    private void aplicarColor(){
       Color elegido = JColorChooser.showDialog(this, "Eliga color", Color.BLACK);
       if (elegido == null)
           return;
       
       SimpleAttributeSet attrs = new SimpleAttributeSet();
       StyleConstants.setForeground(attrs, elegido);
       areaTexto.setCharacterAttributes(attrs, false);
       areaTexto.requestFocusInWindow();
    }
    
    

    
    
   
    
    private void onGuardar(){
      JFileChooser selector = new JFileChooser(carpetaTrabajo);
      selector.setDialogTitle("Guardar Documento");
      selector.setFileFilter(new FileNameExtensionFilter("Archivos de Texto (*.txt)" ,"txt"));
      
      if (archivoActual !=null){
          selector.setSelectedFile(archivoActual);
      }
      
      int resultado = selector.showSaveDialog(this);
      if (resultado != JFileChooser.APPROVE_OPTION)
          return;
      
      File destino = selector.getSelectedFile();
      if (!destino.getName().toLowerCase().endsWith(".txt")){
          destino = new File(destino.getParentFile(), destino.getName() + ".txt");
          
      }
      archivoActual = destino;
      
      try(BufferedWriter escritor = new BufferedWriter(new FileWriter(archivoActual))){
          escritor.write(areaTexto.getText());
          setTitle("Notepad - " +archivoActual.getName());
      } catch(IOException e){
          JOptionPane.showInternalMessageDialog(this, "Error al guardar: " +e.getMessage());
      }
      
      
    } 
    
    private void onAbrir() throws ClassNotFoundException{
       JFileChooser selector = new JFileChooser(carpetaTrabajo);
       selector.setDialogTitle("Abrir Documento");
       selector.setFileFilter(new FileNameExtensionFilter("Archivos de texto(*.txt)", "txt"));
        
       int resultado = selector.showOpenDialog(this);
       if(resultado != JFileChooser.APPROVE_OPTION)
           return;
       archivoActual = selector.getSelectedFile();
       
       try(BufferedReader lector = new BufferedReader(new FileReader(archivoActual))){
           StringBuilder contenido = new StringBuilder();
           String linea;
           while((linea = lector.readLine()) != null) {
               contenido.append(linea).append("\n");
           }
           areaTexto.setText(contenido.toString());
           setTitle("Notepad - " +archivoActual.getName());
       }catch (IOException e){
           JOptionPane.showInternalMessageDialog(this, "Error al abrir: " + e.getMessage());
       }
       
    }
    
   
    
  
    
    
}
