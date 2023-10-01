/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

/**
 *
 * @author Sebastian
 */
public class FacturaController {
    public static void generarFactura(double monto, int tipoPago, int id_pedido, String tipo){
        FacturaDAO fDAO = new FacturaDAO();
        fDAO.generarFactura(tipoPago, id_pedido, tipo);
    }    
}