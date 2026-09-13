/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;
import insta.gestor.GestorUsuarios;
import insta.model.Usuario;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
/**
 *
 * @author riche
 */
public class LoginPanel extends JPanel {
   private final MainFrame frame;
   private JTextField txtUsername;
   private JPasswordField txtPassword;
   private JLabel lblError;
   private boolean passwordEsPlaceholder = true;
   public LoginPanel(MainFrame frame) {
      this.frame = frame;
      setLayout(new GridBagLayout());
      setBackground(new Color(250, 250, 250));
      buildUI();
   }
   public void limpiar() {
      txtUsername.setText("Número de teléfono, usuario o email");
      txtUsername.setForeground(Color.GRAY);
      mostrarPlaceholderPassword();
      lblError.setText(" ");
   }
   private void buildUI() {
      JPanel col = new JPanel();
      col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
      col.setBackground(new Color(250, 250, 250));
      col.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
      JPanel card = new JPanel();
      card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
      card.setBackground(Color.WHITE);
      card.setBorder(new CompoundBorder(
            new LineBorder(new Color(219, 219, 219), 1),
            new EmptyBorder(40, 40, 24, 40)));
      card.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
      JLabel logo = new JLabel("Instagram", SwingConstants.CENTER);
      logo.setFont(new Font("Georgia", Font.BOLD | Font.ITALIC, 46));
      logo.setForeground(new Color(20, 20, 20));
      logo.setAlignmentX(CENTER_ALIGNMENT);
      logo.setBorder(new EmptyBorder(10, 0, 36, 0));
      card.add(logo);
      txtUsername = styledTextField("Número de teléfono, usuario o email");
      card.add(txtUsername);
      card.add(Box.createVerticalStrut(6));
      JPanel passRow = buildPasswordRow();
      card.add(passRow);
      card.add(Box.createVerticalStrut(14));
      lblError = new JLabel(" ");
      lblError.setForeground(new Color(200, 30, 30));
      lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblError.setAlignmentX(CENTER_ALIGNMENT);
      lblError.setHorizontalAlignment(SwingConstants.CENTER);
      card.add(lblError);
      card.add(Box.createVerticalStrut(6));
      JButton btnLogin = roundButton("Ingresar", new Color(0, 149, 246), Color.WHITE);
      btnLogin.addActionListener(e -> intentarLogin());
      card.add(btnLogin);
      card.add(Box.createVerticalStrut(16));
      card.add(buildOrSeparator());
      card.add(Box.createVerticalStrut(16));
      JLabel lblOlvide = new JLabel("¿Olvidaste tu contraseña?", SwingConstants.CENTER);
      lblOlvide.setFont(new Font("SansSerif", Font.PLAIN, 12));
      lblOlvide.setForeground(new Color(0, 55, 210));
      lblOlvide.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblOlvide.setAlignmentX(CENTER_ALIGNMENT);
      lblOlvide.addMouseListener(new RecoveryMouseHandler(frame, lblOlvide));
      card.add(lblOlvide);
      col.add(card);
      col.add(Box.createVerticalStrut(10));
      JPanel cardAbajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 14));
      cardAbajo.setBackground(Color.WHITE);
      cardAbajo.setBorder(new LineBorder(new Color(219, 219, 219), 1));
      cardAbajo.setMaximumSize(new Dimension(350, 60));
      JLabel lblTieneCuenta = new JLabel("¿No tienes una cuenta?");
      lblTieneCuenta.setFont(new Font("SansSerif", Font.PLAIN, 14));
      lblTieneCuenta.setForeground(new Color(100, 100, 100));
      cardAbajo.add(lblTieneCuenta);
      JLabel lblRegistrate = new JLabel("Regístrate");
      lblRegistrate.setFont(new Font("SansSerif", Font.BOLD, 14));
      lblRegistrate.setForeground(new Color(0, 149, 246));
      lblRegistrate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblRegistrate.addMouseListener(new GoToRegisterMouseHandler(frame));
      cardAbajo.add(lblRegistrate);
      col.add(cardAbajo);
      add(col, new GridBagConstraints());
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
      txtPassword.addFocusListener(new PasswordFocusHandler());
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
         if (oculto) {
            txtPassword.setEchoChar((char) 0);
            btnOjo.setText("Ocultar");
         } else {
            txtPassword.setEchoChar('●');
            btnOjo.setText("Mostrar");
         }
      });
      row.add(txtPassword, BorderLayout.CENTER);
      row.add(btnOjo, BorderLayout.EAST);
      return row;
   }
   private void mostrarPlaceholderPassword() {
      passwordEsPlaceholder = true;
      txtPassword.setEchoChar((char) 0);
      txtPassword.setText("Contraseña");
      txtPassword.setForeground(Color.GRAY);
   }
   private void ocultarPlaceholderPassword() {
      passwordEsPlaceholder = false;
      txtPassword.setText("");
      txtPassword.setForeground(Color.BLACK);
      txtPassword.setEchoChar('●');
   }
   private void intentarLogin() {
      String user = txtUsername.getText().trim();
      if (user.equals("Número de teléfono, usuario o email"))
         user = "";
      String pass = passwordEsPlaceholder ? "" : new String(txtPassword.getPassword());
      if (user.isEmpty() || pass.isEmpty()) {
         lblError.setText("Ingresa usuario y contraseña.");
         return;
      }
      try {
         Usuario u = frame.getGestorUsuarios().login(user, pass);
         if (u == null) {
            lblError.setText("Credenciales incorrectas.");
         } else {
            limpiar();
            frame.setSession(u);
         }
      } catch (insta.model.CuentaDesactivadaException ex) {
         lblError.setText(" ");
         final String targetUser = ex.getUsername();
         frame.mostrarConfirmacion(
            "Reactivar cuenta", 
            "Tu cuenta @" + targetUser + " está desactivada. ¿Deseas reactivarla para entrar?", 
            "SÍ, Reactivar", 
            () -> {
               try {
                  frame.getGestorUsuarios().activar(targetUser);
                  Usuario reactivado = frame.getGestorUsuarios().buscarExacto(targetUser);
                  frame.mostrarToast("Cuenta reactivada. ¡Bienvenido de nuevo!");
                  limpiar();
                  frame.setSession(reactivado);
               } catch(Exception e) {
                  lblError.setText("Error al reactivar.");
               }
            }
         );
      } catch (IOException ex) {
         lblError.setText("Error: " + ex.getMessage());
      }
   }
   private JTextField styledTextField(String placeholder) {
      JTextField tf = new JTextField(placeholder);
      tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
      tf.setForeground(Color.GRAY);
      tf.setBackground(new Color(250, 250, 250));
      tf.setBorder(new CompoundBorder(
            new LineBorder(new Color(219, 219, 219), 1),
            new EmptyBorder(8, 10, 8, 10)));
      tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
      tf.addFocusListener(new PlaceholderFocusListener(tf, placeholder));
      return tf;
   }
   
   private static class PlaceholderFocusListener extends FocusAdapter {
      private final JTextField tf;
      private final String placeholder;
      
      public PlaceholderFocusListener(JTextField tf, String placeholder) {
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
   private JButton roundButton(String text, Color bg, Color fg) {
      JButton btn = new JButton(text);
      btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
      btn.setAlignmentX(CENTER_ALIGNMENT);
      btn.setBackground(bg);
      btn.setForeground(fg);
      btn.setFont(new Font("SansSerif", Font.BOLD, 14));
      btn.setFocusPainted(false);
      btn.setBorderPainted(false);
      btn.setOpaque(true);
      btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      return btn;
   }
   private JPanel buildOrSeparator() {
      JPanel sep = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
      sep.setBackground(Color.WHITE);
      sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
      JSeparator left = new JSeparator();
      left.setPreferredSize(new Dimension(100, 1));
      left.setForeground(new Color(219, 219, 219));
      JLabel o = new JLabel("o");
      o.setFont(new Font("SansSerif", Font.BOLD, 13));
      o.setForeground(new Color(140, 140, 140));
      JSeparator right = new JSeparator();
      right.setPreferredSize(new Dimension(100, 1));
      right.setForeground(new Color(219, 219, 219));
      sep.add(left);
      sep.add(o);
      sep.add(right);
      return sep;
   }
   private class PasswordFocusHandler extends FocusAdapter {
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

   private static class RecoveryMouseHandler extends MouseAdapter {
      private final MainFrame frame;
      private final JLabel label;
      public RecoveryMouseHandler(MainFrame frame, JLabel label) {
         this.frame = frame;
         this.label = label;
      }
      @Override
      public void mouseClicked(MouseEvent e) {
         frame.mostrarToast("Función de recuperación no implementada todavía.");
      }
      @Override
      public void mouseEntered(MouseEvent e) {
         label.setText("<html><u>¿Olvidaste tu contraseña?</u></html>");
      }
      @Override
      public void mouseExited(MouseEvent e) {
         label.setText("¿Olvidaste tu contraseña?");
      }
   }

   private static class GoToRegisterMouseHandler extends MouseAdapter {
      private final MainFrame frame;
      public GoToRegisterMouseHandler(MainFrame frame) {
         this.frame = frame;
      }
      @Override
      public void mouseClicked(MouseEvent e) {
         frame.showScreen(MainFrame.Screens.REGISTER);
      }
   }
}
