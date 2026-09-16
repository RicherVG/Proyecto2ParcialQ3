/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import Excepciones.UsernameDuplicadoException;
import Persistencia.GestorUsuario;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import modelo.Usuarios;

/**
 *
 * @author andre
 */
public class AdministrarCuentas extends JInternalFrame {
    
    private final Usuarios admin;
    private final GestorUsuario gestor;
    private DefaultListModel<Usuarios> modeloLista;
    private JList<Usuarios> listaUsuarios;
    private Runnable alEliminarCuentaPropia;
    
    public AdministrarCuentas(Usuarios admin){
        super("Administrar cuentas", true, true, true, true);
        this.admin = admin;
        this.gestor = new GestorUsuario();
        setSize(500, 400);
        construirVentana();
    }
    
    private void construirVentana(){
        setLayout(new BorderLayout());
        
        modeloLista = new DefaultListModel<>();
        for (Usuarios u : gestor.getUsuarios()) {
            modeloLista.addElement(u);
        }
        
        listaUsuarios = new JList<>(modeloLista);
        listaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        add(new JScrollPane(listaUsuarios), BorderLayout.CENTER);
        
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnNuevo = new JButton("Nuevo usuario");
        btnNuevo.addActionListener(e -> crearUsuario());
        JButton btnEliminar = new JButton("Eliminar cuenta");
        btnEliminar.addActionListener(e -> eliminarCuenta());
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        sur.add(btnNuevo);
        sur.add(btnEliminar);
        sur.add(btnCerrar);
        add(sur, BorderLayout.SOUTH);
    }
    
    private void crearUsuario(){
        JTextField txtNombre = new JTextField(15);
        JTextField txtUsername = new JTextField(15);
        JTextField txtPassword = new JTextField(15);
        JTextField txtEdad = new JTextField(15);
        
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Nombre completo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        formulario.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        formulario.add(txtUsername, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        formulario.add(txtPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        formulario.add(txtEdad, gbc);
        
        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Nuevo usuario",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;
        
        try {
            int edad = Integer.parseInt(txtEdad.getText().trim());
            Usuarios nuevo = new Usuarios(
                    txtNombre.getText().trim(),
                    txtUsername.getText().trim(),
                    txtPassword.getText().trim(),
                    edad, 'M', false);
            gestor.registrarUsuario(nuevo);
            modeloLista.addElement(nuevo);
            JOptionPane.showMessageDialog(this, "Usuario creado: " + nuevo.getUsername());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número.");
        } catch (UsernameDuplicadoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private void eliminarCuenta(){
        Usuarios seleccionado = listaUsuarios.getSelectedValue();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta de la lista.");
            return;
        }
        
        // Regla: una cuenta admin solo puede borrarla el propio admin (asi mismo)
        if (seleccionado.isEsAdmin() && !seleccionado.getUsername().equalsIgnoreCase(admin.getUsername())) {
            JOptionPane.showMessageDialog(this, """
                                                No puedes eliminar la cuenta de otro administrador.
                                                Solo puedes eliminar tu propia cuenta de administrador.""");
            return;
        }
        
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la cuenta de " + seleccionado.getUsername() + "?",
                "Eliminar cuenta", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        
        gestor.eliminarUsuario(seleccionado);
        modeloLista.removeElement(seleccionado);
        JOptionPane.showMessageDialog(this, "Cuenta eliminada: " + seleccionado.getUsername());
        
        if(seleccionado.getUsername().equalsIgnoreCase(admin.getUsername())&& alEliminarCuentaPropia != null){
        alEliminarCuentaPropia.run();
    }
    }
    
    public void setOnCuentaPropiaEliminada(Runnable accion){
       this.alEliminarCuentaPropia = accion;
   }
    
}
