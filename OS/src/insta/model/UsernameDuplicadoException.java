package insta.model;

/**
 * Excepción lanzada cuando se intenta registrar un nombre de usuario que ya existe en el sistema.
 * 
 * @author riche
 */
public class UsernameDuplicadoException extends Exception {
   private static final long serialVersionUID = 1L;

   public UsernameDuplicadoException(String username) {
      super("El nombre de usuario '" + username + "' ya existe en el sistema.");
   }

   public UsernameDuplicadoException(String message, Throwable cause) {
      super(message, cause);
   }
}
