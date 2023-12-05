package com.mycompany.fika;

import lombok.Data;

@Data
public class Usuario {
    private Carrito carrito;

    private int id;
    private String nombre;
    private String email;
    private String pass;
    private Boolean admin;
    private String estado;
    private int telefono;
    private String direccion;
}
