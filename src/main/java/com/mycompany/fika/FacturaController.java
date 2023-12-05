package com.mycompany.fika;

public class FacturaController {
    public static void generarFactura(double monto, int tipoPago, int id_pedido, String tipo){
        FacturaDAO fDAO = new FacturaDAO();
        fDAO.generarFactura(tipoPago, id_pedido, tipo);
    }
}
