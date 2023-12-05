package com.mycompany.fika;

final public class FactoryDAO {

    CarritoDAO getCarritoDAO(){
        return new CarritoDAO();
    }

    FacturaDAO getFacturaDAO(){
        return new FacturaDAO();
    }

    PedidoDAO getPedidoDAO(){
        return new PedidoDAO();
    }

    ProductoDAO getProductoDAO(){
        return new ProductoDAO();
    }

    UsuarioDAO getUsuarioDAO(){
        return new UsuarioDAO();
    }
}
