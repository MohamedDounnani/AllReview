package com.example.dounn.menutendina;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.dounn.menutendina.Adapters.UtenteAdapter;
import com.example.dounn.menutendina.Database.Utente;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class A12_Classifica extends SuperActivity {

    private RecyclerView recyclerView;
    ArrayList<Utente> utenti;
    private UtenteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a12_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        utenti = new ArrayList<>();
        adapter = new UtenteAdapter(ctx, utenti);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        enableOnScrollDownAction();
        onScrollDownAction();
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        JSONObject req = new JSONObject();
        try {
            req.put("path", "classifica");
            startCaricamento(100, getResources().getString(R.string.Loading));
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(a.getString("status").equals("OK")) {

                            JSONArray result = a.getJSONArray("result");
                            utenti = new ArrayList<>();
                            for(int i = 0; i < result.length(); i++) {
                                utenti.add(new Utente(result.getJSONObject(i)));
                            }
                            adapter.update(utenti);
                            stopCaricamento(100);
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                        stopCaricamento(100);
                    }
                }

                @Override
                public void noInternetConnection() {
                    noInternetErrorBar();
                    stopCaricamento(100);
                }
            }).execute(req);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}