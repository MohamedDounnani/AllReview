package com.example.dounn.menutendina.Database;

import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 12/06/2017.
 */

public class UtenteLogged extends Utente {

    private Impostazioni impostazioni;
    private boolean attivato = false;
    private String email;
    private String token;

    private int domande_counter;
    private int recensioni_counter;
    private int voti_counter;

    public UtenteLogged(JSONObject utente) throws JSONException {
        super(utente);
        update(utente);
    }

    public String getEmail() {
        return email;
    }

    public Impostazioni getImpostazioni() {
        return impostazioni;
    }

    public boolean isAttivato() {
        return attivato;
    }

    public void setAttivato(boolean attivato) {
        this.attivato = attivato;
    }

    public String getToken() {
        return token;
    }

    public void update(JSONObject utente) throws JSONException {
        super.update(utente);
        attivato = utente.getInt("attivato") > 0;
        email = utente.getString("email");
        impostazioni = new Impostazioni(utente.getJSONObject("impostazioni"));
        token = utente.getString("token");
        domande_counter = utente.getInt("domande_counter");
        recensioni_counter = utente.getInt("recensioni_counter");
        voti_counter = utente.getInt("voti_counter");
    }

    private void launchUpdate() {
        JSONObject req = new JSONObject();
        try {
            req.put("path", "update");
            req.put("token", token);
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        update(a);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void noInternetConnection() {
                }
            }).execute(req);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public int getDomandeCounter() {
        return domande_counter;
    }

    public int getRecensioniCounter() {
        return recensioni_counter;
    }

    public int getVotiCounter() {
        return voti_counter;
    }

    public boolean canDomanda() {
        return domande_counter > 0;
    }

    public boolean canVoto() {
        return voti_counter > 0;
    }

    public boolean canRecensione() {
        return recensioni_counter > 0;
    }

    public void fattaDomanda() {
        domande_counter--;
        launchUpdate();
    }

    public void fattaRecensione() {
        recensioni_counter--;
        launchUpdate();
    }

    public void fattoVoto() {
        voti_counter--;
        launchUpdate();
    }
}
