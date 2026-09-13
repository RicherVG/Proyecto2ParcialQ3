package insta.model;

/**
 * Excepción lanzada cuando se intenta autenticar o acceder con una cuenta que está desactivada.
 * 
 * @author riche
 */
public class CuentaDesactivadaException extends Exception {
   private static final long serialVersionUID = 1L;
   private final String username;

   public CuentaDesactivadaException(String username) {
      super("La cuenta @" + username + " está desactivada.");
      this.username = username;
   }

   public CuentaDesactivadaException(String username, String message) {
      super(message);
      this.username = username;
   }

   public String getUsername() {
      return username;
   }
}
