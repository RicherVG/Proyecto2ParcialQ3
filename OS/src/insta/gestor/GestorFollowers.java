/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.gestor;
import insta.model.ListaEnlazada;
import insta.model.Notificacion;
import insta.storage.AppPaths;
import insta.storage.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author riche
 */
public class GestorFollowers {
   public void seguir(String miUser, String targetUser) throws IOException {
      ListaEnlazada<String> myFollowing = getFollowingEnlazada(miUser);
      if (!myFollowing.contieneIgnoreCase(targetUser)) {
         myFollowing.agregar(targetUser);
         FileUtils.saveList(AppPaths.followingFile(miUser), myFollowing.toList());
      }
      ListaEnlazada<String> theirFollowers = getFollowersEnlazada(targetUser);
      if (!theirFollowers.contieneIgnoreCase(miUser)) {
         theirFollowers.agregar(miUser);
         FileUtils.saveList(AppPaths.followersFile(targetUser), theirFollowers.toList());
         agregarNotificacion(targetUser, new insta.model.Notificacion(
               insta.model.Notificacion.Tipo.SEGUIDOR, miUser,
               "@" + miUser + " comenzó a seguirte."));
      }
   }
   public void dejarDeSeguir(String miUser, String targetUser) throws IOException {
      ListaEnlazada<String> myFollowing = getFollowingEnlazada(miUser);
      myFollowing.eliminarIgnoreCase(targetUser);
      FileUtils.saveList(AppPaths.followingFile(miUser), myFollowing.toList());

      ListaEnlazada<String> theirFollowers = getFollowersEnlazada(targetUser);
      theirFollowers.eliminarIgnoreCase(miUser);
      FileUtils.saveList(AppPaths.followersFile(targetUser), theirFollowers.toList());
   }
   public ListaEnlazada<String> getFollowersEnlazada(String username) throws IOException {
      return ListaEnlazada.fromList(FileUtils.loadList(AppPaths.followersFile(username)));
   }
   public ListaEnlazada<String> getFollowingEnlazada(String username) throws IOException {
      return ListaEnlazada.fromList(FileUtils.loadList(AppPaths.followingFile(username)));
   }
   public List<String> getFollowers(String username) throws IOException {
      return getFollowersEnlazada(username).toList();
   }
   public List<String> getFollowing(String username) throws IOException {
      return getFollowingEnlazada(username).toList();
   }
   public boolean esSeguidor(String yo, String otro) throws IOException {
      return getFollowingEnlazada(yo).contieneIgnoreCase(otro);
   }
   public boolean sonAmigos(String a, String b) throws IOException {
      return esSeguidor(a, b) && esSeguidor(b, a);
   }
   public List<String> getRequests(String username) throws IOException {
      return FileUtils.loadList(AppPaths.requestsFile(username));
   }
   public void enviarSolicitud(String miUser, String targetUser) throws IOException {
      List<String> requests = getRequests(targetUser);
      if (!contieneIgnoreCase(requests, miUser)) {
         requests.add(miUser);
         FileUtils.saveList(AppPaths.requestsFile(targetUser), requests);
      }
      agregarNotificacion(targetUser, new Notificacion(
            Notificacion.Tipo.SOLICITUD, miUser,
            "@" + miUser + " solicitó seguirte."));
   }
   public void aceptarSolicitud(String miUser, String requester) throws IOException {
      rechazarSolicitud(miUser, requester);
      List<String> myFollowers = getFollowers(miUser);
      if (!contieneIgnoreCase(myFollowers, requester)) {
         myFollowers.add(requester);
         FileUtils.saveList(AppPaths.followersFile(miUser), myFollowers);
      }
      List<String> theirFollowing = getFollowing(requester);
      if (!contieneIgnoreCase(theirFollowing, miUser)) {
         theirFollowing.add(miUser);
         FileUtils.saveList(AppPaths.followingFile(requester), theirFollowing);
      }
      agregarNotificacion(requester, new Notificacion(
            Notificacion.Tipo.SEGUIDOR, miUser,
            "@" + miUser + " aceptó tu solicitud. Ahora lo sigues."));
      agregarNotificacion(miUser, new Notificacion(
            Notificacion.Tipo.SEGUIDOR, requester,
            "@" + requester + " comenzó a seguirte."));
   }
   public void rechazarSolicitud(String miUser, String requester) throws IOException {
      List<String> requests = getRequests(miUser);
      eliminarIgnoreCase(requests, requester);
      FileUtils.saveList(AppPaths.requestsFile(miUser), requests);
      eliminarNotificacionTipo(miUser, Notificacion.Tipo.SOLICITUD, requester);
   }
   public boolean haSolicitado(String miUser, String targetUser) throws IOException {
      return contieneIgnoreCase(getRequests(targetUser), miUser);
   }
   @SuppressWarnings("unchecked")
   public List<Notificacion> getNotificaciones(String username) throws IOException {
      try {
         Object obj = FileUtils.loadObject(AppPaths.notifsFile(username));
         if (obj instanceof List) {
            List<?> raw = (List<?>) obj;
            if (!raw.isEmpty() && raw.get(0) instanceof Notificacion) {
               return (List<Notificacion>) obj;
            }
         }
      } catch (Exception ignored) {}
      return new ArrayList<>();
   }
   public void agregarNotificacion(String username, Notificacion n) throws IOException {
      List<Notificacion> notifs = getNotificaciones(username);
      notifs.add(0, n);
      FileUtils.saveObject(AppPaths.notifsFile(username), new java.util.ArrayList<>(notifs));
   }
   public void notificarLike(String autorPost, String likerUsername) throws IOException {
      if (autorPost.equalsIgnoreCase(likerUsername)) return; 
      agregarNotificacion(autorPost, new Notificacion(
            Notificacion.Tipo.LIKE, likerUsername,
            "@" + likerUsername + " le dio me gusta a tu publicación."));
   }
   public void notificarComentario(String autorPost, String commenterUsername, String textoPreview) throws IOException {
      if (autorPost.equalsIgnoreCase(commenterUsername)) return;
      String preview = textoPreview.length() > 40 ? textoPreview.substring(0, 37) + "..." : textoPreview;
      agregarNotificacion(autorPost, new Notificacion(
            Notificacion.Tipo.COMENTARIO, commenterUsername,
            "@" + commenterUsername + " comentó: \"" + preview + "\""));
   }
   private void eliminarNotificacionTipo(String username, Notificacion.Tipo tipo, String autor) throws IOException {
      List<Notificacion> notifs = getNotificaciones(username);
      notifs.removeIf(n -> n.getTipo() == tipo && n.getAutorUsername().equalsIgnoreCase(autor));
      FileUtils.saveObject(AppPaths.notifsFile(username), new java.util.ArrayList<>(notifs));
   }
   public void marcarTodasComoLeidas(String username) throws IOException {
      List<Notificacion> notifs = getNotificaciones(username);
      for (Notificacion n : notifs) {
         n.setLeida(true);
      }
      FileUtils.saveObject(AppPaths.notifsFile(username), new java.util.ArrayList<>(notifs));
   }
   public boolean tieneNotificacionesSinLeer(String username) throws IOException {
      for (Notificacion n : getNotificaciones(username)) {
         if (!n.isLeida()) return true;
      }
      if (!getRequests(username).isEmpty()) return true;
      return false;
   }
   public void limpiarNotificaciones(String username) throws IOException {
      FileUtils.saveObject(AppPaths.notifsFile(username), new java.util.ArrayList<Notificacion>());
   }
   private boolean contieneIgnoreCase(List<String> lista, String valor) {
      for (String s : lista) {
         if (s.equalsIgnoreCase(valor)) return true;
      }
      return false;
   }
   private void eliminarIgnoreCase(List<String> lista, String valor) {
      lista.removeIf(s -> s.equalsIgnoreCase(valor));
   }
   public void autoSeguirCuentasPorDefecto(String nuevoUsername) throws IOException {
      List<String> disponibles = new ArrayList<>();
      for (String cuenta : insta.storage.DatosPorDefecto.CUENTAS) {
         if (!cuenta.equalsIgnoreCase(nuevoUsername)) {
            disponibles.add(cuenta);
         }
      }
      java.util.Collections.shuffle(disponibles);
      int maxASeguir = Math.min(3, disponibles.size());
      for (int i = 0; i < maxASeguir; i++) {
         seguir(nuevoUsername, disponibles.get(i));
      }
   }
}
