package com.mycompany.fika;

final public class FactoryDAO {

    static CarritoDAO getCarritoDAO(){
        return new CarritoDAO();
    }

    static FacturaDAO getFacturaDAO(){
        return new FacturaDAO();
    }

    static PedidoDAO getPedidoDAO(){
        return new PedidoDAO();
    }

    static ProductoDAO getProductoDAO(){
        return new ProductoDAO();
    }

    static UsuarioDAO getUsuarioDAO(){
        return new UsuarioDAO();
    }
}
