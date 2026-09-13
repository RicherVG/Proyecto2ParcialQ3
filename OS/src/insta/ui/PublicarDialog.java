/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;

import insta.model.Publicacion;
import insta.model.Usuario;
import insta.storage.AppPaths;
import insta.storage.FileUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author riche
 */
public class PublicarDialog extends DialogoBase {
   private final MainFrame frame;
   private final CardLayout cardLayout = new CardLayout();
   private final JPanel contentPanel = new JPanel(cardLayout);
   private JTextArea txtContenido;
   private JTextField txtHashtags;
   private JLabel lblMencionesSeleccionadas;
   private JLabel lblContador;
   private JLabel imgPreviewDetail;
   private JComboBox<String> cmbFolders;
   private JComboBox<String> cmbStickers;
   private List<insta.model.Sticker> listaStickers;
   private String rutaImagenSeleccionada = "";
   private final Set<String> mencionesSeleccionadas = new HashSet<>();

   public PublicarDialog(MainFrame frame) {
      super((java.awt.Frame) SwingUtilities.getWindowAncestor(frame), "Crear nueva publicación", true);
      this.frame = frame;
      setSize(520, 640);
      setLocationRelativeTo(frame);
      setResizable(false);
      buildUI();
   }

   private void buildUI() {
      contentPanel.setBackground(Color.WHITE);
      contentPanel.add(buildSelectionPanel(), "SELECT");
      contentPanel.add(buildDetailsPanel(), "DETAILS");
      
      rootPanel.add(contentPanel, BorderLayout.CENTER);
      setContentPane(rootPanel);
      
      esconderBotones();
      
      cardLayout.show(contentPanel, "SELECT");
   }

   private JPanel buildSelectionPanel() {
      JPanel p = new JPanel(new GridBagLayout());
      p.setBackground(Color.WHITE);
      JPanel center = new JPanel();
      center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
      center.setBackground(Color.WHITE);
      JLabel icon = new JLabel("📷");
      icon.setFont(new Font("SansSerif", Font.PLAIN, 64));
      icon.setAlignmentX(Component.CENTER_ALIGNMENT);
      JLabel hint = new JLabel("Arrastra las fotos y los vídeos aquí");
      hint.setFont(new Font("SansSerif", Font.PLAIN, 20));
      hint.setAlignmentX(Component.CENTER_ALIGNMENT);
      hint.setBorder(new EmptyBorder(20, 0, 20, 0));
      JButton btnSelect = new JButton("Seleccionar del ordenador");
      btnSelect.setBackground(new Color(0, 149, 246));
      btnSelect.setForeground(Color.WHITE);
      btnSelect.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnSelect.setFocusPainted(false);
      btnSelect.setBorder(new EmptyBorder(8, 16, 8, 16));
      btnSelect.setCursor(new Cursor(Cursor.HAND_CURSOR));
      btnSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
      btnSelect.addActionListener(e -> seleccionarImagen());
      JLabel lblLoading = new JLabel("Cargando imagen...");
      lblLoading.setFont(new Font("SansSerif", Font.ITALIC, 14));
      lblLoading.setForeground(new Color(142, 142, 142));
      lblLoading.setAlignmentX(Component.CENTER_ALIGNMENT);
      lblLoading.setVisible(false);
      center.add(icon);
      center.add(hint);
      center.add(btnSelect);
      center.add(Box.createVerticalStrut(10));
      center.add(lblLoading);
      p.putClientProperty("lblLoading", lblLoading);
      p.putClientProperty("btnSelect", btnSelect);
      p.putClientProperty("hint", hint);
      p.putClientProperty("icon", icon);
      p.add(center);
      p.setDropTarget(new DropTarget(p, DnDConstants.ACTION_COPY, new PostDropTargetHandler()));
      return p;
   }

   private JPanel buildDetailsPanel() {
      JPanel p = new JPanel(new BorderLayout());
      p.setBackground(Color.WHITE);
      imgPreviewDetail = new JLabel();
      imgPreviewDetail.setHorizontalAlignment(SwingConstants.CENTER);
      imgPreviewDetail.setBackground(new Color(250, 250, 250));
      imgPreviewDetail.setOpaque(true);
      imgPreviewDetail.setPreferredSize(new Dimension(500, 500));
      JPanel infoScrollContent = new JPanel();
      infoScrollContent.setLayout(new BoxLayout(infoScrollContent, BoxLayout.Y_AXIS));
      infoScrollContent.setBackground(Color.WHITE);
      infoScrollContent.setBorder(new EmptyBorder(16, 16, 16, 16));
      Usuario u = frame.getSession();
      JPanel userHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
      userHeader.setBackground(Color.WHITE);
      JLabel lblAvatar = new JLabel(SidebarPanel.cargarFotoCircular(u.getRutaFotoPerfil(), 28));
      JLabel lblUser = new JLabel(u.getUsername());
      lblUser.setFont(new Font("SansSerif", Font.BOLD, 14));
      userHeader.add(lblAvatar);
      userHeader.add(lblUser);
      userHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
      infoScrollContent.add(userHeader);
      infoScrollContent.add(Box.createVerticalStrut(12));
      txtContenido = new JTextArea(6, 20);
      txtContenido.setFont(new Font("SansSerif", Font.PLAIN, 14));
      txtContenido.setLineWrap(true);
      txtContenido.setWrapStyleWord(true);
      txtContenido.setBorder(null);
      txtContenido.setMargin(new Insets(0, 0, 0, 0));
      PlaceholderFocusListener.addPlaceholder(txtContenido, "Escribe un pie de foto...");
      JScrollPane scrollCaption = new JScrollPane(txtContenido);
      scrollCaption.setBorder(null);
      scrollCaption.setAlignmentX(Component.LEFT_ALIGNMENT);
      infoScrollContent.add(scrollCaption);
      lblContador = new JLabel("0 / 220");
      lblContador.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblContador.setForeground(new Color(199, 199, 199));
      lblContador.setAlignmentX(Component.LEFT_ALIGNMENT);
      infoScrollContent.add(lblContador);
      txtContenido.getDocument().addDocumentListener(new CaptionCounterListener(txtContenido, lblContador));
      infoScrollContent.add(Box.createVerticalStrut(12));
      infoScrollContent.add(new JSeparator());
      infoScrollContent.add(Box.createVerticalStrut(10));
      
      JLabel lblFolder = new JLabel("Carpeta personal");
      lblFolder.setFont(new Font("SansSerif", Font.BOLD, 13));
      lblFolder.setAlignmentX(Component.LEFT_ALIGNMENT);
      infoScrollContent.add(lblFolder);
      infoScrollContent.add(Box.createVerticalStrut(6));
      JPanel folderPanel = new JPanel(new BorderLayout(8, 0));
      folderPanel.setBackground(Color.WHITE);
      folderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      folderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
      cmbFolders = new JComboBox<>();
      cmbFolders.setFont(new Font("SansSerif", Font.PLAIN, 13));
      cargarCarpetasPersonales();
      JButton btnNuevaCarpeta = new JButton("+ Nueva");
      btnNuevaCarpeta.setBackground(new Color(239, 239, 239));
      btnNuevaCarpeta.setFont(new Font("SansSerif", Font.PLAIN, 12));
      btnNuevaCarpeta.setFocusPainted(false);
      btnNuevaCarpeta.addActionListener(e -> crearNuevaCarpeta());
      folderPanel.add(cmbFolders, BorderLayout.CENTER);
      folderPanel.add(btnNuevaCarpeta, BorderLayout.EAST);
      infoScrollContent.add(folderPanel);
      infoScrollContent.add(Box.createVerticalStrut(12));

      JLabel lblSticker = new JLabel("Adjuntar Sticker");
      lblSticker.setFont(new Font("SansSerif", Font.BOLD, 13));
      lblSticker.setAlignmentX(Component.LEFT_ALIGNMENT);
      infoScrollContent.add(lblSticker);
      infoScrollContent.add(Box.createVerticalStrut(6));
      cmbStickers = new JComboBox<>();
      cmbStickers.setFont(new Font("SansSerif", Font.PLAIN, 13));
      cmbStickers.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
      cmbStickers.setAlignmentX(Component.LEFT_ALIGNMENT);
      cargarStickers();
      infoScrollContent.add(cmbStickers);
      infoScrollContent.add(Box.createVerticalStrut(12));

      JLabel lblH = new JLabel("Hashtags");
      lblH.setFont(new Font("SansSerif", Font.BOLD, 13));
      lblH.setAlignmentX(Component.LEFT_ALIGNMENT);
      infoScrollContent.add(lblH);
      txtHashtags = crearCampoLimpio("#viajes #amigos...");
      infoScrollContent.add(txtHashtags);
      infoScrollContent.add(Box.createVerticalStrut(12));
      JLabel lblM = new JLabel("Personas etiquetadas");
      lblM.setFont(new Font("SansSerif", Font.BOLD, 13));
      lblM.setAlignmentX(Component.LEFT_ALIGNMENT);
      infoScrollContent.add(lblM);
      infoScrollContent.add(Box.createVerticalStrut(6));
      lblMencionesSeleccionadas = new JLabel("Ninguna");
      lblMencionesSeleccionadas.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblMencionesSeleccionadas.setForeground(new Color(115, 115, 115));
      lblMencionesSeleccionadas.setAlignmentX(Component.LEFT_ALIGNMENT);
      infoScrollContent.add(lblMencionesSeleccionadas);
      infoScrollContent.add(Box.createVerticalStrut(6));
      JButton btnEtiquetar = new JButton("Etiquetar personas →");
      btnEtiquetar.setBackground(new Color(239, 239, 239));
      btnEtiquetar.setForeground(new Color(10, 10, 10));
      btnEtiquetar.setFont(new Font("SansSerif", Font.PLAIN, 13));
      btnEtiquetar.setFocusPainted(false);
      btnEtiquetar.setBorderPainted(false);
      btnEtiquetar.setOpaque(true);
      btnEtiquetar.setBorder(new EmptyBorder(7, 12, 7, 12));
      btnEtiquetar.setCursor(new Cursor(Cursor.HAND_CURSOR));
      btnEtiquetar.setAlignmentX(Component.LEFT_ALIGNMENT);
      btnEtiquetar.addActionListener(e -> abrirPickerMenciones());
      infoScrollContent.add(btnEtiquetar);
      JScrollPane scrollRight = new JScrollPane(infoScrollContent);
      scrollRight.setBorder(null);
      scrollRight.getVerticalScrollBar().setUnitIncrement(12);
      JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imgPreviewDetail, scrollRight);
      split.setDividerLocation(500);
      split.setDividerSize(1);
      split.setBorder(null);
      split.setEnabled(false);
      p.add(split, BorderLayout.CENTER);
      return p;
   }

   private void cargarCarpetasPersonales() {
      cmbFolders.removeAllItems();
      cmbFolders.addItem("General");
      try {
         String miUser = frame.getSession().getUsername();
         File baseDir = AppPaths.foldersPersonalesDir(miUser).toFile();
         if (!baseDir.exists()) {
            baseDir.mkdirs();
         }
         File[] subdirs = baseDir.listFiles(File::isDirectory);
         if (subdirs != null) {
            for (File d : subdirs) {
               if (!"General".equalsIgnoreCase(d.getName())) {
                  cmbFolders.addItem(d.getName());
               }
            }
         }
      } catch (Exception ignored) {
      }
   }

   private void crearNuevaCarpeta() {
      String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva carpeta:", "Nueva carpeta", JOptionPane.PLAIN_MESSAGE);
      if (nombre != null && !nombre.trim().isEmpty()) {
         nombre = nombre.trim().replaceAll("[^a-zA-Z0-9_.-]", "_");
         try {
            String miUser = frame.getSession().getUsername();
            File nuevaDir = AppPaths.foldersPersonalesDir(miUser).resolve(nombre).toFile();
            if (!nuevaDir.exists()) {
               nuevaDir.mkdirs();
            }
            cargarCarpetasPersonales();
            cmbFolders.setSelectedItem(nombre);
         } catch (Exception ignored) {
         }
      }
   }

   private void cargarStickers() {
      cmbStickers.removeAllItems();
      cmbStickers.addItem("Ninguno");
      try {
         String miUser = frame.getSession().getUsername();
         listaStickers = frame.getGestorStickers().getTodosDisponibles(miUser);
         for (insta.model.Sticker s : listaStickers) {
            cmbStickers.addItem(s.getNombre());
         }
      } catch (Exception ignored) {
      }
   }

   private void abrirPickerMenciones() {
      JDialog picker = new JDialog(this, "Etiquetar personas", true);
      picker.setSize(380, 520);
      picker.setLocationRelativeTo(this);
      picker.setResizable(false);
      JPanel root = new JPanel(new BorderLayout());
      root.setBackground(Color.WHITE);
      JPanel pickerHeader = new JPanel(new BorderLayout());
      pickerHeader.setBackground(Color.WHITE);
      pickerHeader.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(219, 219, 219)),
            new EmptyBorder(10, 14, 10, 14)));
      JLabel pickerTitulo = new JLabel("Etiquetar personas", SwingConstants.CENTER);
      pickerTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
      pickerHeader.add(pickerTitulo, BorderLayout.CENTER);
      root.add(pickerHeader, BorderLayout.NORTH);
      JTextField txtBuscar = new JTextField();
      txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 13));
      txtBuscar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(219, 219, 219)),
            new EmptyBorder(8, 14, 8, 14)));
      PlaceholderFocusListener.addPlaceholder(txtBuscar, "Buscar...");
      root.add(txtBuscar, BorderLayout.NORTH);
      JPanel listaPanel = new JPanel();
      listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
      listaPanel.setBackground(Color.WHITE);
      JScrollPane listScroll = new JScrollPane(listaPanel);
      listScroll.setBorder(null);
      listScroll.getVerticalScrollBar().setUnitIncrement(10);
      List<Usuario> candidatos = cargarCandidatosMenciones();
      List<JCheckBox> checkboxes = new ArrayList<>();
      for (Usuario cu : candidatos) {
         JPanel fila = new JPanel(new BorderLayout(10, 0));
         fila.setBackground(Color.WHITE);
         fila.setBorder(new EmptyBorder(6, 14, 6, 14));
         fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
         Icon foto = SidebarPanel.cargarFotoCircular(cu.getRutaFotoPerfil(), 36);
         JLabel avatar = new JLabel(foto != null ? foto : null);
         if (foto == null) {
            avatar.setText("👤");
            avatar.setFont(new Font("SansSerif", Font.PLAIN, 22));
         }
         avatar.setPreferredSize(new Dimension(36, 36));
         fila.add(avatar, BorderLayout.WEST);
         JPanel info = new JPanel();
         info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
         info.setBackground(Color.WHITE);
         JLabel lblUsername = new JLabel("@" + cu.getUsername());
         lblUsername.setFont(new Font("SansSerif", Font.BOLD, 13));
         JLabel lblNombre = new JLabel(cu.getNombreCompleto());
         lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 11));
         lblNombre.setForeground(Color.GRAY);
         info.add(lblUsername);
         info.add(lblNombre);
         fila.add(info, BorderLayout.CENTER);
         JCheckBox cb = new JCheckBox();
         cb.setBackground(Color.WHITE);
         cb.setSelected(mencionesSeleccionadas.contains(cu.getUsername()));
         cb.putClientProperty("username", cu.getUsername());
         checkboxes.add(cb);
         fila.add(cb, BorderLayout.EAST);
         fila.addMouseListener(new MentionRowMouseHandler(fila, info, cb));
         listaPanel.add(fila);
      }
      txtBuscar.getDocument().addDocumentListener(new MentionFilterListener(txtBuscar, listaPanel));
      JPanel north = new JPanel(new BorderLayout());
      north.add(pickerHeader, BorderLayout.NORTH);
      north.add(txtBuscar, BorderLayout.CENTER);
      root.add(north, BorderLayout.NORTH);
      root.add(listScroll, BorderLayout.CENTER);
      JButton btnListo = new JButton("Listo");
      btnListo.setBackground(new Color(0, 149, 246));
      btnListo.setForeground(Color.WHITE);
      btnListo.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnListo.setFocusPainted(false);
      btnListo.setBorderPainted(false);
      btnListo.setOpaque(true);
      btnListo.setBorder(new EmptyBorder(10, 0, 10, 0));
      btnListo.setCursor(new Cursor(Cursor.HAND_CURSOR));
      btnListo.addActionListener(e -> {
         mencionesSeleccionadas.clear();
         for (JCheckBox cb : checkboxes) {
            if (cb.isSelected())
               mencionesSeleccionadas.add((String) cb.getClientProperty("username"));
         }
         actualizarLabelMenciones();
         picker.dispose();
      });
      JPanel south = new JPanel(new BorderLayout());
      south.setBorder(new MatteBorder(1, 0, 0, 0, new Color(219, 219, 219)));
      south.add(btnListo, BorderLayout.CENTER);
      root.add(south, BorderLayout.SOUTH);
      picker.setContentPane(root);
      picker.setVisible(true);
   }

   private void actualizarLabelMenciones() {
      if (mencionesSeleccionadas.isEmpty()) {
         lblMencionesSeleccionadas.setText("Ninguna");
      } else {
         StringBuilder sb = new StringBuilder();
         for (String u : mencionesSeleccionadas)
            sb.append("@").append(u).append("  ");
         lblMencionesSeleccionadas.setText(sb.toString().trim());
      }
   }

   private List<Usuario> cargarCandidatosMenciones() {
      List<Usuario> resultado = new ArrayList<>();
      Set<String> agregados = new HashSet<>();
      try {
         String myU = frame.getSession().getUsername();
         List<String> siguiendo = frame.getGestorFollowers().getFollowing(myU);
         for (String username : siguiendo) {
            Usuario u = frame.getGestorUsuarios().buscarExacto(username);
            if (u != null && !agregados.contains(u.getUsername().toLowerCase())) {
               resultado.add(u);
               agregados.add(u.getUsername().toLowerCase());
            }
         }
         for (Usuario u : frame.getGestorUsuarios().cargarTodos()) {
            if (!agregados.contains(u.getUsername().toLowerCase())
                  && !u.getUsername().equalsIgnoreCase(myU)
                  && u.isActivo()
                  && "PUBLICA".equalsIgnoreCase(u.getTipoCuenta())) {
               resultado.add(u);
               agregados.add(u.getUsername().toLowerCase());
            }
         }
      } catch (IOException ignored) {
      }
      return resultado;
   }

   private JTextField crearCampoLimpio(String placeholder) {
      JTextField tf = new JTextField();
      tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
      tf.setBorder(new MatteBorder(0, 0, 1, 0, new Color(239, 239, 239)));
      tf.setMargin(new Insets(5, 0, 5, 0));
      tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
      PlaceholderFocusListener.addPlaceholder(tf, placeholder);
      tf.setAlignmentX(Component.LEFT_ALIGNMENT);
      return tf;
   }

   private void seleccionarImagen() {
      JFileChooser fc = new JFileChooser();
      fc.setDialogTitle("Seleccionar imagen");
      fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Imágenes (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
      if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
         procesarArchivoImagen(fc.getSelectedFile());
      }
   }

   private void procesarArchivoImagen(File file) {
      if (file == null || !file.exists())
         return;
      JPanel selPanel = (JPanel) contentPanel.getComponent(0);
      ((JLabel) selPanel.getClientProperty("lblLoading")).setVisible(true);
      ((JButton) selPanel.getClientProperty("btnSelect")).setEnabled(false);
      ((JLabel) selPanel.getClientProperty("hint")).setText("Procesando...");
      selPanel.revalidate();
      selPanel.repaint();
      rutaImagenSeleccionada = file.getAbsolutePath();
      new ImageProcessorWorker(file, selPanel).execute();
   }

   private void showDetailsStage() {
      setSize(850, 650);
      setLocationRelativeTo(frame);
      setTitulo("Previsualizar y detalles");
      
      configurarBotonVolver(e -> resetToSelection());
      configurarBotonAccion("Compartir", e -> publicar());
      
      cardLayout.show(contentPanel, "DETAILS");
      txtContenido.requestFocusInWindow();
   }

   private void resetToSelection() {
      setSize(520, 640);
      setLocationRelativeTo(frame);
      setTitulo("Crear nueva publicación");
      esconderBotones();
      mencionesSeleccionadas.clear();
      rutaImagenSeleccionada = "";
      cardLayout.show(contentPanel, "SELECT");
   }

   private void publicar() {
      String contenido = txtContenido.getText().trim();
      if (contenido.equals("Escribe un pie de foto..."))
         contenido = "";
      if (cmbStickers != null && cmbStickers.getSelectedIndex() > 0) {
         String stickerNombre = (String) cmbStickers.getSelectedItem();
         contenido = (contenido.isEmpty() ? "" : contenido + " ") + "[Sticker: " + stickerNombre + "]";
      }
      String hashtags = txtHashtags.getText().trim();
      if (hashtags.equals("#viajes #amigos..."))
         hashtags = "";
      StringBuilder mencionesValidadas = new StringBuilder();
      for (String username : mencionesSeleccionadas) {
         try {
            if (frame.getGestorUsuarios().buscarExacto(username) != null) {
               mencionesValidadas.append("@").append(username).append(" ");
            } else {
               frame.mostrarToast("Usuario @" + username + " no existe, se omitirá.");
            }
         } catch (IOException ignored) {
         }
      }
      String menciones = mencionesValidadas.toString().trim();
      String miUser = frame.getSession().getUsername();
      if (rutaImagenSeleccionada.isEmpty()) {
         frame.mostrarToast("Selecciona una imagen primero.");
         return;
      }
      String rutaImagen = "";
      try {
         File orig = new File(rutaImagenSeleccionada);
         String folderSel = cmbFolders != null ? (String) cmbFolders.getSelectedItem() : "General";
         Path destDir;
         if (folderSel != null && !"General".equalsIgnoreCase(folderSel)) {
            destDir = AppPaths.foldersPersonalesDir(miUser).resolve(folderSel);
         } else {
            destDir = AppPaths.imagesDir(miUser);
         }
         if (!destDir.toFile().exists()) {
            destDir.toFile().mkdirs();
         }
         Path dest = destDir.resolve("post_" + System.currentTimeMillis() + "_" + orig.getName());
         FileUtils.copiarArchivo(orig.toPath(), dest);
         rutaImagen = dest.toString();
      } catch (IOException ex) {
         frame.mostrarToast("Error guardando imagen: " + ex.getMessage());
         return;
      }
      if (contenido.length() > 220) {
         frame.mostrarToast("El texto no puede superar los 220 caracteres.");
         return;
      }
      Publicacion p = new Publicacion(miUser, contenido, hashtags, menciones, rutaImagen, "IMAGEN");
      try {
         frame.getGestorPublicaciones().publicar(p);
         frame.mostrarToast("¡Se ha compartido tu publicación!");
         dispose();
         frame.showScreen(MainFrame.Screens.FEED);
      } catch (IOException ex) {
         frame.mostrarToast("Error: " + ex.getMessage());
      }
   }

   private class PostDropTargetHandler extends DropTargetAdapter {
      @Override
      @SuppressWarnings("unchecked")
      public void drop(DropTargetDropEvent event) {
         try {
            event.acceptDrop(DnDConstants.ACTION_COPY);
            List<File> droppedFiles = (List<File>) event.getTransferable()
                  .getTransferData(DataFlavor.javaFileListFlavor);
            if (droppedFiles != null && !droppedFiles.isEmpty()) {
               procesarArchivoImagen(droppedFiles.get(0));
            }
         } catch (Exception ex) { ex.printStackTrace(); }
      }
   }

   private static class CaptionCounterListener implements javax.swing.event.DocumentListener {
      private final JTextArea textArea;
      private final JLabel label;
      public CaptionCounterListener(JTextArea ta, JLabel l) { this.textArea = ta; this.label = l; }
      public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
      public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
      public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
      private void update() {
         String text = textArea.getText();
         if (text.equals("Escribe un pie de foto...")) text = "";
         label.setText(text.length() + " / 220");
      }
   }

   private static class MentionRowMouseHandler extends java.awt.event.MouseAdapter {
      private final JPanel fila, info;
      private final JCheckBox cb;
      public MentionRowMouseHandler(JPanel f, JPanel i, JCheckBox c) {
         this.fila = f; this.info = i; this.cb = c;
      }
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) { cb.setSelected(!cb.isSelected()); }
      @Override
      public void mouseEntered(java.awt.event.MouseEvent e) {
         Color h = new Color(245, 245, 245);
         fila.setBackground(h); info.setBackground(h); cb.setBackground(h);
      }
      @Override
      public void mouseExited(java.awt.event.MouseEvent e) {
         fila.setBackground(Color.WHITE); info.setBackground(Color.WHITE); cb.setBackground(Color.WHITE);
      }
   }

   private static class MentionFilterListener implements javax.swing.event.DocumentListener {
      private final JTextField searchField;
      private final JPanel listPanel;
      public MentionFilterListener(JTextField sf, JPanel lp) { this.searchField = sf; this.listPanel = lp; }
      public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
      public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
      public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
      private void filter() {
         String q = searchField.getText().toLowerCase().trim();
         if (q.equals("buscar...")) q = "";
         for (int i = 0; i < listPanel.getComponentCount(); i++) {
            JPanel f = (JPanel) listPanel.getComponent(i);
            JPanel info = (JPanel) f.getComponent(1);
            String uname = ((JLabel) info.getComponent(0)).getText().toLowerCase();
            f.setVisible(q.isEmpty() || uname.contains(q));
         }
         listPanel.revalidate(); listPanel.repaint();
      }
   }

   private class ImageProcessorWorker extends SwingWorker<ImageIcon, Void> {
      private final File file;
      private final JPanel selPanel;
      public ImageProcessorWorker(File f, JPanel sp) { this.file = f; this.selPanel = sp; }
      @Override
      protected ImageIcon doInBackground() throws Exception {
         BufferedImage bimg = ImageIO.read(file);
         if (bimg == null) return null;
         BufferedImage scaled = SidebarPanel.getHighQualityScaledInstance(bimg, 500, 500);
         return new ImageIcon(scaled);
      }
      @Override
      protected void done() {
         try {
            ImageIcon res = get();
            if (res != null) {
               imgPreviewDetail.setIcon(res);
               showDetailsStage();
            } else {
               frame.mostrarToast("No se pudo leer la imagen.");
               resetToSelection();
            }
         } catch (Exception ex) {
            frame.mostrarToast("Error: " + ex.getMessage());
            resetToSelection();
         } finally {
            ((JLabel) selPanel.getClientProperty("lblLoading")).setVisible(false);
            ((JButton) selPanel.getClientProperty("btnSelect")).setEnabled(true);
            ((JLabel) selPanel.getClientProperty("hint")).setText("Arrastra las fotos y los vídeos aquí");
         }
      }
   }
}
class PlaceholderFocusListener extends java.awt.event.FocusAdapter {
   private final javax.swing.text.JTextComponent component;
   private final String placeholder;
   private final Color placeholderColor = new Color(199, 199, 199);
   private final Color textColor = Color.BLACK;
   private PlaceholderFocusListener(javax.swing.text.JTextComponent comp, String ph) {
      this.component = comp;
      this.placeholder = ph;
   }
   public static void addPlaceholder(javax.swing.text.JTextComponent comp, String ph) {
      PlaceholderFocusListener listener = new PlaceholderFocusListener(comp, ph);
      comp.addFocusListener(listener);
      comp.setForeground(listener.placeholderColor);
      comp.setText(ph);
   }
   @Override
   public void focusGained(java.awt.event.FocusEvent e) {
      if (component.getText().equals(placeholder)) {
         component.setText("");
         component.setForeground(textColor);
      }
   }
   @Override
   public void focusLost(java.awt.event.FocusEvent e) {
      if (component.getText().isEmpty()) {
         component.setText(placeholder);
         component.setForeground(placeholderColor);
      }
   }
}
