package com.mycompany.fika;

import java.util.Date;
import lombok.Data;

@Data
public class Factura {
    private double monto;
    private int TipoPago_codigo;
    private int Pedido_id;
    private Date demora;
}
