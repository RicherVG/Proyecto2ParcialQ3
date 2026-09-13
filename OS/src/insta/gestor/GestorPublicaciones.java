/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.gestor;
import insta.model.ListaEnlazadaPublicaciones;
import insta.model.Publicacion;
import insta.model.Usuario;
import insta.storage.AppPaths;
import insta.storage.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author riche
 */
public class GestorPublicaciones {
   public void publicar(Publicacion p) throws IOException {
      ListaEnlazadaPublicaciones lista = cargarLista(p.getUsername());
      lista.agregar(p);
      FileUtils.saveObject(AppPaths.instaFile(p.getUsername()), lista);
   }
   public void actualizar(Publicacion modificada) throws IOException {
      ListaEnlazadaPublicaciones lista = cargarLista(modificada.getUsername());
      List<Publicacion> posts = lista.toList();
      for (int i = 0; i < posts.size(); i++) {
         Publicacion p = posts.get(i);
         if (p.getUsername().equals(modificada.getUsername()) &&
            p.getFecha().equals(modificada.getFecha()) &&
            p.getHora().equals(modificada.getHora())) {
            posts.set(i, modificada);
            break;
         }
      }
      ListaEnlazadaPublicaciones nuevaLista = ListaEnlazadaPublicaciones.fromList(posts);
      FileUtils.saveObject(AppPaths.instaFile(modificada.getUsername()), nuevaLista);
   }
   public List<Publicacion> getPostsDeUsuario(String username) throws IOException {
      return cargarLista(username).toList();
   }
   private ListaEnlazadaPublicaciones cargarLista(String username) throws IOException {
      try {
         ListaEnlazadaPublicaciones lista =
               FileUtils.loadObject(AppPaths.instaFile(username));
         return lista != null ? lista : new ListaEnlazadaPublicaciones();
      } catch (ClassCastException e) {
         List<Publicacion> legacy = FileUtils.loadList(AppPaths.instaFile(username));
         ListaEnlazadaPublicaciones nuevaLista = ListaEnlazadaPublicaciones.fromList(legacy);
         FileUtils.saveObject(AppPaths.instaFile(username), nuevaLista);
         return nuevaLista;
      }
   }
   public List<Publicacion> getFeed(String username,
         GestorFollowers gf,
         GestorUsuarios gu) throws IOException {
      List<Publicacion> feed = new ArrayList<>();
      feed.addAll(getPostsActivosDeUsuario(username, gu));
      for (String followed : gf.getFollowing(username)) {
         feed.addAll(getPostsActivosDeUsuario(followed, gu));
      }
      Collections.sort(feed);
      return feed;
   }
   private List<Publicacion> getPostsActivosDeUsuario(String username,
         GestorUsuarios gu) throws IOException {
      Usuario u = gu.buscarExacto(username);
      if (u == null || !u.isActivo()) return new ArrayList<>();
      return getPostsDeUsuario(username);
   }
   public List<Publicacion> getMenciones(String username,
         GestorUsuarios gu) throws IOException {
      List<Publicacion> resultado = new ArrayList<>();
      String mencion = "@" + username.toLowerCase();
      List<String> vistosId = new ArrayList<>();
      for (Usuario u : gu.cargarTodos()) {
         if (!u.isActivo()) continue;
         for (Publicacion p : getPostsDeUsuario(u.getUsername())) {
            String id = p.getUsername() + "_" + p.getFecha() + "_" + p.getHora();
            if (!vistosId.contains(id) &&
                  p.getMenciones().toLowerCase().contains(mencion)) {
               resultado.add(p);
               vistosId.add(id);
            }
         }
      }
      Collections.sort(resultado);
      return resultado;
   }
   public List<Publicacion> getTodasLasPublicaciones(GestorUsuarios gu) throws IOException {
      List<Publicacion> todas = new ArrayList<>();
      List<String> vistosId = new ArrayList<>();
      for (Usuario u : gu.cargarTodos()) {
         if (!u.isActivo() || !"PUBLICA".equalsIgnoreCase(u.getTipoCuenta())) continue;
         for (Publicacion p : getPostsDeUsuario(u.getUsername())) {
            String id = p.getUsername() + "_" + p.getFecha() + "_" + p.getHora();
            if (!vistosId.contains(id)) {
               todas.add(p);
               vistosId.add(id);
            }
         }
      }
      Collections.sort(todas);
      return todas;
   }
   public List<Publicacion> buscarHashtag(String hashtag,
         GestorUsuarios gu) throws IOException {
      List<Publicacion> resultado = new ArrayList<>();
      String buscar = "#" + hashtag.toLowerCase().replace("#", "");
      List<String> vistosId = new ArrayList<>();
      for (Usuario u : gu.cargarTodos()) {
         if (!u.isActivo()) continue;
         List<Publicacion> posts = getPostsDeUsuario(u.getUsername());
         buscarHashtagRecursivo(posts, buscar, 0, resultado, vistosId);
      }
      return resultado;
   }
   private void buscarHashtagRecursivo(List<Publicacion> lista,String buscar,int idx,List<Publicacion> resultado,List<String> vistosId) {
      if (idx >= lista.size()) return;
      Publicacion p = lista.get(idx);
      String id = p.getUsername() + "_" + p.getFecha() + "_" + p.getHora();
      if (!vistosId.contains(id) &&
            p.getHashtags().toLowerCase().contains(buscar)) {
         resultado.add(p);
         vistosId.add(id);
      }
      buscarHashtagRecursivo(lista, buscar, idx + 1, resultado, vistosId);
   }
}
