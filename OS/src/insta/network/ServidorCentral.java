/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.network;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
/**
 *
 * @author riche
 */
public class ServidorCentral {
   private static final int PUERTO = 6000;
   private final ServerSocket servidor;
   private final Map<String, GestorCliente> clientesConectados = new ConcurrentHashMap<>();
   private final ExecutorService executor = Executors.newCachedThreadPool();
   public ServidorCentral() throws IOException {
      this.servidor = new ServerSocket(PUERTO);
      System.out.println("[Servidor] Escuchando en el puerto " + PUERTO);
   }
   public void iniciar() {
      while (true) {
         try {
            Socket socket = servidor.accept();
            executor.execute(new GestorCliente(socket));
         } catch (IOException e) {
            System.err.println("[Servidor] Error al aceptar conexión: " + e.getMessage());
         }
      }
   }
   private class GestorCliente implements Runnable {
      private final Socket socket;
      private ObjectOutputStream salida;
      private ObjectInputStream entrada;
      private String nombreUsuario;
      public GestorCliente(Socket socket) {
         this.socket = socket;
      }
      @Override
      public void run() {
         try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            nombreUsuario = (String) entrada.readObject();
            clientesConectados.put(nombreUsuario, this);
            while (true) {
               Object obj = entrada.readObject();
               if (obj instanceof insta.model.Mensaje) {
                  insta.model.Mensaje msg = (insta.model.Mensaje) obj;
                  retransmitir(msg);
               }
            }
         } catch (Exception e) {
            if (nombreUsuario != null) {
               clientesConectados.remove(nombreUsuario);
            }
         } finally {
            try {
               socket.close();
            } catch (IOException ignored) {}
         }
      }
      private void retransmitir(insta.model.Mensaje msg) {

         GestorCliente destino = clientesConectados.get(msg.getReceptor());
         if (destino != null) {
            try {
               destino.enviar(msg);
            } catch (IOException e) {
               System.err.println("[Servidor] Error al retransmitir: " + e.getMessage());
            }
         } else {

         }
      }
      public void enviar(Object obj) throws IOException {
         salida.writeObject(obj);
         salida.flush();
      }
   }
   public static void main(String[] args) {
      try {
         new ServidorCentral().iniciar();
      } catch (IOException e) {
         System.err.println("[Servidor] Error fatal: " + e.getMessage());
      }
   }
}
