/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import lombok.Data;

/**
 *
 * @author Sebastian
 */

@Data
public class Carrito {         
    private Producto producto;
    private Integer cantidad;   
    private int producto_id;
    private int Pedido_id;
    
}
