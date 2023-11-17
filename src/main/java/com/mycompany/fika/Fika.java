/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.fika;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;
import static spark.Spark.staticFiles;


/**
 *
 * @author Sebastian
 */
public class Fika {
    
    public static void main(String[] args) {
        staticFiles.location("/publicc");
        options("/*",
        (request, response) -> {

            String accessControlRequestHeaders = request
                    .headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request
                    .headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod);
            }

            return "OK";
        });
        
        //before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
        });
        
        get("/login", UsuarioController.getLogin); 
        post("/login", UsuarioController.getLogin);
        get("/logout", UsuarioController.Logout);
        
        get("/inicio", ProductoController.inicio);
        get("/product", ProductoController.product);

        get("/index", IndexController.getIndex);
        
        get("/agregarCarrito", CarritoController.agregarCarrito);
        
        get("/verHistorial", PedidoController.verHistorial);
        
        get("/verCarrito", CarritoController.verCarrito);
        
        get("/verPedidos", PedidoController.verPedidos);
        get("/verPedido", PedidoController.verPedido);
        get("/comprarCarrito", PedidoController.comprarCarrito);
        get("/comprarProducto", PedidoController.comprarProducto);
        
        get("/crearPreferencia", PedidoController.crearPreferencia);
        post("notificacion", PedidoController.aceptarPago);

        // Apache Cordova
        get("/loginCordova", UsuarioController.getLoginCordova); 
        post("/loginCordova", UsuarioController.getLoginCordova);
        get("/logoutCordova", UsuarioController.LogoutCordova); 
        
        get("/inicioCordova", ProductoController.inicioCordova);
        
        //get("/productCordova", ProductoController.productCordova);
        //get("/indexCordova", IndexController.getIndexCordova); 
        get("/agregarCarritoCordova", CarritoController.agregarCarritoCordova); 
        get("/verHistorialCordova", PedidoController.verHistorialCordova); 
        get("/verCarritoCordova", CarritoController.verCarritoCordova);
        get("/comprarCarritoCordova", PedidoController.comprarCarritoCordova);
        //get("/comprarProductoCordova", PedidoController.comprarProductoCordova);       
    }
}
