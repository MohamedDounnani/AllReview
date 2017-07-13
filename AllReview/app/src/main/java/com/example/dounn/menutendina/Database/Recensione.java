package com.example.dounn.menutendina.Database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 16/06/2017.
 */

public class Recensione extends SuperDB{

    private int id;

    private String fotopath;
    private String descr;
    private int voti_positivi;
    private int voti_negativi;

    private int id_elemento;
    private double voto;

    private Utente utente;
    private String titolo;
    private Elemento elemento;

    public Recensione(JSONObject o) throws JSONException {
        super(o.getInt("data"));
        id = o.getInt("id");
        fotopath = o.getString("fotopath").equals("null") ? null : o.getString("fotopath");
        descr = o.getString("descr");
        voti_positivi = o.getInt("voti_positivi");
        voti_negativi = o.getInt("voti_negativi");
        id_elemento = o.getInt("id_elemento");
        voto = o.getDouble("voto");
        utente = new Utente(o.getJSONObject("utente"));
        titolo = o.getString("titolo");
        elemento = new Elemento(o.getJSONObject("elemento"));
    }


    public int getId() {
        return id;
    }

    public String getFotopath() {
        return fotopath;
    }

    public String getDescr() {
        return descr;
    }

    public int getVotiPositivi() {
        return voti_positivi;
    }

    public int getVotiNegativi() {
        return voti_negativi;
    }

    public void setVoti(int voti_positivi , int voti_negativi){
        this.voti_negativi = voti_negativi;
        this.voti_positivi = voti_positivi;
    }

    public int getIdElemento() {
        return id_elemento;
    }

    public double getVoto() {
        return voto;
    }

    public Utente getUtente() {
        return utente;
    }

    public String getTitolo() {
        return titolo;
    }

    public Elemento getElemento() {
        return elemento;
    }
}