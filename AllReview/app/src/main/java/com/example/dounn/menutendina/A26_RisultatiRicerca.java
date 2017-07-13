package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.dounn.menutendina.Adapters.ElementoAdapter;
import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class A26_RisultatiRicerca extends SuperActivity {

    RecyclerView recyclerView;
    private ArrayList<Elemento> elementos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a26_layout);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String tmp = extras.getString("query");

        elementos = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.rv_ris_ricerca);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setVisibility(View.VISIBLE);

        chiamaDatabaseRicercaNomeElemento(tmp);
    }

    public void chiamaDatabaseRicercaNomeElemento(String query) {
        startCaricamento(0, getResources().getString(R.string.Load_search));
        JSONObject req = new JSONObject();
        try {
            req.put("token", getToken());
            req.put("nome", query);
            req.put("path", "search_elemento");
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    //controllo lo stato della richiesta
                    try {
                        if(!a.getString("status").equals("ERROR")) {

                            int lenght = a.getJSONArray("result").length();

                            for(int i = 0; i < lenght; i++) {
                                //NUMERO ELEMENTI ANALIZZATO
                                Elemento elemento = new Elemento(a.getJSONArray("result").getJSONObject(i));
                                elementos.add(elemento);
                            }

                            Intent i = new Intent(ctx, A27_PaginaElemento.class);
                            ElementoAdapter adapter = new ElementoAdapter(ctx, elementos, i);
                            recyclerView.setAdapter(adapter);

                            if (elementos.size() == 0){
                                Button crea_elemento = (Button) findViewById(R.id.elemento_non_trovato_a26);

                                setGone(recyclerView);
                                setVisible(crea_elemento);

                                crea_elemento.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ctx, A21_InserisciElemento.class);
                                        startActivity(intent);
                                    }
                                });
                            }

                            stopCaricamento(200);
                        } else {
                            errorBar(getResources().getString(R.string.errore_server),2000);
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void noInternetConnection() {
                    noInternetErrorBar();
                }
            }).execute(req);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}