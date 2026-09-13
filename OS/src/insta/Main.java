/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta;
import insta.ui.MainFrame;
import javax.swing.SwingUtilities;
/**
 *
 * @author riche
 */
public class Main {
   public static void main(String[] args) {
      try {
         javax.swing.UIManager.setLookAndFeel(
               javax.swing.UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ignored) {
      }
      SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
   }
}
