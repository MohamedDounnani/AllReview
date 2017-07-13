package com.example.dounn.menutendina.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lucadiliello on 16/06/2017.
 */

public class Elemento {

    private String categoria;
    private float rating;
    private ArrayList<Foto> fotos = new ArrayList<>();
    private String descr;
    private String nome;
    private int id;
    private ArrayList<Domanda> domande = new ArrayList<>();

    public Elemento(JSONObject o) throws JSONException {
        categoria = o.getString("categoria");
        nome = o.getString("nome");
        rating = (float) o.getDouble("rating");
        fotos = new ArrayList<>();
        descr = o.getString("descr");
        id = o.getInt("id");
        JSONArray jsonArrayFoto = o.getJSONArray("fotos");
        for(int i = 0; i < jsonArrayFoto.length(); i++) {
            fotos.add(new Foto(jsonArrayFoto.getJSONObject(i)));
        }
        JSONArray jsonArrayDomande = o.getJSONArray("domande");
        for(int i = 0; i < jsonArrayDomande.length(); i++) {
            domande.add(new Domanda(jsonArrayDomande.getJSONObject(i)));
        }
    }


    public String getCategoria() {
        return categoria;
    }

    public float getRating() {
        return rating;
    }

    public ArrayList<Foto> getFotos() {
        return fotos;
    }

    public String getDescr() {
        return descr;
    }

    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Domanda> getDomande() {
        return domande;
    }

    public int getFotoNumber() {
        return fotos.size();
    }

    @Override
    public String toString() {
        return getNome() + "-" + getDescr() + "-" + getRating();
    }

}
