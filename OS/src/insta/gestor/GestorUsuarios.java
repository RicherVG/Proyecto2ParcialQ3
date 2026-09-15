/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.gestor;

import insta.model.CuentaDesactivadaException;
import insta.model.ListaEnlazada;
import insta.model.Usuario;
import insta.model.UsernameDuplicadoException;
import insta.storage.AppPaths;
import insta.storage.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author riche
 */
public class GestorUsuarios {
   public void registrar(Usuario nuevo) throws IOException, UsernameDuplicadoException {
      List<Usuario> todos = cargarTodos();
      for (Usuario u : todos) {
         if (u.getUsername().equalsIgnoreCase(nuevo.getUsername())) {
            throw new UsernameDuplicadoException(nuevo.getUsername());
         }
      }
      FileUtils.ensureUserStructure(nuevo.getUsername());
      todos.add(nuevo);
      guardarTodos(todos);
   }

   public Usuario login(String username, String password) throws IOException, CuentaDesactivadaException {
      for (Usuario u : cargarTodos()) {
         if (u.getUsername().equalsIgnoreCase(username)
               && u.getPassword().equals(password)) {
            if (!u.isActivo()) {
               throw new CuentaDesactivadaException(u.getUsername());
            }
            return u;
         }
      }
      return null;
   }

   public List<Usuario> buscarPorUsername(String parcial) throws IOException {
      ListaEnlazada<Usuario> resultado = buscarPorUsernameEnlazada(parcial);
      return resultado.toList();
   }

   public ListaEnlazada<Usuario> buscarPorUsernameEnlazada(String parcial) throws IOException {
      ListaEnlazada<Usuario> resultado = new ListaEnlazada<>();
      String p = parcial.toLowerCase();
      for (Usuario u : cargarTodos()) {
         if (u.isActivo() && u.getUsername().toLowerCase().contains(p)) {
            resultado.agregar(u);
         }
      }
      return resultado;
   }

   public Usuario buscarExacto(String username) throws IOException {
      for (Usuario u : cargarTodos()) {
         if (u.getUsername().equalsIgnoreCase(username))
            return u;
      }
      return null;
   }

   public void desactivar(String username) throws IOException {
      List<Usuario> todos = cargarTodos();
      for (Usuario u : todos) {
         if (u.getUsername().equalsIgnoreCase(username)) {
            u.setActivo(false);
            break;
         }
      }
      guardarTodos(todos);
   }

   public void activar(String username) throws IOException {
      List<Usuario> todos = cargarTodos();
      for (Usuario u : todos) {
         if (u.getUsername().equalsIgnoreCase(username)) {
            u.setActivo(true);
            break;
         }
      }
      guardarTodos(todos);
   }

   public void actualizar(Usuario modificado) throws IOException {
      List<Usuario> todos = cargarTodos();
      for (int i = 0; i < todos.size(); i++) {
         if (todos.get(i).getUsername().equalsIgnoreCase(modificado.getUsername())) {
            todos.set(i, modificado);
            break;
         }
      }
      guardarTodos(todos);
   }

   public List<Usuario> cargarTodos() throws IOException {
      return FileUtils.loadList(AppPaths.USERS_FILE);
   }

   public void guardarTodos(List<Usuario> usuarios) throws IOException {
      FileUtils.saveList(AppPaths.USERS_FILE, usuarios);
   }

   public boolean usernameExiste(String username) throws IOException {
      for (Usuario u : cargarTodos()) {
         if (u.getUsername().equalsIgnoreCase(username))
            return true;
      }
      return false;
   }
}
