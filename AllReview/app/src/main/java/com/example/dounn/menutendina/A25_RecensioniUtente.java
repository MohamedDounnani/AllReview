package com.example.dounn.menutendina;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.dounn.menutendina.Adapters.RecensioniAdapter;
import com.example.dounn.menutendina.Database.Recensione;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class A25_RecensioniUtente extends SuperActivity {

    private RecyclerView recyclerView;
    private RecensioniAdapter adapter;
    private ArrayList<Recensione> recensiones;
    LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    private int id_utente;
    private int focusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a25_layout);

        if(!getIntent().hasExtra("id_utente")) finish();
        else id_utente = getIntent().getIntExtra("id_utente", 1);

        focusId = getIntent().getIntExtra("focus", -1);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recensiones = new ArrayList<>();

        recyclerView.setLayoutManager(llm);

        enableOnScrollDownAction();
        onScrollDownAction();
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        JSONObject req = new JSONObject();
        try {
            req.put("path", "recensioni_utente");
            req.put("id_utente", id_utente);
            startCaricamento(0, getResources().getString(R.string.Loading));
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(a.getString("status").equals("OK")) {
                            recensiones = new ArrayList<>();
                            JSONArray result = a.getJSONArray("result");
                            for(int i = 0; i < result.length(); i++)
                                recensiones.add(new Recensione(result.getJSONObject(i)));

                            adapter = new RecensioniAdapter(ctx, recensiones, focusId);
                            recyclerView.setAdapter(adapter);
                            if(focusId != -1) {
                                recyclerView.scrollToPosition(focusId);
                                focusId = -1;
                            }
                        } else throw new JSONException("male");
                    } catch(JSONException e) {
                        errorBar(getResources().getString(R.string.Error), 2000);
                    } finally {
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