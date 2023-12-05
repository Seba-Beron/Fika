package com.mycompany.fika;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menu {
    List<HashMap> menu = new ArrayList();  // porque no usa un solo map ?
    public void init() {
            HashMap item = new HashMap();
            item.put("label", "Home");
            item.put("url", "index");
            menu.add(item);
            item = new HashMap();
            item.put("label", "Login");
            item.put("url", "login");
            menu.add(item);
    }
    public void initLog() {
            HashMap item = new HashMap();
            item.put("label", "Home");
            item.put("url", "index");
            menu.add(item);
            item = new HashMap();
            item.put("label", "Logout");
            item.put("url", "logout");
            menu.add(item);
    }

    public List<HashMap> getMenu() {
        init();
        return menu;
    }
    public List<HashMap> getMenuLog() {
        initLog();
        return menu;
    }
}
