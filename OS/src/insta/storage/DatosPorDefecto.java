/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.storage;
import insta.gestor.GestorPublicaciones;
import insta.gestor.GestorUsuarios;
import insta.model.Usuario;
import java.io.IOException;
/**
 *
 * @author riche
 */
public class DatosPorDefecto {
   public static final String[] CUENTAS = {
      "OnePiece_Oficial", "LeoMessi", "nike", "marvel", "Netflix", "Lospasosdejulion", 
      "Camilamx", "natgeo", "nasa", "starwars", "playstation", "xbox"
   };
   public static void inicializarCuentasSiNoExisten(GestorUsuarios gu, GestorPublicaciones gp) {
      for (String cuenta : CUENTAS) {
         try {
            if (!gu.usernameExiste(cuenta)) {
               Usuario defaultUser = new Usuario(
                     capitalize(cuenta) + " Oficial",
                     "M", 
                     cuenta,
                     "1234", 
                     100, 
                     "PUBLICA",
                     "" 
               );
               gu.registrar(defaultUser);
            }
         } catch (Exception e) {
            System.err.println("No se pudo inicializar la cuenta default: " + cuenta + " - " + e.getMessage());
         }
      }
   }
   private static String capitalize(String str) {
      if (str == null || str.isEmpty()) return str;
      return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
   }
}
