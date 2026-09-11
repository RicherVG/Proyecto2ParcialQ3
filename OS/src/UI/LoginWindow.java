/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import Excepciones.CuentaDesactivadaException;
import Persistencia.GestorUsuario;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
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
        add(contenedor);
        
        
        
        
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
    
    
    private static class FlechaBoton extends JButton{
        FlechaBoton(){
            setFocusable(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            
        }
        
        protected void paintComponent(java.awt.Graphics g){
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(2.2f,java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            
            int w = getWidth();
            int h = getHeight();
            int cx = w/2;
            int cy = h / 2;
            int tam = Math.min(w, h) / 4;
            
            g2.drawLine(cx - tam, cy, cx + tam, cy);
            g2.drawLine(cx + tam - 5, cy - 5, cx + tam, cy);
            g2.drawLine(cx + tam - 5, cy + 5, cx + tam, cy);
            
            g2.dispose();
        }
    }
    
    
    private static class CircularAvatar extends javax.swing.JComponent{
        private final int diametro;
        
        
        CircularAvatar(int diametro){
            this.diametro = diametro;
            setPreferredSize(new Dimension(diametro,diametro));
            setMaximumSize(new Dimension(diametro,diametro));
        }
        
        protected void paintComponent(java.awt.Graphics g ){
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(120,120,120));
            g2.fillRoundRect(0, 0, diametro, diametro, diametro / 6, diametro / 6);
            
            float grosor = diametro * 0.06f;
            g2.setStroke(new java.awt.BasicStroke(grosor,java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            g2.setColor(Color.WHITE);
            
            int cabezaDiam = (int)(diametro * 0.34);
            int cabezaX = (diametro - cabezaDiam) /2;
            int cabezaY = (int) (diametro * 0.16);
            g2.drawOval(cabezaX, cabezaY, cabezaDiam, cabezaDiam);
            
            
            int cuerpoAncho = (int) (diametro * 0.72);
            int cuerpoAlto = (int)(diametro * 0.62);
            int cuerpoX = (diametro - cuerpoAncho) / 2;
            int cuerpoY = cabezaY + cabezaDiam - (int) (diametro * 0.02);
            g2.drawArc(cuerpoX, cuerpoY, cuerpoAncho, cuerpoAlto, 0, 180);
            
        }
        
        
    }
    
    
    private static class CampoTransparente extends JTextField{
        CampoTransparente(){
            setOpaque(false);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(8,14,8,14));
            setCaretColor(Color.WHITE);
            setFont(getFont().deriveFont(15f));
        }
        
        protected void paintComponent(java.awt.Graphics g){
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255,255,255,45));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
            
        }
        
    }
    
    private static class CampoContraTransparente extends JPasswordField{
        CampoContraTransparente(){
            setOpaque(false);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(8,14,8,14));
            setCaretColor(Color.WHITE);
            setFont(getFont().deriveFont(15f));
        }
        
        
        protected void paintComponent(java.awt.Graphics g){
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255,255,255,45));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
        
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
