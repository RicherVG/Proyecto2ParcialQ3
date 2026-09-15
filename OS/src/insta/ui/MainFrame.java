/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;
import insta.gestor.*;
import insta.model.Usuario;
import insta.network.ClienteMensajeria;
import insta.network.ServidorCentral;
import insta.storage.FileUtils;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;
/**
 *
 * @author riche
 */
public class MainFrame extends JFrame {
   private Usuario sessionUser;
   private ClienteMensajeria clienteSocket;
   private final GestorUsuarios gestorUsuarios = new GestorUsuarios();
   private final GestorFollowers gestorFollowers = new GestorFollowers();
   private final GestorPublicaciones gestorPublicaciones = new GestorPublicaciones();
   private final GestorInbox gestorInbox = new GestorInbox();
   private final GestorStickers gestorStickers = new GestorStickers();
   private final CardLayout cardLayout = new CardLayout();
   private final JPanel contentPanel = new JPanel(cardLayout);
   private SidebarPanel sidebar;
   private FeedPanel feedPanel;
   private PerfilPanel perfilPanel;
   private InboxPanel inboxPanel;
   private ExplorarPanel explorarPanel;
   private BuscarPanel buscarPanel;
   private NotificacionesPanel notifPanel;
   private InteraccionesPanel interaccionesPanel;
   private final JLayeredPane layeredPane = new JLayeredPane();
   private final JPanel contentWrapper = new JPanel(new BorderLayout());
   public MainFrame() {
      FileUtils.ensureBaseStructure();
      insta.storage.DatosPorDefecto.inicializarCuentasSiNoExisten(gestorUsuarios, gestorPublicaciones);
      new Thread(() -> {
         try {
            new ServidorCentral().iniciar();
         } catch (Exception ignored) {}
      }).start();
      setTitle("InstaVisual");
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setSize(Screens.W(), Screens.H());
      setMinimumSize(new Dimension(1024, 720));
      setLocationRelativeTo(null);
      setResizable(true);
      getContentPane().add(layeredPane);
      layeredPane.setLayout(null);
      layeredPane.setBackground(new Color(250, 250, 250));
      layeredPane.setOpaque(true);
      contentPanel.setBackground(Color.WHITE);
      contentWrapper.setBackground(new Color(250, 250, 250));
      contentWrapper.setOpaque(true);
      contentWrapper.add(contentPanel, BorderLayout.CENTER);
      layeredPane.add(contentWrapper);
      layeredPane.setLayer(contentWrapper, JLayeredPane.DEFAULT_LAYER);
      layeredPane.addComponentListener(new MainResizeHandler());
      mostrarLogin();
   }
   private LoginPanel loginPanel;
   private RegisterPanel registerPanel;
   private void mostrarLogin() {
      contentPanel.removeAll();
      loginPanel = new LoginPanel(this);
      registerPanel = new RegisterPanel(this);
      contentPanel.add(loginPanel, Screens.LOGIN);
      contentPanel.add(registerPanel, Screens.REGISTER);
      cardLayout.show(contentPanel, Screens.LOGIN);
      if (sidebar != null) {
         layeredPane.remove(sidebar);
         sidebar = null;
      }
      layeredPane.revalidate();
      layeredPane.repaint();
   }
   public void setSession(Usuario u) {
      this.sessionUser = u;
      if (clienteSocket != null) {
         clienteSocket.desconectar();
      }
      clienteSocket = new ClienteMensajeria(u.getUsername());
      clienteSocket.conectar();
      initMainUI();
   }
   public void cerrarSesion() {
      ocultarOverlay();
      if (clienteSocket != null) {
         clienteSocket.desconectar();
         clienteSocket = null;
      }
      sessionUser = null;
      feedPanel = null;
      perfilPanel = null;
      inboxPanel = null;
      explorarPanel = null;
      buscarPanel = null;
      notifPanel = null;
      interaccionesPanel = null;
      mostrarLogin();
   }
   private void initMainUI() {
      contentPanel.removeAll();
      feedPanel = new FeedPanel(this);
      perfilPanel = new PerfilPanel(this);
      inboxPanel = new InboxPanel(this);
      explorarPanel = new ExplorarPanel(this);
      buscarPanel = new BuscarPanel(this);
      notifPanel = new NotificacionesPanel(this);
      interaccionesPanel = new InteraccionesPanel(this);
      contentPanel.add(feedPanel, Screens.FEED);
      contentPanel.add(perfilPanel, Screens.PERFIL);
      contentPanel.add(inboxPanel, Screens.INBOX);
      contentPanel.add(explorarPanel, Screens.EXPLORAR);
      contentPanel.add(interaccionesPanel, Screens.INTERACCIONES);
      if (sidebar != null) {
         layeredPane.remove(sidebar);
      }
      sidebar = new SidebarPanel(this);
      sidebar.addComponentListener(new SidebarResizeHandler());
      layeredPane.add(sidebar);
      layeredPane.setLayer(sidebar, JLayeredPane.POPUP_LAYER); 
      layeredPane.setComponentZOrder(sidebar, 0); 
      layeredPane.setComponentZOrder(contentWrapper, 1);
      for (java.awt.event.ComponentListener cl : layeredPane.getComponentListeners()) {
         cl.componentResized(
               new java.awt.event.ComponentEvent(layeredPane, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
      }
      showScreen(Screens.FEED);
      layeredPane.revalidate();
      layeredPane.repaint();
   }
   public void mostrarEditarPerfil() {
      Usuario u = getSession();
      if (u == null)
         return;
      JPanel modalBg = new JPanel(new GridBagLayout());
      modalBg.setBackground(new Color(0, 0, 0, 140));
      modalBg.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
      modalBg.addMouseListener(new EmptyMouseHandler());
      JPanel dialog = new JPanel();
      dialog.setLayout(new BoxLayout(dialog, BoxLayout.Y_AXIS));
      dialog.setBackground(Color.WHITE);
      dialog.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(new Color(219, 219, 219), 1),
            new javax.swing.border.EmptyBorder(24, 30, 24, 30)));
      JLabel lblTit = new JLabel("Editar Perfil");
      lblTit.setFont(new Font("SansSerif", Font.BOLD, 18));
      lblTit.setAlignmentX(Component.CENTER_ALIGNMENT);
      dialog.add(lblTit);
      dialog.add(Box.createVerticalStrut(20));
      JPanel fotoPanel = new JPanel();
      fotoPanel.setLayout(new BoxLayout(fotoPanel, BoxLayout.Y_AXIS));
      fotoPanel.setBackground(Color.WHITE);
      JLabel lblPreview = new JLabel(SidebarPanel.cargarFotoCircular(u.getRutaFotoPerfil(), 64));
      lblPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
      fotoPanel.add(lblPreview);
      fotoPanel.add(Box.createVerticalStrut(8));
      JButton btnCambiarFoto = new JButton("Cambiar foto");
      btnCambiarFoto.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnCambiarFoto.setForeground(new Color(0, 149, 246));
      btnCambiarFoto.setBackground(Color.WHITE);
      btnCambiarFoto.setBorder(null);
      btnCambiarFoto.setFocusPainted(false);
      btnCambiarFoto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnCambiarFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
      fotoPanel.add(btnCambiarFoto);
      dialog.add(fotoPanel);
      dialog.add(Box.createVerticalStrut(16));
      String[] rutaNuevaFoto = { u.getRutaFotoPerfil() };
      btnCambiarFoto.addActionListener(e -> {
         JFileChooser fc = new JFileChooser();
         fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png"));
         if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            rutaNuevaFoto[0] = fc.getSelectedFile().getAbsolutePath();
            lblPreview.setIcon(SidebarPanel.cargarFotoCircular(rutaNuevaFoto[0], 64));
         }
      });
      JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
      form.setBackground(Color.WHITE);
      JLabel lblPres = new JLabel("Presentación:");
      lblPres.setFont(new Font("SansSerif", Font.BOLD, 13));
      form.add(lblPres);
      JTextArea txtPres = new JTextArea(u.getPresentacion());
      txtPres.setLineWrap(true);
      txtPres.setWrapStyleWord(true);
      txtPres.setFont(new Font("SansSerif", Font.PLAIN, 14));
      txtPres.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(new Color(219, 219, 219), 1),
            new javax.swing.border.EmptyBorder(8, 8, 8, 8)));
      JScrollPane scrollPres = new JScrollPane(txtPres);
      scrollPres.setPreferredSize(new Dimension(300, 70));
      scrollPres.setBorder(null);
      form.add(scrollPres);
      JLabel lblGen = new JLabel("Género:");
      lblGen.setFont(new Font("SansSerif", Font.BOLD, 13));
      form.add(lblGen);
      JComboBox<String> cmbGen = new JComboBox<>(new String[] { "Masculino", "Femenino", "Personalizado" });
      String genActual = u.getGenero();
      if ("M".equals(genActual))
         cmbGen.setSelectedIndex(0);
      else if ("F".equals(genActual))
         cmbGen.setSelectedIndex(1);
      else
         cmbGen.setSelectedIndex(2);
      cmbGen.setBackground(Color.WHITE);
      cmbGen.setFont(new Font("SansSerif", Font.PLAIN, 14));
      form.add(cmbGen);
      dialog.add(form);
      dialog.add(Box.createVerticalStrut(24));
      JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
      btns.setBackground(Color.WHITE);
      JButton btnCancel = new JButton("Cancelar");
      btnCancel.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnCancel.setBackground(new Color(239, 239, 239));
      btnCancel.setFocusPainted(false);
      btnCancel.setBorder(new javax.swing.border.EmptyBorder(8, 20, 8, 20));
      btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JButton btnGuardar = new JButton("Guardar");
      btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnGuardar.setBackground(new Color(0, 149, 246));
      btnGuardar.setForeground(Color.WHITE);
      btnGuardar.setFocusPainted(false);
      btnGuardar.setBorder(new javax.swing.border.EmptyBorder(8, 20, 8, 20));
      btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnCancel.addActionListener(e -> {
         layeredPane.remove(modalBg);
         layeredPane.repaint();
      });
      btnGuardar.addActionListener(e -> {
         String nr = rutaNuevaFoto[0];
         String pr = txtPres.getText();
         if (pr.length() > 150)
            pr = pr.substring(0, 150);
         String gu = cmbGen.getSelectedIndex() == 0 ? "M" : (cmbGen.getSelectedIndex() == 1 ? "F" : "P");
         if (nr != null && !nr.equals(u.getRutaFotoPerfil())) {
            java.io.File origen = new java.io.File(nr);
            java.nio.file.Path destino = insta.storage.AppPaths.imagesDir(u.getUsername())
                  .resolve("perfil_" + origen.getName());
            try {
               insta.storage.FileUtils.copiarArchivo(origen.toPath(), destino);
               u.setRutaFotoPerfil(destino.toString());
            } catch (java.io.IOException ex) {
               mostrarToast("No se pudo copiar la foto");
            }
         }
         u.setPresentacion(pr);
         u.setGenero(gu);
         try {
            gestorUsuarios.actualizar(u);
            mostrarToast("Perfil actualizado");
            sidebar.refresh(u);
            showScreen(Screens.PERFIL); 
         } catch (java.io.IOException ex) {
            mostrarToast("Error al guardar: " + ex.getMessage());
         }
         layeredPane.remove(modalBg);
         layeredPane.repaint();
      });
      btns.add(btnCancel);
      btns.add(btnGuardar);
      dialog.add(btns);
      modalBg.add(dialog);
      layeredPane.add(modalBg, JLayeredPane.MODAL_LAYER);
      layeredPane.moveToFront(modalBg);
      layeredPane.revalidate();
      layeredPane.repaint();
   }
   public void showScreen(String screenId) {
      if (screenId == null)
         return;
      ocultarOverlay();
      if (Screens.LOGIN.equals(screenId) && loginPanel != null)
         loginPanel.limpiar();
      if (Screens.REGISTER.equals(screenId) && registerPanel != null)
         registerPanel.limpiar();
      cardLayout.show(contentPanel, screenId);
      if (Screens.FEED.equals(screenId) && feedPanel != null)
         feedPanel.cargarFeed();
      if (Screens.INBOX.equals(screenId) && inboxPanel != null)
         inboxPanel.cargarConversaciones();
      if (Screens.PERFIL.equals(screenId) && perfilPanel != null)
         perfilPanel.mostrarPerfil(sessionUser);
      if (Screens.EXPLORAR.equals(screenId) && explorarPanel != null)
         explorarPanel.cargar();
      if (Screens.INTERACCIONES.equals(screenId) && interaccionesPanel != null)
         interaccionesPanel.cargarInteracciones();
      if (sidebar != null)
         sidebar.setActiveScreen(screenId);
      refreshSidebar();
   }
   private JPanel overlayActivo = null;
   public void toggleBuscar() {
      if (overlayActivo == buscarPanel) {
         ocultarOverlay();
      } else {
         mostrarOverlay(buscarPanel);
      }
   }
   public void toggleNotificaciones() {
      if (overlayActivo == notifPanel) {
         ocultarOverlay();
      } else {
         notifPanel.cargar();
         mostrarOverlay(notifPanel);
      }
   }
   public void abrirBusquedaCon(String query) {
      if (overlayActivo != buscarPanel) {
         toggleBuscar();
      }
      if (buscarPanel != null) {
         buscarPanel.setBusqueda(query);
      }
   }
   private void mostrarOverlay(JPanel panel) {
      if (overlayActivo != null) {
         layeredPane.remove(overlayActivo);
      }
      overlayActivo = panel;
      if (sidebar != null) {
         sidebar.collapse();
         sidebar.setHoverEnabled(false);
      }
      layeredPane.add(panel, JLayeredPane.PALETTE_LAYER);
      int h = layeredPane.getHeight();
      panel.setBounds(72, 0, 397, h);
      layeredPane.revalidate();
      layeredPane.repaint();
   }
   private void ocultarOverlay() {
      if (overlayActivo != null) {
         layeredPane.remove(overlayActivo);
         overlayActivo = null;
      }
      if (sidebar != null) {
         sidebar.setHoverEnabled(true);
         sidebar.collapse();
      }
      layeredPane.revalidate();
      layeredPane.repaint();
   }
   public Usuario getSession() {
      return sessionUser;
   }
   public GestorUsuarios getGestorUsuarios() {
      return gestorUsuarios;
   }
   public GestorFollowers getGestorFollowers() {
      return gestorFollowers;
   }
   public GestorPublicaciones getGestorPublicaciones() {
      return gestorPublicaciones;
   }
   public GestorInbox getGestorInbox() {
      return gestorInbox;
   }
   public GestorStickers getGestorStickers() {
      return gestorStickers;
   }
   public ClienteMensajeria getClienteSocket() {
      return clienteSocket;
   }
   public JLayeredPane getMainLayeredPane() {
      return layeredPane;
   }
   public void refreshSidebar() {
      if (sidebar != null && sessionUser != null)
         sidebar.refresh(sessionUser);
   }
   public void abrirPerfilDeUsuario(Usuario u) {
      showScreen(Screens.PERFIL);
      if (perfilPanel != null)
         perfilPanel.mostrarPerfil(u);
   }
   public void mostrarPerfilDe(String username) {
      try {
         Usuario u = gestorUsuarios.buscarExacto(username);
         if (u != null) {
            abrirPerfilDeUsuario(u);
         } else {
            mostrarToast("Usuario @" + username + " no encontrado.");
         }
      } catch (IOException ex) {
         mostrarToast("Error al buscar usuario: " + ex.getMessage());
      }
   }
   public void abrirMensajesCon(String username) {
      showScreen(Screens.INBOX);
      if (inboxPanel != null)
         inboxPanel.abrirConversacionCon(username);
   }
   public void mostrarToast(String mensaje) {
      ToastPanel toast = new ToastPanel(mensaje);
      toast.setSize(toast.getPreferredSize());
      toast.setSize(toast.getPreferredSize());
      int x = (layeredPane.getWidth() - toast.getWidth()) / 2;
      int y = layeredPane.getHeight() - toast.getHeight() - 60; 
      toast.setLocation(x, y);
      layeredPane.add(toast, JLayeredPane.POPUP_LAYER);
      Timer timer = new Timer(3000, e -> {
         layeredPane.remove(toast);
         layeredPane.repaint();
      });
      timer.setRepeats(false);
      timer.start();
   }
   public void mostrarConfirmacion(String titulo, String mensaje, String btnConfirmText, Runnable onConfirm) {
      JPanel modalBg = new JPanel();
      modalBg.setLayout(new GridBagLayout());
      modalBg.setBackground(new Color(0, 0, 0, 140));
      modalBg.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
      modalBg.addMouseListener(new EmptyMouseHandler());
      JPanel dialog = new JPanel();
      dialog.setLayout(new BoxLayout(dialog, BoxLayout.Y_AXIS));
      dialog.setBackground(Color.WHITE);
      dialog.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(new Color(219, 219, 219), 1),
            new javax.swing.border.EmptyBorder(24, 30, 24, 30)));
      JLabel lblTit = new JLabel(titulo);
      lblTit.setFont(new Font("SansSerif", Font.BOLD, 18));
      lblTit.setAlignmentX(Component.CENTER_ALIGNMENT);
      dialog.add(lblTit);
      dialog.add(Box.createVerticalStrut(12));
      JLabel lblMsg = new JLabel("<html><center style='width:250px;'>" + mensaje + "</center></html>");
      lblMsg.setFont(new Font("SansSerif", Font.PLAIN, 14));
      lblMsg.setForeground(new Color(115, 115, 115));
      lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
      dialog.add(lblMsg);
      dialog.add(Box.createVerticalStrut(24));
      JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
      btns.setBackground(Color.WHITE);
      JButton btnCancel = new JButton("Cancelar");
      btnCancel.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnCancel.setFocusPainted(false);
      btnCancel.setBackground(new Color(239, 239, 239));
      btnCancel.setForeground(new Color(10, 10, 10));
      btnCancel.setBorder(new javax.swing.border.EmptyBorder(8, 20, 8, 20));
      btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JButton btnConf = new JButton(btnConfirmText);
      btnConf.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnConf.setFocusPainted(false);
      btnConf.setBackground(new Color(237, 73, 86)); 
      btnConf.setForeground(Color.WHITE);
      btnConf.setBorder(new javax.swing.border.EmptyBorder(8, 20, 8, 20));
      btnConf.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnCancel.addActionListener(e -> {
         layeredPane.remove(modalBg);
         layeredPane.repaint();
      });
      btnConf.addActionListener(e -> {
         layeredPane.remove(modalBg);
         layeredPane.repaint();
         if (onConfirm != null)
            onConfirm.run();
      });
      btns.add(btnCancel);
      btns.add(btnConf);
      dialog.add(btns);
      modalBg.add(dialog);
      layeredPane.add(modalBg, JLayeredPane.MODAL_LAYER);
      layeredPane.moveToFront(modalBg);
      layeredPane.revalidate();
      layeredPane.repaint();
   }
   public void mostrarInput(String titulo, String mensaje, String placeholder,
         java.util.function.Consumer<String> onConfirm) {
      JPanel modalBg = new JPanel();
      modalBg.setLayout(new GridBagLayout());
      modalBg.setBackground(new Color(0, 0, 0, 140));
      modalBg.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
      modalBg.addMouseListener(new EmptyMouseHandler());
      JPanel dialog = new JPanel();
      dialog.setLayout(new BoxLayout(dialog, BoxLayout.Y_AXIS));
      dialog.setBackground(Color.WHITE);
      dialog.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(new Color(219, 219, 219), 1),
            new javax.swing.border.EmptyBorder(24, 30, 24, 30)));
      JLabel lblTit = new JLabel(titulo);
      lblTit.setFont(new Font("SansSerif", Font.BOLD, 18));
      lblTit.setAlignmentX(Component.CENTER_ALIGNMENT);
      dialog.add(lblTit);
      dialog.add(Box.createVerticalStrut(12));
      JLabel lblMsg = new JLabel("<html><center style='width:250px;'>" + mensaje + "</center></html>");
      lblMsg.setFont(new Font("SansSerif", Font.PLAIN, 14));
      lblMsg.setForeground(new Color(115, 115, 115));
      lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
      dialog.add(lblMsg);
      dialog.add(Box.createVerticalStrut(16));
      JTextField txtInput = new JTextField();
      txtInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
      txtInput.setMaximumSize(new Dimension(250, 40));
      txtInput.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(new Color(219, 219, 219), 1),
            new javax.swing.border.EmptyBorder(8, 12, 8, 12)));
      dialog.add(txtInput);
      dialog.add(Box.createVerticalStrut(24));
      JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
      btns.setBackground(Color.WHITE);
      JButton btnCancel = new JButton("Cancelar");
      btnCancel.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnCancel.setFocusPainted(false);
      btnCancel.setBackground(new Color(239, 239, 239));
      btnCancel.setForeground(new Color(10, 10, 10));
      btnCancel.setBorder(new javax.swing.border.EmptyBorder(8, 20, 8, 20));
      btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JButton btnConf = new JButton("Aceptar");
      btnConf.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnConf.setFocusPainted(false);
      btnConf.setBackground(new Color(0, 149, 246));
      btnConf.setForeground(Color.WHITE);
      btnConf.setBorder(new javax.swing.border.EmptyBorder(8, 20, 8, 20));
      btnConf.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnCancel.addActionListener(e -> {
         layeredPane.remove(modalBg);
         layeredPane.repaint();
      });
      btnConf.addActionListener(e -> {
         layeredPane.remove(modalBg);
         layeredPane.repaint();
         if (onConfirm != null)
            onConfirm.accept(txtInput.getText());
      });
      txtInput.addActionListener(e -> btnConf.doClick());
      btns.add(btnCancel);
      btns.add(btnConf);
      dialog.add(btns);
      modalBg.add(dialog);
      layeredPane.add(modalBg, JLayeredPane.MODAL_LAYER);
      layeredPane.moveToFront(modalBg);
      layeredPane.revalidate();
      layeredPane.repaint();
      SwingUtilities.invokeLater(txtInput::requestFocusInWindow);
   }
   public void mostrarListaUsuariosModal(String titulo, java.util.List<String> usernames) {
      JPanel modalBg = new JPanel(new GridBagLayout());
      modalBg.setBackground(new Color(0, 0, 0, 140));
      modalBg.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
      modalBg.addMouseListener(new EmptyMouseHandler());
      JPanel dialog = new JPanel();
      dialog.setLayout(new BorderLayout());
      dialog.setBackground(Color.WHITE);
      dialog.setPreferredSize(new Dimension(400, 500));
      dialog.setBorder(new javax.swing.border.LineBorder(new Color(219, 219, 219), 1));
      JPanel header = new JPanel(new BorderLayout());
      header.setBackground(Color.WHITE);
      header.setBorder(new javax.swing.border.EmptyBorder(12, 16, 12, 16));
      JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
      lblTit.setFont(new Font("SansSerif", Font.BOLD, 16));
      header.add(lblTit, BorderLayout.CENTER);
      JButton btnClose = new JButton("✕");
      btnClose.setFont(new Font("SansSerif", Font.PLAIN, 18));
      btnClose.setBorder(null);
      btnClose.setBackground(Color.WHITE);
      btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnClose.addActionListener(e -> {
         layeredPane.remove(modalBg);
         layeredPane.repaint();
      });
      header.add(btnClose, BorderLayout.EAST);
      dialog.add(header, BorderLayout.NORTH);
      JSeparator sep = new JSeparator();
      sep.setForeground(new Color(219, 219, 219));
      header.add(sep, BorderLayout.SOUTH);
      JPanel listPanel = new JPanel();
      listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
      listPanel.setBackground(Color.WHITE);
      if (usernames == null || usernames.isEmpty()) {
         JLabel empty = new JLabel("No hay usuarios para mostrar.", SwingConstants.CENTER);
         empty.setBorder(new javax.swing.border.EmptyBorder(30, 0, 30, 0));
         empty.setAlignmentX(Component.CENTER_ALIGNMENT);
         listPanel.add(empty);
      } else {
         for (String uname : usernames) {
            listPanel.add(createUserRowModal(uname, modalBg));
         }
      }
      JScrollPane scroll = new JScrollPane(listPanel);
      scroll.setBorder(null);
      scroll.getVerticalScrollBar().setUnitIncrement(12);
      dialog.add(scroll, BorderLayout.CENTER);
      modalBg.add(dialog);
      layeredPane.add(modalBg, JLayeredPane.MODAL_LAYER);
      layeredPane.moveToFront(modalBg);
      layeredPane.revalidate();
      layeredPane.repaint();
   }
   private JPanel createUserRowModal(String uname, JPanel modalToClose) {
      JPanel row = new JPanel(new BorderLayout(15, 0));
      row.setBackground(Color.WHITE);
      row.setBorder(new javax.swing.border.EmptyBorder(10, 15, 10, 15));
      row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
      row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      try {
         Usuario u = gestorUsuarios.buscarExacto(uname);
         if (u != null) {
            JLabel lblFoto = new JLabel();
            Icon foto = SidebarPanel.cargarFotoCircular(u.getRutaFotoPerfil(), 44);
            if (foto != null)
               lblFoto.setIcon(foto);
            else {
               lblFoto.setText("👤");
               lblFoto.setFont(new Font("SansSerif", Font.PLAIN, 28));
            }
            row.add(lblFoto, BorderLayout.WEST);
            JPanel info = new JPanel(new GridLayout(2, 1));
            info.setBackground(Color.WHITE);
            JLabel lblU = new JLabel("@" + u.getUsername());
            lblU.setFont(new Font("SansSerif", Font.BOLD, 14));
            JLabel lblN = new JLabel(u.getNombreCompleto());
            lblN.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblN.setForeground(Color.GRAY);
            info.add(lblU);
            info.add(lblN);
            row.add(info, BorderLayout.CENTER);
            row.addMouseListener(new UserRowMouseHandler(row, u, modalToClose, info));
         } else {
            row.add(new JLabel("@" + uname + " (No encontrado)"), BorderLayout.CENTER);
         }
      } catch (IOException ex) {
         row.add(new JLabel("Error"), BorderLayout.CENTER);
      }
      return row;
   }
   public static class Screens {
      public static int W() {
         return insta.storage.AppPaths.getWindowWidth();
      }
      public static int H() {
         return insta.storage.AppPaths.getWindowHeight();
      }
      public static final String LOGIN = "LOGIN";
      public static final String REGISTER = "REGISTER";
      public static final String FEED = "FEED";
      public static final String PERFIL = "PERFIL";
      public static final String INBOX = "INBOX";
      public static final String EXPLORAR = "EXPLORAR";
      public static final String INTERACCIONES = "INTERACCIONES";
   }
   
   private static class ToastPanel extends JPanel {
      public ToastPanel(String mensaje) {
         setOpaque(false);
         setLayout(new BorderLayout());
         JLabel lbl = new JLabel(mensaje, SwingConstants.CENTER);
         lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
         lbl.setForeground(Color.WHITE);
         lbl.setBorder(new javax.swing.border.EmptyBorder(12, 24, 12, 24));
         add(lbl, BorderLayout.CENTER);
      }
      @Override
      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D) g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setColor(new Color(30, 30, 30, 220));
         g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
         g2.dispose();
         super.paintComponent(g);
      }
   }

   private class MainResizeHandler extends java.awt.event.ComponentAdapter {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
         int w = layeredPane.getWidth();
         int h = layeredPane.getHeight();
         if (sidebar != null)
            sidebar.setBounds(0, 0, sidebar.getPreferredSize().width, h);
         contentWrapper.setBounds(72, 0, w - 72, h);
         if (overlayActivo != null)
            overlayActivo.setBounds(72, 0, 397, h);
      }
   }

   private class SidebarResizeHandler extends java.awt.event.ComponentAdapter {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
         for (java.awt.event.ComponentListener cl : layeredPane.getComponentListeners()) {
            cl.componentResized(new java.awt.event.ComponentEvent(layeredPane,
                  java.awt.event.ComponentEvent.COMPONENT_RESIZED));
         }
      }
   }

   private static class EmptyMouseHandler extends java.awt.event.MouseAdapter {
   }

   private class UserRowMouseHandler extends java.awt.event.MouseAdapter {
      private final JPanel row;
      private final Usuario u;
      private final JPanel modal;
      private final JPanel info;

      public UserRowMouseHandler(JPanel row, Usuario u, JPanel modal, JPanel info) {
         this.row = row;
         this.u = u;
         this.modal = modal;
         this.info = info;
      }

      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
         layeredPane.remove(modal);
         layeredPane.repaint();
         mostrarPerfilDe(u.getUsername());
      }

      @Override
      public void mouseEntered(java.awt.event.MouseEvent e) {
         row.setBackground(new Color(250, 250, 250));
         info.setBackground(new Color(250, 250, 250));
      }

      @Override
      public void mouseExited(java.awt.event.MouseEvent e) {
         row.setBackground(Color.WHITE);
         info.setBackground(Color.WHITE);
      }
   }
}
