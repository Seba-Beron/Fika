/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import java.time.LocalDateTime;
import java.util.ArrayList;
import org.sql2o.Connection;

/**
 *
 * @author Sebastian
 */
public class PedidoDAO {

    public int obtenerPedidoActual(int id_usuario){
        try (Connection con = Sql2oDAO.getSql2o().open()) {    

            String sql_pedido = "SELECT id FROM Pedido WHERE id = :id AND Estado_codigo = 0"; 
            
            return con.createQuery(sql_pedido)
                    .addParameter("id", id_usuario)
                    .executeAndFetchFirst(Integer.class);
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return 0; // error
    }

    public Pedido buscarPedido(int id) {
        
        Pedido pedido = new Pedido();
        
        try (Connection con = Sql2oDAO.getSql2o().open()) {    

            String sql_pedido = "SELECT * FROM Pedido WHERE id = :id"; 
            
            pedido = con.createQuery(sql_pedido)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Pedido.class);

            if (pedido != null) {
                CarritoDAO cDAO = new CarritoDAO();
                ArrayList<Carrito> carritos = cDAO.buscarCarritos(id);
                pedido.setCarritos(carritos);
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return pedido;
    }

    public ArrayList<Pedido> buscarPedidos(){
        
        ArrayList<Pedido> pedidos = new ArrayList<>();
        
        try (Connection con = Sql2oDAO.getSql2o().open()) {    
            

            String sql_pedido = "SELECT * FROM Pedido"; 
            
            pedidos.addAll(con.createQuery(sql_pedido)
                .executeAndFetch(Pedido.class));
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return pedidos;
    }

    public int comprarCarrito(int id_usuario, String direccion, String tipo){
        try (Connection con = Sql2oDAO.getSql2o().open()) {    
            
            String consulta_id_pedido = "SELECT id FROM pedido WHERE Usuario_id = :id_usuario AND Estado_codigo = 0"; // estado actual
            String update_pedido = "UPDATE pedido SET Estado_codigo = :codigo_Estado, fecha = :fecha, direccion = :direccion, tipo = :tipo WHERE id = :id_pedido";

            int id_pedido = con                                 
                .createQuery(consulta_id_pedido)
                .addParameter("id_usuario", id_usuario)
                .executeScalar(Integer.class);
            
            LocalDateTime locaDate = LocalDateTime.now();

            if(locaDate.getHour() < 0 ||  locaDate.getHour() > 24){
            }
            if(locaDate.getHour() < 8 ||  locaDate.getHour() > 20){

                return -1;  // fuera del horario de atencion
            }
            
            con.createQuery(update_pedido)  // el valor 1 no se carga !!??
                .addParameter("codigo_Estado", 1)   // estado comprado / en preparacion
                .addParameter("fecha", locaDate)    // fecha de cuando se realizo la compra
                .addParameter("direccion", direccion)
                .addParameter("tipo", tipo)
                .addParameter("id_pedido", id_pedido)
                .executeUpdate();
            
            this.crearPedido(id_usuario, 0, null, null);
            return id_pedido;   // retorno el id del pedido el cual cambio de estado para hacer la factura
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    public int crearPedido(int id_usuario, int id_estado, String direccion, String tipo){
        int id_pedido = 0;
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            
            String insert_pedido = "INSERT INTO pedido(id, fecha, Estado_codigo, Usuario_id, direccion, tipo) VALUES (:id, :fecha, :id_estado, :usuario_id, :direccion, :tipo)";
            
            
            String consulta_id_mayor = "SELECT MAX(id) FROM pedido";
            id_pedido = con                                 
                .createQuery(consulta_id_mayor)
                .executeScalar(Integer.class) + 1; 
            
            LocalDateTime locaDate = LocalDateTime.now();
            
            if(locaDate.getHour() < 8 ||  locaDate.getHour() > 20){
                return -1;  // fuera del horario de atencion
            }
            
            con.createQuery(insert_pedido)
                .addParameter("id", id_pedido) 
                .addParameter("fecha", locaDate)
                .addParameter("id_estado", id_estado)
                .addParameter("usuario_id", id_usuario)
                .addParameter("direccion", direccion)
                .addParameter("tipo", tipo)
                .executeUpdate();
        }
        catch(Exception e) {
            System.out.println(e);
        } 
        return id_pedido;
    }
    
    public ArrayList<Pedido> buscarHistorial(int id_usuario){
        
        ArrayList<Pedido> pedidos = new ArrayList<>();
        
        try (Connection con = Sql2oDAO.getSql2o().open()) {    
            
            // podemos usar un as para renombrar Estado_codigo
            String sql_pedido = "SELECT * FROM Pedido WHERE Usuario_id = :id_usuario"; 
            String sql_carrito = "SELECT *  FROM Carrito WHERE Pedido_id = :id_pedido";
            String sql_producto = "SELECT *  FROM Producto WHERE id = :id_producto";
            
            pedidos.addAll(con.createQuery(sql_pedido)
                .addParameter("id_usuario", id_usuario)
                .executeAndFetch(Pedido.class));
            
            
            
            pedidos.forEach((pedido)-> {
                pedido.setCarritos( con.createQuery(sql_carrito)
                    .addParameter("id_pedido", pedido.getId())
                    .executeAndFetch(Carrito.class));
                
                pedido.getCarritos().forEach((c) -> {
                    c.setProducto(con.createQuery(sql_producto)
                        .addParameter("id_producto", c.getProducto_id())
                        .executeAndFetch(Producto.class).get(0));   // get(0) xd
                });                
            });
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return pedidos;
    }
}
