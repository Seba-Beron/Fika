/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import java.util.List;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;

import java.math.BigDecimal;
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

        int id = Integer.parseInt(req.queryParams("id")); // id del pedido

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
        // req.session().attribute("id");

        PedidoDAO pDAO = new PedidoDAO();
        ArrayList<Pedido> pedidos = pDAO.buscarHistorial(id_usuario); // cast ???

        HashMap model = new HashMap();
        model.put("pedidos", pedidos);

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templatesCordova/historial.vsl"));
    };

    public static Route verHistorial = (Request req, Response res) -> {

        int id_usuario = req.session().attribute("id");

        PedidoDAO pDAO = new PedidoDAO();
        ArrayList<Pedido> pedidos = pDAO.buscarHistorial(id_usuario); // cast ???

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
            proDAO.actualizarStock(id_pedido); // actualiza el stock

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
            proDAO.actualizarStock(id_pedido); // actualiza el stock

            res.redirect("/inicio");
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route crearPreferencia = (Request req, Response res) -> {

        MercadoPagoConfig.setAccessToken("TEST-4546216443926115-110409-61a3ac5fe6da930fa33b3357cd5b6a76-216697042");
        // String url = "https://api.mercadopago.com/checkout/preferences";

        CarritoDAO cDAO = new CarritoDAO();

        try {

            HashMap<Producto, Integer> carrito = cDAO.verCarrito(req.session().attribute("id"));

            List<PreferenceItemRequest> items = new ArrayList<>();

            carrito.forEach((p, c) -> {
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                        .id(String.valueOf(p.getId()))
                        .title(p.getNombre())
                        .description(p.getNombre())
                        .pictureUrl(p.getImagen())
                        .categoryId(p.getTipo())
                        .quantity(c)
                        .currencyId("ARS")
                        .unitPrice(new BigDecimal(p.getPrecio()))
                        .build();

                items.add(itemRequest);
            });

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:4567/inicio")
                    .pending("http://localhost:4567/inicio")
                    .failure("http://localhost:4567/inicio")
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .build();

            PreferenceClient client = new PreferenceClient();
            try {
                Preference preference = client.create(preferenceRequest);

                if (preference != null && preference.getId() != null) {
                    System.out.println(preference);
                    System.out.println(preference.getId());
                    return preference.getId();
                } else {
                    // Manejar el caso en el que la creación de preferencia no fue exitosa.
                    return "Error al crear la preferencia";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error error al llamar a la api";
            }
        } catch (Exception e) {
            // Manejar excepciones, por ejemplo, loguear el error.
            e.printStackTrace();
            return "Error interno al procesar la solicitud";
        }
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

        if (id_pedido < 0) {
            System.out.println("fuera del horario de atencion");
            model.put("error", "no se pueden realizar pedidos fuera del horario de atencion");
        } else {
            CarritoDAO cDAO = new CarritoDAO();
            cDAO.crearCarrito(id_pedido, id_producto, cantidad); // crear carrito

            FacturaDAO fDAO = new FacturaDAO();
            fDAO.generarFactura(id_pedido, tipo_pago, tipo); // crear factura

            ProductoDAO proDAO = new ProductoDAO();
            proDAO.actualizarStock(cantidad, id_producto); // actualiza el stock
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };
}
