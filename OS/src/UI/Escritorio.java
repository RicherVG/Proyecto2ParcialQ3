package UI;

import Persistencia.GestorUsuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import modelo.Usuarios;

/**
 *
 * @author andre
 */
public class Escritorio extends JFrame {
    
    private Usuarios usuarioActual;
    private JDesktopPane areaEscritorio;
    private JPanel barraTareas;
    private JLabel lblReloj;
    private GestorUsuario gestor;
    
    
    public Escritorio(Usuarios usuario){
        this.usuarioActual = usuario;
        this.gestor = new GestorUsuario();
        construirVentana();
        iniciarReloj();
    }
    
   private void construirVentana(){
       setTitle("Escritorio");
       setUndecorated(true);
       setExtendedState(JFrame.MAXIMIZED_BOTH);
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setLayout(new BorderLayout());
       
       areaEscritorio = new JDesktopPane();
       areaEscritorio.setBackground(new Color(15,15,20));
       
       barraTareas = crearBarraTareas();
       
       add(areaEscritorio, BorderLayout.CENTER);
       add(barraTareas, BorderLayout.SOUTH);
   }
    
   private JPanel crearBarraTareas(){
       JPanel barra = new JPanel (new BorderLayout());
       barra.setPreferredSize(new Dimension(0,46));
       barra.setBackground(new Color(35,35,40));
       
       JButton botonInicio = BotonInicio();
       
       JPanel iconosApps = new JPanel(new FlowLayout(FlowLayout.CENTER,10,6));
       iconosApps.setOpaque(false);
       
       iconosApps.add(crearBotonApp("carpeta.png", "Explorador de archivos",() ->{
         ExploradorArchivos explorador = new ExploradorArchivos (usuarioActual);
         areaEscritorio.add(explorador);
         explorador.setVisible(true);
       }));
 iconosApps.add(crearBotonApp("editor.png", "Editor de texto", () -> {
    EditorTexto editor = new EditorTexto(new File("Z" + File.separator + usuarioActual.getUsername()));
    areaEscritorio.add(editor);
    editor.setVisible(true);
}));
       iconosApps.add(crearBotonApp("imagen.png","Galeria", () -> {
           Galeria galeria = new Galeria(usuarioActual);
           areaEscritorio.add(galeria);
           galeria.setVisible(true);
       }));
       iconosApps.add(crearBotonApp("terminal.png", "Consola", () -> {
           CMD consola = new CMD(usuarioActual);
           areaEscritorio.add(consola);
           consola.setVisible(true);
       
               }));

       iconosApps.add(crearBotonApp("musica.png", "Reproductor de música",
                () -> {
                    ReproductorMusica reproductor = new ReproductorMusica(usuarioActual);
                    areaEscritorio.add(reproductor);
                    reproductor.setVisible(true);
                }));

       iconosApps.add(crearBotonApp("instagram.png", "INSTA+", () -> abrirInsta()));
       
       JPanel bandejaSistema = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10,6));
       bandejaSistema.setOpaque(false);
       
      lblReloj = new JLabel();
      lblReloj.setForeground(Color.WHITE);
      lblReloj.setFont(lblReloj.getFont().deriveFont(13f));
      bandejaSistema.add(lblReloj);
      
      barra.add(botonInicio, BorderLayout.WEST);
      barra.add(iconosApps,BorderLayout.CENTER);
      barra.add(bandejaSistema, BorderLayout.EAST);
      
      return barra;
   }
   
   private void abrirInsta() {
       SwingUtilities.invokeLater(() -> {
           insta.ui.MainFrame instaFrame = new insta.ui.MainFrame();
           if (usuarioActual != null && usuarioActual.getUsername() != null) {
               try {
                   insta.model.Usuario uInsta = instaFrame.getGestorUsuarios().buscarExacto(usuarioActual.getUsername());
                   if (uInsta != null) {
                       instaFrame.setSession(uInsta);
                   }
               } catch (Exception ignored) {}
           }

           // Envolver el MainFrame dentro de un JInternalFrame para que viva en el escritorio
           javax.swing.JInternalFrame internalFrame = new javax.swing.JInternalFrame(
                   "INSTA+", true, true, true, true);
           internalFrame.setSize(instaFrame.getSize());
           internalFrame.setLayout(new BorderLayout());

           // Transferir el contenido del JFrame al JInternalFrame
           internalFrame.setContentPane(instaFrame.getContentPane());
           internalFrame.setJMenuBar(instaFrame.getJMenuBar());

           // Cerrar sólo el internal frame, no el OS
           internalFrame.setDefaultCloseOperation(javax.swing.JInternalFrame.DISPOSE_ON_CLOSE);

           // Añadir al escritorio y mostrar
           areaEscritorio.add(internalFrame);
           internalFrame.setVisible(true);

           // Centrar dentro del escritorio
           int x = (areaEscritorio.getWidth() - internalFrame.getWidth()) / 2;
           int y = (areaEscritorio.getHeight() - internalFrame.getHeight()) / 2;
           internalFrame.setLocation(Math.max(0, x), Math.max(0, y));
       });
   }
   
   
   
   
   private JButton BotonInicio(){
       JButton boton = new JButton();
       boton.setPreferredSize(new Dimension(50,44));
       boton.setFocusable(false);
       boton.setBorderPainted(false);
       boton.setContentAreaFilled(false);
       boton.setOpaque(false);
       boton.setToolTipText("Menu inicio");
       
       java.net.URL ruta = getClass().getResource("/imagenesOS/windows.png");
       if(ruta!= null){
           Image img = new ImageIcon(ruta).getImage();
           Image escalada = img.getScaledInstance(38, 38, Image.SCALE_SMOOTH);
           boton.setIcon(new ImageIcon(escalada));
       }
       else{
           boton.setText("inicio");
       }
       
       JPopupMenu menu = new JPopupMenu();
       
       if(usuarioActual.isEsAdmin()){
       JMenuItem itemAdmin = new JMenuItem("Administrar cuentas");
       itemAdmin.addActionListener(e-> {
           AdministrarCuentas admin = new AdministrarCuentas(usuarioActual);
           admin.setOnCuentaPropiaEliminada(()->{
               this.dispose();
               new UI.LoginWindow().setVisible(true);
           });
           areaEscritorio.add(admin);
           admin.setVisible(true);
       });
        
        menu.add(itemAdmin);
        menu.addSeparator();
       }

       JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
       itemCerrarSesion.addActionListener(e -> {
           this.dispose();
           new UI.LoginWindow().setVisible(true);
       });
       menu.add(itemCerrarSesion);
       JMenuItem itemApagar = new JMenuItem("Apagar Sistema");
       itemApagar.addActionListener(e -> {
           int confirmar = JOptionPane.showConfirmDialog(this, "Apagar sistema?" , "Apagar", JOptionPane.YES_NO_OPTION);
           if (confirmar == JOptionPane.YES_OPTION) {
               System.exit(0);
           }
       });
       menu.add(itemApagar);

       boton.addActionListener(e -> menu.show(boton, 0, -menu.getPreferredSize().height));
       return boton;
   }
   
   private JButton crearBotonApp(String nombreArchivo, String tooltip, Runnable accion){
       JButton boton = new JButton();
       boton.setPreferredSize(new Dimension(34,34));
       boton.setFocusable(false);
       boton.setBorderPainted(false);
       boton.setContentAreaFilled(false);
       boton.setOpaque(false);
       boton.setToolTipText(tooltip);
       
       java.net.URL ruta = getClass().getResource("/imagenesOS/" + nombreArchivo);
       if (ruta!= null){
           Image img = new ImageIcon(ruta).getImage();
           Image escalar = img.getScaledInstance(28, 28, Image.SCALE_SMOOTH);
           boton.setIcon(new ImageIcon(escalar));
       }
       else{
           boton.setText("?");
       }
       boton.addActionListener(e -> accion.run());
       return boton;
   }
   
   private void iniciarReloj(){
       SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
       
       Timer timer = new Timer (1000, e -> {
           lblReloj.setText(formato.format(new Date()));
       });
       timer.start();
   }
}
