/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;
import insta.model.Usuario;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 *
 * @author riche
 */
public class SidebarPanel extends JPanel {
   private static final int W_FULL = 240;
   private static final int W_MINI = 72;
   private static final Color BG = Color.WHITE;
   private static final Color HOVER_BG = new Color(245, 245, 245);
   private static final Color ACTIVE_BG = new Color(238, 238, 238);
   private static final Color TEXT_COLOR = new Color(10, 10, 10);
   private static final Color BORDER_COLOR = new Color(219, 219, 219);
   private final MainFrame frame;
   private boolean collapsed = true;
   private boolean hoverEnabled = true;
   private NavItem[] navItems;
   private JLabel logoLabel;
   private JLabel lblFoto;
   private JLabel lblNombre;
   public SidebarPanel(MainFrame frame) {
      this.frame = frame;
      setBackground(BG);
      setBorder(new MatteBorder(0, 0, 0, 1, BORDER_COLOR));
      setLayout(new BorderLayout());
      setPreferredSize(new Dimension(W_MINI, 0)); 
      buildUI();
      setupHoverBehavior();
   }
   private void buildUI() {
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.setBackground(BG);
      topPanel.setBorder(new EmptyBorder(24, 14, 12, 14));
      logoLabel = new JLabel("📷"); 
      logoLabel.setFont(new Font("SansSerif", Font.PLAIN, 28));
      logoLabel.setForeground(TEXT_COLOR);
      topPanel.add(logoLabel, BorderLayout.CENTER);
      add(topPanel, BorderLayout.NORTH);
      JPanel navPanel = new JPanel();
      navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
      navPanel.setBackground(BG);
      navPanel.setBorder(new EmptyBorder(4, 0, 4, 0));
      navItems = new NavItem[] {
            new NavItem("⌂", "Inicio", MainFrame.Screens.FEED, () -> frame.showScreen(MainFrame.Screens.FEED)), 
            new NavItem("✉", "Mensajes", MainFrame.Screens.INBOX, () -> frame.showScreen(MainFrame.Screens.INBOX)),
            new NavItem("🔍", "Buscar", "BUSCAR", () -> frame.toggleBuscar()),
            new NavItem("◵", "Explorar", MainFrame.Screens.EXPLORAR, 
                  () -> frame.showScreen(MainFrame.Screens.EXPLORAR)),
            new NavItem("@", "Interacciones", MainFrame.Screens.INTERACCIONES, 
                  () -> frame.showScreen(MainFrame.Screens.INTERACCIONES)),
            new NavItem("♡", "Notificaciones", "NOTIF", () -> {
               frame.toggleNotificaciones();
               try {
                  String miUser = frame.getSession().getUsername();
                  frame.getGestorFollowers().marcarTodasComoLeidas(miUser);
                  for (NavItem item : navItems) {
                     if ("NOTIF".equals(item.getScreenId())) {
                        item.setBadge(false);
                        break;
                     }
                  }
               } catch (IOException ignored) {
               }
            }), 
            new NavItem("+", "Crear", "CREAR", () -> new PublicarDialog(frame).setVisible(true)), 
      };
      for (NavItem item : navItems) {
         navPanel.add(item);
         navPanel.add(Box.createVerticalStrut(2));
      }
      JScrollPane scrollNav = new JScrollPane(navPanel);
      scrollNav.setBorder(null);
      scrollNav.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scrollNav.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
      scrollNav.getViewport().setBackground(BG);
      add(scrollNav, BorderLayout.CENTER);
      JPanel bottomPanel = new JPanel();
      bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
      bottomPanel.setBackground(BG);
      bottomPanel.setBorder(new EmptyBorder(0, 0, 14, 0));
      JPanel perfilBtn = buildPerfilBtn();
      bottomPanel.add(perfilBtn);
      bottomPanel.add(Box.createVerticalStrut(2));
      JPanel cerrarBtn = buildCerrarBtn();
      bottomPanel.add(cerrarBtn);
      add(bottomPanel, BorderLayout.SOUTH);
      for (NavItem item : navItems) {
         item.setCollapsed(true);
      }
      lblNombre.setVisible(false);
      Usuario u = frame.getSession();
      if (u != null)
         refresh(u);
   }
   private JPanel buildPerfilBtn() {
      JPanel btn = new JPanel(new BorderLayout(10, 0));
      btn.setBackground(BG);
      btn.setBorder(new EmptyBorder(10, 14, 10, 14));
      btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
      btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblFoto = new JLabel("👤");
      lblFoto.setFont(new Font("SansSerif", Font.PLAIN, 24));
      lblFoto.setPreferredSize(new Dimension(32, 32));
      btn.add(lblFoto, BorderLayout.WEST);
      lblNombre = new JLabel("Perfil"); 
      lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 15));
      lblNombre.setForeground(TEXT_COLOR);
      btn.add(lblNombre, BorderLayout.CENTER);
      btn.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent e) {
            frame.showScreen(MainFrame.Screens.PERFIL);
         }
         public void mouseEntered(java.awt.event.MouseEvent e) {
            btn.setBackground(HOVER_BG);
         }
         public void mouseExited(java.awt.event.MouseEvent e) {
            btn.setBackground(BG);
         }
      });
      return btn;
   }
   private JPanel buildCerrarBtn() {
      JPanel btn = new JPanel(new BorderLayout(10, 0));
      btn.setBackground(BG);
      btn.setBorder(new EmptyBorder(8, 14, 10, 14));
      btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
      btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      JLabel ico = new JLabel("🚪");
      ico.setFont(new Font("SansSerif", Font.PLAIN, 20));
      ico.setPreferredSize(new Dimension(32, 28));
      btn.add(ico, BorderLayout.WEST);
      JLabel txt = new JLabel("Cerrar sesión"); 
      txt.setFont(new Font("SansSerif", Font.PLAIN, 15));
      txt.setForeground(TEXT_COLOR);
      btn.add(txt, BorderLayout.CENTER);
      btn.addMouseListener(new java.awt.event.MouseAdapter() {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent e) {
            frame.mostrarConfirmacion(
                  "Cerrar sesión",
                  "¿Estás seguro de que quieres cerrar sesión?",
                  "Cerrar sesión",
                  () -> frame.cerrarSesion());
         }
         @Override
         public void mouseEntered(java.awt.event.MouseEvent e) {
            btn.setBackground(HOVER_BG);
         }
         @Override
         public void mouseExited(java.awt.event.MouseEvent e) {
            btn.setBackground(BG);
         }
      });
      return btn;
   }
   public void refresh(Usuario u) {
      lblNombre.setText(u.getUsername());
      Icon foto = SidebarPanel.cargarFotoCircular(u.getRutaFotoPerfil(), 32);
      if (foto != null) {
         lblFoto.setIcon(foto);
         lblFoto.setText("");
      } else {
         lblFoto.setIcon(null);
         lblFoto.setText("👤");
      }
      try {
         boolean noLeidos = frame.getGestorInbox().tieneNoLeidos(u.getUsername(), frame.getGestorUsuarios());
         actualizarBadgeMensajes(noLeidos);
         boolean notifsSinLeer = frame.getGestorFollowers().tieneNotificacionesSinLeer(u.getUsername());
         actualizarBadgeNotificaciones(notifsSinLeer);
      } catch (IOException ignored) {
      }
      revalidate();
      repaint();
   }
   private void actualizarBadgeMensajes(boolean tieneBadge) {
      for (NavItem item : navItems) {
         if (MainFrame.Screens.INBOX.equals(item.getScreenId())) {
            item.setBadge(tieneBadge);
            break;
         }
      }
   }
   private void actualizarBadgeNotificaciones(boolean tieneBadge) {
      for (NavItem item : navItems) {
         if ("NOTIF".equals(item.getScreenId())) {
            item.setBadge(tieneBadge);
            break;
         }
      }
   }
   public void setActiveScreen(String screenId) {
      for (NavItem item : navItems) {
         item.setActive(screenId.equals(item.getScreenId()));
      }
   }
   public void collapse() {
      if (collapsed)
         return;
      collapsed = true;
      setPreferredSize(new Dimension(W_MINI, 0));
      logoLabel.setText("📷");
      logoLabel.setFont(new Font("SansSerif", Font.PLAIN, 28));
      for (NavItem item : navItems)
         item.setCollapsed(true);
      lblNombre.setVisible(false);
      try {
         Component txtLabel = ((BorderLayout) ((JPanel) ((JPanel) getComponent(2)).getComponent(2)).getLayout())
               .getLayoutComponent(BorderLayout.CENTER);
         if (txtLabel != null)
            txtLabel.setVisible(false);
      } catch (Exception ignored) {
      }
      if (getParent() != null) {
         setBounds(0, 0, W_MINI, getParent().getHeight());
      }
      revalidate();
      repaint();
   }
   public void expand() {
      if (!collapsed || !hoverEnabled)
         return;
      collapsed = false;
      setPreferredSize(new Dimension(W_FULL, 0));
      logoLabel.setText("Instagram");
      logoLabel.setFont(new Font("Georgia", Font.BOLD | Font.ITALIC, 22));
      for (NavItem item : navItems)
         item.setCollapsed(false);
      lblNombre.setVisible(true);
      try {
         Component txtLabel = ((BorderLayout) ((JPanel) ((JPanel) getComponent(2)).getComponent(2)).getLayout())
               .getLayoutComponent(BorderLayout.CENTER);
         if (txtLabel != null)
            txtLabel.setVisible(true);
      } catch (Exception ignored) {
      }
      if (getParent() != null) {
         setBounds(0, 0, W_FULL, getParent().getHeight());
      }
      revalidate();
      repaint();
   }
   public void setHoverEnabled(boolean enabled) {
      this.hoverEnabled = enabled;
   }
   private void setupHoverBehavior() {
      java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(null);
      Toolkit.getDefaultToolkit().addAWTEventListener(new java.awt.event.AWTEventListener() {
         @Override
         public void eventDispatched(AWTEvent event) {
            if (event instanceof java.awt.event.MouseEvent && SidebarPanel.this.isShowing()) {
               java.awt.event.MouseEvent me = (java.awt.event.MouseEvent) event;
               if (me.getID() == java.awt.event.MouseEvent.MOUSE_MOVED
                     || me.getID() == java.awt.event.MouseEvent.MOUSE_ENTERED) {
                  Point p = me.getLocationOnScreen();
                  Point sp = SidebarPanel.this.getLocationOnScreen();
                  Rectangle rect = new Rectangle(sp, SidebarPanel.this.getSize());
                  if (rect.contains(p)) {
                     if (collapsed && hoverEnabled)
                        expand();
                  } else {
                     if (!collapsed)
                        collapse();
                  }
               }
            }
         }
      }, AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
   }
   public static BufferedImage cargarImagenPost(String ruta) {
      if (ruta == null || ruta.isEmpty())
         return null;
      File f = new File(ruta);
      if (!f.exists())
         return null;
      try {
         BufferedImage orig = ImageIO.read(f);
         if (orig == null)
            return null;
         double ratio = (double) orig.getWidth() / orig.getHeight();
         int targetW = 600;
         int targetH;
         if (ratio > 1.1) { 
            targetH = 400;
         } else if (ratio < 0.9) { 
            targetH = 750;
         } else { 
            targetH = 600;
         }
         BufferedImage processed = procesarImagenInteligente(orig, targetW, targetH);
         return processed;
      } catch (IOException e) {
         return null;
      }
   }
   public static ImageIcon cargarImagenModal(String ruta, int targetW, int targetH) {
      if (ruta == null || ruta.isEmpty()) return null;
      File f = new File(ruta);
      if (!f.exists()) return null;
      try {
         BufferedImage orig = ImageIO.read(f);
         if (orig == null) return null;
         double scale = Math.min((double) targetW / orig.getWidth(), (double) targetH / orig.getHeight());
         int finalW = (int) Math.round(orig.getWidth() * scale);
         int finalH = (int) Math.round(orig.getHeight() * scale);
         BufferedImage finalImg;
         if (scale != 1.0) {
            finalImg = getHighQualityScaledInstance(orig, finalW, finalH);
         } else {
            finalImg = orig;
         }

         return new ImageIcon(finalImg);
      } catch (Exception e) {
         return null;
      }
   }
   public static ImageIcon cargarMiniaturaGrid(String ruta) {
      if (ruta == null || ruta.isEmpty())
         return null;
      File f = new File(ruta);
      if (!f.exists())
         return null;
      try {
         BufferedImage orig = ImageIO.read(f);
         if (orig == null)
            return null;
         BufferedImage processed = procesarImagenInteligente(orig, 300, 300);

         return new ImageIcon(processed);
      } catch (IOException e) {
         return null;
      }
   }
   public static Icon cargarFotoPerfil(String ruta) {
      return cargarFotoCircular(ruta, 320);
   }
   private static BufferedImage procesarImagenInteligente(BufferedImage orig, int targetW, int targetH) {
      if (orig.getWidth() >= targetW && orig.getHeight() >= targetH) {
         double targetRatio = (double) targetW / targetH;
         double origRatio = (double) orig.getWidth() / orig.getHeight();
         int cropW, cropH;
         if (origRatio > targetRatio) {
            cropH = orig.getHeight();
            cropW = (int) (cropH * targetRatio);
         } else {
            cropW = orig.getWidth();
            cropH = (int) (cropW / targetRatio);
         }
         int x = (orig.getWidth() - cropW) / 2;
         int y = (orig.getHeight() - cropH) / 2;
         BufferedImage cropped = orig.getSubimage(x, y, cropW, cropH);
         return getHighQualityScaledInstance(cropped, targetW, targetH);
      } else {
         BufferedImage canvas = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = canvas.createGraphics();
         g2.setColor(Color.BLACK);
         g2.fillRect(0, 0, targetW, targetH);
         double scale = Math.min((double) targetW / orig.getWidth(), (double) targetH / orig.getHeight());
         if (scale > 1.2) {
            scale = 1.2;
         }
         int finalW = (int) Math.round(orig.getWidth() * scale);
         int finalH = (int) Math.round(orig.getHeight() * scale);
         BufferedImage finalImg;
         if (scale < 1.0) {
            finalImg = getHighQualityScaledInstance(orig, finalW, finalH);
         } else if (scale > 1.0) {
            finalImg = getHighQualityScaledInstance(orig, finalW, finalH);
         } else {
            finalImg = orig;
         }
         int drawX = (targetW - finalW) / 2;
         int drawY = (targetH - finalH) / 2;
         g2.drawImage(finalImg, drawX, drawY, null);
         g2.dispose();
         return canvas;
      }
   }
   public static Icon cargarFotoCircular(String ruta, int size) {
      if (ruta == null || ruta.isEmpty()) return null;
      
      File f = new File(ruta);
      if (!f.exists()) return null;
      
      try {
         BufferedImage orig = ImageIO.read(f);
         if (orig == null) return null;
         
         int minSide = Math.min(orig.getWidth(), orig.getHeight());
         int x = (orig.getWidth() - minSide) / 2;
         int y = (orig.getHeight() - minSide) / 2;
         
         BufferedImage square = orig.getSubimage(x, y, minSide, minSide);
         BufferedImage scaled = getHighQualityScaledInstance(square, size, size);
         
         BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g2 = out.createGraphics();
         
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
         g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         
         g2.fillOval(0, 0, size, size);
         g2.setComposite(AlphaComposite.SrcIn);
         g2.drawImage(scaled, 0, 0, null);
         g2.dispose();
         
         return new ImageIcon(out);
      } catch (Exception e) {
         return null;
      }
   }

   public static BufferedImage getHighQualityScaledInstance(BufferedImage img, int targetW, int targetH) {
      int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
      BufferedImage ret = img;
      
      int w = img.getWidth();
      int h = img.getHeight();
      
      do {
         if (w > targetW) {
            w /= 2;
            if (w < targetW) w = targetW;
         } else {
            w = targetW;
         }
         
         if (h > targetH) {
            h /= 2;
            if (h < targetH) h = targetH;
         } else {
            h = targetH;
         }
         
         BufferedImage tmp = new BufferedImage(w, h, type);
         Graphics2D g2 = tmp.createGraphics();
         
         g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
         g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.drawImage(ret, 0, 0, w, h, null);
         g2.dispose();
         
         ret = tmp;
      } while (w != targetW || h != targetH);
      
      return ret;
   }
   private class NavItem extends JPanel {
      private final String screenId;
      private final JLabel iconLabel;
      private final JLabel textLabel;
      private JLabel badgeLabel;
      private boolean active = false;
      private boolean hasBadge = false;
      NavItem(String icon, String label, String screenId, Runnable action) {
         this.screenId = screenId;
         setLayout(new BorderLayout(10, 0));
         setBackground(BG);
         setBorder(new EmptyBorder(10, 14, 10, 14));
         setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
         setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         iconLabel = new JLabel(icon);
         iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
         iconLabel.setPreferredSize(new Dimension(32, 28));
         iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
         add(iconLabel, BorderLayout.WEST);
         JPanel rightPanel = new JPanel(new BorderLayout(4, 0));
         rightPanel.setBackground(BG);
         textLabel = new JLabel(label);
         textLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
         textLabel.setForeground(TEXT_COLOR);
         rightPanel.add(textLabel, BorderLayout.CENTER);
         badgeLabel = new JLabel("●");
         badgeLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
         badgeLabel.setForeground(new Color(230, 50, 50));
         badgeLabel.setVisible(false);
         rightPanel.add(badgeLabel, BorderLayout.EAST);
         add(rightPanel, BorderLayout.CENTER);
         addMouseListener(new NavItemMouseHandler(rightPanel, action));
      }

      private class NavItemMouseHandler extends java.awt.event.MouseAdapter {
         private final JPanel rightPanel;
         private final Runnable action;
         public NavItemMouseHandler(JPanel rp, Runnable action) { 
            this.rightPanel = rp; 
            this.action = action;
         }

         @Override
         public void mouseClicked(java.awt.event.MouseEvent e) {
            action.run();
         }
         @Override
         public void mouseEntered(java.awt.event.MouseEvent e) {
            if (!active)
               setBackground(HOVER_BG);
            rightPanel.setBackground(active ? ACTIVE_BG : HOVER_BG);
         }
         @Override
         public void mouseExited(java.awt.event.MouseEvent e) {
            setBackground(active ? ACTIVE_BG : BG);
            rightPanel.setBackground(active ? ACTIVE_BG : BG);
         }
      }
      String getScreenId() {
         return screenId;
      }
      void setActive(boolean a) {
         active = a;
         setBackground(a ? ACTIVE_BG : BG);
         textLabel.setFont(new Font("SansSerif", a ? Font.BOLD : Font.PLAIN, 15));
      }
      void setBadge(boolean show) {
         hasBadge = show;
         badgeLabel.setVisible(show);
      }
      void setCollapsed(boolean mini) {
         textLabel.setVisible(!mini);
         badgeLabel.setVisible(!mini && hasBadge);
         setBorder(new EmptyBorder(10, mini ? 18 : 14, 10, mini ? 18 : 14));
      }
   }
}
