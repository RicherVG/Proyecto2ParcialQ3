/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import Excepciones.CuentaDesactivadaException;
import Persistencia.GestorUsuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelo.Usuarios;

/**
 *
 * @author user
 */
public class LoginWindow extends JFrame {
    
    private GestorUsuario gestor;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    
    private char echoRealContra;
    private boolean mostrandoPlaceHoldCon = true;
    
    private static final String PLACEHOLDER_USER = "Usuario";
    private static final String PLACEHOLDER_CONTRA = "Contraseña";
    
    public LoginWindow(){
        gestor = new GestorUsuario();
        construirVentana();
    }
    
    private void construirVentana(){
        
        setTitle("Inicio Sesion");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel contenedor  = new JPanel (new GridBagLayout());
        contenedor.setBackground(new Color(30,30,40));
        
        
        JPanel tarjeta= new JPanel();
        tarjeta.setOpaque(false);
        tarjeta.setLayout(new javax.swing.BoxLayout(tarjeta, javax.swing.BoxLayout.Y_AXIS));
        
        CircularAvatar avatar = new CircularAvatar(140);
        avatar.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        txtUsername = campoPlaceHolder(PLACEHOLDER_USER);
        txtUsername.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        txtUsername.setMaximumSize(new Dimension(260,34));
        
        JLayeredPane panelContra = campoContra();
        panelContra.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        tarjeta.add(avatar);
        tarjeta.add(javax.swing.Box.createVerticalStrut(14));
        tarjeta.add(txtUsername);
        tarjeta.add(javax.swing.Box.createVerticalStrut(10));
        tarjeta.add(panelContra);
        
        contenedor.add(tarjeta);
      JButton btnApagar = crearBotonApagado();
      JPanel esquina = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
      esquina.setOpaque(false);
      esquina.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 20));
      esquina.add(btnApagar);
        
      JPanel raiz = new JPanel(new BorderLayout());
      raiz.setBackground(new Color(30, 30, 40));
      raiz.add(contenedor, BorderLayout.CENTER);
      raiz.add(esquina, BorderLayout.SOUTH);
      add(raiz);
       
        
        
        
        
    }
    
    
  
    
    
    private void mostrarPlaceholderContra(){
        mostrandoPlaceHoldCon = true;
        txtPassword.setEchoChar((char) 0);
        txtPassword.setText(PLACEHOLDER_CONTRA);
        txtPassword.setForeground(new Color(210,210,210));
    }
    
    
    private void ocularPlaceholderContra(){
    mostrandoPlaceHoldCon = false;
    txtPassword.setText("");
    txtPassword.setEchoChar(echoRealContra);
    txtPassword.setForeground(new Color(210,210,210));
}
    
    
    private JLayeredPane campoContra(){
        txtPassword = new CampoContraTransparente();
        echoRealContra = txtPassword.getEchoChar();
        mostrarPlaceholderContra();
        txtPassword.addFocusListener(new FocusAdapter(){
            
            public void focusGained(FocusEvent e){
                if (mostrandoPlaceHoldCon)
                    ocularPlaceholderContra();
            }
            
            public void focusLost(FocusEvent e){
                if (txtPassword.getPassword().length == 0){
                    mostrarPlaceholderContra();
                }
            }
            
            
        });
        
       btnLogin = new FlechaBoton();
       btnLogin.addActionListener(this::onLogin);
       
       JLayeredPane capas = new JLayeredPane();
       capas.setPreferredSize(new Dimension(260,34));
       capas.setMaximumSize(new Dimension(260,34));
       capas.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
       txtPassword.setBounds(0,0,260,34);
       btnLogin.setBounds(226, 2, 30, 30);
       
       capas.add(txtPassword,JLayeredPane.DEFAULT_LAYER);
       capas.add(btnLogin, JLayeredPane.PALETTE_LAYER);
       
       return capas;
        
        
    }
    
    
    
    
    private JTextField campoPlaceHolder(String placeholder){
        JTextField campo = new CampoTransparente();
        campo.setText(placeholder);
       campo.setForeground(new Color(210,210,210));
       
       campo.addFocusListener(new FocusAdapter(){
           public void focusGained(FocusEvent e){
               if (campo.getText().equals(placeholder)){
                   campo.setText("");
                   campo.setForeground(Color.WHITE);
               }
           }
           
           public void focusLost(FocusEvent e){
               if (campo.getText().isEmpty()){
                   campo.setText(placeholder);
                   campo.setForeground(new Color(210,210,210));
               }
           }
           
       });
       return campo;
    }
    
    private JButton crearBotonApagado(){
    JButton boton = new JButton("⏻");
    boton.setToolTipText("Apagar");
    boton.setFocusPainted(false);
    boton.setBorderPainted(false);
    boton.setContentAreaFilled(false);
    boton.setForeground(new Color(210, 210, 210));
    boton.setFont(new Font("Segoe UI", Font.PLAIN, 22));

    java.net.URL ruta = getClass().getResource("/ImagenesOS/apagado.png");
    if (ruta != null){
        Image img = new ImageIcon(ruta).getImage();
        Image escalada = img.getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        boton.setIcon(new ImageIcon(escalada));
        boton.setText("");
    }

    boton.addActionListener(e -> onApagar());
    return boton;
}

private void onApagar(){
    int opcion = JOptionPane.showConfirmDialog(this,
            "¿Desea apagar el equipo?", "Apagar",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (opcion == JOptionPane.YES_OPTION){
        System.exit(0);
    }
}
    
    private void onLogin(ActionEvent evt){
        String username = txtUsername.getText().trim();
        if (username.equals(PLACEHOLDER_USER)){
            username = "";
        }
        
        String password  =  mostrandoPlaceHoldCon ? "" : new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Completar usuario y contraseña");
            return;
      
        }
        
        try{
            Usuarios usuario = gestor.autenticar(username, password);
            
            if (usuario == null){
                JOptionPane.showMessageDialog(this, "Usuario o contra incorrecta");
                return;
            }
            
            JOptionPane.showMessageDialog(this, "Bienvenido, " +usuario.getNombreCompleto());
            this.dispose();
            new Escritorio(usuario).setVisible(true);
            
        }catch (CuentaDesactivadaException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
}
