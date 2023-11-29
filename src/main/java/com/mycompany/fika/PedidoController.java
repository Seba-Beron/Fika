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
import com.mercadopago.client.preference.PreferencePaymentMethodRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferencePaymentTypeRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.preference.Preference;
// import com.mercadopago.resources.preference.PreferencePaymentMethod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
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

        PedidoDAO pDAO = new PedidoDAO();
        int id_pedido = pDAO.comprarCarrito(id_usuario, direccion, tipo);

        HashMap model = new HashMap();

        if (id_pedido < 0) {
            System.out.println("fuera del horario de atencion");
            model.put("error", "no se pueden realizar pedidos fuera del horario de atencion");
        } else {
            FacturaDAO fDAO = new FacturaDAO();
            fDAO.generarFactura(tipo_pago, id_pedido, tipo);

            ProductoDAO proDAO = new ProductoDAO();
            proDAO.actualizarStock(id_pedido);

            res.redirect("/inicio");
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route aceptarPago = (Request req, Response res) -> {

        //String Token = "TEST-4546216443926115-110409-61a3ac5fe6da930fa33b3357cd5b6a76-216697042";
        //MercadoPagoConfig.setAccessToken(Token);

        String body = req.body();
        System.out.println(body);

        String topic = "";
        topic = (req.queryParams("topic") != null) ? req.queryParams("topic") : topic;
        topic = (req.queryParams("type") != null) ? req.queryParams("type") : topic;

        System.out.println(topic);

        switch (topic) {
            case "payment":
                System.out.println("xd");
            case "merchant_order":
                System.out.println(req.queryParams("id"));
        }
        /*
         * System.out.println("entro: ");
         * //int id_pedido = Integer.parseInt(req.queryParams("external_reference"));
         * //System.out.println(id_pedido);
         *
         * for (String param : req.queryParams()) {
         * String value = req.queryParams(param);
         * System.out.println(param + ": " + value);
         * }
         *
         * System.out.println(req.queryParams("merchant_order"));
         * //ProductoDAO proDAO = new ProductoDAO();
         * //proDAO.actualizarStock(id_pedido);
         * return true;
         */
        return false;
    };

    /*
     * ?collection_id=1319746203
     * collection_status=approved
     * payment_id=1319746203
     * status=approved
     * external_reference=6
     * payment_type=credit_card
     * merchant_order_id=13623875614
     * preference_id=216697042-4575f116-f93c-418c-83f0-b9d50c8a0420
     * site_id=MLA
     * processing_mode=aggregator
     * merchant_account_id=null
     */

    public static Route crearPreferencia = (Request req, Response res) -> {
        // vendedor de prueba
        // String Token = "APP_USR-8684122316708044-111915-9b580f8f53b2b76440eb7868c77db01b-1556192526";
        // String Token = "TEST-8684122316708044-111915-aaa5dbac6ba6efce41b679534618bdd6-1556192526";

        // real
        String Token = "TEST-4546216443926115-110409-61a3ac5fe6da930fa33b3357cd5b6a76-216697042";
        // String Token = "APP_USR-8684122316708044-111915-9b580f8f53b2b76440eb7868c77db01b-1556192526";
        MercadoPagoConfig.setAccessToken(Token);
        String url_segura = "https://72ea-168-90-72-71.ngrok.io/notificacion";
        int id_usuario = req.session().attribute("id");

        CarritoDAO cDAO = new CarritoDAO();
        HashMap<Producto, Integer> carrito = cDAO.verCarrito(id_usuario);
        List<PreferenceItemRequest> items = new ArrayList<>();

        PedidoDAO pDao = new PedidoDAO();
        String id_pedido = String.valueOf(pDao.obtenerPedidoActual(id_usuario));

        try {
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

            List<PreferencePaymentMethodRequest> excludedPaymentMethods = new ArrayList<>();
            // excludedPaymentMethods.add(PreferencePaymentMethodRequest.builder().id("master").build());
            // excludedPaymentMethods.add(PreferencePaymentMethodRequest.builder().id("amex").build());

            List<PreferencePaymentTypeRequest> excludedPaymentTypes = new ArrayList<>();
            excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("credit_card").build());
            excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("debit_card").build());
            excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("cash").build());
            // excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("rapipago").build());

            PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                    .excludedPaymentMethods(excludedPaymentMethods)
                    .excludedPaymentTypes(excludedPaymentTypes)
                    .installments(12)
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    // .paymentMethods(paymentMethods)
                    .notificationUrl(url_segura)
                    .externalReference(id_pedido)
                    .build();
            try {
                PreferenceClient client = new PreferenceClient();
                Preference preference = client.create(preferenceRequest);
                return preference.getId();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error al enviar las preferencias";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al crear las preferencias";
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
