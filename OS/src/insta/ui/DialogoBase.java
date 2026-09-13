/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 *
 * @author riche
 */
public class DialogoBase extends JDialog {

   protected JPanel rootPanel;
   protected JPanel headerPanel;
   protected JButton btnBack;
   protected JButton btnAction;
   protected JLabel lblTitulo;
   
   public DialogoBase(java.awt.Frame owner, String title, boolean modal) {
      super(owner, title, modal);
      
      rootPanel = new JPanel(new BorderLayout());
      rootPanel.setBackground(Color.WHITE);
      
      headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBackground(Color.WHITE);
      headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(219, 219, 219)));
      
      lblTitulo = new JLabel(title, SwingConstants.CENTER);
      lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
      lblTitulo.setBorder(new EmptyBorder(12, 0, 12, 0));
      
      btnBack = new JButton("←");
      btnBack.setFont(new Font("SansSerif", Font.PLAIN, 24));
      btnBack.setBorder(new EmptyBorder(0, 16, 0, 0));
      btnBack.setContentAreaFilled(false);
      btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
      btnBack.setVisible(false);
      
      btnAction = new JButton("Acción");
      btnAction.setFont(new Font("SansSerif", Font.BOLD, 14));
      btnAction.setForeground(new Color(0, 149, 246));
      btnAction.setBorder(new EmptyBorder(0, 0, 0, 16));
      btnAction.setContentAreaFilled(false);
      btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
      btnAction.setVisible(false);
      
      headerPanel.add(btnBack, BorderLayout.WEST);
      headerPanel.add(lblTitulo, BorderLayout.CENTER);
      headerPanel.add(btnAction, BorderLayout.EAST);
      
      rootPanel.add(headerPanel, BorderLayout.NORTH);
   }
   
   protected void setTitulo(String texto) {
      lblTitulo.setText(texto);
   }
   
   protected void configurarBotonAccion(String texto, ActionListener accion) {
      btnAction.setText(texto);
      btnAction.setVisible(true);
      for (ActionListener al : btnAction.getActionListeners()) {
          btnAction.removeActionListener(al);
      }
      btnAction.addActionListener(accion);
   }
   
   protected void configurarBotonVolver(ActionListener accion) {
      btnBack.setVisible(true);
      for (ActionListener al : btnBack.getActionListeners()) {
          btnBack.removeActionListener(al);
      }
      btnBack.addActionListener(accion);
   }
   
   protected void esconderBotones() {
      btnBack.setVisible(false);
      btnAction.setVisible(false);
   }
}
