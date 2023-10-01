/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fika;

import java.util.HashMap;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

/**
 *
 * @author Sebastian
 */
public class IndexController {    
    public static Route getIndex = (Request req, Response res) -> {
    
            HashMap model = new HashMap();
            String email = req.session().attribute("email");
            System.out.println(email);
            
            if(email == null){
                Menu menu = new Menu();
                model.put("menu", menu.getMenu());  
                model.put("menuActivo", "index");   
                model.put("template", "templates/index.vsl");
            }else{
                Menu menu = new Menu();
                model.put("menu", menu.getMenuLog()); 
                model.put("email-usuario", email);   
                model.put("menuActivo", "index");     
                model.put("template", "templates/main.vsl");       // revisar
            }
            
            return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/layout.vsl")); 
        };
}
