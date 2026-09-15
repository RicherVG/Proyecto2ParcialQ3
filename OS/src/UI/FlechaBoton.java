/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import java.awt.Color;
import javax.swing.JButton;

/**
 *
 * @author andre
 */
public class FlechaBoton extends JButton {
    public FlechaBoton(){
        setFocusable(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
    }
    
     protected void paintComponent(java.awt.Graphics g){
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setStroke(new java.awt.BasicStroke(2.2f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
        
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        int tam = Math.min(w, h) / 4;
        
        g2.drawLine(cx - tam, cy, cx + tam, cy);
        g2.drawLine(cx + tam - 5, cy - 5, cx + tam, cy);
        g2.drawLine(cx + tam - 5, cy + 5, cx + tam, cy);
        
        g2.dispose();
    }
}
