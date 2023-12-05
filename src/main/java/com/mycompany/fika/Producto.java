package com.mycompany.fika;

import lombok.Data;

@Data
public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private int stock;
    private String tipo;
    private String imagen;
}
