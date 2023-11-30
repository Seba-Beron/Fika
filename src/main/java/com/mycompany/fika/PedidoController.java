/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferencePaymentTypeRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
// import com.mercadopago.resources.preference.PreferencePaymentMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;
import org.slf4j.Logger;

/**
 *
 * @author Sebastian
 */
public class PedidoController {

    final static Logger logger = LoggerFactory.getLogger(PedidoController.class);

    public static Route verPedidos = (Request req, Response res) -> {

        HashMap model = new HashMap();

        try {
            ArrayList<Pedido> pedidos = FactoryDAO.getPedidoDAO().buscarPedidos();
            List<Usuario> usuarios = FactoryDAO.getUsuarioDAO().buscarUsuarios();

            model.put("pedidos", pedidos);
            model.put("usuarios", usuarios);
            model.put("template", "templates/pedidos.vsl");
        } catch (Exception e) {
            logger.error("Error al mostrar pedidos: " + e.getMessage());
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route verPedido = (Request req, Response res) -> {
        HashMap model = new HashMap();

        try {
            int id = Integer.parseInt(req.queryParams("id")); // id del pedido

            Pedido pedido = FactoryDAO.getPedidoDAO().buscarPedido(id);
            Usuario usuario = FactoryDAO.getUsuarioDAO().buscarUsuario(pedido.getUsuario_id());

            model.put("pedido", pedido);
            model.put("usuario", usuario);
            model.put("template", "templates/pedido.vsl");
        } catch (Exception e) {
            logger.error("Error al mostrar pedido: " + e.getMessage());
        }
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));

    };

    public static Route verHistorial = (Request req, Response res) -> {

        int id_usuario = req.session().attribute("id");
        ArrayList<Pedido> pedidos = FactoryDAO.getPedidoDAO().buscarHistorial(id_usuario);

        HashMap model = new HashMap();
        model.put("pedidos", pedidos);
        model.put("template", "templates/historial.vsl");
        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route comprarCarrito = (Request req, Response res) -> {

        int id_usuario = req.session().attribute("id");
        String direccion = req.queryParams("direccion");
        String tipo = req.queryParams("tipo");
        int tipo_pago = Integer.parseInt(req.queryParams("tipo-pago"));

        int id_pedido = FactoryDAO.getPedidoDAO().comprarCarrito(id_usuario, direccion, tipo);

        HashMap model = new HashMap();

        if (id_pedido < 0) {
            System.out.println("fuera del horario de atencion");
            model.put("error", "no se pueden realizar pedidos fuera del horario de atencion");
        } else {
            FactoryDAO.getFacturaDAO().generarFactura(tipo_pago, id_pedido, tipo);

            FactoryDAO.getProductoDAO().actualizarStock(id_pedido);

            res.redirect("/inicio");
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route aceptarPago = (Request req, Response res) -> {

        // String Token =
        // "TEST-4546216443926115-110409-61a3ac5fe6da930fa33b3357cd5b6a76-216697042";
        // MercadoPagoConfig.setAccessToken(Token);

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
        // String Token =
        // "APP_USR-8684122316708044-111915-9b580f8f53b2b76440eb7868c77db01b-1556192526";
        // String Token =
        // "TEST-8684122316708044-111915-aaa5dbac6ba6efce41b679534618bdd6-1556192526";

        // real
        String Token = "TEST-4546216443926115-110409-61a3ac5fe6da930fa33b3357cd5b6a76-216697042";
        // String Token =
        // "APP_USR-8684122316708044-111915-9b580f8f53b2b76440eb7868c77db01b-1556192526";
        MercadoPagoConfig.setAccessToken(Token);
        String url_segura = "https://72ea-168-90-72-71.ngrok.io/notificacion";
        int id_usuario = req.session().attribute("id");

        HashMap<Producto, Integer> carrito = FactoryDAO.getCarritoDAO().verCarrito(id_usuario);


        String id_pedido = String.valueOf(FactoryDAO.getPedidoDAO().obtenerPedidoActual(id_usuario));

        List<PreferenceItemRequest> items = new ArrayList<>();

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

        int id_pedido = FactoryDAO.getPedidoDAO().crearPedido(id_usuario, 1, direccion, tipo);

        HashMap model = new HashMap();

        if (id_pedido < 0) {
            System.out.println("fuera del horario de atencion");
            model.put("error", "no se pueden realizar pedidos fuera del horario de atencion");
        } else {
            FactoryDAO.getCarritoDAO().crearCarrito(id_pedido, id_producto, cantidad); // crear carrito

            FactoryDAO.getFacturaDAO().generarFactura(id_pedido, tipo_pago, tipo); // crear factura

            FactoryDAO.getProductoDAO().actualizarStock(cantidad, id_producto); // actualiza el stock
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };
}
