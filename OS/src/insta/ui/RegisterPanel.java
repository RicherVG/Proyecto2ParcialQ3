/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;
import insta.model.Usuario;
import insta.storage.AppPaths;
import insta.storage.FileUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
/**
 *
 * @author riche
 */
public class RegisterPanel extends JPanel {
   private final MainFrame frame;
   private JTextField txtNombre;
   private JTextField txtUsername;
   private JTextField txtEdad;
   private JPasswordField txtPassword;
   private JComboBox<String> cmbGenero;
   private JComboBox<String> cmbTipoCuenta;
   private JLabel lblError;
   private JLabel lblFoto;
   private String rutaFotoSeleccionada = "";
   private boolean passwordEsPlaceholder = true;
   private static final String PH_NOMBRE = "Nombre y apellido";
   private static final String PH_USERNAME = "Nombre de usuario";
   private static final String PH_EDAD = "Edad";
   private static final String PH_PASS = "Contraseña";
   public RegisterPanel(MainFrame frame) {
      this.frame = frame;
      setLayout(new GridBagLayout());
      setBackground(new Color(250, 250, 250));
      buildUI();
   }
   public void limpiar() {
      resetField(txtNombre, PH_NOMBRE);
      resetField(txtUsername, PH_USERNAME);
      resetField(txtEdad, PH_EDAD);
      mostrarPlaceholderPassword();
      cmbGenero.setSelectedIndex(0);
      cmbTipoCuenta.setSelectedIndex(0);
      if (avatarPreview != null) {
         avatarPreview.setIcon(null);
         avatarPreview.repaint();
      }
      lblFoto.setText("Sin foto seleccionada");
      lblFoto.setForeground(Color.GRAY);
      rutaFotoSeleccionada = "";
      lblError.setText(" ");
   }
   private JLabel avatarPreview; 
   private static final int AVATAR_SIZE = 88;
   private void buildUI() {
      JPanel card = new JPanel();
      card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
      card.setBackground(Color.WHITE);
      card.setBorder(new CompoundBorder(
            new LineBorder(new Color(219, 219, 219), 1),
            new EmptyBorder(32, 40, 32, 40)));
      JLabel logo = new JLabel("Instagram", SwingConstants.CENTER);
      logo.setFont(new Font("Georgia", Font.BOLD | Font.ITALIC, 46));
      logo.setForeground(new Color(20, 20, 20));
      logo.setAlignmentX(CENTER_ALIGNMENT);
      card.add(logo);
      card.add(Box.createVerticalStrut(10));
      JLabel sub = labelCentrado("Regístrate para ver fotos de tus amigos.", 14, new Color(115, 115, 115));
      card.add(sub);
      card.add(Box.createVerticalStrut(24));
      avatarPreview = new AvatarLabel();
      avatarPreview.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
      avatarPreview.setMaximumSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
      avatarPreview.setAlignmentX(CENTER_ALIGNMENT);
      avatarPreview.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      avatarPreview.setToolTipText("Añadir foto de perfil (opcional)");
      avatarPreview.addMouseListener(new AvatarMouseHandler(avatarPreview));
      card.add(avatarPreview);
      card.add(Box.createVerticalStrut(6));
      JLabel lblFotoHint = labelCentrado("Añadir foto de perfil", 13, new Color(0, 149, 246));
      lblFotoHint.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblFotoHint.addMouseListener(new FotoHintMouseHandler());
      card.add(lblFotoHint);
      lblFoto = new JLabel("Sin foto seleccionada");
      lblFoto.setVisible(false);
      card.add(lblFoto);
      card.add(Box.createVerticalStrut(20));
      txtNombre = styledField(PH_NOMBRE);
      card.add(txtNombre);
      card.add(Box.createVerticalStrut(8));
      txtUsername = styledField(PH_USERNAME);
      card.add(txtUsername);
      card.add(Box.createVerticalStrut(8));
      card.add(buildPasswordRow());
      card.add(Box.createVerticalStrut(8));
      txtEdad = styledField(PH_EDAD);
      card.add(txtEdad);
      card.add(Box.createVerticalStrut(8));
      cmbGenero = styledCombo(new String[] { "M - Masculino", "F - Femenino", "P - Personalizado" });
      card.add(cmbGenero);
      card.add(Box.createVerticalStrut(8));
      cmbTipoCuenta = styledCombo(new String[] { "PUBLICA", "PRIVADA" });
      card.add(cmbTipoCuenta);
      card.add(Box.createVerticalStrut(14));
      lblError = new JLabel(" ");
      lblError.setForeground(new Color(200, 30, 30));
      lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblError.setAlignmentX(CENTER_ALIGNMENT);
      lblError.setHorizontalAlignment(SwingConstants.CENTER);
      card.add(lblError);
      card.add(Box.createVerticalStrut(4));
      JButton btnReg = roundButton("Registrarse");
      btnReg.addActionListener(e -> registrar());
      card.add(btnReg);
      JPanel cardAbajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 14));
      cardAbajo.setBackground(Color.WHITE);
      cardAbajo.setBorder(new LineBorder(new Color(219, 219, 219), 1));
      cardAbajo.setPreferredSize(new Dimension(350, 56));
      cardAbajo.setMaximumSize(new Dimension(350, 56));
      JLabel lblTiene = new JLabel("¿Tienes cuenta?");
      lblTiene.setFont(new Font("SansSerif", Font.PLAIN, 14));
      lblTiene.setForeground(new Color(100, 100, 100));
      cardAbajo.add(lblTiene);
      JLabel lblEntrar = new JLabel("Inicia sesión");
      lblEntrar.setFont(new Font("SansSerif", Font.BOLD, 14));
      lblEntrar.setForeground(new Color(0, 149, 246));
      lblEntrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblEntrar.addMouseListener(new SwitchLoginListener(frame));
      cardAbajo.add(lblEntrar);
      JPanel col = new JPanel();
      col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
      col.setBackground(new Color(250, 250, 250));
      col.setBorder(new EmptyBorder(20, 0, 20, 0));
      card.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
      card.setAlignmentX(CENTER_ALIGNMENT);
      cardAbajo.setAlignmentX(CENTER_ALIGNMENT);
      col.add(Box.createHorizontalGlue()); 
      col.add(card);
      col.add(Box.createVerticalStrut(10));
      col.add(cardAbajo);
      JScrollPane scroll = new JScrollPane(col);
      scroll.setBorder(null);
      scroll.getVerticalScrollBar().setUnitIncrement(16);
      scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scroll.getViewport().setBackground(new Color(250, 250, 250));
      setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.BOTH;
      gbc.weightx = 1;
      gbc.weighty = 1;
      add(scroll, gbc);
   }
   private JPanel buildPasswordRow() {
      JPanel row = new JPanel(new BorderLayout(0, 0));
      row.setBackground(new Color(250, 250, 250));
      row.setBorder(new CompoundBorder(
            new LineBorder(new Color(219, 219, 219), 1),
            new EmptyBorder(0, 10, 0, 6)));
      row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
      txtPassword = new JPasswordField();
      txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
      txtPassword.setBackground(new Color(250, 250, 250));
      txtPassword.setBorder(BorderFactory.createEmptyBorder());
      txtPassword.setOpaque(true);
      mostrarPlaceholderPassword();
      txtPassword.addFocusListener(new PasswordFocusListener());
      JButton btnOjo = new JButton("Mostrar");
      btnOjo.setFont(new Font("SansSerif", Font.BOLD, 12));
      btnOjo.setForeground(new Color(30, 30, 30));
      btnOjo.setBorderPainted(false);
      btnOjo.setContentAreaFilled(false);
      btnOjo.setFocusPainted(false);
      btnOjo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btnOjo.addActionListener(e -> {
         if (passwordEsPlaceholder)
            return;
         boolean oculto = txtPassword.getEchoChar() != 0;
         txtPassword.setEchoChar(oculto ? (char) 0 : '●');
         btnOjo.setText(oculto ? "Ocultar" : "Mostrar");
      });
      row.add(txtPassword, BorderLayout.CENTER);
      row.add(btnOjo, BorderLayout.EAST);
      return row;
   }
   private void mostrarPlaceholderPassword() {
      passwordEsPlaceholder = true;
      txtPassword.setEchoChar((char) 0);
      txtPassword.setText(PH_PASS);
      txtPassword.setForeground(Color.GRAY);
   }
   private void ocultarPlaceholderPassword() {
      passwordEsPlaceholder = false;
      txtPassword.setText("");
      txtPassword.setForeground(Color.BLACK);
      txtPassword.setEchoChar('●');
   }
   private void registrar() {
      lblError.setText(" ");
      String nombre = getValue(txtNombre, PH_NOMBRE);
      String uname = getValue(txtUsername, PH_USERNAME);
      String pass = passwordEsPlaceholder ? "" : new String(txtPassword.getPassword());
      String edadStr = getValue(txtEdad, PH_EDAD);
      String genero = ((String) cmbGenero.getSelectedItem()).substring(0, 1);
      String tipo = (String) cmbTipoCuenta.getSelectedItem();
      if (nombre.isEmpty() || uname.isEmpty() || pass.isEmpty() || edadStr.isEmpty()) {
         lblError.setText("Completa todos los campos obligatorios.");
         return;
      }
      if (!uname.matches("^[a-zA-Z0-9_.]+$")) {
         lblError.setText("El usuario no puede tener espacios ni caracteres especiales.");
         return;
      }
      int edad;
      try {
         edad = Integer.parseInt(edadStr);
         if (edad < 1 || edad > 120) {
            lblError.setText("Ingresa una edad válida (entre 1 y 120).");
            return;
         }
      } catch (NumberFormatException ex) {
         lblError.setText("Edad inválida.");
         return;
      }
      try {
         Usuario nuevo = new Usuario(nombre, genero, uname, pass, edad, tipo, "");
         frame.getGestorUsuarios().registrar(nuevo);
         frame.getGestorFollowers().autoSeguirCuentasPorDefecto(nuevo.getUsername());
         if (!rutaFotoSeleccionada.isEmpty()) {
            File fotoOrigen = new File(rutaFotoSeleccionada);
            if (fotoOrigen.exists()) {
               Path destino = AppPaths.imagesDir(uname).resolve("perfil_" + fotoOrigen.getName());
               FileUtils.copiarArchivo(fotoOrigen.toPath(), destino);
               nuevo.setRutaFotoPerfil(destino.toString());
               frame.getGestorUsuarios().actualizar(nuevo);
            }
         }
         frame.mostrarToast("¡Cuenta creada con éxito! Bienvenido @" + nuevo.getUsername());
         limpiar();
         frame.setSession(nuevo);
      } catch (insta.model.UsernameDuplicadoException ex) {
         lblError.setText(ex.getMessage());
         frame.mostrarToast(ex.getMessage());
      } catch (Exception ex) {
         lblError.setText("Error al registrar: " + ex.getMessage());
         frame.mostrarToast("Error al registrar: " + ex.getMessage());
      }
   }
   private void seleccionarFoto() {
      JFileChooser fc = new JFileChooser();
      fc.setDialogTitle("Selecciona foto de perfil");
      fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Imágenes (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
      if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
         rutaFotoSeleccionada = fc.getSelectedFile().getAbsolutePath();
         lblFoto.setText(fc.getSelectedFile().getName());
         lblFoto.setForeground(new Color(0, 149, 246));
         if (avatarPreview != null) {
            Icon icon = SidebarPanel.cargarFotoCircular(rutaFotoSeleccionada, AVATAR_SIZE);
            avatarPreview.setIcon(icon);
            avatarPreview.repaint();
         }
      }
   }
   private JTextField styledField(String placeholder) {
      JTextField tf = new JTextField(placeholder);
      tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
      tf.setForeground(Color.GRAY);
      tf.setBackground(new Color(250, 250, 250));
      tf.setBorder(new CompoundBorder(
            new LineBorder(new Color(219, 219, 219), 1),
            new EmptyBorder(8, 10, 8, 10)));
      tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
      tf.addFocusListener(new PlaceholderFocusHandler(tf, placeholder));
      return tf;
   }
   private JButton roundButton(String text) {
      JButton btn = new JButton(text);
      btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
      btn.setAlignmentX(CENTER_ALIGNMENT);
      btn.setBackground(new Color(0, 149, 246));
      btn.setForeground(Color.WHITE);
      btn.setFont(new Font("SansSerif", Font.BOLD, 14));
      btn.setFocusPainted(false);
      btn.setBorderPainted(false);
      btn.setOpaque(true);
      btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      return btn;
   }
   private JComboBox<String> styledCombo(String[] items) {
      JComboBox<String> cb = new JComboBox<>(items);
      cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
      cb.setBackground(new Color(250, 250, 250));
      cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
      return cb;
   }
   private JLabel labelCentrado(String text, int size, Color color) {
      JLabel l = new JLabel(text, SwingConstants.CENTER);
      l.setFont(new Font("SansSerif", Font.PLAIN, size));
      l.setForeground(color);
      l.setAlignmentX(CENTER_ALIGNMENT);
      return l;
   }
   private void resetField(JTextField tf, String placeholder) {
      tf.setText(placeholder);
      tf.setForeground(Color.GRAY);
   }
   private String getValue(JTextField tf, String placeholder) {
      String v = tf.getText().trim();
      return v.equals(placeholder) ? "" : v;
   }

   private static class SwitchLoginListener extends MouseAdapter {
      private final MainFrame frame;
      public SwitchLoginListener(MainFrame frame) { this.frame = frame; }
      @Override
      public void mouseClicked(MouseEvent e) {
         frame.showScreen(MainFrame.Screens.LOGIN);
      }
   }

   private class PasswordFocusListener extends FocusAdapter {
      @Override
      public void focusGained(FocusEvent e) {
         if (passwordEsPlaceholder)
            ocultarPlaceholderPassword();
      }
      @Override
      public void focusLost(FocusEvent e) {
         if (txtPassword.getPassword().length == 0)
            mostrarPlaceholderPassword();
      }
   }
   private class AvatarLabel extends JLabel {
      @Override
      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D) g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         if (getIcon() == null) {
            g2.setColor(new Color(219, 219, 219));
            g2.fillOval(0, 0, AVATAR_SIZE, AVATAR_SIZE);
            g2.setColor(new Color(140, 140, 140));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 30));
            FontMetrics fm = g2.getFontMetrics();
            String cam = "📷";
            g2.drawString(cam, (AVATAR_SIZE - fm.stringWidth(cam)) / 2,
                  (AVATAR_SIZE + fm.getAscent()) / 2 - 4);
         } else {
            super.paintComponent(g2);
         }
         g2.setColor(new Color(200, 200, 200));
         g2.setStroke(new java.awt.BasicStroke(2));
         g2.drawOval(1, 1, AVATAR_SIZE - 2, AVATAR_SIZE - 2);
         g2.dispose();
      }
   }

   private class AvatarMouseHandler extends MouseAdapter {
      private final JLabel label;
      public AvatarMouseHandler(JLabel label) { this.label = label; }
      @Override
      public void mouseClicked(MouseEvent e) { seleccionarFoto(); }
      @Override
      public void mouseEntered(MouseEvent e) { label.setOpaque(false); }
   }

   private class FotoHintMouseHandler extends MouseAdapter {
      @Override
      public void mouseClicked(MouseEvent e) { seleccionarFoto(); }
   }

   private static class PlaceholderFocusHandler extends FocusAdapter {
      private final JTextField tf;
      private final String placeholder;
      public PlaceholderFocusHandler(JTextField tf, String placeholder) {
         this.tf = tf;
         this.placeholder = placeholder;
      }
      @Override
      public void focusGained(FocusEvent e) {
         if (tf.getText().equals(placeholder)) {
            tf.setText("");
            tf.setForeground(Color.BLACK);
         }
      }
      @Override
      public void focusLost(FocusEvent e) {
         if (tf.getText().isEmpty()) {
            tf.setText(placeholder);
            tf.setForeground(Color.GRAY);
         }
      }
   }
}
