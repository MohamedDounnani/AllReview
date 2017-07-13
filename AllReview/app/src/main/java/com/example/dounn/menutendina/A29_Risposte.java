package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dounn.menutendina.Adapters.RisposteAdapter;
import com.example.dounn.menutendina.Database.Domanda;
import com.example.dounn.menutendina.Database.Risposta;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.example.dounn.menutendina.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by dounn on 17/06/2017.
 */

public class A29_Risposte extends SuperActivity {

    private Domanda domanda;

    public ImageViewLoading fotoProfilo;
    public TextView nomeProfilo;
    public TextView orario;
    public TextView domandaUtente;
    public CardView settoreMigliorRisposta;
    public ImageViewLoading fotoProfiloRisposta;
    public TextView utenteMigliorRisposta;
    public TextView migliorRisposta;
    public TextView orarioRisposta;

    private RisposteAdapter adapter;
    private RecyclerView recyclerView;

    private int focusId = -1;
    private TextView risposteUtenti;

    private Button inserisciRisposta;
    private boolean scegliTopRisposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a29_layout);

        scegliTopRisposta = false;

        //SEZIONE DOMANDA
        fotoProfilo = (ImageViewLoading) findViewById(R.id.foto_profilo);
        nomeProfilo = (TextView) findViewById(R.id.nome_profilo);
        orario = (TextView) findViewById(R.id.orario);
        domandaUtente = (TextView) findViewById(R.id.domanda_utente);
        settoreMigliorRisposta = (CardView) findViewById(R.id.settore_miglior_risposta);
        fotoProfiloRisposta = (ImageViewLoading) findViewById(R.id.foto_profilo_risposta);
        utenteMigliorRisposta = (TextView) findViewById(R.id.utente_miglior_risposta);
        migliorRisposta = (TextView) findViewById(R.id.miglior_risposta);
        risposteUtenti = (TextView) findViewById(R.id.risposte_utenti);
        orarioRisposta = (TextView) findViewById(R.id.orario_risposta);

        //Inserisci nuova risposta
        inserisciRisposta = (Button) findViewById(R.id.inserisci_risposta);

        if(isActivated()) {
            inserisciRisposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ctx, A32_InserisciRisposta.class));
                }
            });
        } else {
            inserisciRisposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorBar(getResources().getString(R.string.not_logged), 2000);
                }
            });
        }

        //Lista risposte
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        domanda = (Domanda) Store.get("domanda");

        if(domanda != null) {
            init();
            enableOnScrollDownAction();
        } else if(getIntent().hasExtra("id_domanda")) {
            int id_domanda = getIntent().getIntExtra("id_domanda", 1);
            focusId = getIntent().getIntExtra("focus", -1);

            JSONObject req = new JSONObject();
            try {
                req.put("path", "domanda");
                req.put("id_domanda", id_domanda);
                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        try {
                            if(a.getString("status").equals("OK")) {
                                domanda = new Domanda(a.getJSONObject("result"));
                                init();
                                enableOnScrollDownAction();
                            } else throw new JSONException("else");

                        } catch(JSONException e) {
                            errorBar(getResources().getString(R.string.Error), 2000);
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


        } else finish();
    }

    void init() {
        scegliTopRisposta = isActivated() && (domanda.getRispostaTop() == null) && (domanda.getUtente().getId() == getUser().getId());

        //setting domanda
        orario.setText(domanda.getUtente().reduceData());
        domandaUtente.setText(domanda.getTesto());
        nomeProfilo.setText(domanda.getUtente().getUsername());

        nomeProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, A20_ProfiloPubblico.class);
                intent.putExtra("id_utente", domanda.getUtente().getId());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });

        try {
            if(domanda.getUtente().getId() == getUser().getId()) {
                inserisciRisposta.setVisibility(View.GONE);
            }
        } catch(NullPointerException e) {
            inserisciRisposta.setVisibility(View.GONE);
        }

        fotoProfilo.setFotoPath(domanda.getUtente().getFotopath());

        //setting risposta top
        if(domanda.getRispostaTop() != null) {
            utenteMigliorRisposta.setText(domanda.getRispostaTop().getUtente().getUsername());
            migliorRisposta.setText(domanda.getRispostaTop().getTesto());
            fotoProfiloRisposta.setFotoPath(domanda.getRispostaTop().getUtente().getFotopath());
            orarioRisposta.setText(domanda.getRispostaTop().reduceData());
        } else {
            settoreMigliorRisposta.setVisibility(View.GONE);
        }

        adapter = new RisposteAdapter(this, domanda.getRisposte() == null ? new ArrayList<Risposta>() : domanda.getRisposte(), focusId, scegliTopRisposta);

        if(domanda.getRisposte().size() == 0) {
            risposteUtenti.setText(getResources().getString(R.string.risposte_utenti_non));
        } else {
            risposteUtenti.setText(getResources().getString(R.string.riposte_per_questa_domanda));
        }
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        if(focusId >= 0) {
            recyclerView.scrollToPosition(focusId);
            focusId = -1;
        } else if(focusId == -2) {
            focusId = -1;
            Utility.highLight(settoreMigliorRisposta);
        }
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        if(domanda != null) {
            startCaricamento(50, getResources().getString(R.string.aggiorno));

            int id_domanda = domanda.getId();
            JSONObject req = new JSONObject();
            try {
                req.put("path", "domanda");
                req.put("id_domanda", id_domanda);

                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        try {
                            if(a.getString("status").equals("OK")) {
                                domanda = new Domanda(a.getJSONObject("result"));
                                adapter.update(domanda.getRisposte(), scegliTopRisposta);
                                Store.add("domanda", domanda);
                                init();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                        stopCaricamento(100);
                    }

                    @Override
                    public void noInternetConnection() {
                        stopCaricamento(100);
                        noInternetErrorBar();
                    }
                }).execute(req);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        } else {
            errorBar(getResources().getString(R.string.errore_server), 2000);
        }
    }
}