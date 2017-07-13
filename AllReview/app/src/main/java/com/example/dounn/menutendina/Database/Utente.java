package com.example.dounn.menutendina.Database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 13/06/2017.
 */

public class Utente extends SuperDB {

    private String username;
    private String fotopath;
    private Level level;
    private int id;
    private Grafico grafico;

    public Utente(JSONObject utente) throws JSONException {
        super(utente.getInt("data_reg"));
        update(utente);
    }

    @Override
    public String toString() {
        return username + id + fotopath;
    }

    public String getUsername() {
        return username;
    }

    public Grafico getGrafico() {
        return grafico;
    }

    public String getFotopath() {
        return fotopath;
    }

    public Level getLevel() {
        return level;
    }

    public int getId() {
        return id;
    }

    void update(JSONObject utente) throws JSONException {
        username = utente.getString("nome");
        fotopath = !utente.getString("fotopath").equals("null") ? utente.getString("fotopath") : null;
        id = utente.getInt("id");
        level = new Level(utente.getJSONObject("level"));
        grafico = new Grafico(utente.getJSONArray("grafico"));
    }
}
