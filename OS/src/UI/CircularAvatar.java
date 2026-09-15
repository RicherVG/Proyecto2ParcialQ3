/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;

/**
 *
 * @author andre
 */
public class CircularAvatar extends JComponent {
    private final int diametro;
    
    
    public CircularAvatar(int diametro){
        this.diametro = diametro;
        setPreferredSize(new Dimension(diametro, diametro));
        setMaximumSize(new Dimension(diametro,diametro));
    }
    
    protected void paintComponent(java.awt.Graphics g){
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(120, 120, 120));
        g2.fillRoundRect(0, 0, diametro, diametro, diametro / 6, diametro / 6);
        
        float grosor = diametro * 0.06f;
        g2.setStroke(new java.awt.BasicStroke(grosor, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
        g2.setColor(Color.WHITE);
        
        int cabezaDiam = (int) (diametro * 0.34);
        int cabezaX = (diametro - cabezaDiam) / 2;
        int cabezaY = (int) (diametro * 0.16);
        g2.drawOval(cabezaX, cabezaY, cabezaDiam, cabezaDiam);
        
        int cuerpoAncho = (int) (diametro * 0.72);
        int cuerpoAlto = (int) (diametro * 0.62);
        int cuerpoX = (diametro - cuerpoAncho) / 2;
        int cuerpoY = cabezaY + cabezaDiam - (int) (diametro * 0.02);
        g2.drawArc(cuerpoX, cuerpoY, cuerpoAncho, cuerpoAlto, 0, 180);
    }
    
    
}
