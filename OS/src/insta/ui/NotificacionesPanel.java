/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;
import insta.model.Notificacion;
import insta.model.Usuario;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
/**
 *
 * @author riche
 */
public class NotificacionesPanel extends JPanel {
   private static final Color BG = Color.WHITE;
   private static final Color BORDER = new Color(219, 219, 219);
   private static final Color GRAY = new Color(115, 115, 115);
   private static final Color BLUE = new Color(0, 149, 246);
   private static final Color ACTIVE = new Color(239, 239, 239);
   private static final Color HOVER = new Color(245, 245, 245);
   private final MainFrame frame;
   private JPanel listaPanel;
   public NotificacionesPanel(MainFrame frame) {
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
      JLabel titulo = new JLabel("Notificaciones");
      titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
      titulo.setForeground(new Color(10, 10, 10));
      header.add(titulo, BorderLayout.WEST);
      JButton btnCerrar = new JButton("✕");
      btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 16));
      btnCerrar.setForeground(new Color(80, 80, 80));
      btnCerrar.setBorderPainted(false);
      btnCerrar.setContentAreaFilled(false);
      btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnCerrar.setFocusPainted(false);
      btnCerrar.addActionListener(e -> frame.toggleNotificaciones());
      header.add(btnCerrar, BorderLayout.EAST);
      add(header, BorderLayout.NORTH);
      listaPanel = new JPanel();
      listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
      listaPanel.setBackground(BG);
      listaPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
      JScrollPane scroll = new JScrollPane(listaPanel);
      scroll.setBorder(null);
      scroll.getVerticalScrollBar().setUnitIncrement(12);
      scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      add(scroll, BorderLayout.CENTER);
   }
   public void cargar() {
      listaPanel.removeAll();
      try {
         String miUser = frame.getSession().getUsername();
         List<String> requests = frame.getGestorFollowers().getRequests(miUser);
         List<Notificacion> notifs = frame.getGestorFollowers().getNotificaciones(miUser);
         boolean hayAlgo = !requests.isEmpty() || !notifs.isEmpty();
         if (!hayAlgo) {
            JLabel vacio = new JLabel("<html><center>No tienes notificaciones todavía.</center></html>");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 14));
            vacio.setForeground(GRAY);
            vacio.setHorizontalAlignment(SwingConstants.CENTER);
            vacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            vacio.setBorder(new EmptyBorder(40, 20, 20, 20));
            listaPanel.add(vacio);
            listaPanel.revalidate();
            listaPanel.repaint();
            return;
         }
         LocalDateTime ahora = LocalDateTime.now();
         List<String> pendientes = new ArrayList<>(requests);
         List<Notificacion> hoy = new ArrayList<>();
         List<Notificacion> semana = new ArrayList<>();
         List<Notificacion> mes = new ArrayList<>();
         List<Notificacion> anterior = new ArrayList<>();
         for (Notificacion n : notifs) {
            if (n.getTimestamp() == null)
               continue; 
            if (n.getTipo() == Notificacion.Tipo.SOLICITUD) {
               continue;
            }
            if (n.getTipo() == Notificacion.Tipo.SEGUIDOR) {
               if (ChronoUnit.DAYS.between(n.getTimestamp(), ahora) > 30)
                  continue;
            }
            long dias = ChronoUnit.DAYS.between(n.getTimestamp().toLocalDate(), ahora.toLocalDate());
            if (dias == 0)
               hoy.add(n);
            else if (dias <= 7)
               semana.add(n);
            else if (dias <= 30)
               mes.add(n);
            else
               anterior.add(n);
         }
         if (!pendientes.isEmpty()) {
            agregarSeparadorSeccion("Solicitudes de seguimiento (" + pendientes.size() + ")");
            for (String reqUser : pendientes) {
               try {
                  Usuario u = frame.getGestorUsuarios().buscarExacto(reqUser);
                  if (u != null)
                     listaPanel.add(crearFilaSolicitud(u, miUser));
               } catch (IOException ignored) {
               }
            }
            listaPanel.add(Box.createVerticalStrut(8));
         }
         if (!hoy.isEmpty()) {
            agregarSeparadorSeccion("Hoy");
            for (Notificacion n : hoy)
               listaPanel.add(crearFilaNotificacion(n, ahora));
         }
         if (!semana.isEmpty()) {
            agregarSeparadorSeccion("Esta semana");
            for (Notificacion n : semana)
               listaPanel.add(crearFilaNotificacion(n, ahora));
         }
         if (!mes.isEmpty()) {
            agregarSeparadorSeccion("Este mes");
            for (Notificacion n : mes)
               listaPanel.add(crearFilaNotificacion(n, ahora));
         }
         if (!anterior.isEmpty()) {
            agregarSeparadorSeccion("Anterior");
            for (Notificacion n : anterior)
               listaPanel.add(crearFilaNotificacion(n, ahora));
         }
      } catch (IOException ex) {
         JLabel err = new JLabel("Error: " + ex.getMessage());
         err.setForeground(Color.RED);
         err.setAlignmentX(Component.LEFT_ALIGNMENT);
         listaPanel.add(err);
      }
      listaPanel.revalidate();
      listaPanel.repaint();
   }
   private JPanel crearFilaSolicitud(Usuario u, String miUser) {
      JPanel fila = new JPanel(new BorderLayout(10, 0));
      fila.setBackground(BG);
      fila.setBorder(new EmptyBorder(8, 20, 8, 20));
      fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
      fila.setAlignmentX(Component.LEFT_ALIGNMENT);
      JLabel avatar = crearAvatar(u, 44);
      fila.add(avatar, BorderLayout.WEST);
      JPanel textoPanel = new JPanel();
      textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
      textoPanel.setBackground(BG);
      JLabel lblUser = new JLabel("@" + u.getUsername());
      lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
      lblUser.setForeground(new Color(10, 10, 10));
      lblUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblUser.addMouseListener(new NotifUserMouseHandler(u));
      JLabel lblSub = new JLabel("solicitó seguirte.");
      lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblSub.setForeground(GRAY);
      textoPanel.add(lblUser);
      textoPanel.add(lblSub);
      fila.add(textoPanel, BorderLayout.CENTER);
      JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
      acciones.setBackground(BG);
      JButton btnConf = botonAccion("Confirmar", BLUE, Color.WHITE);
      btnConf.addActionListener(e -> {
         try {
            frame.getGestorFollowers().aceptarSolicitud(miUser, u.getUsername());
            cargar();
            frame.mostrarToast("Solicitud de @" + u.getUsername() + " aceptada.");
         } catch (IOException ex) {
            frame.mostrarToast("Error: " + ex.getMessage());
         }
      });
      JButton btnElim = botonAccion("Eliminar", ACTIVE, new Color(10, 10, 10));
      btnElim.addActionListener(e -> {
         try {
            frame.getGestorFollowers().rechazarSolicitud(miUser, u.getUsername());
            cargar();
         } catch (IOException ex) {
            frame.mostrarToast("Error: " + ex.getMessage());
         }
      });
      acciones.add(btnConf);
      acciones.add(btnElim);
      fila.add(acciones, BorderLayout.EAST);
      agregarHover(fila, textoPanel, acciones);
      return fila;
   }
   private JPanel crearFilaNotificacion(Notificacion n, LocalDateTime ahora) {
      JPanel fila = new JPanel(new BorderLayout(10, 0));
      fila.setBackground(BG);
      fila.setBorder(new EmptyBorder(8, 20, 8, 20));
      fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
      fila.setAlignmentX(Component.LEFT_ALIGNMENT);
      JLabel avatar;
      try {
         Usuario u = frame.getGestorUsuarios().buscarExacto(n.getAutorUsername());
         avatar = crearAvatar(u, 44);
         if (u != null) {
            avatar.addMouseListener(new NotifAvatarMouseHandler(u));
            avatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         }
      } catch (IOException ignored) {
         avatar = new JLabel("👤");
         avatar.setFont(new Font("SansSerif", Font.PLAIN, 26));
      }
      avatar.setPreferredSize(new Dimension(44, 44));
      fila.add(avatar, BorderLayout.WEST);
      JPanel textoPanel = new JPanel();
      textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
      textoPanel.setBackground(BG);
      JLabel lblTexto = new JLabel("<html><div style='width:230px'>" + n.getTexto() + "</div></html>");
      lblTexto.setFont(new Font("SansSerif", Font.PLAIN, 13));
      lblTexto.setForeground(new Color(10, 10, 10));
      textoPanel.add(lblTexto);
      JLabel lblTiempo = new JLabel(tiempoRelativo(n.getTimestamp(), ahora));
      lblTiempo.setFont(new Font("SansSerif", Font.PLAIN, 11));
      lblTiempo.setForeground(GRAY);
      textoPanel.add(lblTiempo);
      fila.add(textoPanel, BorderLayout.CENTER);
      if (n.getTipo() == Notificacion.Tipo.SEGUIDOR) {
         JButton btnSig = botonAccion("Siguiendo", ACTIVE, new Color(10, 10, 10));
         fila.add(btnSig, BorderLayout.EAST);
      }
      agregarHover(fila, textoPanel, null);
      return fila;
   }
   private void agregarSeparadorSeccion(String titulo) {
      JLabel lbl = new JLabel(titulo);
      lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
      lbl.setForeground(new Color(10, 10, 10));
      lbl.setBorder(new EmptyBorder(14, 20, 6, 20));
      lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
      listaPanel.add(lbl);
   }
   private JLabel crearAvatar(Usuario u, int size) {
      JLabel lbl = new JLabel();
      lbl.setPreferredSize(new Dimension(size, size));
      if (u != null) {
         String ruta = u.getRutaFotoPerfil();
         Icon foto = (ruta != null && !ruta.isEmpty())
               ? SidebarPanel.cargarFotoCircular(ruta, size)
               : null;
         if (foto != null) {
            lbl.setIcon(foto);
         } else {
            lbl.setText("👤");
            lbl.setFont(new Font("SansSerif", Font.PLAIN, size - 6));
         }
      } else {
         lbl.setText("👤");
         lbl.setFont(new Font("SansSerif", Font.PLAIN, size - 6));
      }
      return lbl;
   }
   private void agregarHover(JPanel fila, JPanel textoPanel, JPanel acciones) {
      fila.addMouseListener(new NotifRowHoverHandler(fila, textoPanel, acciones));
   }

   private class NotifUserMouseHandler extends java.awt.event.MouseAdapter {
      private final Usuario u;
      public NotifUserMouseHandler(Usuario u) { this.u = u; }
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) { frame.abrirPerfilDeUsuario(u); }
   }

   private class NotifAvatarMouseHandler extends java.awt.event.MouseAdapter {
      private final Usuario u;
      public NotifAvatarMouseHandler(Usuario u) { this.u = u; }
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) { frame.abrirPerfilDeUsuario(u); }
   }

   private class NotifRowHoverHandler extends java.awt.event.MouseAdapter {
      private final JPanel fila, textoPanel, acciones;
      public NotifRowHoverHandler(JPanel f, JPanel t, JPanel a) {
         this.fila = f; this.textoPanel = t; this.acciones = a;
      }
      @Override
      public void mouseEntered(java.awt.event.MouseEvent e) {
         fila.setBackground(HOVER);
         textoPanel.setBackground(HOVER);
         if (acciones != null) acciones.setBackground(HOVER);
      }
      @Override
      public void mouseExited(java.awt.event.MouseEvent e) {
         fila.setBackground(BG);
         textoPanel.setBackground(BG);
         if (acciones != null) acciones.setBackground(BG);
      }
   }
   private String tiempoRelativo(LocalDateTime ts, LocalDateTime ahora) {
      long mins = ChronoUnit.MINUTES.between(ts, ahora);
      long horas = ChronoUnit.HOURS.between(ts, ahora);
      long dias = ChronoUnit.DAYS.between(ts.toLocalDate(), ahora.toLocalDate());
      if (mins < 1)
         return "Ahora";
      if (mins < 60)
         return mins + " min";
      if (horas < 24)
         return horas + " h";
      if (dias == 1)
         return "Ayer";
      if (dias < 7)
         return dias + " días";
      if (dias < 30) {
         long sem = dias / 7;
         return sem + (sem == 1 ? " semana" : " semanas");
      }
      return ts.toLocalDate().getDayOfMonth() + "-" +
            String.format("%02d", ts.toLocalDate().getMonthValue());
   }
   private JButton botonAccion(String texto, Color bg, Color fg) {
      JButton btn = new JButton(texto);
      btn.setBackground(bg);
      btn.setForeground(fg);
      btn.setFont(new Font("SansSerif", Font.BOLD, 13));
      btn.setFocusPainted(false);
      btn.setBorderPainted(false);
      btn.setOpaque(true);
      btn.setBorder(new EmptyBorder(7, 14, 7, 14));
      btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      return btn;
   }
}
