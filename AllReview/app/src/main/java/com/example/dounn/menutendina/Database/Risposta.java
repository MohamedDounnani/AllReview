package com.example.dounn.menutendina.Database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 18/06/2017.
 */

public class Risposta extends SuperDB {

    private int id;
    private String testo;
    private int id_domanda;
    private Utente utente;

    public Risposta(JSONObject o) throws JSONException {
        super(o.getInt("data"));
        id = o.getInt("id");
        testo = o.getString("testo");
        utente = new Utente(o.getJSONObject("utente"));
        id_domanda = o.getInt("id_domanda");
    }

    public int getIdDomanda() {
        return id_domanda;
    }

    public int getId() {
        return id;
    }

    public String getTesto() {
        return testo;
    }

    public Utente getUtente() {
        return utente;
    }

}

