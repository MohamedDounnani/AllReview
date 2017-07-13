package com.example.dounn.menutendina.Database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 18/06/2017.
 */

public class Voto extends SuperDB {
    private int id;
    private Utente utente;
    private Recensione recensione;
    private int voto;

    public Voto(JSONObject o) throws JSONException {
        super(o.getInt("data"));
        id = o.getInt("id");
        utente = new Utente(o.getJSONObject("utente"));
        recensione = new Recensione(o.getJSONObject("recensione"));
        voto = o.getInt("voto");
    }

    public int getId() {
        return id;
    }

    public Utente getUtente() {
        return utente;
    }

    public Recensione getRecensione() {
        return recensione;
    }

    public int getVoto() {
        return voto;
    }
}
