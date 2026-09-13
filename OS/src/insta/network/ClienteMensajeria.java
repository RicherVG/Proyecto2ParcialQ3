/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.network;
import java.io.*;
import java.net.*;
import java.util.function.Consumer;
/**
 *
 * @author riche
 */
public class ClienteMensajeria {
   private static final String HOST = "localhost";
   private static final int PUERTO = 6000;
   private Socket socket;
   private ObjectOutputStream salida;
   private ObjectInputStream entrada;
   private final String miUsuario;
   private Consumer<insta.model.Mensaje> onMensajeRecibido;
   private boolean conectado = false;
   public ClienteMensajeria(String miUsuario) {
      this.miUsuario = miUsuario;
   }
   public void conectar() {
      new Thread(() -> {
         try {
            socket = new Socket(HOST, PUERTO);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            salida.writeObject(miUsuario);
            salida.flush();
            conectado = true;

            while (conectado) {
               Object obj = entrada.readObject();
               if (obj instanceof insta.model.Mensaje && onMensajeRecibido != null) {
                  onMensajeRecibido.accept((insta.model.Mensaje) obj);
               }
            }
         } catch (Exception e) {
            System.err.println("[Cliente] Error de conexión: " + e.getMessage());
            conectado = false;
         }
      }).start();
   }
   public void enviarMensaje(insta.model.Mensaje msg) {
      if (!conectado) return;
      try {
         salida.writeObject(msg);
         salida.flush();
      } catch (IOException e) {
         System.err.println("[Cliente] Error al enviar mensaje: " + e.getMessage());
      }
   }
   public void setOnMensajeRecibido(Consumer<insta.model.Mensaje> callback) {
      this.onMensajeRecibido = callback;
   }
   public void desconectar() {
      conectado = false;
      try {
         if (socket != null) socket.close();
      } catch (IOException ignored) {}
   }
   public boolean isConectado() {
      return conectado;
   }
}
