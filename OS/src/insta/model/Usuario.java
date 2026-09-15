/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package insta.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

/**
 *
 * @author riche
 */
public class Usuario extends Persona {
   private static final long serialVersionUID = 1L;
   private String username;
   private String password;
   private LocalDate fechaRegistro;
   private boolean activo;
   private TipoCuenta tipoCuenta;
   private String rutaFotoPerfil;
   private String presentacion;

   public Usuario(String nombreCompleto, String genero, String username,
         String password, int edad, String tipoCuentaStr, String rutaFotoPerfil) {
      super(nombreCompleto, genero, edad);
      this.username = username;
      this.password = password;
      this.tipoCuenta = parseTipoCuenta(tipoCuentaStr);
      this.rutaFotoPerfil = rutaFotoPerfil;
      this.presentacion = "";
      this.fechaRegistro = LocalDate.now();
      this.activo = true;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField fields = ois.readFields();
      username = (String) fields.get("username", null);
      password = (String) fields.get("password", null);
      fechaRegistro = (LocalDate) fields.get("fechaRegistro", null);
      activo = fields.get("activo", true);
      rutaFotoPerfil = (String) fields.get("rutaFotoPerfil", null);
      presentacion = (String) fields.get("presentacion", "");
      Object tc = fields.get("tipoCuenta", null);
      if (tc instanceof TipoCuenta) {
         tipoCuenta = (TipoCuenta) tc;
      } else if (tc instanceof String) {
         tipoCuenta = parseTipoCuenta((String) tc);
      } else {
         tipoCuenta = TipoCuenta.PUBLICA;
      }
   }

   private void writeObject(ObjectOutputStream oos) throws IOException {
      oos.defaultWriteObject();
   }

   private static TipoCuenta parseTipoCuenta(String valor) {
      if (valor == null)
         return TipoCuenta.PUBLICA;
      try {
         return TipoCuenta.valueOf(valor.toUpperCase());
      } catch (IllegalArgumentException e) {
         return TipoCuenta.PUBLICA;
      }
   }

   public String getUsername() {
      return username;
   }

   public String getPassword() {
      return password;
   }

   public LocalDate getFechaRegistro() {
      return fechaRegistro;
   }

   public boolean isActivo() {
      return activo;
   }

   public TipoCuenta getTipoCuentaEnum() {
      return tipoCuenta;
   }

   public String getTipoCuenta() {
      return tipoCuenta != null ? tipoCuenta.name() : "PUBLICA";
   }

   public String getRutaFotoPerfil() {
      return rutaFotoPerfil;
   }

   public String getPresentacion() {
      return presentacion;
   }

   public void setActivo(boolean activo) {
      this.activo = activo;
   }

   public void setTipoCuenta(String tipoCuentaStr) {
      this.tipoCuenta = parseTipoCuenta(tipoCuentaStr);
   }

   public void setTipoCuentaEnum(TipoCuenta tc) {
      this.tipoCuenta = tc;
   }

   public void setRutaFotoPerfil(String ruta) {
      this.rutaFotoPerfil = ruta;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setPresentacion(String pres) {
      if (pres == null) {
         this.presentacion = "";
         return;
      }
      pres = pres.replaceAll("(\\n|\\r\\n){2,}", "\n");
      String[] lines = pres.split("\n");
      if (lines.length > 5) {
         pres = String.join("\n", java.util.Arrays.copyOfRange(lines, 0, 5));
      }
      this.presentacion = pres;
   }

   @Override
   public String toString() {
      return "@" + username + " (" + nombreCompleto + ")";
   }
}
