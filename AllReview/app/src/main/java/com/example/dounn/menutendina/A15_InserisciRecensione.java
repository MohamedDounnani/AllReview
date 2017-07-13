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


public class A15_InserisciRecensione extends SuperActivity {

    private ArrayList<Elemento> elementi;
    private RecyclerView recyclerView;
    private Button creaElementoNonTrovato;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.a15_layout);
        navigationView.getMenu().getItem(pref.getInt("nav_inserisci_recensione", 0)).setChecked(true);

        /*
        SE PROVIENE DALLA RICERCA FATTA SU INSERISCI RICERCA ALLORA PRENDERE LA QUERY DI RICERCA
        ALTRIMENTI SI COMPORTA COME UNA NORMALE ACTIVITY
         */
        creaElementoNonTrovato = (Button) findViewById(R.id.crea_elemento_non_trovato);
        elementi = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.rv_ris_ricerca);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        //DICO CHE LA RICERCA E' FATTA SU INSERISCI RECENSIONE
        search.setIsRecensione(true);

        creaElementoNonTrovato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, A21_InserisciElemento.class));
            }
        });
        Intent i = getIntent();
        if(i.hasExtra("query")) {

            Bundle extras = i.getExtras();
            String tmp = extras.getString("query");
            chiamaDatabaseRicercaNomeElemento(tmp);
        }
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
                            for(int i = 0; i < a.getJSONArray("result").length(); i++) {
                                //NUMERO ELEMENTI ANALIZZATO

                                Elemento elemento = new Elemento(a.getJSONArray("result").getJSONObject(i));
                                elementi.add(elemento);
                                stopCaricamento(200);
                            }
                            Intent i = new Intent(ctx, A16_CreaRecensione.class);
                            recyclerView.setAdapter(new ElementoAdapter(ctx, elementi, i));
                        } else {
                            errorBar(getResources().getString(R.string.errore_server), 2000);
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