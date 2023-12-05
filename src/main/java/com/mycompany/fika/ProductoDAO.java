package com.mycompany.fika;

import java.util.ArrayList;
import java.util.List;
import org.sql2o.Connection;

public class ProductoDAO {

    private List<Producto> productos;

    public List<Producto> buscarProductos() {   // busco todos los productos de la bd

        try (Connection con = Sql2oDAO.getSql2o().open()) {

            String sql = "SELECT * FROM producto";

            productos = con
                .createQuery(sql)
                .executeAndFetch(Producto.class);
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return productos;
    }

    public boolean actualizarStock(int cantidad, int id_producto){  // esto solamente para cuando se compra
        try (Connection con = Sql2oDAO.getSql2o().open()) {

            String sqlSelect = "SELECT stock FROM producto WHERE id = :id_producto";

            int stock_anterior = con.createQuery(sqlSelect)
                                    .addParameter("id_producto",id_producto)
                                    .executeScalar(Integer.class);

            int stock_nuevo = stock_anterior - cantidad;

            if(stock_nuevo > 0){
                String sqlUpdate = "UPDATE producto SET stock = :stock_nuevo WHERE id = :id_producto";

                con.createQuery(sqlUpdate)
                .addParameter("stock_nuevo", stock_nuevo)
                .addParameter("id_producto", id_producto)
                .executeUpdate();
            }

            return (stock_nuevo > 0);
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return false;
    }

    public void actualizarStock(int id_pedido){
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            String consulta_carritos = "SELECT * FROM Carrito WHERE Pedido_id = :id_pedido";

            ArrayList<Carrito> carritos = new ArrayList<>();

            carritos.addAll(con.createQuery(consulta_carritos)
                .addParameter("id_pedido", id_pedido)
                .executeAndFetch(Carrito.class));

            carritos.forEach((c) ->
                this.actualizarStock(c.getCantidad(),c.getProducto_id())
            );
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
}
