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
public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private int stock;    
    private String tipo;
    private String imagen;
}
