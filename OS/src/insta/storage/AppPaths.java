/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.storage;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 *
 * @author riche
 */
public class AppPaths {
   public static int getWindowWidth() {
      return 1366;
   }
   public static int getWindowHeight() {
      return 768;
   }
   public static final Path ROOT = Paths.get("INSTA_RAIZ");
   public static final Path USERS_FILE = ROOT.resolve("users.ins");
   public static final Path STICKERS_GLOBALES_DIR = ROOT.resolve("stickers_globales");
   public static Path userDir(String u) {
      return ROOT.resolve(u);
   }
   public static Path followersFile(String u) {
      return userDir(u).resolve("followers.ins");
   }
   public static Path followingFile(String u) {
      return userDir(u).resolve("following.ins");
   }
   public static Path instaFile(String u) {
      return userDir(u).resolve("insta.ins");
   }
   public static Path inboxFile(String u) {
      return userDir(u).resolve("inbox.ins");
   }
   public static Path requestsFile(String username) {
      return userDir(username).resolve("requests.ins");
   }
   public static Path notifsFile(String username) {
      return userDir(username).resolve("notificaciones.ins");
   }
   public static Path stickersFile(String u) {
      return userDir(u).resolve("stickers.ins");
   }
   public static Path imagesDir(String u) {
      return userDir(u).resolve("imagenes");
   }
   public static Path foldersPersonalesDir(String u) {
      return userDir(u).resolve("folders_personales");
   }
   public static Path stickersPersonalesDir(String u) {
      return userDir(u).resolve("stickers_personales");
   }
   private AppPaths() {
   } 
}
