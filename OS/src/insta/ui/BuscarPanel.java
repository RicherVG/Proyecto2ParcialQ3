/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;
import insta.model.ListaEnlazada;
import insta.model.Publicacion;
import insta.model.Usuario;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author riche
 */
public class BuscarPanel extends JPanel {
   private static final Color BG = Color.WHITE;
   private static final Color BORDER = new Color(219, 219, 219);
   private static final Color GRAY_TXT = new Color(115, 115, 115);
   private static final Color FIELD_BG = new Color(239, 239, 239);
   private static final Color BLUE = new Color(0, 149, 246);
   private final MainFrame frame;
   private JTextField txtBuscar;
   private JPanel resultadosPanel;
   public BuscarPanel(MainFrame frame) {
      this.frame = frame;
      setLayout(new BorderLayout());
      setBackground(BG);
      setPreferredSize(new Dimension(397, 0));
      setBorder(new MatteBorder(0, 0, 0, 1, BORDER));
      buildUI();
   }
   private void buildUI() {
      JPanel header = new JPanel(new BorderLayout());
      header.setBackground(BG);
      header.setBorder(new EmptyBorder(22, 20, 10, 20));
      JLabel titulo = new JLabel("Buscar");
      titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
      titulo.setForeground(new Color(10, 10, 10));
      header.add(titulo, BorderLayout.WEST);
      JButton btnCerrar = new JButton("X");
      btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 16));
      btnCerrar.setForeground(new Color(80, 80, 80));
      btnCerrar.setBorderPainted(false);
      btnCerrar.setContentAreaFilled(false);
      btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnCerrar.setFocusPainted(false);
      btnCerrar.addActionListener(e -> frame.toggleBuscar());
      header.add(btnCerrar, BorderLayout.EAST);
      add(header, BorderLayout.NORTH);
      JPanel body = new JPanel();
      body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
      body.setBackground(BG);
      body.setBorder(new EmptyBorder(0, 20, 0, 20));
      JPanel searchRow = new JPanel(new BorderLayout(8, 0));
      searchRow.setBackground(BG);
      searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
      searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
      txtBuscar = new JTextField();
      txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 14));
      txtBuscar.setBackground(FIELD_BG);
      txtBuscar.setBorder(new CompoundBorder(
            new LineBorder(FIELD_BG, 0),
            new EmptyBorder(8, 12, 8, 12)));
      txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar");
      txtBuscar.addActionListener(e -> buscar());
      JPanel roundedField = new RoundedPanel(12, FIELD_BG);
      roundedField.setLayout(new BorderLayout(6, 0));
      roundedField.setBorder(new EmptyBorder(0, 10, 0, 10));
      JLabel searchIco = new JLabel("🔍");
      searchIco.setFont(new Font("SansSerif", Font.PLAIN, 14));
      searchIco.setForeground(GRAY_TXT);
      roundedField.add(searchIco, BorderLayout.WEST);
      roundedField.add(txtBuscar, BorderLayout.CENTER);
      searchRow.add(roundedField, BorderLayout.CENTER);
      JButton btnBuscar = new JButton("Buscar");
      btnBuscar.setBackground(BLUE);
      btnBuscar.setForeground(Color.WHITE);
      btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 13));
      btnBuscar.setFocusPainted(false);
      btnBuscar.setBorderPainted(false);
      btnBuscar.setOpaque(true);
      btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnBuscar.addActionListener(e -> buscar());
      searchRow.add(btnBuscar, BorderLayout.EAST);
      body.add(searchRow);
      body.add(Box.createVerticalStrut(18));
      JSeparator sep = new JSeparator();
      sep.setForeground(BORDER);
      sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
      sep.setAlignmentX(Component.LEFT_ALIGNMENT);
      body.add(sep);
      body.add(Box.createVerticalStrut(12));
      resultadosPanel = new JPanel();
      resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));
      resultadosPanel.setBackground(BG);
      resultadosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      JLabel hint = new JLabel("Escribe usuario o #hashtag y presiona Enter");
      hint.setFont(new Font("SansSerif", Font.PLAIN, 13));
      hint.setForeground(GRAY_TXT);
      hint.setAlignmentX(Component.LEFT_ALIGNMENT);
      resultadosPanel.add(hint);
      body.add(resultadosPanel);
      JScrollPane scroll = new JScrollPane(body);
      scroll.setBorder(null);
      scroll.getVerticalScrollBar().setUnitIncrement(12);
      scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      add(scroll, BorderLayout.CENTER);
   }
   private void buscar() {
      String query = txtBuscar.getText().trim();
      if (query.isEmpty())
         return;
      resultadosPanel.removeAll();
      if (query.startsWith("#")) {
         buscarHashtag(query.substring(1));
      } else {
         buscarUsuario(query.startsWith("@") ? query.substring(1) : query);
      }
      resultadosPanel.revalidate();
      resultadosPanel.repaint();
   }
   public void setBusqueda(String query) {
      txtBuscar.setText(query);
      buscar();
   }
   private void buscarUsuario(String parcial) {
      try {
         ListaEnlazada<Usuario> encontrados = frame.getGestorUsuarios().buscarPorUsernameEnlazada(parcial);
         if (encontrados.isEmpty()) {
            sinResultados("No se encontraron usuarios con '" + parcial + "'.");
            return;
         }
         JLabel seccion = new JLabel("Usuarios  (" + encontrados.size() + ")");
         seccion.setFont(new Font("SansSerif", Font.BOLD, 13));
         seccion.setForeground(new Color(10, 10, 10));
         seccion.setAlignmentX(Component.LEFT_ALIGNMENT);
         resultadosPanel.add(seccion);
         resultadosPanel.add(Box.createVerticalStrut(8));
         for (Usuario u : encontrados) {
            resultadosPanel.add(crearFilaUsuario(u));
            resultadosPanel.add(Box.createVerticalStrut(2));
         }
      } catch (IOException ex) {
         sinResultados("Error: " + ex.getMessage());
      }
   }
   private JPanel crearFilaUsuario(Usuario u) {
      JPanel fila = new JPanel(new BorderLayout(12, 0));
      fila.setBackground(BG);
      fila.setBorder(new EmptyBorder(8, 0, 8, 0));
      fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
      fila.setAlignmentX(Component.LEFT_ALIGNMENT);
      fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      Icon foto = SidebarPanel.cargarFotoCircular(u.getRutaFotoPerfil(), 44);
      JLabel avatar = new JLabel(foto != null ? foto : null);
      if (foto == null) {
         avatar.setText("👤");
         avatar.setFont(new Font("SansSerif", Font.PLAIN, 28));
      }
      avatar.setPreferredSize(new Dimension(44, 44));
      fila.add(avatar, BorderLayout.WEST);
      JPanel info = new JPanel();
      info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
      info.setBackground(BG);
      JLabel lblUser = new JLabel(u.getUsername());
      lblUser.setFont(new Font("SansSerif", Font.BOLD, 14));
      lblUser.setForeground(new Color(10, 10, 10));
      info.add(lblUser);
      boolean sigo = false;
      try {
         String miUser = frame.getSession().getUsername();
         sigo = frame.getGestorFollowers().esSeguidor(miUser, u.getUsername());
      } catch (Exception ignored) {}
      String estadoSeguimiento = sigo ? "Lo sigues" : "No lo sigues";
      String detalles = String.format("%s — %s  ·  %s (%d años)",
            u.getUsername(),
            estadoSeguimiento,
            u.getNombreCompleto(),
            u.getEdad()
      );
      JLabel lblDetalles = new JLabel(detalles);
      lblDetalles.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblDetalles.setForeground(GRAY_TXT);
      info.add(lblDetalles);
      fila.add(info, BorderLayout.CENTER);
      JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
      btns.setBackground(BG);
      try {
         String miUser = frame.getSession().getUsername();
         if (!miUser.equalsIgnoreCase(u.getUsername())) {
            boolean solicitado = !sigo && frame.getGestorFollowers().haSolicitado(miUser, u.getUsername());
            String btnTexto = sigo ? "Siguiendo" : (solicitado ? "Solicitado" : "Seguir");
            Color bgBtn = (sigo || solicitado) ? new Color(239, 239, 239) : BLUE;
            Color fgBtn = (sigo || solicitado) ? new Color(10, 10, 10) : Color.WHITE;
            JButton btnSeg = new JButton(btnTexto);
            btnSeg.setBackground(bgBtn);
            btnSeg.setForeground(fgBtn);
            btnSeg.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnSeg.setFocusPainted(false);
            btnSeg.setBorderPainted(false);
            btnSeg.setOpaque(true);
            btnSeg.setBorder(new EmptyBorder(6, 14, 6, 14));
            btnSeg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnSeg.addActionListener(e -> {
               try {
                  boolean sigoAhora = frame.getGestorFollowers().esSeguidor(miUser, u.getUsername());
                  boolean solAhora = !sigoAhora && frame.getGestorFollowers().haSolicitado(miUser, u.getUsername());
                  if (sigoAhora) {
                     frame.mostrarConfirmacion(
                           "Dejar de seguir",
                           "¿Dejar de seguir a @" + u.getUsername() + "?",
                           "Dejar de seguir",
                           () -> {
                              try {
                                 frame.getGestorFollowers().dejarDeSeguir(miUser, u.getUsername());
                                 buscar();
                              } catch (IOException ex) {
                                 frame.mostrarToast("Error: " + ex.getMessage());
                              }
                           });
                  } else if (solAhora) {
                     frame.getGestorFollowers().rechazarSolicitud(u.getUsername(), miUser);
                     buscar();
                  } else {
                     if (u.getTipoCuenta().equalsIgnoreCase("PRIVADA")) {
                        frame.getGestorFollowers().enviarSolicitud(miUser, u.getUsername());
                        frame.mostrarToast("Solicitud enviada a @" + u.getUsername());
                     } else {
                        frame.getGestorFollowers().seguir(miUser, u.getUsername());
                     }
                     buscar();
                  }
               } catch (IOException ex) {
                  frame.mostrarToast("Error: " + ex.getMessage());
               }
            });
            btns.add(btnSeg);
         }
      } catch (IOException ignored) {
      }
      JButton btnPerfil = new JButton("Ver perfil");
      btnPerfil.setBackground(new Color(239, 239, 239));
      btnPerfil.setForeground(new Color(10, 10, 10));
      btnPerfil.setFont(new Font("SansSerif", Font.BOLD, 12));
      btnPerfil.setFocusPainted(false);
      btnPerfil.setBorderPainted(false);
      btnPerfil.setOpaque(true);
      btnPerfil.setBorder(new EmptyBorder(6, 14, 6, 14));
      btnPerfil.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnPerfil.addActionListener(e -> frame.abrirPerfilDeUsuario(u));
      btns.add(btnPerfil);
      fila.add(btns, BorderLayout.EAST);
      fila.addMouseListener(new SearchUserMouseHandler(u, fila, info, btns));
      return fila;
   }
   private void buscarHashtag(String hashtag) {
      try {
         List<Publicacion> rawPosts = frame.getGestorPublicaciones()
               .buscarHashtag(hashtag, frame.getGestorUsuarios());
         java.util.Collections.sort(rawPosts);
         ListaEnlazada<Publicacion> posts = ListaEnlazada.fromList(rawPosts);
         if (posts.isEmpty()) {
            sinResultados("No hay publicaciones con #" + hashtag + ".");
            return;
         }
         JLabel sec = new JLabel("Publicaciones con #" + hashtag + " (" + posts.size() + ")");
         sec.setFont(new Font("SansSerif", Font.BOLD, 13));
         sec.setAlignmentX(Component.LEFT_ALIGNMENT);
         resultadosPanel.add(sec);
         resultadosPanel.add(Box.createVerticalStrut(8));
         for (Publicacion p : posts) {
            resultadosPanel.add(crearFilaPublicacion(p));
            resultadosPanel.add(Box.createVerticalStrut(2));
         }
      } catch (IOException ex) {
         sinResultados("Error: " + ex.getMessage());
      }
   }
   private JPanel crearFilaPublicacion(Publicacion p) {
      JPanel card = new JPanel(new BorderLayout());
      card.setBackground(BG);
      card.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(10, 0, 10, 0)));
      card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
      card.setAlignmentX(Component.LEFT_ALIGNMENT);
      JLabel user = new JLabel(p.getUsername() + "  ·  " + p.getFecha());
      user.setFont(new Font("SansSerif", Font.BOLD, 13));
      card.add(user, BorderLayout.NORTH);
      JLabel contenido = new JLabel("<html><div style='width:300px'>" +
            p.getContenido() + "</div></html>");
      contenido.setFont(new Font("SansSerif", Font.PLAIN, 13));
      contenido.setBorder(new EmptyBorder(4, 0, 4, 0));
      card.add(contenido, BorderLayout.CENTER);
      JLabel tags = new JLabel(p.getHashtags() + "  " + p.getMenciones());
      tags.setFont(new Font("SansSerif", Font.PLAIN, 12));
      tags.setForeground(BLUE);
      card.add(tags, BorderLayout.SOUTH);
      card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      card.addMouseListener(new SearchPostMouseHandler(p));
      return card;
   }
   private void sinResultados(String msg) {
      JLabel lbl = new JLabel(msg);
      lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
      lbl.setForeground(GRAY_TXT);
      lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
      resultadosPanel.add(Box.createVerticalStrut(16));
      resultadosPanel.add(lbl);
   }

   private class SearchUserMouseHandler extends java.awt.event.MouseAdapter {
      private final Usuario u;
      private final JPanel fila, info, btns;
      public SearchUserMouseHandler(Usuario u, JPanel f, JPanel i, JPanel b) {
         this.u = u; this.fila = f; this.info = i; this.btns = b;
      }
      @Override
      public void mouseEntered(java.awt.event.MouseEvent e) {
         Color h = new Color(245, 245, 245);
         fila.setBackground(h); info.setBackground(h); btns.setBackground(h);
      }
      @Override
      public void mouseExited(java.awt.event.MouseEvent e) {
         fila.setBackground(BG); info.setBackground(BG); btns.setBackground(BG);
      }
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) { frame.abrirPerfilDeUsuario(u); }
   }

   private class SearchPostMouseHandler extends java.awt.event.MouseAdapter {
      private final Publicacion p;
      public SearchPostMouseHandler(Publicacion p) { this.p = p; }
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) { ExplorarPanel.mostrarPostAmpliado(frame, p); }
   }

   private static class RoundedPanel extends JPanel {
      private final int radius;
      private final Color bg;
      RoundedPanel(int radius, Color bg) {
         this.radius = radius;
         this.bg = bg;
         setOpaque(false);
      }
      @Override
      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D) g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setColor(bg);
         g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
         g2.dispose();
         super.paintComponent(g);
      }
   }
}
