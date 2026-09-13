/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.gestor;
import insta.model.Mensaje;
import insta.model.Usuario;
import insta.storage.AppPaths;
import insta.storage.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author riche
 */
public class GestorInbox {
   public boolean puedeEnviar(String emisor, String receptor,
         GestorFollowers gf,
         GestorUsuarios gu) throws IOException {
      if (emisor.equalsIgnoreCase(receptor))
         return false; 
      Usuario rec = gu.buscarExacto(receptor);
      if (rec == null || !rec.isActivo())
         return false;
      if (gf.sonAmigos(emisor, receptor))
         return true;
      return "PUBLICA".equalsIgnoreCase(rec.getTipoCuenta());
   }
   public void enviarMensaje(Mensaje m) throws IOException {
      List<Mensaje> inboxReceptor = getMensajesDeUsuario(m.getReceptor());
      inboxReceptor.add(m);
      FileUtils.saveList(AppPaths.inboxFile(m.getReceptor()), inboxReceptor);
      List<Mensaje> inboxEmisor = getMensajesDeUsuario(m.getEmisor());
      inboxEmisor.add(m);
      FileUtils.saveList(AppPaths.inboxFile(m.getEmisor()), inboxEmisor);
   }
   public List<String> getConversaciones(String username, GestorUsuarios gu) throws IOException {
      List<String> interlocutores = new ArrayList<>();
      for (Mensaje m : getMensajesDeUsuario(username)) {
         String otro = m.getEmisor().equalsIgnoreCase(username)
               ? m.getReceptor()
               : m.getEmisor();
         if (!interlocutores.contains(otro)) {
            Usuario uOtro = gu.buscarExacto(otro);
            if (uOtro != null && uOtro.isActivo()) {
               interlocutores.add(otro);
            }
         }
      }
      return interlocutores;
   }
   public List<Mensaje> getMensajesConversacion(String yo, String otro) throws IOException {
      List<Mensaje> conv = new ArrayList<>();
      for (Mensaje m : getMensajesDeUsuario(yo)) {
         boolean esDeConversacion = (m.getEmisor().equalsIgnoreCase(yo) && m.getReceptor().equalsIgnoreCase(otro)) ||
               (m.getEmisor().equalsIgnoreCase(otro) && m.getReceptor().equalsIgnoreCase(yo));
         if (esDeConversacion)
            conv.add(m);
      }
      return conv;
   }
   public void marcarLeido(String yo, String otro) throws IOException {
      List<Mensaje> todos = getMensajesDeUsuario(yo);
      for (Mensaje m : todos) {
         if (m.getReceptor().equalsIgnoreCase(yo) &&
               m.getEmisor().equalsIgnoreCase(otro)) {
            m.setLeido(true);
         }
      }
      FileUtils.saveList(AppPaths.inboxFile(yo), todos);
   }
   public void eliminarConversacion(String yo, String otro) throws IOException {
      List<Mensaje> todos = getMensajesDeUsuario(yo);
      List<Mensaje> restantes = new ArrayList<>();
      for (Mensaje m : todos) {
         boolean esDeConversacion = (m.getEmisor().equalsIgnoreCase(yo) && m.getReceptor().equalsIgnoreCase(otro)) ||
               (m.getEmisor().equalsIgnoreCase(otro) && m.getReceptor().equalsIgnoreCase(yo));
         if (!esDeConversacion)
            restantes.add(m);
      }
      FileUtils.saveList(AppPaths.inboxFile(yo), restantes);
   }
   public boolean tieneNoLeidos(String username, GestorUsuarios gu) throws IOException {
      for (Mensaje m : getMensajesDeUsuario(username)) {
         if (m.getReceptor().equalsIgnoreCase(username) && !m.isLeido()) {
            Usuario emisor = gu.buscarExacto(m.getEmisor());
            if (emisor != null && emisor.isActivo()) {
               return true;
            }
         }
      }
      return false;
   }
   private List<Mensaje> getMensajesDeUsuario(String username) throws IOException {
      return FileUtils.loadList(AppPaths.inboxFile(username));
   }
}
