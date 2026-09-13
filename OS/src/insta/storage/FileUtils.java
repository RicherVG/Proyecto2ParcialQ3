/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.storage;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author riche
 */
public class FileUtils {
   private FileUtils() {
   } 
   public static void ensureBaseStructure() {
      try {
         Files.createDirectories(AppPaths.ROOT);
         Files.createDirectories(AppPaths.STICKERS_GLOBALES_DIR);
         if (!Files.exists(AppPaths.USERS_FILE)) {
            Files.createFile(AppPaths.USERS_FILE);
            saveList(AppPaths.USERS_FILE, new ArrayList<>());
         }
      } catch (IOException e) {
         throw new RuntimeException("Error creando estructura base: " + e.getMessage(), e);
      }
   }
   public static synchronized void ensureUserStructure(String username) {
      try {
         Files.createDirectories(AppPaths.imagesDir(username));
         Files.createDirectories(AppPaths.foldersPersonalesDir(username));
         Files.createDirectories(AppPaths.stickersPersonalesDir(username));
         initIfEmpty(AppPaths.followersFile(username));
         initIfEmpty(AppPaths.followingFile(username));
         initIfEmpty(AppPaths.instaFile(username));
         initIfEmpty(AppPaths.inboxFile(username));
         initIfEmpty(AppPaths.stickersFile(username));
         initIfEmpty(AppPaths.requestsFile(username));
         initIfEmpty(AppPaths.notifsFile(username));
      } catch (IOException e) {
         throw new RuntimeException("Error creando estructura de usuario: " + e.getMessage(), e);
      }
   }
   private static synchronized void initIfEmpty(Path p) throws IOException {
      if (!Files.exists(p) || Files.size(p) == 0) {
         saveList(p, new ArrayList<>());
      }
   }
   public static synchronized <T extends Serializable> void saveList(Path path, List<T> data)
         throws IOException {
      Files.createDirectories(path.getParent());
      try (ObjectOutputStream oos = new ObjectOutputStream(
            new BufferedOutputStream(Files.newOutputStream(path)))) {
         oos.writeObject(data);
      }
   }
   @SuppressWarnings("unchecked")
   public static synchronized <T extends Serializable> List<T> loadList(Path path) throws IOException {
      if (!Files.exists(path) || Files.size(path) == 0)
         return new ArrayList<>();
      try (ObjectInputStream ois = new ObjectInputStream(
            new BufferedInputStream(Files.newInputStream(path)))) {
         return (List<T>) ois.readObject();
      } catch (EOFException e) {
         return new ArrayList<>();
      } catch (ClassNotFoundException | InvalidClassException | StreamCorruptedException e) {
         throw new insta.model.ArchivoCorruptoException(path.toString(), e);
      }
   }
   public static synchronized <T extends Serializable> void saveObject(Path path, T objeto)
         throws IOException {
      Files.createDirectories(path.getParent());
      try (ObjectOutputStream oos = new ObjectOutputStream(
            new BufferedOutputStream(Files.newOutputStream(path)))) {
         oos.writeObject(objeto);
      }
   }
   @SuppressWarnings("unchecked")
   public static synchronized <T extends Serializable> T loadObject(Path path) throws IOException {
      if (!Files.exists(path) || Files.size(path) == 0) return null;
      try (ObjectInputStream ois = new ObjectInputStream(
            new BufferedInputStream(Files.newInputStream(path)))) {
         return (T) ois.readObject();
      } catch (EOFException e) {
         return null;
      } catch (ClassNotFoundException | InvalidClassException | StreamCorruptedException e) {
         throw new insta.model.ArchivoCorruptoException(path.toString(), e);
      }
   }
   public static synchronized void copiarArchivo(Path origen, Path destino) throws IOException {
      Files.createDirectories(destino.getParent());
      Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
   }
}
