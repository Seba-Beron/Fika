/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

/**
 *
 * @author Sebastian
 */
public class PedidoController {

    public static Route verPedidos = (Request req, Response res) -> {

        PedidoDAO pDAO = new PedidoDAO();
        ArrayList<Pedido> pedidos = pDAO.buscarPedidos();

        UsuarioDAO uDAO = new UsuarioDAO();
        List<Usuario> usuarios = uDAO.buscarUsuarios();

        HashMap model = new HashMap();
        model.put("pedidos", pedidos);
        model.put("usuarios", usuarios);
        model.put("template", "templates/pedidos.vsl");
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route verPedido = (Request req, Response res) -> {
        
        int id = Integer.parseInt(req.queryParams("id"));

        PedidoDAO pDAO = new PedidoDAO();
        Pedido pedido = pDAO.buscarPedido(id);
        
        UsuarioDAO uDAO = new UsuarioDAO();
        Usuario usuario = uDAO.buscarUsuario(pedido.getUsuario_id());
        
        HashMap model = new HashMap();
        model.put("pedido", pedido);
        model.put("usuario", usuario);
        model.put("template", "templates/pedido.vsl");
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl")); 
    };

    public static Route verHistorialCordova = (Request req, Response res) -> {
        int id_usuario = 2;
        //req.session().attribute("id");
        
        PedidoDAO pDAO = new PedidoDAO();
        ArrayList<Pedido> pedidos = pDAO.buscarHistorial(id_usuario);   // cast ???
        
        HashMap model = new HashMap();
        model.put("pedidos", pedidos);

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templatesCordova/historial.vsl")); 
    };

    public static Route verHistorial = (Request req, Response res) -> {
        
        int id_usuario = req.session().attribute("id");
        
        PedidoDAO pDAO = new PedidoDAO();
        ArrayList<Pedido> pedidos = pDAO.buscarHistorial(id_usuario);   // cast ???
        
        HashMap model = new HashMap();
        model.put("pedidos", pedidos);
        model.put("template", "templates/historial.vsl");
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl")); 
    };

    public static Route comprarCarritoCordova = (Request req, Response res) -> {
        int id_usuario = req.session().attribute("id");
        String direccion = req.queryParams("direccion");
        String tipo = req.queryParams("tipo");
        int tipo_pago = Integer.parseInt(req.queryParams("tipo-pago"));

        System.out.println("pedido");
        PedidoDAO pDAO = new PedidoDAO();

        int id_pedido = pDAO.comprarCarrito(id_usuario, direccion, tipo);

        HashMap model = new HashMap();

        if (id_pedido < 0) {
            System.out.println("fuera del horario de atencion");
            model.put("error", "no se pueden realizar pedidos fuera del horario de atencion");
        } else {
            System.out.println("factura: " + id_pedido);
            FacturaDAO fDAO = new FacturaDAO();
            fDAO.generarFactura(tipo_pago, id_pedido, tipo); // crear factura

            System.out.println("stock");
            ProductoDAO proDAO = new ProductoDAO();
            proDAO.actualizarStock(id_pedido); //actualiza el stock

            res.redirect("/inicioCordova");
        }
        // que hago con el layout?
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route comprarCarrito = (Request req, Response res) -> {
        int id_usuario = req.session().attribute("id");
        String direccion = req.queryParams("direccion");
        String tipo = req.queryParams("tipo");
        int tipo_pago = Integer.parseInt(req.queryParams("tipo-pago"));

        System.out.println("pedido");
        PedidoDAO pDAO = new PedidoDAO();

        int id_pedido = pDAO.comprarCarrito(id_usuario, direccion, tipo);

        HashMap model = new HashMap();

        if (id_pedido < 0) {
            System.out.println("fuera del horario de atencion");
            model.put("error", "no se pueden realizar pedidos fuera del horario de atencion");
        } else {
            System.out.println("factura: " + id_pedido);
            FacturaDAO fDAO = new FacturaDAO();
            fDAO.generarFactura(tipo_pago, id_pedido, tipo); // crear factura

            System.out.println("stock");
            ProductoDAO proDAO = new ProductoDAO();
            proDAO.actualizarStock(id_pedido); //actualiza el stock

            res.redirect("/inicio");
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route comprarProducto = (Request req, Response res) -> {
        
        int id_usuario = req.session().attribute("id_usuario");
        int id_producto = Integer.parseInt(req.queryParams("id_producto"));
        int cantidad = Integer.parseInt(req.queryParams("cantidad"));
        String direccion = req.queryParams("direccion");
        String tipo = req.queryParams("tipo");
        int tipo_pago = Integer.parseInt(req.queryParams("tipo-pago"));
        
        // q se comunique con los controller en ves de con los dao?
        
        PedidoDAO pedDAO = new PedidoDAO();
        int id_pedido = pedDAO.crearPedido(id_usuario, 1, direccion, tipo); // crea el pedido
        
        HashMap model = new HashMap();
        
        if(id_pedido < 0){
            System.out.println("fuera del horario de atencion");
            model.put("error", "no se pueden realizar pedidos fuera del horario de atencion");
        }
        else{
            CarritoDAO cDAO = new CarritoDAO();
            cDAO.crearCarrito(id_pedido, id_producto, cantidad);    // crear carrito        
        
            FacturaDAO fDAO = new FacturaDAO();
            fDAO.generarFactura(id_pedido, tipo_pago, tipo);   // crear factura
        
            ProductoDAO proDAO = new ProductoDAO();
            proDAO.actualizarStock(cantidad, id_producto);    //actualiza el stock
        }
        
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl")); 
    };
}
