package com.example.dounn.menutendina.Database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 16/06/2017.
 */

public class Foto extends SuperDB {
    private int id;
    private String path;
    private int id_utente;
    private int id_elemento;

    public Foto(JSONObject foto) throws JSONException {
        super(foto.getInt("data"));
        id = foto.getInt("id");
        id_utente = foto.getInt("id_utente");
        path = foto.getString("path");
        id_elemento = foto.getInt("id_elemento");
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public int getIdUtente() {
        return id_utente;
    }

    public int getIdElemento() {
        return id_elemento;
    }
}
