package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.dounn.menutendina.Adapters.MieRecensioniAdapter;
import com.example.dounn.menutendina.Database.Recensione;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class A23_MieRecensioni extends LoggedActivity {

    RecyclerView recyclerview;
    private ArrayList<Recensione> recensiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a23_layout);
        navigationView.getMenu().getItem(pref.getInt("nav_mie_recensioni", 0)).setChecked(true);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(llm);
        recensiones = new ArrayList<>();

        chiamaDatabaseMieRecensioni();
        enableOnScrollDownAction();
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        chiamaDatabaseMieRecensioni();
    }

    public void chiamaDatabaseMieRecensioni() {

        startCaricamento(100, getResources().getString(R.string.Loading_rece));
        JSONObject req = new JSONObject();
        try {
            req.put("id_utente", getUser().getId());
            req.put("path", "recensioni_utente");
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    //controllo lo stato della richiesta
                    try {
                        if(!a.getString("status").equals("ERROR")) {
                            recensiones = new ArrayList<>();
                            for(int i = 0; i < a.getJSONArray("result").length(); i++) {
                                Recensione recensione = new Recensione(a.getJSONArray("result").getJSONObject(i));
                                recensiones.add(recensione);
                            }
                            Intent intent = new Intent(ctx, A24_Recensione.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            recyclerview.setAdapter(new MieRecensioniAdapter(ctx, recensiones, intent));
                        } else {
                            errorBar(getResources().getString(R.string.errore_server), 2000);
                        }

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    stopCaricamento(200);
                }

                @Override
                public void noInternetConnection() {
                    stopCaricamento(200);
                    noInternetErrorBar();
                }
            }).execute(req);
        } catch(JSONException e) {
            stopCaricamento(200);
            e.printStackTrace();
        }
    }


}