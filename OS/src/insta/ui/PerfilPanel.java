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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author riche
 */
public class PerfilPanel extends JPanel {
   private final MainFrame frame;
   private Usuario usuarioPerfil; 
   private JPanel headerPanel;
   private JPanel gridPanel;
   private JScrollPane scrollPerfil;
   public PerfilPanel(MainFrame frame) {
      this.frame = frame;
      setLayout(new BorderLayout());
      setBackground(Color.WHITE);
   }
   public void mostrarPerfil(Usuario u) {
      this.usuarioPerfil = u;
      removeAll();
      buildHeaderSection();
      buildGridSection();
      revalidate();
      repaint();
      if (scrollPerfil != null) {
         SwingUtilities.invokeLater(() -> scrollPerfil.getVerticalScrollBar().setValue(0));
      }
   }
   private void buildHeaderSection() {
      headerPanel = new JPanel();
      headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
      headerPanel.setBackground(Color.WHITE);
      headerPanel.setBorder(new EmptyBorder(24, 40, 16, 40));
      JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
      topRow.setBackground(Color.WHITE);
      JLabel fotoLbl = new JLabel();
      Icon foto = SidebarPanel.cargarFotoPerfil(usuarioPerfil.getRutaFotoPerfil());
      if (foto != null)
         fotoLbl.setIcon(foto);
      else {
         try {
            BufferedImage ph = new BufferedImage(320, 320, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gph = ph.createGraphics();
            gph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gph.setColor(new Color(230, 230, 230));
            gph.fillOval(0, 0, 320, 320);
            gph.dispose();
            fotoLbl.setIcon(new ImageIcon(ph));
         } catch (Exception ignored) {
         }
      }
      topRow.add(fotoLbl);
      JPanel infoPanel = new JPanel();
      infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
      infoPanel.setBackground(Color.WHITE);
      JPanel userHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
      userHeader.setBackground(Color.WHITE);
      JLabel lblUsername = new JLabel(usuarioPerfil.getUsername());
      lblUsername.setFont(new Font("SansSerif", Font.BOLD, 22));
      userHeader.add(lblUsername);
      JPanel accionesHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
      accionesHeader.setBackground(Color.WHITE);
      buildAcciones(accionesHeader);
      userHeader.add(accionesHeader);
      infoPanel.add(userHeader);
      infoPanel.add(Box.createVerticalStrut(12));
      JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
      stats.setBackground(Color.WHITE);
      try {
         int numPosts = frame.getGestorPublicaciones().getPostsDeUsuario(usuarioPerfil.getUsername()).size();
         int numFollowers = frame.getGestorFollowers().getFollowers(usuarioPerfil.getUsername()).size();
         int numFollowing = frame.getGestorFollowers().getFollowing(usuarioPerfil.getUsername()).size();
         boolean autorizado = puedeVerPublicaciones();
         stats.add(statLabel(numPosts, "publicaciones", null));
         stats.add(statLabel(numFollowers, "seguidores", autorizado ? () -> abrirListaSeguidores() : null));
         stats.add(statLabel(numFollowing, "seguidos", autorizado ? () -> abrirListaSeguidos() : null));
      } catch (IOException ignored) {
      }
      infoPanel.add(stats);
      infoPanel.add(Box.createVerticalStrut(12));
      JLabel lblNombre = new JLabel(usuarioPerfil.getNombreCompleto());
      lblNombre.setFont(new Font("SansSerif", Font.BOLD, 14));
      infoPanel.add(lblNombre);
      if (usuarioPerfil.getPresentacion() != null && !usuarioPerfil.getPresentacion().trim().isEmpty()) {
         JLabel lblPres = new JLabel("<html><div style='width:350px;'>" +
               usuarioPerfil.getPresentacion().replace("\n", "<br>") + "</div></html>");
         lblPres.setFont(new Font("SansSerif", Font.PLAIN, 14));
         infoPanel.add(lblPres);
      }
      infoPanel.add(Box.createVerticalStrut(8));
      String infoExtra = (usuarioPerfil.getTipoCuenta().equals("PRIVADA") ? "🔒 Privada" : "🌐 Pública") +
            "  ·  " + usuarioPerfil.getGenero() + "  ·  " + usuarioPerfil.getEdad() + " años" +
            usuarioPerfil.getFechaRegistro() +
            "  ·  " + (usuarioPerfil.isActivo() ? "Activa" : "Desactivada");
      JLabel lblExtra = new JLabel(infoExtra);
      lblExtra.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblExtra.setForeground(Color.GRAY);
      infoPanel.add(lblExtra);
      topRow.add(infoPanel);
      headerPanel.add(topRow);
      JSeparator sep = new JSeparator();
      sep.setForeground(new Color(219, 219, 219));
      headerPanel.add(Box.createVerticalStrut(12));
      headerPanel.add(sep);
      add(headerPanel, BorderLayout.NORTH);
   }
   private void buildAcciones(JPanel acciones) {
      String miUser = frame.getSession().getUsername();
      String targetUser = usuarioPerfil.getUsername();
      boolean esMiPerfil = miUser.equalsIgnoreCase(targetUser);
      try {
         if (esMiPerfil) {
            JButton btnEdit = accionBtn("Editar perfil", new Color(239, 239, 239), new Color(10, 10, 10));
            btnEdit.addActionListener(e -> frame.mostrarEditarPerfil());
            acciones.add(btnEdit);
            if (usuarioPerfil.isActivo()) {
               JButton btnDes = accionBtn("Desactivar cuenta", new Color(255, 220, 220), new Color(180, 30, 30));
               btnDes.addActionListener(e -> desactivarCuenta());
               acciones.add(btnDes);
            } else {
               JButton btnAct = accionBtn("Activar cuenta", new Color(220, 255, 220), new Color(30, 130, 30));
               btnAct.addActionListener(e -> activarCuenta());
               acciones.add(btnAct);
            }
         } else {
            boolean siguo = frame.getGestorFollowers().esSeguidor(miUser, targetUser);
            boolean solicitado = !siguo && frame.getGestorFollowers().haSolicitado(miUser, targetUser);
            String btnTexto = siguo ? "Siguiendo" : (solicitado ? "Solicitado" : "Seguir");
            Color bgBtn = (siguo || solicitado) ? new Color(239, 239, 239) : new Color(0, 149, 246);
            Color fgBtn = (siguo || solicitado) ? new Color(10, 10, 10) : Color.WHITE;
            JButton btnSeguir = accionBtn(btnTexto, bgBtn, fgBtn);
            btnSeguir.addActionListener(e -> toggleSeguir(siguo, solicitado));
            acciones.add(btnSeguir);
            JButton btnMsg = accionBtn("Mensaje", new Color(239, 239, 239), new Color(10, 10, 10));
            btnMsg.addActionListener(e -> frame.abrirMensajesCon(targetUser));
            acciones.add(btnMsg);
         }
      } catch (IOException ignored) {
      }
   }
   private void toggleSeguir(boolean yaLoSigo, boolean yaSolicitado) {
      String miUser = frame.getSession().getUsername();
      String targetUser = usuarioPerfil.getUsername();
      try {
         if (yaLoSigo) {
            frame.mostrarConfirmacion(
                  "Dejar de seguir",
                  "¿Dejar de seguir a @" + targetUser + "?",
                  "Dejar de seguir",
                  () -> {
                     try {
                        frame.getGestorFollowers().dejarDeSeguir(miUser, targetUser);
                        mostrarPerfil(usuarioPerfil);
                     } catch (IOException ex) {
                     }
                  });
         } else if (yaSolicitado) {
            frame.getGestorFollowers().rechazarSolicitud(targetUser, miUser);
            mostrarPerfil(usuarioPerfil);
         } else {
            if (usuarioPerfil.getTipoCuenta().equalsIgnoreCase("PRIVADA")) {
               frame.getGestorFollowers().enviarSolicitud(miUser, targetUser);
               frame.mostrarToast("Solicitud enviada a @" + targetUser);
            } else {
               frame.getGestorFollowers().seguir(miUser, targetUser);
            }
            mostrarPerfil(usuarioPerfil); 
         }
      } catch (IOException ex) {
         frame.mostrarToast("Error: " + ex.getMessage());
      }
   }
   private void desactivarCuenta() {
      frame.mostrarConfirmacion(
            "Desactivar cuenta",
            "¿Desactivar tu cuenta? No aparecerás en búsquedas ni podrás iniciar sesión.",
            "Desactivar",
            () -> {
               try {
                  frame.getGestorUsuarios().desactivar(usuarioPerfil.getUsername());
                  frame.mostrarToast("Cuenta desactivada. Cerrando sesión...");
                  frame.cerrarSesion();
               } catch (IOException ex) {
                  frame.mostrarToast("Error: " + ex.getMessage());
               }
            });
   }
   private void activarCuenta() {
      try {
         frame.getGestorUsuarios().activar(usuarioPerfil.getUsername());
         usuarioPerfil.setActivo(true);
         frame.mostrarToast("Cuenta activada exitosamente.");
         mostrarPerfil(usuarioPerfil);
      } catch (IOException ex) {
         frame.mostrarToast("Error: " + ex.getMessage());
      }
   }
   private void buildGridSection() {
      try {
         boolean puedeVer = puedeVerPublicaciones();
         if (!puedeVer) {
            JLabel privado = new JLabel("🔒 Esta cuenta es privada.", SwingConstants.CENTER);
            privado.setFont(new Font("SansSerif", Font.PLAIN, 14));
            privado.setForeground(Color.GRAY);
            add(privado, BorderLayout.CENTER);
            return;
         }
         List<Publicacion> posts = frame.getGestorPublicaciones()
               .getPostsDeUsuario(usuarioPerfil.getUsername());
         java.util.Collections.sort(posts);
         gridPanel = new JPanel();
         gridPanel.setBackground(Color.WHITE);
         if (posts.isEmpty()) {
            gridPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            JLabel vacio = new JLabel("Sin publicaciones.", SwingConstants.CENTER);
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 13));
            vacio.setForeground(Color.GRAY);
            gridPanel.add(vacio);
         } else {
            gridPanel.setLayout(new GridLayout(0, 3, 4, 4));
            for (Publicacion p : posts) {
               gridPanel.add(crearMiniaturaGrid(p));
            }
         }
      } catch (IOException ex) {
         gridPanel = new JPanel();
         gridPanel.add(new JLabel("Error: " + ex.getMessage()));
      }
      JPanel containerConstrainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
      containerConstrainer.setBackground(Color.WHITE);
      containerConstrainer.add(gridPanel);
      JPanel wrapGrid = new JPanel(new BorderLayout());
      wrapGrid.setBackground(Color.WHITE);
      wrapGrid.add(containerConstrainer, BorderLayout.NORTH);
      scrollPerfil = new JScrollPane(wrapGrid);
      scrollPerfil.setBorder(null);
      scrollPerfil.getVerticalScrollBar().setUnitIncrement(16);
      scrollPerfil.getHorizontalScrollBar().setUnitIncrement(16);
      add(scrollPerfil, BorderLayout.CENTER);
   }
   private JPanel crearMiniaturaGrid(Publicacion p) {
      JPanel item = new JPanel(new BorderLayout());
      item.setBackground(Color.WHITE);
      Dimension itemSize = new Dimension(300, 300);
      item.setPreferredSize(itemSize);
      item.setMinimumSize(itemSize);
      item.setMaximumSize(itemSize);
      item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JLabel imgLbl = new JLabel("", SwingConstants.CENTER);
      imgLbl.setPreferredSize(itemSize);
      imgLbl.setMinimumSize(itemSize);
      imgLbl.setMaximumSize(itemSize);
      new GridThumbnailWorker(imgLbl, p, itemSize).execute();
      item.add(imgLbl, BorderLayout.CENTER);
      item.addMouseListener(new GridItemMouseHandler(frame, p));
      JPanel strictWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
      strictWrap.setBackground(Color.WHITE);
      strictWrap.add(item);
      return strictWrap;
   }
   private boolean puedeVerPublicaciones() throws IOException {
      if (usuarioPerfil.getTipoCuenta().equals("PUBLICA"))
         return true;
      String miUser = frame.getSession().getUsername();
      if (miUser.equalsIgnoreCase(usuarioPerfil.getUsername()))
         return true;
      return frame.getGestorFollowers().sonAmigos(miUser, usuarioPerfil.getUsername());
   }
   private JLabel statLabel(int count, String label, Runnable onClick) {
      JLabel lbl = new JLabel("<html><b>" + count + "</b><br><span style='color:gray'>" + label + "</span></html>");
      lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
      if (onClick != null) {
         lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         lbl.addMouseListener(new StatMouseHandler(onClick));
      }
      return lbl;
   }
   private void abrirListaSeguidores() {
      try {
         List<String> lista = frame.getGestorFollowers().getFollowers(usuarioPerfil.getUsername());
         frame.mostrarListaUsuariosModal("Seguidores de @" + usuarioPerfil.getUsername(), lista);
      } catch (IOException ex) {
         frame.mostrarToast("Error al cargar seguidores");
      }
   }
   private void abrirListaSeguidos() {
      try {
         List<String> lista = frame.getGestorFollowers().getFollowing(usuarioPerfil.getUsername());
         frame.mostrarListaUsuariosModal("Seguidos por @" + usuarioPerfil.getUsername(), lista);
      } catch (IOException ex) {
         frame.mostrarToast("Error al cargar seguidos");
      }
   }
   private JButton accionBtn(String text, Color bg, Color fg) {
      JButton btn = new JButton(text);
      btn.setBackground(bg);
      btn.setForeground(fg);
      btn.setFont(new Font("SansSerif", Font.BOLD, 13));
      btn.setFocusPainted(false);
      btn.setBorderPainted(false);
      btn.setOpaque(true);
      btn.setBorder(new EmptyBorder(8, 14, 8, 14));
      btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      return btn;
   }
   private static class GridThumbnailWorker extends SwingWorker<Icon, Void> {
      private final JLabel label;
      private final Publicacion post;
      public GridThumbnailWorker(JLabel l, Publicacion p, Dimension s) {
         this.label = l; this.post = p;
      }
      @Override
      protected Icon doInBackground() {
         return SidebarPanel.cargarMiniaturaGrid(post.getRutaImagen());
      }
      @Override
      protected void done() {
         try {
            Icon icn = get();
            if (icn != null) {
               label.setIcon(icn);
            } else {
               label.setOpaque(true); label.setBackground(new Color(245, 245, 245)); label.setText("⚠");
            }
         } catch (Exception ignored) {}
      }
   }

   private static class GridItemMouseHandler extends MouseAdapter {
      private final MainFrame frame;
      private final Publicacion post;
      public GridItemMouseHandler(MainFrame f, Publicacion p) { this.frame = f; this.post = p; }
      @Override
      public void mouseClicked(MouseEvent e) { ExplorarPanel.mostrarPostAmpliado(frame, post); }
   }

   private static class StatMouseHandler extends MouseAdapter {
      private final Runnable action;
      public StatMouseHandler(Runnable r) { this.action = r; }
      @Override
      public void mouseClicked(MouseEvent e) { action.run(); }
   }
}
