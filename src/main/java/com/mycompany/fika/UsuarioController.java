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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian
 */
public class UsuarioController {

    final static Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    public static Route getLogin = (Request req, Response res) -> {

        HashMap model = new HashMap();

        // LLAMADO AL MENU
        Menu menu = new Menu();
        model.put("menu", menu.getMenu());
        model.put("menuActivo", "login");

        if (req.queryParams("pass") != null && req.queryParams("email") != null) {

            List<Usuario> usuario = FactoryDAO.getUsuarioDAO().verificarPersona(req.queryParams("email"), req.queryParams("pass"));

            if (!usuario.isEmpty()) {
                // CREAR SEASION/COOKIE
                // model.put("template", "templates/main.vsl"); esto es para cuendo se loguea
                Usuario usuarioLogeado = usuario.get(0);
                req.session(true); // Crear y retornar la sesion
                req.session().attribute("id", usuarioLogeado.getId());
                req.session().attribute("email", usuarioLogeado.getEmail());
                req.session().attribute("admin", usuarioLogeado.getAdmin());
                res.redirect("/inicio");
            } else {
                model.put("template", "templates/login.vsl");
                model.put("request", req);
                model.put("error", "La contraseña o el email es incorrecto.");
                logger.info("La contraseña o el email es incorrecto.");

            }
        } else {
            model.put("email", "");
            model.put("template", "templates/login.vsl");
        }
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route Logout = (Request req, Response res) -> {
        req.session().removeAttribute("id");
        req.session().removeAttribute("email");
        res.redirect("/index");
        logger.info("Usuario deslogeado");
        return null;
    };
}
