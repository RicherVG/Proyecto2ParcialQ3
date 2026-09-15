/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

/**
 *
 * @author andre
 */
public class CampoContraTransparente extends JPasswordField {
    public CampoContraTransparente(){
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8,14,8,14));
        setCaretColor(Color.WHITE);
        setFont(getFont().deriveFont(15f));
    }
    
    protected void paintComponent(java.awt.Graphics g){
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 45));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
    
}
