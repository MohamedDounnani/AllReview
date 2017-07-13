package com.example.dounn.menutendina.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lucadiliello on 18/06/2017.
 */

public class Domanda extends SuperDB{
    private ArrayList<Risposta> risposte = new ArrayList<>();
    private Risposta rispostaTop;
    private Utente utente;

    private int id;
    private String testo;

    public Domanda(JSONObject o) throws JSONException {
        super(o.getInt("data"));
        JSONArray risp = o.getJSONArray("risposte");
        for(int i = 0; i < risp.length(); i++) {
            risposte.add(new Risposta(risp.getJSONObject(i)));
        }
        try {
            rispostaTop = new Risposta(o.getJSONObject("risposta_top"));
        } catch(JSONException e) {
            rispostaTop = null;
        }

        utente = new Utente(o.getJSONObject("utente"));
        id = o.getInt("id");
        testo = o.getString("testo");
    }

    public String getTesto() {
        return testo;
    }

    public ArrayList<Risposta> getRisposte() {
        return risposte;
    }

    public Risposta getRispostaTop() {
        return rispostaTop;
    }

    public Utente getUtente() {
        return utente;
    }

    public int getId() {
        return id;
    }

}
