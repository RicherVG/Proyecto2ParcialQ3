/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.gestor;

import insta.model.Sticker;
import insta.storage.AppPaths;
import insta.storage.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author riche
 */
public class GestorStickers {
   private static final String[] NOMBRES_GLOBALES = {
         "Feliz", "Triste", "Corazón", "Risa", "Aplauso"
   };

   public List<Sticker> getStickersGlobales() {
      List<Sticker> lista = new ArrayList<>();
      for (String nombre : NOMBRES_GLOBALES) {
         String ruta = buscarImagen(AppPaths.STICKERS_GLOBALES_DIR, nombre);
         lista.add(new Sticker(nombre, ruta));
      }
      return lista;
   }

   public List<Sticker> getStickersPersonales(String username) throws IOException {
      return FileUtils.loadList(AppPaths.stickersFile(username));
   }

   public void importarSticker(String username, File origen) throws IOException {
      String nombreArchivo = origen.getName();
      String nombre = nombreArchivo.contains(".")
            ? nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'))
            : nombreArchivo;
      Path destino = AppPaths.stickersPersonalesDir(username).resolve(nombreArchivo);
      FileUtils.copiarArchivo(origen.toPath(), destino);
      List<Sticker> personales = getStickersPersonales(username);
      personales.add(new Sticker(nombre, destino.toString()));
      FileUtils.saveList(AppPaths.stickersFile(username), personales);
   }

   public List<Sticker> getTodosDisponibles(String username) throws IOException {
      List<Sticker> todos = new ArrayList<>();
      todos.addAll(getStickersGlobales());
      todos.addAll(getStickersPersonales(username));
      return todos;
   }

   private String buscarImagen(Path directorio, String nombre) {
      File dir = directorio.toFile();
      if (dir.exists() && dir.isDirectory()) {
         File[] archivos = dir.listFiles();
         if (archivos != null) {
            for (File f : archivos) {
               String nombreArchivo = f.getName();
               int punto = nombreArchivo.lastIndexOf('.');
               if (punto > 0) {
                  String sinExt = nombreArchivo.substring(0, punto);
                  String ext = nombreArchivo.substring(punto).toLowerCase();
                  if (sinExt.equalsIgnoreCase(nombre) &&
                        (ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".gif"))) {
                     return f.getAbsolutePath();
                  }
               }
            }
         }
      }
      return "";
   }
}
