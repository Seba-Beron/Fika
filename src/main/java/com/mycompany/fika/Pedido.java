package com.mycompany.fika;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Pedido {

    private ArrayList<Carrito> carritos;

    private int id;
    private int Estado_codigo;
    Date fecha;
    private int Usuario_id;
    private String direccion;
    private String tipo;

    public void setCarritos(List<Carrito> cs){
        carritos = new ArrayList<>();
        carritos.addAll(cs);
    }
}
