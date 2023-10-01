/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import spark.ModelAndView;
import java.util.HashMap;

import java.util.List;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

/**
 *
 * @author Sebastian
 */
public class UsuarioController {
    
    public static Route getLoginCordova = (Request req, Response res) -> {
            System.out.println("entro");
            HashMap model = new HashMap();
            
            //LLAMADO AL MENU
            Menu menu = new Menu();
            model.put("menu", menu.getMenu());  
            model.put("menuActivo", "login");   
            System.out.println("pass: "+ req.queryParams("pass") + "email: " + req.queryParams("email"));
            
            if(req.queryParams("pass")!=null && req.queryParams("email")!=null){
                System.out.println("if1");
                UsuarioDAO uDAO = new UsuarioDAO();
                List<Usuario> usuario = uDAO.verificarPersona(req.queryParams("email"),req.queryParams("pass"));

                if(!usuario.isEmpty()){
                    System.out.println("if2");
                    //CREAR SEASION/COOKIE
                    //model.put("template", "templates/main.vsl"); esto es para cuendo se loguea
                    Usuario usuarioLogeado = usuario.get(0);
                    req.session(true);                     // Crear y retornar la sesion
                    req.session().attribute("id", usuarioLogeado.getId() );       
                    req.session().attribute("email", usuarioLogeado.getEmail());
                    req.session().attribute("admin", usuarioLogeado.getAdmin() );
                    res.redirect("/inicioCordova");
                }else{
                    System.out.println("else1");
                    model.put("request",req);
                    model.put("error", "La contraseña o el email es incorrecto.");
                }
            }else{
                System.out.println("else2");
                model.put("email","");
            }
            System.out.println("return");
            return new VelocityTemplateEngine().render(new ModelAndView(model, "templatesCordova/login.vsl")); 
        }; 
    
    public static Route getLogin = (Request req, Response res) -> {
     
            HashMap model = new HashMap();
            
            //LLAMADO AL MENU
            Menu menu = new Menu();
            model.put("menu", menu.getMenu());  
            model.put("menuActivo", "login");   
            
            if(req.queryParams("pass")!=null && req.queryParams("email")!=null){

                UsuarioDAO uDAO = new UsuarioDAO();
                List<Usuario> usuario = uDAO.verificarPersona(req.queryParams("email"),req.queryParams("pass"));

                if(!usuario.isEmpty()){
                    //CREAR SEASION/COOKIE
                    //model.put("template", "templates/main.vsl"); esto es para cuendo se loguea
                    Usuario usuarioLogeado = usuario.get(0);
                    req.session(true);                     // Crear y retornar la sesion
                    req.session().attribute("id", usuarioLogeado.getId() );       
                    req.session().attribute("email", usuarioLogeado.getEmail());
                    req.session().attribute("admin", usuarioLogeado.getAdmin() );
                    res.redirect("/inicio");
                }else{
                    model.put("template", "templates/login.vsl");
                    model.put("request",req);
                    model.put("error", "La contraseña o el email es incorrecto.");
                }
            }else{
                model.put("email","");
                model.put("template", "templates/login.vsl");   
            }
            return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl")); 
        }; 
    
    public static Route LogoutCordova = (Request req, Response res) -> {
            req.session().removeAttribute("id");
            req.session().removeAttribute("email");
            res.redirect("/indexCordova");
            return null;
    };
    
    public static Route Logout = (Request req, Response res) -> {
            req.session().removeAttribute("id");
            req.session().removeAttribute("email");
            res.redirect("/index");
            return null;
    };
}
