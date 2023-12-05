package com.mycompany.fika;

import lombok.Data;

@Data
public class Carrito {
    private Producto producto;
    private Integer cantidad;
    private int producto_id;
    private int Pedido_id;

}
