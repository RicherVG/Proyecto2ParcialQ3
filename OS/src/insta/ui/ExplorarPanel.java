/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;
import insta.model.Publicacion;
import insta.model.Usuario;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
/**
 *
 * @author riche
 */
public class ExplorarPanel extends JPanel {
   private final MainFrame frame;
   private JPanel gridPanel;
   private JScrollPane scrollExplorar;
   public ExplorarPanel(MainFrame frame) {
      this.frame = frame;
      setLayout(new BorderLayout());
      setBackground(new Color(250, 250, 250));
      buildUI();
   }
   private void buildUI() {
      JPanel header = new JPanel(new BorderLayout());
      header.setBackground(Color.WHITE);
      header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(219, 219, 219)),
            new EmptyBorder(12, 20, 12, 20)));
      JLabel lbl = new JLabel("Explorar");
      lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
      header.add(lbl, BorderLayout.WEST);
      JButton btnRefresh = new JButton("Actualizar");
      btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 13));
      btnRefresh.setBackground(new Color(0, 149, 246));
      btnRefresh.setForeground(Color.WHITE);
      btnRefresh.setFocusPainted(false);
      btnRefresh.setBorderPainted(false);
      btnRefresh.setOpaque(true);
      btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnRefresh.addActionListener(e -> cargar());
      header.add(btnRefresh, BorderLayout.EAST);
      add(header, BorderLayout.NORTH);
      gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
      gridPanel.setBackground(new Color(250, 250, 250));
      JPanel wrapper = new JPanel(new BorderLayout());
      wrapper.setBackground(new Color(250, 250, 250));
      wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
      wrapper.add(gridPanel, BorderLayout.NORTH);
      scrollExplorar = new JScrollPane(wrapper);
      scrollExplorar.setBorder(null);
      scrollExplorar.getVerticalScrollBar().setUnitIncrement(16);
      scrollExplorar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      add(scrollExplorar, BorderLayout.CENTER);
   }
   public void cargar() {
      gridPanel.removeAll();
      try {
         List<Publicacion> todas = frame.getGestorPublicaciones()
               .getTodasLasPublicaciones(frame.getGestorUsuarios());
         java.util.Collections.sort(todas);
         if (todas.isEmpty()) {
            gridPanel.setLayout(new BorderLayout());
            JLabel vacio = new JLabel("No hay publicaciones en la plataforma todavía.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 14));
            vacio.setForeground(Color.GRAY);
            vacio.setHorizontalAlignment(SwingConstants.CENTER);
            vacio.setBorder(new EmptyBorder(60, 20, 20, 20));
            gridPanel.add(vacio, BorderLayout.CENTER);
         } else {
            gridPanel.setLayout(new GridLayout(0, 3, 4, 4));
            for (Publicacion p : todas) {
               if (p.getRutaImagen() == null || p.getRutaImagen().trim().isEmpty())
                  continue;
               gridPanel.add(crearMiniaturaGrid(p));
            }
         }
      } catch (IOException ex) {
         gridPanel.setLayout(new BorderLayout());
         JLabel err = new JLabel("Error: " + ex.getMessage());
         err.setForeground(Color.RED);
         err.setHorizontalAlignment(SwingConstants.CENTER);
         gridPanel.add(err);
      }
      gridPanel.revalidate();
      gridPanel.repaint();
      if (scrollExplorar != null) {
         SwingUtilities.invokeLater(() -> scrollExplorar.getVerticalScrollBar().setValue(0));
      }
   }
   private JPanel crearMiniaturaGrid(Publicacion p) {
      JPanel wrap = new JPanel(new BorderLayout());
      wrap.setBackground(Color.WHITE);
      Dimension fixSize = new Dimension(300, 300);
      wrap.setPreferredSize(fixSize);
      wrap.setMinimumSize(fixSize);
      wrap.setMaximumSize(fixSize);
      wrap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JLabel imgLbl = new JLabel("", SwingConstants.CENTER);
      imgLbl.setPreferredSize(fixSize);
      imgLbl.setMinimumSize(fixSize);
      imgLbl.setMaximumSize(fixSize);
      new GridImageWorker(imgLbl, p.getRutaImagen()).execute();
      
      wrap.add(imgLbl, BorderLayout.CENTER);

      wrap.addMouseListener(new GridPhotoMouseHandler(frame, p, wrap));
      JPanel strictWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
      strictWrap.setBackground(Color.WHITE);
      strictWrap.add(wrap);
      return strictWrap;
   }
   public static void mostrarPostAmpliado(MainFrame frame, Publicacion p) {
      JLayeredPane lp = lp(frame);
      JPanel bg = new JPanel(new GridBagLayout());
      bg.setBackground(new Color(0, 0, 0, 180));
      bg.setBounds(0, 0, lp.getWidth(), lp.getHeight());
      bg.addMouseListener(new ModalBgMouseHandler(lp, bg));

      JPanel card = crearTarjetaPostFull(frame, p, bg);
      card.addMouseListener(new ModalBgMouseHandler(null, null)); // Consume clics
      bg.add(card);
      lp.add(bg, JLayeredPane.MODAL_LAYER);
      lp.moveToFront(bg);
      lp.revalidate();
      lp.repaint();
   }
   private static JPanel crearTarjetaPostFull(MainFrame frame, Publicacion p, JPanel overlay) {
      int rawCardW = (int) (lp(frame).getWidth() * 0.85);
      int rawCardH = (int) (lp(frame).getHeight() * 0.85);
      final int cardW = Math.max(rawCardW, 700);
      final int cardH = Math.max(rawCardH, 500);
      final int commentsW = 360;
      final int imgW = cardW - commentsW;
      JPanel card = new JPanel(new BorderLayout());
      card.setBackground(Color.WHITE);
      card.setBorder(new LineBorder(new Color(219, 219, 219), 1));
      card.setPreferredSize(new Dimension(cardW, cardH));
      card.setMaximumSize(new Dimension(cardW, cardH));
      JPanel leftSide = new JPanel(new BorderLayout());
      leftSide.setBackground(Color.BLACK);
      leftSide.setPreferredSize(new Dimension(imgW, cardH));
      JLayeredPane imageLayer = new JLayeredPane();
      imageLayer.setBackground(Color.BLACK);
      imageLayer.setOpaque(true);
      imageLayer.setPreferredSize(new Dimension(imgW, cardH));
      JPanel imgContainer = new JPanel(new GridBagLayout());
      imgContainer.setOpaque(false);
      imgContainer.setBounds(0, 0, imgW, cardH);
      JLabel imgLbl = new JLabel();
      imgContainer.add(imgLbl);
      imageLayer.add(imgContainer, Integer.valueOf(0));
      leftSide.add(imageLayer, BorderLayout.CENTER);
      if (!p.getRutaImagen().isEmpty()) {
         new ModalImageWorker(imgLbl, p.getRutaImagen(), imgW, cardH, imgContainer).execute();
         String menciones = p.getMenciones();
         if (menciones != null && !menciones.trim().isEmpty()) {
            JButton btnTags = new JButton("👤");
            btnTags.setFont(new Font("SansSerif", Font.PLAIN, 16));
            btnTags.setBackground(new Color(30, 30, 30, 200));
            btnTags.setForeground(Color.WHITE);
            btnTags.setBorder(new EmptyBorder(4, 8, 4, 8));
            btnTags.setFocusPainted(false);
            btnTags.setContentAreaFilled(true);
            btnTags.setOpaque(true);
            btnTags.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnTags.setBounds(12, cardH - 46, 44, 30);
            imageLayer.add(btnTags, Integer.valueOf(2));
            JPanel bubblesPanel = new JPanel(null);
            bubblesPanel.setOpaque(false);
            bubblesPanel.setBounds(0, 0, imgW, cardH);
            bubblesPanel.setVisible(false);
            String[] tags = menciones.trim().split("\\s+");
            int bx = 16, by = 16;
            for (String tag : tags) {
               if (!tag.startsWith("@"))
                  continue;
               boolean existe = false;
               try {
                  existe = frame.getGestorUsuarios().buscarExacto(tag.substring(1)) != null;
               } catch (IOException ignored) {
               }
               if (!existe)
                  continue;
               JLabel burbuja = new JLabel("  " + tag + "  ");
               burbuja.setFont(new Font("SansSerif", Font.BOLD, 12));
               burbuja.setForeground(Color.WHITE);
               burbuja.setBackground(new Color(0, 0, 0, 170));
               burbuja.setOpaque(true);
               burbuja.setBorder(new EmptyBorder(4, 10, 4, 10));
               burbuja.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
               int bw = 20 + tag.length() * 8;
               burbuja.setBounds(bx, by, bw, 28);
               bubblesPanel.add(burbuja);
               bx += bw + 8;
               if (bx > imgW - 120) {
                  bx = 16;
                  by += 38;
               }
               final String username = tag.substring(1);
               burbuja.addMouseListener(new MentionBubbleMouseHandler(frame, username, overlay));
            }
            imageLayer.add(bubblesPanel, Integer.valueOf(1));
            btnTags.addActionListener(e -> bubblesPanel.setVisible(!bubblesPanel.isVisible()));
         }
      }
      JPanel rightSide = new JPanel(new BorderLayout());
      rightSide.setBackground(Color.WHITE);
      rightSide.setBorder(new MatteBorder(0, 1, 0, 0, new Color(219, 219, 219)));
      JPanel head = buildPostHeader(frame, p, overlay);
      rightSide.add(head, BorderLayout.NORTH);
      JPanel centerPanel = new JPanel(new BorderLayout());
      centerPanel.setBackground(Color.WHITE);
      JPanel descPanel = new JPanel();
      descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
      descPanel.setBackground(Color.WHITE);
      if (!p.getContenido().trim().isEmpty()) {
         JTextArea txtDesc = new JTextArea(p.getContenido());
         txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
         txtDesc.setEditable(false);
         txtDesc.setLineWrap(true);
         txtDesc.setWrapStyleWord(true);
         txtDesc.setBackground(Color.WHITE);
         txtDesc.setBorder(new EmptyBorder(10, 14, 6, 14));
         descPanel.add(txtDesc);
      }
      if (!p.getHashtags().trim().isEmpty()) {
         JPanel hashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
         hashPanel.setBackground(Color.WHITE);
         hashPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
         hashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
         
         hashPanel.setPreferredSize(new Dimension(340, 50));
         hashPanel.setMaximumSize(new Dimension(340, 50));

         for (String tag : p.getHashtags().trim().split("\\s+")) {
            if (!tag.startsWith("#")) continue;
            JLabel lblTag = new JLabel(tag);
            lblTag.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblTag.setForeground(new Color(0, 55, 210));
            lblTag.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblTag.addMouseListener(new HashtagMouseHandler(frame, overlay, tag, lblTag));
            hashPanel.add(lblTag);
         }
         if (hashPanel.getComponentCount() > 0) descPanel.add(hashPanel);
      }
      descPanel.add(new JSeparator());
      centerPanel.add(descPanel, BorderLayout.NORTH);
      JPanel commentsInner = new JPanel();
      commentsInner.setLayout(new BoxLayout(commentsInner, BoxLayout.Y_AXIS));
      commentsInner.setBackground(Color.WHITE);
      commentsInner.setBorder(new EmptyBorder(6, 14, 6, 14));
      if (p.getComentarios().isEmpty()) {
         JLabel noC = new JLabel("Sin comentarios aún.");
         noC.setFont(new Font("SansSerif", Font.ITALIC, 12));
         noC.setForeground(Color.GRAY);
         commentsInner.add(noC);
      } else {
         for (insta.model.Comentario c : p.getComentarios()) {
            JLabel cmt = new JLabel("<html><b>" + c.getAutor() + "</b> " + c.getTexto() + "</html>");
            cmt.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cmt.setBorder(new EmptyBorder(3, 0, 3, 0));
            commentsInner.add(cmt);
         }
      }
      JScrollPane commScroll = new JScrollPane(commentsInner);
      commScroll.setBorder(null);
      commScroll.getVerticalScrollBar().setUnitIncrement(8);
      centerPanel.add(commScroll);
      rightSide.add(centerPanel, BorderLayout.CENTER);
      JPanel bottomBar = new JPanel();
      bottomBar.setLayout(new BoxLayout(bottomBar, BoxLayout.Y_AXIS));
      bottomBar.setBackground(Color.WHITE);
      bottomBar.setBorder(new MatteBorder(1, 0, 0, 0, new Color(219, 219, 219)));
      JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
      actionBar.setBackground(Color.WHITE);
      String myU = frame.getSession().getUsername();
      JButton btnLike = new JButton(p.getLikes().contains(myU) ? "❤" : "♡");
      btnLike.setFont(new Font("SansSerif", Font.PLAIN, 22));
      btnLike.setForeground(p.getLikes().contains(myU) ? new Color(237, 73, 86) : Color.BLACK);
      btnLike.setBorderPainted(false);
      btnLike.setContentAreaFilled(false);
      btnLike.setFocusPainted(false);
      btnLike.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JLabel lblLikes = new JLabel(p.getLikes().size() + "");
      lblLikes.setFont(new Font("SansSerif", Font.BOLD, 13));
      btnLike.addActionListener(e -> {
         if (p.getLikes().contains(myU)) {
            p.removeLike(myU);
            btnLike.setText("♡");
            btnLike.setForeground(Color.BLACK);
         } else {
            p.addLike(myU);
            btnLike.setText("❤");
            btnLike.setForeground(new Color(237, 73, 86));
            try {
               frame.getGestorFollowers().notificarLike(p.getUsername(), myU);
            } catch (IOException ignored) {
            }
         }
         lblLikes.setText(p.getLikes().size() + "");
         try {
            frame.getGestorPublicaciones().actualizar(p);
         } catch (IOException ex) {
            frame.mostrarToast("Error guardando like");
         }
      });
      actionBar.add(btnLike);
      actionBar.add(lblLikes);
      bottomBar.add(actionBar);
      JPanel inputPanel = new JPanel(new BorderLayout(6, 0));
      inputPanel.setBackground(Color.WHITE);
      inputPanel.setBorder(new EmptyBorder(6, 12, 10, 12));
      JTextField txtComentario = new JTextField();
      txtComentario.setFont(new Font("SansSerif", Font.PLAIN, 13));
      txtComentario.setBorder(new CompoundBorder(
            new LineBorder(new Color(219, 219, 219), 1, true),
            new EmptyBorder(6, 10, 6, 10)));
      txtComentario.putClientProperty("JTextField.placeholderText", "Agrega un comentario...");
      JButton btnEnviar = new JButton("Publicar");
      btnEnviar.setFont(new Font("SansSerif", Font.BOLD, 13));
      btnEnviar.setForeground(new Color(0, 149, 246));
      btnEnviar.setBorderPainted(false);
      btnEnviar.setContentAreaFilled(false);
      btnEnviar.setFocusPainted(false);
      btnEnviar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      Runnable enviarComentario = () -> {
         String txt = txtComentario.getText().trim();
         if (txt.isEmpty())
            return;
         p.addComentario(new insta.model.Comentario(myU, txt));
         try {
            frame.getGestorPublicaciones().actualizar(p);
            frame.getGestorFollowers().notificarComentario(p.getUsername(), myU, txt);
         } catch (IOException ex) {
            frame.mostrarToast("Error");
         }
         if (p.getComentarios().size() == 1) {
            commentsInner.removeAll();
         }
         insta.model.Comentario ultimo = p.getComentarios().get(p.getComentarios().size() - 1);
         JLabel cmt = new JLabel("<html><b>" + ultimo.getAutor() + "</b> " + ultimo.getTexto() + "</html>");
         cmt.setFont(new Font("SansSerif", Font.PLAIN, 12));
         cmt.setBorder(new EmptyBorder(3, 0, 3, 0));
         commentsInner.add(cmt);
         commentsInner.revalidate();
         commentsInner.repaint();
         JScrollBar vsb = commScroll.getVerticalScrollBar();
         vsb.setValue(vsb.getMaximum());
         txtComentario.setText("");
      };
      btnEnviar.addActionListener(e -> enviarComentario.run());
      txtComentario.addActionListener(e -> enviarComentario.run());
      inputPanel.add(txtComentario, BorderLayout.CENTER);
      inputPanel.add(btnEnviar, BorderLayout.EAST);
      bottomBar.add(inputPanel);
      rightSide.add(bottomBar, BorderLayout.SOUTH);
      JPanel contentCard = new JPanel(new BorderLayout());
      contentCard.setOpaque(false);
      contentCard.add(leftSide, BorderLayout.WEST);
      contentCard.add(rightSide, BorderLayout.CENTER);
      card.add(contentCard, BorderLayout.CENTER);
      return card;
   }
   private static JPanel buildPostHeader(MainFrame frame, Publicacion p, JPanel overlay) {
      JPanel head = new JPanel(new BorderLayout());
      head.setBackground(Color.WHITE);
      head.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(219, 219, 219)),
            new EmptyBorder(10, 14, 10, 14)));
      JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
      userInfo.setBackground(Color.WHITE);
      try {
         Usuario autor = frame.getGestorUsuarios().buscarExacto(p.getUsername());
         if (autor != null) {
            Icon fotoC = SidebarPanel.cargarFotoCircular(autor.getRutaFotoPerfil(), 40);
            JLabel fotoLbl = new JLabel(fotoC != null ? fotoC : new ImageIcon(avatarCircle(40)));
            fotoLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            fotoLbl.addMouseListener(new HeaderAvatarMouseHandler(frame, autor, overlay));
            userInfo.add(fotoLbl);
         }
      } catch (IOException ignored) {
      }
      JLabel lblUser = new JLabel(p.getUsername());
      lblUser.setFont(new Font("SansSerif", Font.BOLD, 14));
      lblUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblUser.addMouseListener(new HeaderUserMouseHandler(frame, p.getUsername(), overlay));
      userInfo.add(lblUser);
      head.add(userInfo, BorderLayout.WEST);
      JLabel lblFecha = new JLabel(p.getFecha().toString());
      lblFecha.setFont(new Font("SansSerif", Font.PLAIN, 11));
      lblFecha.setForeground(Color.GRAY);
      head.add(lblFecha, BorderLayout.EAST);
      return head;
   }
   private static JLayeredPane lp(MainFrame frame) {
      return frame.getMainLayeredPane();
   }
   private static BufferedImage avatarCircle(int size) {
      BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = img.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setColor(new Color(200, 200, 200));
      g.fillOval(0, 0, size, size);
      g.dispose();
      return img;
   }
   private static class GridImageWorker extends SwingWorker<ImageIcon, Void> {
      private final JLabel label;
      private final String path;
      public GridImageWorker(JLabel label, String path) { this.label = label; this.path = path; }
      @Override
      protected ImageIcon doInBackground() throws Exception {
         return SidebarPanel.cargarMiniaturaGrid(path);
      }
      @Override
      protected void done() {
         try {
            ImageIcon icon = get();
            if (icon != null) label.setIcon(icon);
            else label.setText("⚠ Imagen Rota");
         } catch (Exception e) { label.setText("⚠ Error"); }
      }
   }

   private static class GridPhotoMouseHandler extends MouseAdapter {
      private final MainFrame frame;
      private final Publicacion p;
      private final JPanel wrap;
      public GridPhotoMouseHandler(MainFrame frame, Publicacion p, JPanel wrap) {
         this.frame = frame; this.p = p; this.wrap = wrap;
      }
      @Override
      public void mouseClicked(MouseEvent e) { mostrarPostAmpliado(frame, p); }
      @Override
      public void mouseEntered(MouseEvent e) {
         wrap.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 2));
      }
      @Override
      public void mouseExited(MouseEvent e) { wrap.setBorder(null); }
   }

   private static class ModalBgMouseHandler extends MouseAdapter {
      private final JLayeredPane lp;
      private final JPanel bg;
      public ModalBgMouseHandler(JLayeredPane lp, JPanel bg) { this.lp = lp; this.bg = bg; }
      @Override
      public void mouseClicked(MouseEvent e) {
         if (lp != null && bg != null) {
            lp.remove(bg);
            lp.repaint();
         }
      }
   }

   private static class ModalImageWorker extends SwingWorker<ImageIcon, Void> {
      private final JLabel label;
      private final String path;
      private final int w, h;
      private final JPanel container;
      public ModalImageWorker(JLabel l, String p, int w, int h, JPanel c) {
         this.label = l; this.path = p; this.w = w; this.h = h; this.container = c;
      }
      @Override
      protected ImageIcon doInBackground() { return SidebarPanel.cargarImagenModal(path, w, h); }
      @Override
      protected void done() {
         try {
            ImageIcon icon = get();
            if (icon == null) return;
            label.setIcon(icon);
            Dimension d = new Dimension(icon.getIconWidth(), icon.getIconHeight());
            label.setPreferredSize(d); label.setMinimumSize(d); label.setMaximumSize(d);
            container.revalidate(); container.repaint();
         } catch (Exception ignored) {}
      }
   }

   private static class MentionBubbleMouseHandler extends MouseAdapter {
      private final MainFrame frame;
      private final String username;
      private final JPanel overlay;
      public MentionBubbleMouseHandler(MainFrame f, String u, JPanel o) {
         this.frame = f; this.username = u; this.overlay = o;
      }
      @Override
      public void mouseClicked(MouseEvent e) {
         try {
            Usuario u = frame.getGestorUsuarios().buscarExacto(username);
            if (u != null) {
               lp(frame).remove(overlay); lp(frame).repaint();
               frame.abrirPerfilDeUsuario(u);
            }
         } catch (IOException ignored) {}
      }
   }

   private static class HashtagMouseHandler extends MouseAdapter {
      private final MainFrame frame;
      private final JPanel overlay;
      private final String tag;
      private final JLabel label;
      public HashtagMouseHandler(MainFrame f, JPanel o, String t, JLabel l) {
         this.frame = f; this.overlay = o; this.tag = t; this.label = l;
      }
      @Override
      public void mouseClicked(MouseEvent e) {
         lp(frame).remove(overlay); lp(frame).repaint();
         frame.abrirBusquedaCon(tag);
      }
      @Override
      public void mouseEntered(MouseEvent e) { label.setText("<html><u>" + tag + "</u></html>"); }
      @Override
      public void mouseExited(MouseEvent e) { label.setText(tag); }
   }

   private static class HeaderAvatarMouseHandler extends MouseAdapter {
      private final MainFrame frame;
      private final Usuario autor;
      private final JPanel overlay;
      public HeaderAvatarMouseHandler(MainFrame f, Usuario a, JPanel o) {
         this.frame = f; this.autor = a; this.overlay = o;
      }
      @Override
      public void mouseClicked(MouseEvent e) {
         lp(frame).remove(overlay); lp(frame).repaint();
         frame.abrirPerfilDeUsuario(autor);
      }
   }

   private static class HeaderUserMouseHandler extends MouseAdapter {
      private final MainFrame frame;
      private final String username;
      private final JPanel overlay;
      public HeaderUserMouseHandler(MainFrame f, String u, JPanel o) {
         this.frame = f; this.username = u; this.overlay = o;
      }
      @Override
      public void mouseClicked(MouseEvent e) {
         try {
            Usuario u = frame.getGestorUsuarios().buscarExacto(username);
            if (u != null) {
               lp(frame).remove(overlay); lp(frame).repaint();
               frame.abrirPerfilDeUsuario(u);
            }
         } catch (IOException ignored) {}
      }
   }
}
