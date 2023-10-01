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