/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Sebastian
 */

@Data
public class Factura {
    private double monto;
    private int TipoPago_codigo;
    private int Pedido_id;
    private Date demora;
}
