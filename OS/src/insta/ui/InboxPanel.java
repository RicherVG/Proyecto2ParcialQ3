/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;
import insta.model.Mensaje;
import insta.model.Sticker;
import insta.model.Usuario;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
/**
 *
 * @author riche
 */
public class InboxPanel extends JPanel {
   private final MainFrame frame;
   private String conversacionActual = null;
   private JPanel listaConvPanel;
   private CardLayout derechoCard;
   private JPanel derechoPanel;
   private static final String CARD_VACIO = "VACIO";
   private static final String CARD_CHAT = "CHAT";
   private JPanel mensajesPanel;
   private JScrollPane scrollMensajes;
   private JTextField txtMensaje;
   private JLabel lblChatHeader; 
   public InboxPanel(MainFrame frame) {
      this.frame = frame;
      setLayout(new BorderLayout());
      setBackground(Color.WHITE);
      buildUI();
      configurarSocketListener();
   }
   private void configurarSocketListener() {
      Timer checkSocket = new Timer(1000, e -> {
         if (frame.getClienteSocket() != null) {
            frame.getClienteSocket().setOnMensajeRecibido(msg -> {
               SwingUtilities.invokeLater(() -> {
                  String miUser = frame.getSession().getUsername();
                  if (msg.getReceptor().equalsIgnoreCase(miUser) || msg.getEmisor().equalsIgnoreCase(miUser)) {
                     String otro = msg.getEmisor().equalsIgnoreCase(miUser) ? msg.getReceptor() : msg.getEmisor();
                     if (conversacionActual != null && conversacionActual.equalsIgnoreCase(otro)) {
                        cargarMensajesConversacion(conversacionActual);
                     }
                     cargarConversaciones();
                     frame.refreshSidebar();
                  }
               });
            });
            ((Timer)e.getSource()).stop();
         }
      });
      checkSocket.start();
   }
   private void buildUI() {
      JPanel izquierdo = new JPanel(new BorderLayout());
      izquierdo.setPreferredSize(new Dimension(320, 0));
      izquierdo.setBackground(Color.WHITE);
      izquierdo.setBorder(new MatteBorder(0, 0, 0, 1, new Color(219, 219, 219)));
      JPanel headerIzq = new JPanel(new BorderLayout());
      headerIzq.setBackground(Color.WHITE);
      headerIzq.setBorder(new EmptyBorder(18, 20, 14, 16));
      String username = frame.getSession() != null ? frame.getSession().getUsername() : "Mensajes";
      JLabel lblTitulo = new JLabel("@" + username);
      lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
      lblTitulo.setForeground(new Color(10, 10, 10));
      headerIzq.add(lblTitulo, BorderLayout.CENTER);
      JLabel icoNuevo = new JLabel("✏");
      icoNuevo.setFont(new Font("SansSerif", Font.PLAIN, 22));
      icoNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      icoNuevo.setToolTipText("Nueva conversación");
      icoNuevo.addMouseListener(new NewConvMouseHandler());
      headerIzq.add(icoNuevo, BorderLayout.EAST);
      izquierdo.add(headerIzq, BorderLayout.NORTH);
      listaConvPanel = new JPanel();
      listaConvPanel.setLayout(new BoxLayout(listaConvPanel, BoxLayout.Y_AXIS));
      listaConvPanel.setBackground(Color.WHITE);
      JScrollPane scrollConv = new JScrollPane(listaConvPanel);
      scrollConv.setBorder(null);
      scrollConv.getVerticalScrollBar().setUnitIncrement(12);
      izquierdo.add(scrollConv, BorderLayout.CENTER);
      add(izquierdo, BorderLayout.WEST);
      derechoCard = new CardLayout();
      derechoPanel = new JPanel(derechoCard);
      derechoPanel.setBackground(Color.WHITE);
      JPanel vacioCentrado = new JPanel(new GridBagLayout());
      vacioCentrado.setBackground(Color.WHITE);
      JPanel vacioCont = new JPanel();
      vacioCont.setLayout(new BoxLayout(vacioCont, BoxLayout.Y_AXIS));
      vacioCont.setBackground(Color.WHITE);
      JLabel iconoDM = new DMIconLabel();
      iconoDM.setPreferredSize(new Dimension(100, 100));
      iconoDM.setAlignmentX(CENTER_ALIGNMENT);
      JLabel lblVT = new JLabel("Tus mensajes");
      lblVT.setFont(new Font("SansSerif", Font.BOLD, 20));
      lblVT.setForeground(new Color(10, 10, 10));
      lblVT.setAlignmentX(CENTER_ALIGNMENT);
      JLabel lblVS = new JLabel("Envía fotos y mensajes privados a un amigo.");
      lblVS.setFont(new Font("SansSerif", Font.PLAIN, 14));
      lblVS.setForeground(new Color(115, 115, 115));
      lblVS.setAlignmentX(CENTER_ALIGNMENT);
      JButton btnEnviarMsg = new JButton("Enviar mensaje");
      btnEnviarMsg.setBackground(new Color(0, 149, 246));
      btnEnviarMsg.setForeground(Color.WHITE);
      btnEnviarMsg.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnEnviarMsg.setFocusPainted(false);
      btnEnviarMsg.setBorderPainted(false);
      btnEnviarMsg.setOpaque(true);
      btnEnviarMsg.setBorder(new EmptyBorder(10, 24, 10, 24));
      btnEnviarMsg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnEnviarMsg.setAlignmentX(CENTER_ALIGNMENT);
      btnEnviarMsg.addActionListener(e -> nuevaConversacion());
      vacioCont.add(iconoDM);
      vacioCont.add(Box.createVerticalStrut(20));
      vacioCont.add(lblVT);
      vacioCont.add(Box.createVerticalStrut(8));
      vacioCont.add(lblVS);
      vacioCont.add(Box.createVerticalStrut(16));
      vacioCont.add(btnEnviarMsg);
      vacioCentrado.add(vacioCont);
      derechoPanel.add(vacioCentrado, CARD_VACIO);
      JPanel chatCard = new JPanel(new BorderLayout());
      chatCard.setBackground(Color.WHITE);
      JPanel chatHeader = new JPanel(new BorderLayout());
      chatHeader.setBackground(Color.WHITE);
      chatHeader.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(219, 219, 219)),
            new EmptyBorder(14, 18, 14, 18)));
      lblChatHeader = new JLabel("", SwingConstants.LEFT);
      lblChatHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
      chatHeader.add(lblChatHeader, BorderLayout.CENTER);
      chatCard.add(chatHeader, BorderLayout.NORTH);
      mensajesPanel = new JPanel();
      mensajesPanel.setLayout(new BoxLayout(mensajesPanel, BoxLayout.Y_AXIS));
      mensajesPanel.setBackground(Color.WHITE);
      mensajesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
      scrollMensajes = new JScrollPane(mensajesPanel);
      scrollMensajes.setBorder(null);
      scrollMensajes.getVerticalScrollBar().setUnitIncrement(12);
      chatCard.add(scrollMensajes, BorderLayout.CENTER);
      JPanel barraEnvio = new JPanel(new BorderLayout(6, 0));
      barraEnvio.setBackground(Color.WHITE);
      barraEnvio.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(219, 219, 219)),
            new EmptyBorder(10, 14, 10, 14)));
      txtMensaje = new JTextField();
      txtMensaje.setFont(new Font("SansSerif", Font.PLAIN, 14));
      txtMensaje.setBackground(new Color(239, 239, 239));
      txtMensaje.setBorder(new CompoundBorder(
            new LineBorder(new Color(219, 219, 219), 1, true),
            new EmptyBorder(8, 14, 8, 14)));
      final String PLACEHOLDER_MSG = "Mensaje...";
      txtMensaje.setText(PLACEHOLDER_MSG);
      txtMensaje.setForeground(Color.GRAY);
      txtMensaje.addFocusListener(new MessagePlaceholderHandler(txtMensaje, PLACEHOLDER_MSG));
      txtMensaje.addActionListener(e -> enviarTexto());
      JButton btnSticker = new JButton("😊");
      btnSticker.setFont(new Font("SansSerif", Font.PLAIN, 20));
      btnSticker.setFocusPainted(false);
      btnSticker.setBorderPainted(false);
      btnSticker.setContentAreaFilled(false);
      btnSticker.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnSticker.setToolTipText("Stickers");
      btnSticker.addActionListener(e -> mostrarSelectorStickers());
      JButton btnFoto = new JButton("📸");
      btnFoto.setFont(new Font("SansSerif", Font.PLAIN, 18));
      btnFoto.setFocusPainted(false);
      btnFoto.setBorderPainted(false);
      btnFoto.setContentAreaFilled(false);
      btnFoto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnFoto.setToolTipText("Enviar foto");
      btnFoto.addActionListener(e -> enviarImagen());
      JPanel paletaIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      paletaIzquierda.setBackground(Color.WHITE);
      paletaIzquierda.add(btnSticker);
      paletaIzquierda.add(btnFoto);
      JButton btnEnviar = new JButton("Enviar");
      btnEnviar.setFont(new Font("SansSerif", Font.BOLD, 13));
      btnEnviar.setBackground(Color.WHITE);
      btnEnviar.setForeground(new Color(0, 149, 246));
      btnEnviar.setFocusPainted(false);
      btnEnviar.setBorderPainted(false);
      btnEnviar.setOpaque(true);
      btnEnviar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnEnviar.addActionListener(e -> enviarTexto());
      barraEnvio.add(paletaIzquierda, BorderLayout.WEST);
      barraEnvio.add(txtMensaje, BorderLayout.CENTER);
      barraEnvio.add(btnEnviar, BorderLayout.EAST);
      chatCard.add(barraEnvio, BorderLayout.SOUTH);
      derechoPanel.add(chatCard, CARD_CHAT);
      derechoCard.show(derechoPanel, CARD_VACIO);
      add(derechoPanel, BorderLayout.CENTER);
   }
   public void cargarConversaciones() {
      listaConvPanel.removeAll();
      String miUser = frame.getSession().getUsername();
      try {
         List<String> convs = frame.getGestorInbox().getConversaciones(miUser, frame.getGestorUsuarios());
         if (convs.isEmpty()) {
            JLabel vacio = new JLabel("Sin conversaciones.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 13));
            vacio.setForeground(Color.GRAY);
            vacio.setBorder(new EmptyBorder(16, 14, 0, 0));
            listaConvPanel.add(vacio);
         } else {
            for (String otro : convs) {
               listaConvPanel.add(crearItemConversacion(otro));
            }
         }
      } catch (IOException ex) {
         listaConvPanel.add(new JLabel("Error: " + ex.getMessage()));
      }
      listaConvPanel.revalidate();
      listaConvPanel.repaint();
   }
   public void abrirConversacionCon(String username) {
      cargarConversaciones(); 
      abrirConversacion(username); 
   }
   private JPanel crearItemConversacion(String otro) {
      JPanel item = new JPanel(new BorderLayout(10, 0));
      item.setBackground(Color.WHITE);
      item.setBorder(new EmptyBorder(10, 12, 10, 12));
      item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
      item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JLabel avatarLbl = new JLabel();
      avatarLbl.setPreferredSize(new Dimension(46, 46));
      avatarLbl.setIcon(buildAvatarIcon(otro, 46));
      item.add(avatarLbl, BorderLayout.WEST);
      JPanel center = new JPanel();
      center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
      center.setBackground(Color.WHITE);
      JLabel lblUser = new JLabel("@" + otro);
      lblUser.setFont(new Font("SansSerif", Font.BOLD, 14));
      lblUser.setForeground(new Color(10, 10, 10));
      center.add(lblUser);
      try {
         String miUser = frame.getSession().getUsername();
         List<Mensaje> msgs = frame.getGestorInbox().getMensajesConversacion(miUser, otro);
         if (!msgs.isEmpty()) {
            Mensaje ultimo = msgs.get(msgs.size() - 1);
            String preview = ultimo.getContenido();
            if (preview.length() > 36)
               preview = preview.substring(0, 34) + "…";
            JLabel lblPreview = new JLabel(preview);
            lblPreview.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblPreview.setForeground(new Color(140, 140, 140));
            center.add(lblPreview);
         }
      } catch (IOException ignored) {
      }
      item.add(center, BorderLayout.CENTER);
      JPanel eastPanel = new JPanel(new BorderLayout());
      eastPanel.setBackground(Color.WHITE);
      try {
         String miUser = frame.getSession().getUsername();
         List<Mensaje> msgs = frame.getGestorInbox().getMensajesConversacion(miUser, otro);
         boolean noLeido = msgs.stream()
               .anyMatch(m -> m.getReceptor().equalsIgnoreCase(miUser) && !m.isLeido());
         if (noLeido) {
            JLabel punto = new JLabel("●");
            punto.setForeground(new Color(0, 149, 246));
            punto.setFont(new Font("SansSerif", Font.BOLD, 12));
            eastPanel.add(punto, BorderLayout.NORTH);
         }
      } catch (IOException ignored) {
      }
      JButton btnDel = new JButton("✕");
      btnDel.setFont(new Font("SansSerif", Font.PLAIN, 10));
      btnDel.setForeground(Color.GRAY);
      btnDel.setBorderPainted(false);
      btnDel.setContentAreaFilled(false);
      btnDel.setToolTipText("Eliminar conversación");
      btnDel.addActionListener(e -> eliminarConversacion(otro));
      eastPanel.add(btnDel, BorderLayout.SOUTH);
      item.add(eastPanel, BorderLayout.EAST);
      Color bgNormal = conversacionActual != null && conversacionActual.equals(otro)
            ? new Color(239, 239, 239)
            : Color.WHITE;
      item.setBackground(bgNormal);
      center.setBackground(bgNormal);
      eastPanel.setBackground(bgNormal);
      item.addMouseListener(new ConvItemMouseHandler(item, center, eastPanel, otro, bgNormal));
      return item;
   }
   private Icon buildAvatarIcon(String username, int size) {
      try {
         Usuario u = frame.getGestorUsuarios().buscarExacto(username);
         if (u != null) {
            Icon foto = SidebarPanel.cargarFotoCircular(u.getRutaFotoPerfil(), size);
            if (foto != null)
               return foto;
         }
      } catch (IOException ignored) {
      }
      return new ImageIcon(defaultAvatar(size));
   }
   private BufferedImage defaultAvatar(int size) {
      BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = img.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setClip(new Ellipse2D.Float(0, 0, size, size));
      g.setColor(new Color(200, 200, 200));
      g.fillRect(0, 0, size, size);
      g.setColor(new Color(120, 120, 120));
      g.setFont(new Font("SansSerif", Font.BOLD, size / 2));
      FontMetrics fm = g.getFontMetrics();
      String t = "?";
      g.drawString(t, (size - fm.stringWidth(t)) / 2, (size + fm.getAscent()) / 2 - 2);
      g.dispose();
      return img;
   }
   private void abrirConversacion(String otro) {
      conversacionActual = otro;
      lblChatHeader.setText("@" + otro);
      derechoCard.show(derechoPanel, CARD_CHAT);
      cargarMensajesConversacion(otro);
      try {
         frame.getGestorInbox().marcarLeido(frame.getSession().getUsername(), otro);
         frame.refreshSidebar();
         cargarConversaciones(); 
      } catch (IOException ignored) {
      }
   }
   private void cargarMensajesConversacion(String otro) {
      mensajesPanel.removeAll();
      String miUser = frame.getSession().getUsername();
      try {
         List<Mensaje> msgs = frame.getGestorInbox().getMensajesConversacion(miUser, otro);
         if (msgs.isEmpty()) {
            JLabel sinMsgs = new JLabel("Inicia la conversación.");
            sinMsgs.setFont(new Font("SansSerif", Font.PLAIN, 13));
            sinMsgs.setForeground(Color.GRAY);
            sinMsgs.setAlignmentX(CENTER_ALIGNMENT);
            mensajesPanel.add(Box.createVerticalGlue());
            mensajesPanel.add(sinMsgs);
            mensajesPanel.add(Box.createVerticalGlue());
         } else {
            for (Mensaje m : msgs) {
               mensajesPanel.add(crearBurbuja(m, miUser));
               mensajesPanel.add(Box.createVerticalStrut(4));
            }
            mensajesPanel.add(Box.createVerticalGlue());
         }
      } catch (IOException ex) {
         mensajesPanel.add(new JLabel("Error: " + ex.getMessage()));
      }
      mensajesPanel.revalidate();
      mensajesPanel.repaint();
      SwingUtilities.invokeLater(() -> {
         JScrollBar bar = scrollMensajes.getVerticalScrollBar();
         bar.setValue(bar.getMaximum());
      });
   }
   private JPanel crearBurbuja(Mensaje m, String miUser) {
      boolean esMio = m.getEmisor().equalsIgnoreCase(miUser);
      JPanel fila = new JPanel(new BorderLayout());
      fila.setBackground(Color.WHITE);
      fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
      String texto = "";
      boolean isImagen = m.getTipoEnum() == insta.model.TipoMensaje.IMAGEN;
      boolean isSticker = m.getTipoEnum() == insta.model.TipoMensaje.STICKER;
      if (!isImagen && !isSticker) {
         texto = m.getContenido();
      }
      JPanel burbuja = new ChatBubblePanel(esMio);
      burbuja.setOpaque(false);
      if (isImagen || isSticker) {
         burbuja.setBorder(new EmptyBorder(4, 4, 4, 4));
         try {
            String ruta = m.getContenido();
            if (isSticker) {
               List<Sticker> todos = frame.getGestorStickers().getTodosDisponibles(miUser);
               for (Sticker s : todos) {
                  if (s.getNombre().equalsIgnoreCase(m.getContenido())) {
                     ruta = s.getRutaImagen();
                     break;
                  }
               }
            }
            File f = new File(ruta);
            if (f.exists() && !ruta.isEmpty()) {
               BufferedImage orig = ImageIO.read(f);
               int maxW = isSticker ? 120 : 280;
               int maxH = isSticker ? 120 : 280;
               double ratio = Math.min((double) maxW / orig.getWidth(), (double) maxH / orig.getHeight());
               int w = (int) (orig.getWidth() * ratio);
               int h = (int) (orig.getHeight() * ratio);
               BufferedImage scaled = SidebarPanel.getHighQualityScaledInstance(orig, w, h);
               JLabel imgLbl = new JLabel(new ImageIcon(scaled));
               burbuja.add(imgLbl, BorderLayout.CENTER);
            } else {
               String label = isSticker ? "🎉 " + m.getContenido() : "[Imagen no encontrada]";
               JLabel lbl = new JLabel(label, SwingConstants.CENTER);
               lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
               lbl.setForeground(esMio ? Color.WHITE : Color.BLACK);
               lbl.setBorder(new EmptyBorder(6, 10, 6, 10));
               burbuja.add(lbl, BorderLayout.CENTER);
            }
         } catch (Exception ex) {
            burbuja.add(new JLabel(" [Error] ", SwingConstants.CENTER));
         }
      } else {
         burbuja.setBorder(new EmptyBorder(10, 16, 10, 16));
         JLabel lblTexto = new JLabel("<html><div style='max-width:280px;'>" + texto + "</div></html>");
         lblTexto.setFont(new Font("SansSerif", Font.PLAIN, 14));
         lblTexto.setForeground(esMio ? Color.WHITE : Color.BLACK);
         burbuja.add(lblTexto, BorderLayout.CENTER);
      }
      JPanel wrap = new JPanel(new FlowLayout(esMio ? FlowLayout.RIGHT : FlowLayout.LEFT, 8, 2));
      wrap.setBackground(Color.WHITE);
      wrap.add(burbuja);
      JLabel ts = new JLabel(m.getHora().toString());
      ts.setFont(new Font("SansSerif", Font.PLAIN, 10));
      ts.setForeground(new Color(160, 160, 160));
      JPanel tsWrap = new JPanel(new FlowLayout(esMio ? FlowLayout.RIGHT : FlowLayout.LEFT, 16, 0));
      tsWrap.setBackground(Color.WHITE);
      tsWrap.add(ts);
      JPanel vertical = new JPanel();
      vertical.setLayout(new BoxLayout(vertical, BoxLayout.Y_AXIS));
      vertical.setBackground(Color.WHITE);
      vertical.add(wrap);
      vertical.add(tsWrap);
      fila.add(vertical, esMio ? BorderLayout.EAST : BorderLayout.WEST);
      JPanel finalWrapper = new JPanel(new BorderLayout());
      finalWrapper.setBackground(Color.WHITE);
      finalWrapper.add(fila, BorderLayout.NORTH);
      finalWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, finalWrapper.getPreferredSize().height));
      SwingUtilities.invokeLater(() -> {
         finalWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, finalWrapper.getPreferredSize().height));
      });
      return finalWrapper;
   }
   private void enviarTexto() {
      if (conversacionActual == null)
         return;
      String texto = txtMensaje.getText().trim();
      if (texto.isEmpty() || texto.equals("Mensaje..."))
         return;
      String miUser = frame.getSession().getUsername();
      try {
         boolean puede = frame.getGestorInbox().puedeEnviar(
               miUser, conversacionActual,
               frame.getGestorFollowers(),
               frame.getGestorUsuarios());
         if (!puede) {
            frame.mostrarToast("El perfil es privado y no son amigos. No te es posible enviar mensajes.");
            return;
         }
         Mensaje mObj = new Mensaje(miUser, conversacionActual, texto, "TEXTO");
         frame.getGestorInbox().enviarMensaje(mObj);
         if (frame.getClienteSocket() != null) {
            frame.getClienteSocket().enviarMensaje(mObj);
         }
         txtMensaje.setText("Mensaje...");
         txtMensaje.setForeground(Color.GRAY);
         cargarMensajesConversacion(conversacionActual);
         cargarConversaciones();
      } catch (IOException ex) {
         frame.mostrarToast("Error: " + ex.getMessage());
      }
   }
   private void enviarImagen() {
      if (conversacionActual == null)
         return;
      String miUser = frame.getSession().getUsername();
      try {
         boolean puede = frame.getGestorInbox().puedeEnviar(
               miUser, conversacionActual,
               frame.getGestorFollowers(),
               frame.getGestorUsuarios());
         if (!puede) {
            frame.mostrarToast("El perfil es privado y no son amigos.");
            return;
         }
      } catch (IOException ex) {
         return;
      }
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Selecciona una imagen para enviar");
      fileChooser
            .setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png"));
      int seleccion = fileChooser.showOpenDialog(this);
      if (seleccion == JFileChooser.APPROVE_OPTION) {
         File destFolder = insta.storage.AppPaths.userDir(frame.getSession().getUsername()).resolve("inbox_media")
               .toFile();
         if (!destFolder.exists())
            destFolder.mkdirs();
         File source = fileChooser.getSelectedFile();
         File dest = new File(destFolder, System.currentTimeMillis() + "_" + source.getName());
         try {
            java.nio.file.Files.copy(source.toPath(), dest.toPath());
            Mensaje mObj = new Mensaje(miUser, conversacionActual, dest.getAbsolutePath(), "IMAGEN");
            frame.getGestorInbox().enviarMensaje(mObj);
            if (frame.getClienteSocket() != null) {
               frame.getClienteSocket().enviarMensaje(mObj);
            }
            cargarMensajesConversacion(conversacionActual);
            cargarConversaciones();
         } catch (IOException ex) {
            frame.mostrarToast("Error enviando imagen: " + ex.getMessage());
         }
      }
   }
   private void mostrarSelectorStickers() {
      if (conversacionActual == null)
         return;
      String miUser = frame.getSession().getUsername();
      JLayeredPane lp = frame.getMainLayeredPane();
      JPanel modalBg = new JPanel(new GridBagLayout());
      modalBg.setBackground(new Color(0, 0, 0, 140));
      modalBg.setBounds(0, 0, lp.getWidth(), lp.getHeight());
      modalBg.addMouseListener(new InboxEmptyMouseHandler()); 
      JPanel dialog = new JPanel(new BorderLayout());
      dialog.setBackground(Color.WHITE);
      dialog.setPreferredSize(new Dimension(380, 450));
      dialog.setBorder(new LineBorder(new Color(219, 219, 219), 1));
      JPanel header = new JPanel(new BorderLayout());
      header.setBackground(Color.WHITE);
      header.setBorder(new EmptyBorder(12, 16, 12, 16));
      JLabel lblTit = new JLabel("Stickers", SwingConstants.CENTER);
      lblTit.setFont(new Font("SansSerif", Font.BOLD, 16));
      header.add(lblTit, BorderLayout.CENTER);
      JButton btnClose = new JButton("✕");
      btnClose.setFont(new Font("SansSerif", Font.PLAIN, 18));
      btnClose.setBorder(null);
      btnClose.setBackground(Color.WHITE);
      btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnClose.addActionListener(e -> {
         lp.remove(modalBg);
         lp.repaint();
      });
      header.add(btnClose, BorderLayout.EAST);
      dialog.add(header, BorderLayout.NORTH);
      JPanel grid = new JPanel(new GridLayout(0, 3, 10, 10)); 
      grid.setBackground(Color.WHITE);
      grid.setBorder(new EmptyBorder(15, 15, 15, 15));
      JButton btnAdd = new JButton("+");
      btnAdd.setFont(new Font("SansSerif", Font.BOLD, 28));
      btnAdd.setForeground(new Color(0, 149, 246));
      btnAdd.setBackground(new Color(250, 250, 250));
      btnAdd.setFocusPainted(false);
      btnAdd.setBorder(new LineBorder(new Color(230, 230, 230), 1));
      btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnAdd.addActionListener(e -> {
         JFileChooser fc = new JFileChooser();
         fc.setFileFilter(
               new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "png", "jpg", "jpeg", "gif"));
         if (fc.showOpenDialog(modalBg) == JFileChooser.APPROVE_OPTION) {
            try {
               frame.getGestorStickers().importarSticker(miUser, fc.getSelectedFile());
               frame.mostrarToast("Sticker personal añadido");
               lp.remove(modalBg);
               lp.repaint();
               mostrarSelectorStickers(); 
            } catch (IOException ex) {
               frame.mostrarToast("Error: " + ex.getMessage());
            }
         }
      });
      grid.add(btnAdd);
      try {
         List<Sticker> stickers = frame.getGestorStickers().getTodosDisponibles(miUser);
         for (Sticker s : stickers) {
            JButton btn = new JButton();
            btn.setLayout(new BorderLayout());
            btn.setBackground(Color.WHITE);
            btn.setBorder(new LineBorder(new Color(240, 240, 240), 1));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (s.getRutaImagen() != null && !s.getRutaImagen().isEmpty()) {
               try {
                  BufferedImage img = ImageIO.read(new File(s.getRutaImagen()));
                  if (img != null) {
                     JLabel imgLbl = new JLabel(
                           new ImageIcon(SidebarPanel.getHighQualityScaledInstance(img, 70, 70)));
                     imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
                     btn.add(imgLbl, BorderLayout.CENTER);
                  }
               } catch (Exception ignored) {
               }
            }
            JLabel lblName = new JLabel(s.getNombre(), SwingConstants.CENTER);
            lblName.setFont(new Font("SansSerif", Font.PLAIN, 10));
            lblName.setForeground(Color.GRAY);
            btn.add(lblName, BorderLayout.SOUTH);
            btn.addActionListener(e -> {
               enviarSticker(s.getNombre());
               lp.remove(modalBg);
               lp.repaint();
            });
            grid.add(btn);
         }
      } catch (IOException ex) {
         grid.add(new JLabel("Error"));
      }
      JScrollPane scroll = new JScrollPane(grid);
      scroll.setBorder(null);
      scroll.getVerticalScrollBar().setUnitIncrement(16);
      dialog.add(scroll, BorderLayout.CENTER);
      modalBg.add(dialog);
      lp.add(modalBg, JLayeredPane.MODAL_LAYER);
      lp.moveToFront(modalBg);
      lp.revalidate();
      lp.repaint();
   }
   private void enviarSticker(String nombreSticker) {
      String miUser = frame.getSession().getUsername();
      try {
         boolean puede = frame.getGestorInbox().puedeEnviar(
               miUser, conversacionActual,
               frame.getGestorFollowers(),
               frame.getGestorUsuarios());
         if (!puede) {
            frame.mostrarToast("No puedes enviar mensajes a este usuario.");
            return;
         }
         Mensaje mObj = new Mensaje(miUser, conversacionActual, nombreSticker, "STICKER");
         frame.getGestorInbox().enviarMensaje(mObj);
         cargarMensajesConversacion(conversacionActual);
         cargarConversaciones();
      } catch (IOException ex) {
         frame.mostrarToast("Error: " + ex.getMessage());
      }
   }
   private void nuevaConversacion() {
      frame.mostrarInput(
            "Nueva conversación",
            "A quién deseas enviar mensaje:",
            "Username...",
            inputDest -> {
               if (inputDest == null || inputDest.trim().isEmpty())
                  return;
               String dest = inputDest.trim();
               String miUser = frame.getSession().getUsername();
               if (dest.equalsIgnoreCase(miUser)) {
                  frame.mostrarToast("No puedes enviarte mensajes a ti mismo.");
                  return;
               }
               try {
                  Usuario rec = frame.getGestorUsuarios().buscarExacto(dest);
                  if (rec == null || !rec.isActivo()) {
                     frame.mostrarToast("Usuario no encontrado o inactivo.");
                     return;
                  }
                  boolean puede = frame.getGestorInbox().puedeEnviar(
                        miUser, dest,
                        frame.getGestorFollowers(),
                        frame.getGestorUsuarios());
                  if (!puede) {
                     frame.mostrarToast(
                           "No puedes enviar mensajes a @" + dest + ". Perfil privado o no son amigos.");
                     return;
                  }
                  abrirConversacionCon(dest);
               } catch (IOException ex) {
                  frame.mostrarToast("Error: " + ex.getMessage());
               }
            });
   }
   private void eliminarConversacion(String otro) {
      frame.mostrarConfirmacion(
            "Eliminar",
            "¿Eliminar la conversación con @" + otro + "?",
            "Eliminar",
            () -> {
               try {
                  frame.getGestorInbox().eliminarConversacion(frame.getSession().getUsername(), otro);
                  if (otro.equals(conversacionActual)) {
                     conversacionActual = null;
                     derechoCard.show(derechoPanel, CARD_VACIO);
                  }
                  cargarConversaciones();
               } catch (IOException ex) {
                  frame.mostrarToast("Error: " + ex.getMessage());
               }
            });
   }
   private class NewConvMouseHandler extends MouseAdapter {
      @Override
      public void mouseClicked(MouseEvent e) { nuevaConversacion(); }
   }

   private class DMIconLabel extends JLabel {
      @Override
      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D) g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int cx = getWidth() / 2, cy = getHeight() / 2, r = 46;
         g2.setColor(Color.WHITE);
         g2.fillOval(cx - r, cy - r, r * 2, r * 2);
         g2.setColor(new Color(10, 10, 10));
         g2.setStroke(new BasicStroke(2.0f));
         g2.drawOval(cx - r, cy - r, r * 2, r * 2);
         g2.setFont(new Font("SansSerif", Font.PLAIN, 38));
         FontMetrics fm = g2.getFontMetrics();
         String t = "✈";
         g2.drawString(t, cx - fm.stringWidth(t) / 2, cy + fm.getAscent() / 2 - 4);
         g2.dispose();
      }
   }

   private static class MessagePlaceholderHandler extends FocusAdapter {
      private final JTextField tf;
      private final String ph;
      public MessagePlaceholderHandler(JTextField tf, String ph) {
         this.tf = tf;
         this.ph = ph;
      }
      @Override
      public void focusGained(FocusEvent e) {
         if (tf.getText().equals(ph)) {
            tf.setText("");
            tf.setForeground(Color.BLACK);
         }
      }
      @Override
      public void focusLost(FocusEvent e) {
         if (tf.getText().isEmpty()) {
            tf.setText(ph);
            tf.setForeground(Color.GRAY);
         }
      }
   }

   private class ConvItemMouseHandler extends MouseAdapter {
      private final JPanel item, center, east;
      private final String otro;
      private final Color bgNormal;
      public ConvItemMouseHandler(JPanel i, JPanel c, JPanel e, String o, Color b) {
         this.item = i; this.center = c; this.east = e; this.otro = o; this.bgNormal = b;
      }
      @Override
      public void mouseClicked(MouseEvent e) { abrirConversacion(otro); }
      @Override
      public void mouseEntered(MouseEvent e) {
         Color h = new Color(245, 245, 245);
         item.setBackground(h); center.setBackground(h); east.setBackground(h);
      }
      @Override
      public void mouseExited(MouseEvent e) {
         item.setBackground(bgNormal); center.setBackground(bgNormal); east.setBackground(bgNormal);
      }
   }

   private static class ChatBubblePanel extends JPanel {
      private final boolean esMio;
      public ChatBubblePanel(boolean esMio) {
         super(new BorderLayout());
         this.esMio = esMio;
         setOpaque(false);
      }
      @Override
      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D) g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setColor(esMio ? new Color(0, 149, 246) : new Color(239, 239, 239));
         g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
         g2.dispose();
         super.paintComponent(g);
      }
   }

   private static class InboxEmptyMouseHandler extends MouseAdapter {}
}
