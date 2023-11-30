/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import spark.Route;

import spark.template.velocity.VelocityTemplateEngine;

/**
 *
 * @author Sebastian
 */
public class CarritoController {

    final static Logger logger = LoggerFactory.getLogger(ProductoController.class);

    public static Route agregarCarritoCordova = (Request req, Response res) -> {

        int id_usuario = req.session().attribute("id");
        int id_producto = Integer.parseInt(req.queryParams("id_producto"));
        int cantidad = Integer.parseInt(req.queryParams("cantidad"));

        res.redirect("/inicioCordova");

        HashMap model = new HashMap();

        CarritoDAO cDAO = new CarritoDAO();

        model.put("agregado", cDAO.actualizarCarrito(id_usuario, id_producto, cantidad));
        // q hago con el layout?
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route agregarCarrito = (Request req, Response res) -> {
        HashMap model = new HashMap();

        try {
            int id_usuario = req.session().attribute("id");
            int id_producto = Integer.parseInt(req.queryParams("id_producto"));
            int cantidad = Integer.parseInt(req.queryParams("cantidad"));

            res.redirect("/inicio"); // ver el tema del alert!

            CarritoDAO cDAO = new CarritoDAO();

            model.put("agregado", cDAO.actualizarCarrito(id_usuario, id_producto, cantidad));
        } catch (Exception e) {
            logger.error("Error al agregar al carrito: " + e.getMessage());
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route verCarritoCordova = (Request req, Response res) -> {
        CarritoDAO cDAO = new CarritoDAO();

        List<Producto> productos = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();

        cDAO.verCarrito(req.session().attribute("id")).forEach((p, c) -> {
            productos.add(p);
            cantidades.add(c);
        });

        HashMap model = new HashMap();
        model.put("productos", productos);
        model.put("cantidades", cantidades);

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templatesCordova/carrito.vsl"));
    };
    public static Route verCarrito = (Request req, Response res) -> {
        CarritoDAO cDAO = new CarritoDAO();

        List<Producto> productos = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();
        HashMap<Producto, Integer> carrito = cDAO.verCarrito(req.session().attribute("id"));

        carrito.forEach((p, c) -> {
            productos.add(p);
            cantidades.add(c);
        });

        HashMap model = new HashMap();
        model.put("productos", productos);
        model.put("cantidades", cantidades);
        model.put("template", "templates/carrito.vsl");
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };
}
