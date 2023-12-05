package com.mycompany.fika;

import java.util.ArrayList;
import java.util.HashMap;
import org.sql2o.Connection;

public class FacturaDAO {

    private int monto;

    public void generarFactura(int tipoPago, int id_pedido, String tipo){
        try (Connection con = Sql2oDAO.getSql2o().open()) {

            String sql_carrito = "SELECT *  FROM Carrito WHERE Pedido_id = :id_pedido";
            String sql_producto = "SELECT precio  FROM Producto WHERE id = :id_producto";

            monto = 0;
            ArrayList<Carrito> carritos = new ArrayList<>();
            HashMap<Integer, Integer> precio_cant = new HashMap<>();

            carritos.addAll(con.createQuery(sql_carrito)
                .addParameter("id_pedido", id_pedido)
                .executeAndFetch(Carrito.class));

            carritos.forEach((c) -> {
                precio_cant.put(con.createQuery(sql_producto)
                .addParameter("id_producto", c.getProducto_id())
                .executeScalar(Integer.class), c.getCantidad());
            });

            precio_cant.forEach((p,c)->{monto += p*c;});

            if(tipo.equals("Delivery")){ monto += 500; }

            System.out.println("monto: " + monto);

            String insertar_factura = "INSERT INTO factura (monto, TipoPago_codigo, Pedido_id, demora) VALUES (:monto, :tipoPago, :id_pedido, 50)";

            con.createQuery(insertar_factura)
                .addParameter("monto", monto)
                .addParameter("tipoPago", tipoPago)
                .addParameter("id_pedido", id_pedido)
                .executeUpdate();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
}
