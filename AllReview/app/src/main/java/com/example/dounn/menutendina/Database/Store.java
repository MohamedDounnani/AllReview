package com.example.dounn.menutendina.Database;


import java.util.HashMap;

/**
 * Created by lucadiliello on 16/06/2017.
 */

public class Store {

    static HashMap<String, Object> store = new HashMap<>();

    public static synchronized void add(String i, Object o) {
        store.put(i, o);
    }

    public static synchronized Object get(String i) {
        return store.get(i);
    }
}
