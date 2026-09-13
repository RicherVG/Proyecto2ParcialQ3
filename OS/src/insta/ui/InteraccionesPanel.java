package insta.ui;

import insta.model.Publicacion;
import insta.model.Usuario;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 *
 * @author riche
 */
public class InteraccionesPanel extends JPanel {
   private static final Color BG = new Color(250, 250, 250);
   private static final Color CARD_BG = Color.WHITE;
   private static final Color BORDER = new Color(219, 219, 219);
   private static final Color GRAY_TXT = new Color(115, 115, 115);
   private static final Color BLUE = new Color(0, 149, 246);

   private final MainFrame frame;
   private JPanel feedContainer;

   public InteraccionesPanel(MainFrame frame) {
      this.frame = frame;
      setLayout(new BorderLayout());
      setBackground(BG);
      buildUI();
   }

   private void buildUI() {
      JPanel header = new JPanel(new BorderLayout());
      header.setBackground(CARD_BG);
      header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(16, 28, 16, 28)));

      JLabel lblTitulo = new JLabel("Interacciones (Menciones)");
      lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
      lblTitulo.setForeground(new Color(10, 10, 10));
      header.add(lblTitulo, BorderLayout.WEST);

      JButton btnRefrescar = new JButton("↻ Actualizar");
      btnRefrescar.setFont(new Font("SansSerif", Font.BOLD, 13));
      btnRefrescar.setForeground(BLUE);
      btnRefrescar.setBackground(CARD_BG);
      btnRefrescar.setBorder(null);
      btnRefrescar.setFocusPainted(false);
      btnRefrescar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnRefrescar.addActionListener(e -> cargarInteracciones());
      header.add(btnRefrescar, BorderLayout.EAST);

      add(header, BorderLayout.NORTH);

      feedContainer = new JPanel();
      feedContainer.setLayout(new BoxLayout(feedContainer, BoxLayout.Y_AXIS));
      feedContainer.setBackground(BG);
      feedContainer.setBorder(new EmptyBorder(24, 0, 30, 0));

      JScrollPane scroll = new JScrollPane(feedContainer);
      scroll.setBorder(null);
      scroll.getVerticalScrollBar().setUnitIncrement(16);
      scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scroll.getViewport().setBackground(BG);

      add(scroll, BorderLayout.CENTER);
   }

   public void cargarInteracciones() {
      feedContainer.removeAll();
      Usuario yo = frame.getSession();
      if (yo == null) return;

      try {
         List<Publicacion> menciones = frame.getGestorPublicaciones()
               .getMenciones(yo.getUsername(), frame.getGestorUsuarios());

         if (menciones.isEmpty()) {
            JPanel vacio = new JPanel();
            vacio.setLayout(new BoxLayout(vacio, BoxLayout.Y_AXIS));
            vacio.setBackground(BG);
            vacio.setBorder(new EmptyBorder(60, 20, 60, 20));

            JLabel ico = new JLabel("@", SwingConstants.CENTER);
            ico.setFont(new Font("SansSerif", Font.BOLD, 54));
            ico.setForeground(new Color(180, 180, 180));
            ico.setAlignmentX(Component.CENTER_ALIGNMENT);
            vacio.add(ico);

            vacio.add(Box.createVerticalStrut(12));
            JLabel txt = new JLabel("Aún no tienes menciones de otros usuarios.", SwingConstants.CENTER);
            txt.setFont(new Font("SansSerif", Font.BOLD, 16));
            txt.setForeground(new Color(40, 40, 40));
            txt.setAlignmentX(Component.CENTER_ALIGNMENT);
            vacio.add(txt);

            vacio.add(Box.createVerticalStrut(6));
            JLabel sub = new JLabel("Cuando otros usuarios te etiqueten con @" + yo.getUsername() + ", aparecerán aquí.", SwingConstants.CENTER);
            sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
            sub.setForeground(GRAY_TXT);
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);
            vacio.add(sub);

            feedContainer.add(vacio);
         } else {
            JLabel lblCount = new JLabel("Publicaciones donde te mencionaron (" + menciones.size() + "):");
            lblCount.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblCount.setForeground(GRAY_TXT);
            lblCount.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblCount.setBorder(new EmptyBorder(0, 0, 16, 0));
            feedContainer.add(lblCount);

            for (Publicacion p : menciones) {
               feedContainer.add(crearTarjetaMencion(p));
               feedContainer.add(Box.createVerticalStrut(18));
            }
         }
      } catch (IOException ex) {
         JLabel err = new JLabel("Error al cargar interacciones: " + ex.getMessage());
         err.setForeground(Color.RED);
         err.setAlignmentX(Component.CENTER_ALIGNMENT);
         feedContainer.add(err);
      }

      feedContainer.revalidate();
      feedContainer.repaint();
   }

   private JPanel crearTarjetaMencion(Publicacion p) {
      JPanel card = new JPanel(new BorderLayout());
      card.setBackground(CARD_BG);
      card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(16, 18, 16, 18)));
      card.setMaximumSize(new Dimension(560, Integer.MAX_VALUE));
      card.setAlignmentX(Component.CENTER_ALIGNMENT);

      JPanel header = new JPanel(new BorderLayout(10, 0));
      header.setBackground(CARD_BG);

      Usuario autor = null;
      try {
         autor = frame.getGestorUsuarios().buscarExacto(p.getUsername());
      } catch (Exception ignored) {}

      String rutaFoto = autor != null ? autor.getRutaFotoPerfil() : "";
      Icon iconFoto = SidebarPanel.cargarFotoCircular(rutaFoto, 40);
      JLabel lblAvatar = new JLabel(iconFoto != null ? iconFoto : null);
      if (iconFoto == null) {
         lblAvatar.setText("👤");
         lblAvatar.setFont(new Font("SansSerif", Font.PLAIN, 24));
      }
      lblAvatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      final Usuario finalAutor = autor;
      lblAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent e) {
            if (finalAutor != null) frame.abrirPerfilDeUsuario(finalAutor);
         }
      });
      header.add(lblAvatar, BorderLayout.WEST);

      JPanel infoAutor = new JPanel(new GridLayout(2, 1, 0, 2));
      infoAutor.setBackground(CARD_BG);
      JLabel lblUser = new JLabel("@" + p.getUsername());
      lblUser.setFont(new Font("SansSerif", Font.BOLD, 14));
      lblUser.setForeground(new Color(10, 10, 10));
      lblUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblUser.addMouseListener(new java.awt.event.MouseAdapter() {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent e) {
            if (finalAutor != null) frame.abrirPerfilDeUsuario(finalAutor);
         }
      });

      JLabel lblFecha = new JLabel(p.getFecha() + " · " + p.getHora());
      lblFecha.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblFecha.setForeground(GRAY_TXT);

      infoAutor.add(lblUser);
      infoAutor.add(lblFecha);
      header.add(infoAutor, BorderLayout.CENTER);

      card.add(header, BorderLayout.NORTH);

      JPanel body = new JPanel();
      body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
      body.setBackground(CARD_BG);
      body.setBorder(new EmptyBorder(12, 0, 8, 0));

      if (p.getRutaImagen() != null && !p.getRutaImagen().trim().isEmpty()) {
         File imgFile = new File(p.getRutaImagen());
         if (imgFile.exists()) {
            JLabel lblFoto = new JLabel();
            lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
            new SwingWorker<ImageIcon, Void>() {
               @Override
               protected ImageIcon doInBackground() {
                  ImageIcon orig = new ImageIcon(imgFile.getAbsolutePath());
                  Image scaled = orig.getImage().getScaledInstance(520, 320, Image.SCALE_SMOOTH);
                  return new ImageIcon(scaled);
               }
               @Override
               protected void done() {
                  try {
                     lblFoto.setIcon(get());
                  } catch (Exception ignored) {}
               }
            }.execute();
            body.add(lblFoto);
            body.add(Box.createVerticalStrut(10));
         }
      }

      JLabel lblContenido = new JLabel("<html><div style='width:500px;font-size:13px;'>"
            + "<b>@" + p.getUsername() + ":</b> " + p.getContenido() + "</div></html>");
      lblContenido.setFont(new Font("SansSerif", Font.PLAIN, 14));
      body.add(lblContenido);

      if (p.getHashtags() != null && !p.getHashtags().trim().isEmpty()) {
         body.add(Box.createVerticalStrut(4));
         JLabel lblHash = new JLabel(p.getHashtags());
         lblHash.setFont(new Font("SansSerif", Font.BOLD, 12));
         lblHash.setForeground(BLUE);
         body.add(lblHash);
      }

      card.add(body, BorderLayout.CENTER);

      JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
      footer.setBackground(CARD_BG);
      footer.setBorder(new MatteBorder(1, 0, 0, 0, new Color(240, 240, 240)));

      JLabel lblLikes = new JLabel("♥ " + (p.getLikes() != null ? p.getLikes().size() : 0) + " me gusta");
      lblLikes.setFont(new Font("SansSerif", Font.PLAIN, 13));
      lblLikes.setForeground(GRAY_TXT);

      JLabel lblComms = new JLabel("💬 " + (p.getComentarios() != null ? p.getComentarios().size() : 0) + " comentarios");
      lblComms.setFont(new Font("SansSerif", Font.PLAIN, 13));
      lblComms.setForeground(GRAY_TXT);

      footer.add(lblLikes);
      footer.add(lblComms);

      card.add(footer, BorderLayout.SOUTH);

      return card;
   }
}
