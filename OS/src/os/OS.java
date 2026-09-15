/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package os;

import UI.LoginWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author user
 */
public class OS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
       }catch (Exception e){
           e.printStackTrace();
       }
       SwingUtilities.invokeLater(() -> new UI.LoginWindow().setVisible(true));
    }
    
}
