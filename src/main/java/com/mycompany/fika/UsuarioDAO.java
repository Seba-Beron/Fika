/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import java.util.List;
import org.sql2o.Connection;

/**
 *
 * @author Sebastian
 */
public class UsuarioDAO {
    
    private List<Usuario> usuarios;

    public Usuario buscarUsuario(int id) {

        Usuario usuario = new Usuario();
        
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            
            String sql = "SELECT * FROM usuario WHERE id = :id";
            
            usuario = con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Usuario.class);
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return usuario;
    }

    public List<Usuario> buscarUsuarios() {
        
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            
            String sql = "SELECT * FROM usuario";
            
            usuarios = con
                .createQuery(sql)
                .executeAndFetch(Usuario.class);
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return usuarios;
    }
    
    public List<Usuario> verificarPersona( String email, String pass) {
        
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            
            String sql = "SELECT * FROM usuario WHERE email = :email and  pass = :pass";
            
            usuarios = con
                .createQuery(sql)
                .addParameter("email", email)
                .addParameter("pass", pass)
                .executeAndFetch(Usuario.class);
        }
        catch(Exception e) {
            System.out.println(e);}
        return usuarios;
    }
}
