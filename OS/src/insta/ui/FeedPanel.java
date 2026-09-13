/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;
import insta.model.Publicacion;
import insta.model.Usuario;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author riche
 */
public class FeedPanel extends JPanel {
   private final MainFrame frame;
   private JPanel postsContainer;
   private JScrollPane scrollFeed;
   public FeedPanel(MainFrame frame) {
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
      JLabel lbl = new JLabel("Inicio");
      lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
      header.add(lbl, BorderLayout.WEST);
      add(header, BorderLayout.NORTH);
      postsContainer = new JPanel();
      postsContainer.setLayout(new BoxLayout(postsContainer, BoxLayout.Y_AXIS));
      postsContainer.setBackground(new Color(250, 250, 250));
      scrollFeed = new JScrollPane(postsContainer);
      scrollFeed.setBorder(null);
      scrollFeed.getVerticalScrollBar().setUnitIncrement(16);
      scrollFeed.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      add(scrollFeed, BorderLayout.CENTER);
   }
   public void cargarFeed() {
      postsContainer.removeAll();
      try {
         List<Publicacion> feed = frame.getGestorPublicaciones().getFeed(
               frame.getSession().getUsername(),
               frame.getGestorFollowers(),
               frame.getGestorUsuarios());
         java.util.Collections.sort(feed);
         if (feed.isEmpty()) {
            JLabel vacio = new JLabel("¡No hay publicaciones! Sigue a alguien o publica algo.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 14));
            vacio.setForeground(Color.GRAY);
            vacio.setHorizontalAlignment(SwingConstants.CENTER);
            vacio.setBorder(new EmptyBorder(60, 20, 20, 20));
            postsContainer.add(vacio);
         } else {
            for (Publicacion p : feed) {
               postsContainer.add(crearTarjetaPost(p));
               postsContainer.add(Box.createVerticalStrut(16));
            }
         }
      } catch (IOException ex) {
         JLabel err = new JLabel("Error cargando feed: " + ex.getMessage());
         err.setForeground(Color.RED);
         postsContainer.add(err);
      }
      postsContainer.revalidate();
      postsContainer.repaint();
      SwingUtilities.invokeLater(() -> scrollFeed.getVerticalScrollBar().setValue(0));
   }
   private JPanel crearTarjetaPost(Publicacion p) {
      final int TARGET_WIDTH = 600;
      JPanel card = new FeedCardPanel();
      card.setBackground(Color.WHITE);
      card.setBorder(new CompoundBorder(
            new LineBorder(new Color(219, 219, 219), 1),
            new EmptyBorder(0, 0, 0, 0)));
      card.setAlignmentX(Component.CENTER_ALIGNMENT);
      JPanel head = new JPanel(new BorderLayout());
      head.setBackground(Color.WHITE);
      head.setBorder(new EmptyBorder(10, 14, 10, 14));
      JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
      userInfo.setBackground(Color.WHITE);
      try {
         Usuario autor = frame.getGestorUsuarios().buscarExacto(p.getUsername());
         if (autor != null) {
            Icon fotoCircular = SidebarPanel.cargarFotoCircular(autor.getRutaFotoPerfil(), 44);
            JLabel fotoLbl = new JLabel(fotoCircular != null
                  ? fotoCircular
                  : new ImageIcon(avatarCircle(44)));
            userInfo.add(fotoLbl);
         }
      } catch (IOException ignored) {
      }
      String cleanUser = p.getUsername();
      if (cleanUser.startsWith("@"))
         cleanUser = cleanUser.substring(1);
      JLabel lblUser = new JLabel(cleanUser);
      lblUser.setFont(new Font("SansSerif", Font.BOLD, 14));
      lblUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblUser.addMouseListener(new FeedUserMouseHandler(p.getUsername()));
      userInfo.add(lblUser);
      head.add(userInfo, BorderLayout.WEST);
      JLabel lblFecha = new JLabel(p.getFecha().toString());
      lblFecha.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblFecha.setForeground(Color.GRAY);
      head.add(lblFecha, BorderLayout.EAST);
      JPanel southPanel = new JPanel();
      southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
      southPanel.setBackground(Color.WHITE);
      JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
      actionBar.setBackground(Color.WHITE);
      actionBar.setAlignmentX(Component.LEFT_ALIGNMENT);
      String myU = frame.getSession().getUsername();
      JButton btnLike = new JButton(p.getLikes().contains(myU) ? "❤" : "♡");
      btnLike.setFont(new Font("SansSerif", Font.PLAIN, 24));
      btnLike.setForeground(p.getLikes().contains(myU) ? new Color(237, 73, 86) : Color.BLACK);
      btnLike.setBorderPainted(false);
      btnLike.setContentAreaFilled(false);
      btnLike.setFocusPainted(false);
      btnLike.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JLabel lblLikesCount = new JLabel(p.getLikes().size() + "");
      lblLikesCount.setFont(new Font("SansSerif", Font.BOLD, 14));
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
            } catch (IOException ex) {
            }
         }
         lblLikesCount.setText(p.getLikes().size() + "");
         try {
            frame.getGestorPublicaciones().actualizar(p);
         } catch (IOException ex) {
            frame.mostrarToast("Error guardando like");
         }
      });
      JButton btnComment = new JButton("💬");
      btnComment.setFont(new Font("SansSerif", Font.PLAIN, 22));
      btnComment.setBorderPainted(false);
      btnComment.setContentAreaFilled(false);
      btnComment.setFocusPainted(false);
      btnComment.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JLabel lblCommCount = new JLabel(p.getComentarios().size() + "");
      lblCommCount.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnComment.addActionListener(e -> ExplorarPanel.mostrarPostAmpliado(frame, p));
      actionBar.add(btnLike);
      actionBar.add(lblLikesCount);
      actionBar.add(btnComment);
      actionBar.add(lblCommCount);
      southPanel.add(actionBar);
      if (!p.getContenido().trim().isEmpty()) {
         JTextArea txtContenido = new JTextArea(p.getContenido());
         txtContenido.setFont(new Font("SansSerif", Font.PLAIN, 14));
         txtContenido.setEditable(false);
         txtContenido.setLineWrap(true);
         txtContenido.setWrapStyleWord(true);
         txtContenido.setBackground(Color.WHITE);
         txtContenido.setBorder(new EmptyBorder(0, 14, 4, 14));
         txtContenido.setAlignmentX(Component.LEFT_ALIGNMENT);
         southPanel.add(txtContenido);
      }
      if (!p.getHashtags().trim().isEmpty()) {
         JPanel hashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
         hashPanel.setBackground(Color.WHITE);
         hashPanel.setBorder(new EmptyBorder(0, 10, 8, 10));
         hashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
         for (String tag : p.getHashtags().trim().split("\\s+")) {
            if (!tag.startsWith("#")) continue;
            JLabel lblTag = new JLabel(tag);
            lblTag.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblTag.setForeground(new Color(0, 55, 210));
            lblTag.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblTag.addMouseListener(new HashtagListener(frame, lblTag, tag));
            hashPanel.add(lblTag);
         }
         if (hashPanel.getComponentCount() > 0) southPanel.add(hashPanel);
      }
      String menciones = p.getMenciones();
      if (menciones != null && !menciones.trim().isEmpty()) {
         JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
         chipsPanel.setBackground(Color.WHITE);
         chipsPanel.setBorder(new EmptyBorder(0, 10, 8, 10));
         chipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
         for (String tag : menciones.trim().split("\\s+")) {
            if (!tag.startsWith("@")) continue;
            final String username = tag.substring(1);
            JLabel chip = new JLabel(tag);
            chip.setFont(new Font("SansSerif", Font.BOLD, 12));
            chip.setForeground(new Color(0, 149, 246));
            chip.setBackground(new Color(232, 244, 255));
            chip.setOpaque(true);
            chip.setBorder(new EmptyBorder(3, 10, 3, 10));
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.addMouseListener(new MencionListener(frame, username));
            chipsPanel.add(chip);
         }
         if (chipsPanel.getComponentCount() > 0) southPanel.add(chipsPanel);
      }
      if (!p.getRutaImagen().isEmpty()) {
         JPanel centerWrapper = new JPanel(new GridBagLayout());
         centerWrapper.setBackground(Color.BLACK);
         JPanel imgPanel = new FeedImagePanel(p.getRutaImagen(), centerWrapper);
         centerWrapper.add(imgPanel);
         card.add(centerWrapper, BorderLayout.CENTER);
      }
      card.add(head, BorderLayout.NORTH);
      card.add(southPanel, BorderLayout.SOUTH);
      JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
      wrapper.setBackground(new Color(250, 250, 250));
      wrapper.setBorder(new EmptyBorder(0, 0, 16, 0));
      wrapper.add(card);
      return wrapper;
   }
   private void abrirPerfil(String username) {
      try {
         Usuario u = frame.getGestorUsuarios().buscarExacto(username);
         if (u != null) frame.abrirPerfilDeUsuario(u);
      } catch (IOException ignored) {}
   }
   private BufferedImage avatarCircle(int size) {
      BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = img.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setColor(new Color(200, 200, 200));
      g.fillOval(0, 0, size, size);
      g.dispose();
      return img;
   }
   private static class HashtagListener extends MouseAdapter {
      private final MainFrame frame;
      private final JLabel label;
      private final String tag;
      public HashtagListener(MainFrame frame, JLabel label, String tag) {
         this.frame = frame;
         this.label = label;
         this.tag = tag;
      }
      @Override
      public void mouseClicked(MouseEvent e) { frame.abrirBusquedaCon(tag); }
      @Override
      public void mouseEntered(MouseEvent e) { label.setText("<html><u>" + tag + "</u></html>"); }
      @Override
      public void mouseExited(MouseEvent e) { label.setText(tag); }
   }

   private static class MencionListener extends MouseAdapter {
      private final MainFrame frame;
      private final String username;
      public MencionListener(MainFrame frame, String username) {
         this.frame = frame;
         this.username = username;
      }
      @Override
      public void mouseClicked(MouseEvent e) {
         try {
            insta.model.Usuario u = frame.getGestorUsuarios().buscarExacto(username);
            if (u != null) frame.abrirPerfilDeUsuario(u);
         } catch (IOException ex) {}
      }
   }
   private static class FeedCardPanel extends JPanel {
      public FeedCardPanel() { super(new BorderLayout()); }
      @Override
      public Dimension getPreferredSize() {
         Dimension d = super.getPreferredSize();
         return new Dimension(602, d.height);
      }
      @Override
      public Dimension getMinimumSize() {
         Dimension d = super.getMinimumSize();
         return new Dimension(602, d.height);
      }
      @Override
      public Dimension getMaximumSize() {
         return new Dimension(602, Integer.MAX_VALUE);
      }
   }

   private class FeedUserMouseHandler extends MouseAdapter {
      private final String username;
      public FeedUserMouseHandler(String u) { this.username = u; }
      @Override
      public void mouseClicked(MouseEvent e) { abrirPerfil(username); }
   }

   private class FeedImagePanel extends JPanel {
      private final String path;
      private final JPanel wrapper;
      private volatile BufferedImage img = null;
      private volatile boolean cargando = false;
      public FeedImagePanel(String p, JPanel w) { this.path = p; this.wrapper = w; }
      @Override
      public void addNotify() {
         super.addNotify();
         if (img == null && !cargando) {
            cargando = true;
            new ImageLoaderWorker(this, path, wrapper).execute();
         }
      }
      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         if (img != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
         }
      }
      @Override
      public Dimension getPreferredSize() {
         if (img != null) return new Dimension(img.getWidth(), img.getHeight());
         return new Dimension(600, 600);
      }
      public void setImg(BufferedImage i) { this.img = i; }
   }

   private static class ImageLoaderWorker extends SwingWorker<BufferedImage, Void> {
      private final FeedImagePanel panel;
      private final String path;
      private final JPanel wrapper;
      public ImageLoaderWorker(FeedImagePanel p, String s, JPanel w) {
         this.panel = p; this.path = s; this.wrapper = w;
      }
      @Override
      protected BufferedImage doInBackground() {
         return SidebarPanel.cargarImagenPost(path);
      }
      @Override
      protected void done() {
         try {
            BufferedImage bi = get();
            if (bi != null) {
               panel.setImg(bi);
               Dimension d = new Dimension(bi.getWidth(), bi.getHeight());
               panel.setPreferredSize(d); panel.setMinimumSize(d); panel.setMaximumSize(d); panel.setSize(d);
               wrapper.setPreferredSize(d); wrapper.setMinimumSize(d); wrapper.setMaximumSize(d);

            }
         } catch (Exception ignored) {}
         panel.revalidate(); panel.repaint();
      }
   }
}
