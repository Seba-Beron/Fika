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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;
import org.slf4j.Logger;

public class PedidoController {

    final static Logger logger = LoggerFactory.getLogger(PedidoController.class);

    public static Route verPedidos = (Request req, Response res) -> {

        HashMap model = new HashMap();
        FactoryDAO fDAO = new FactoryDAO();

        try {
            ArrayList<Pedido> pedidos = fDAO.getPedidoDAO().buscarPedidos();
            List<Usuario> usuarios = fDAO.getUsuarioDAO().buscarUsuarios();

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
        FactoryDAO fDAO = new FactoryDAO();

        try {
            int id = Integer.parseInt(req.queryParams("id")); // id del pedido

            Pedido pedido = fDAO.getPedidoDAO().buscarPedido(id);
            Usuario usuario = fDAO.getUsuarioDAO().buscarUsuario(pedido.getUsuario_id());

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
        FactoryDAO fDAO = new FactoryDAO();
        ArrayList<Pedido> pedidos = fDAO.getPedidoDAO().buscarHistorial(id_usuario);

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
        FactoryDAO fDAO = new FactoryDAO();
        int id_pedido = fDAO.getPedidoDAO().comprarCarrito(id_usuario, direccion, tipo);

        HashMap model = new HashMap();

        if (id_pedido < 0) {
            System.out.println("fuera del horario de atencion");
            model.put("error", "no se pueden realizar pedidos fuera del horario de atencion");
        } else {
            fDAO.getFacturaDAO().generarFactura(tipo_pago, id_pedido, tipo);

            fDAO.getProductoDAO().actualizarStock(id_pedido);

            res.redirect("/inicio");
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };

    public static Route aceptarPago = (Request req, Response res) -> {

        String body = req.body();

        if(body.contains("payment.created")){

            String id_usuario = req.queryParams("id");

            FactoryDAO fDAO = new FactoryDAO();

            int id_pedido = fDAO.getPedidoDAO().comprarCarrito(Integer.parseInt(id_usuario), "", "3");
            fDAO.getProductoDAO().actualizarStock(id_pedido);
            return true;
        }
        return false;
    };

    public static Route crearPreferencia = (Request req, Response res) -> {

        String Token = "TEST-4546216443926115-110409-61a3ac5fe6da930fa33b3357cd5b6a76-216697042";

        MercadoPagoConfig.setAccessToken(Token);

        FactoryDAO fDAO = new FactoryDAO();
        int id_usuario = req.session().attribute("id");
        HashMap<Producto, Integer> carrito = fDAO.getCarritoDAO().verCarrito(id_usuario);

        String url_segura = "https://ad8e-168-90-72-71.ngrok.io/notificacion?id=" + id_usuario;

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

        if(items.isEmpty()){
            return "No hay productos en el carrito";
        }

        try {
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
                    //.externalReference(id_usuario)
                    .build();
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getId();

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
        FactoryDAO fDAO = new FactoryDAO();

        int id_pedido = fDAO.getPedidoDAO().crearPedido(id_usuario, 1, direccion, tipo);

        HashMap model = new HashMap();

        if (id_pedido < 0) {
            System.out.println("fuera del horario de atencion");
            model.put("error", "no se pueden realizar pedidos fuera del horario de atencion");
        } else {
            fDAO.getCarritoDAO().crearCarrito(id_pedido, id_producto, cantidad); // crear carrito

            fDAO.getFacturaDAO().generarFactura(id_pedido, tipo_pago, tipo); // crear factura

            fDAO.getProductoDAO().actualizarStock(cantidad, id_producto); // actualiza el stock
        }

        return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl"));
    };
}
