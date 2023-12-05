package com.mycompany.fika;

import java.util.ArrayList;
import java.util.HashMap;
import org.sql2o.Connection;

public class CarritoDAO {

    // redireccionar al vsl
    public void crearCarrito(int id_pedido, int id_producto, int cantidad){ // REVISAR EL STOCK podemos poner un limite para seleccionar la cantidad en html (velocity)
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            String insertSql = "insert into Carrito(Pedido_id, Producto_id, cantidad) values (:Pedido_id, :Producto_id, :cantidad)";

            con.createQuery(insertSql)
                .addParameter("Pedido_id", id_pedido)
                .addParameter("Producto_id", id_producto)
                .addParameter("cantidad", cantidad)
                .executeUpdate();


        }catch(Exception e) {
            System.out.println(e);
        }
    }

    public boolean actualizarCarrito(int id_usuario, int id_producto, int cantidad){
        if(cantidad > 0){
            try (Connection con = Sql2oDAO.getSql2o().open()) {

                String sql = "SELECT id FROM Pedido WHERE Usuario_id = :id_usuario and  Estado_codigo = 0";  // estado actual

                int id_pedido = con
                    .createQuery(sql)
                    .addParameter("id_usuario", id_usuario)
                    .executeScalar(Integer.class);

                String sqlConsulta = "SELECT cantidad FROM Carrito WHERE Producto_id = :id_producto AND Pedido_id = :id_pedido";

                Integer cantidad_anterior = con
                    .createQuery(sqlConsulta)
                    .addParameter("id_producto", id_producto)
                    .addParameter("id_pedido", id_pedido)
                    .executeScalar(Integer.class);

                if(cantidad_anterior == null){
                    String insertSql = "insert into Carrito(Pedido_id, Producto_id, cantidad) values (:Pedido_id, :Producto_id, :cantidad)";

                    con.createQuery(insertSql)
                        .addParameter("Pedido_id", id_pedido)
                        .addParameter("Producto_id", id_producto)
                        .addParameter("cantidad", cantidad)
                        .executeUpdate();
                }
                else{
                    String updateSql = "update Carrito set cantidad = :cantidad_nueva WHERE Producto_id = :id_producto AND Pedido_id = :id_pedido";

                    int cantidad_nueva = cantidad_anterior + cantidad;

                    con.createQuery(updateSql)
                        .addParameter("cantidad_nueva", cantidad_nueva)
                        .addParameter("id_producto", id_producto)
                        .addParameter("id_pedido", id_pedido)
                        .executeUpdate();
                }
                return true;
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }
        return false;
    }

    public HashMap<Producto, Integer> verCarrito(int id_usuario) {

        HashMap<Producto, Integer> carrito = new HashMap<>();

        try (Connection con = Sql2oDAO.getSql2o().open()) {

            String consulta_id_pedido = "SELECT id FROM pedido WHERE Usuario_id = :id_usuario AND Estado_codigo = 0"; // estado actual
            String consulta_carritos = "SELECT * FROM carrito WHERE Pedido_id = :id_pedido";
            String consulta_productos = "SELECT * FROM producto WHERE id = :id_producto";

            int id_pedido = con
                    .createQuery(consulta_id_pedido)
                    .addParameter("id_usuario", id_usuario)
                    .executeScalar(Integer.class);

            ArrayList<Carrito> carritos = new ArrayList<>();

            boolean addAll = carritos.addAll(con
                    .createQuery(consulta_carritos)
                    .addParameter("id_pedido", id_pedido)
                    .executeAndFetch(Carrito.class));

            if (addAll) {
                carritos.forEach((c) -> carrito.put(con
                        .createQuery(consulta_productos)
                        .addParameter("id_producto", c.getProducto_id())
                        .executeAndFetch(Producto.class).get(0), c.getCantidad()) // get(0) xd
                );
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return carrito;
    }

    public ArrayList<Carrito> buscarCarritos(int id_pedido) {

        ArrayList<Carrito> carritos = new ArrayList<>();

        try (Connection con = Sql2oDAO.getSql2o().open()) {

            String consulta_carritos = "SELECT * FROM carrito WHERE Pedido_id = :id_pedido";
            String consulta_productos = "SELECT * FROM producto WHERE id = :id_producto";

            boolean addAll = carritos.addAll(con
                    .createQuery(consulta_carritos)
                    .addParameter("id_pedido", id_pedido)
                    .executeAndFetch(Carrito.class));

            if(addAll){
                carritos.forEach((c)->
                    c.setProducto(con
                        .createQuery(consulta_productos)
                        .addParameter("id_producto", c.getProducto_id())
                        .executeAndFetch(Producto.class).get(0))
                );
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return carritos;
    }
}
