package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dounn.menutendina.Adapters.RecensioniAdapter;
import com.example.dounn.menutendina.Adapters.ScrollImagesAdapter;
import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Database.Recensione;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.MyDialogFragment;
import com.example.dounn.menutendina.Utility.GenericRequest;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.example.dounn.menutendina.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Enrico on 15/06/2017.
 */

public class A27_PaginaElemento extends SuperActivity implements MyDialogFragment.RispostaDialog {

    TextView titoloElemento;
    ViewPager viewPager;
    RatingBar votazioneElemento;
    TextView testoVotazioneElemento;
    TextView getCategoria;
    TextView descrizione;
    Button bottonePreferiti;
    ProgressBar caricamentoPreferiti;
    Button bottoneRimuoviPreferiti;
    Button bottoneSegnala;
    Button domandeRisposteButton;
    Button modificaElementoButton;
    Button inserisciRecensione;
    TextView testoRecensioniCliente;
    Elemento elemento;
    int conteggioFoto;

    //elementi per i puntini sotto le immagini scorrevoli
    LinearLayout sliderDotspanel;
    private ImageView[] dots;


    //elementi per le recensioni
    private RecyclerView recyclerView;
    private ArrayList<Recensione> recensioni;
    private LinearLayoutManager llm;
    private int focusId = -1;
    private RecensioniAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a27_layout);

        viewPager = (ViewPager) findViewById(R.id.viewPager_pagina_elemento);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots_a27);
        titoloElemento = (TextView) findViewById(R.id.titolo_elemento);
        bottonePreferiti = (Button) findViewById(R.id.preferiti_a27);
        bottoneRimuoviPreferiti = (Button) findViewById(R.id.rimuovi_preferiti_a27);
        caricamentoPreferiti = (ProgressBar) findViewById(R.id.progress_preferiti_a27);
        bottoneSegnala = (Button) findViewById(R.id.segnala_elemento_a27);
        votazioneElemento = (RatingBar) findViewById(R.id.votazione_complessiva_elemento);
        testoVotazioneElemento = (TextView) findViewById(R.id.textvie_votazione_stelle_a27);
        getCategoria = (TextView) findViewById(R.id.get_categoria);
        descrizione = (TextView) findViewById(R.id.descrizione_elemento);
        domandeRisposteButton = (Button) findViewById(R.id.domande_e_risposte_button);
        modificaElementoButton = (Button) findViewById(R.id.button_modifica_elemento);
        inserisciRecensione = (Button) findViewById(R.id.inserire_recensione_text);
        testoRecensioniCliente = (TextView) findViewById(R.id.testo_recensioni_cliente_a27);

        enableOnScrollDownAction();
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    void update() {
        JSONObject req = new JSONObject();

        startCaricamento(0, getResources().getString(R.string.aggiorno));
        try {
            req.put("path", "elemento");
            req.put("id_elemento", elemento.getId());
            if(isLogged()) req.put("token", getUser().getToken());
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(a.getString("status").equals("OK")) {
                            elemento = new Elemento(a.getJSONObject("result"));
                            Store.add("elemento", elemento);
                            init();
                        } else
                            throw new JSONException("else");
                    } catch(JSONException e) {
                        errorBar(getResources().getString(R.string.Error), 2000);
                    } finally {
                        stopCaricamento(100);
                    }
                }

                @Override
                public void noInternetConnection() {
                    noInternetErrorBar();
                }
            }).execute(req);
        } catch(JSONException e) {
            e.printStackTrace();
            stopCaricamento(100);
            errorBar(getResources().getString(R.string.Error), 2000);
        }
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        update();
    }


    void init() {
        //prendo l'elemento dalla precedente activity
        elemento = (Elemento) Store.get("elemento");
        if(isLogged()) {
            try {
                JSONObject req = new JSONObject();
                new GenericRequest().execute(req);
                req.put("token", getToken());
                req.put("path", "visita");
                req.put("id_elemento", elemento.getId());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        if(getIntent().hasExtra("focus")) {
            focusId = getIntent().getIntExtra("focus", 0);
        }

        //setto il titolo, la categoria, la votazione e la descrizione
        titoloElemento.setText(elemento.getNome());
        getCategoria.setText(Utility.catchCategoria(elemento.getCategoria()));
        votazioneElemento.setRating(elemento.getRating());
        if(elemento.getRating() < 0) {
            testoVotazioneElemento.setText(getResources().getString(R.string.not_available));
        } else testoVotazioneElemento.setText(String.format("%.1f", (elemento.getRating())));
        descrizione.setText(elemento.getDescr());

        //Richiesta per le recensioni dell'elemento *
        JSONObject req = new JSONObject();

        //carico la recyclerView per le recensioni
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_recensioni_a27);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recensioni = new ArrayList<>();

        try {
            req.put("id_elemento", elemento.getId());
            req.put("path", "recensioni_elemento");

            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(!a.getString("status").equals("ERROR")) {

                            JSONArray result = a.getJSONArray("result");

                            if(result.length() != 0) {

                                for(int i = 0; i < result.length(); i++) {
                                    recensioni.add(new Recensione(result.getJSONObject(i)));
                                }

                                adapter = new RecensioniAdapter(ctx, recensioni, focusId);
                                recyclerView.setAdapter(adapter);
                                if(focusId != -1) {
                                    recyclerView.scrollToPosition(focusId);
                                    focusId = -1;
                                }
                            } else {
                                testoRecensioniCliente.setText(ctx.getResources().getString(R.string.non_ci_sono_recensioni));
                            }
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                        stopCaricamento(200);
                    }
                }

                @Override
                public void noInternetConnection() {
                    stopCaricamento(200);
                }
            }).execute(req);

        } catch(JSONException e) {
            e.printStackTrace();
        }

        viewPager.setAdapter(new ScrollImagesAdapter(ctx, elemento.getFotos()));
        sliderDotspanel.removeAllViews();
        conteggioFoto = elemento.getFotos().size();
        dots = new ImageView[conteggioFoto];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for(int i = 0; i < conteggioFoto; i++) {
            dots[i] = new ImageView(ctx);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
            params.setMargins(12, 0, 12, 0);
            sliderDotspanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //metto tutti i punti vuoti
                for(int i = 0; i < conteggioFoto; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                //metto quello giusto selezionato
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //setto un timer temporizzato che fa girare in automatico le foto
        Timer timer = new Timer();
        //2o param:tempo entro quale partirà il timer
        //30 param:tempo tra un intervallo e l'altro
        timer.scheduleAtFixedRate(new MyTimerTask(), 5000, 7000);


        if(!isActivated()) {
            setGone(modificaElementoButton);
        } else if(getUser().getLevel().getLivello() < 18) {
            setGone(modificaElementoButton);
        } else
            modificaElementoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Store.add("elemento", elemento);
                    startActivity(new Intent(ctx, A17_ModificaElementoAvviso.class));
                }
            });

        domandeRisposteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store.add("elemento", elemento);
                startActivity(new Intent(ctx, A28_Domande.class));
            }
        });


        //se non sono loggato non vedo preferiti e segnala
        if(!isActivated()) {
            setGone(bottonePreferiti, bottoneSegnala, bottoneRimuoviPreferiti);
        } else {
            //mostro un breve caricamento
            caricamentoPreferiti.setVisibility(View.VISIBLE);

            //richiesta per controllare il bottone dei preferiti da mostrare
            JSONObject reqpref = new JSONObject();
            try {
                reqpref.put("id_elemento", elemento.getId());
                reqpref.put("token", getToken());
                reqpref.put("path", "is_preferito");

                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        try {
                            if(!a.getString("status").equals("ERROR")) {
                                if(a.getString("result").equals("si")) {
                                    caricamentoPreferiti.setVisibility(View.GONE);
                                    //se è già preferito mostro bottone per rimuoverlo
                                    bottoneRimuoviPreferiti.setVisibility(View.VISIBLE);
                                } else {
                                    caricamentoPreferiti.setVisibility(View.GONE);
                                    //altrimenti mostro bottone per poterlo aggiungere
                                    bottonePreferiti.setVisibility(View.VISIBLE);
                                }
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
                }).execute(reqpref);
            } catch(JSONException e) {
                e.printStackTrace();
            }


            //associo il listener al bottone per la rimozione
            bottoneRimuoviPreferiti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mostro caricamento
                    setGone(bottoneRimuoviPreferiti, bottonePreferiti);
                    setVisible(caricamentoPreferiti);

                    JSONObject reqpref = new JSONObject();
                    try {
                        reqpref.put("id_elemento", elemento.getId());
                        reqpref.put("token", getToken());
                        reqpref.put("path", "remove_preferito");

                        //richiesta rimossione dai preferiti
                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(!a.getString("status").equals("ERROR")) {
                                        successBar(getResources().getString(R.string.Remove_favorites), 3000);
                                        setGone(bottoneRimuoviPreferiti, caricamentoPreferiti);
                                        setVisible(bottonePreferiti);
                                    } else {
                                        setGone(caricamentoPreferiti);
                                        setVisible(bottoneRimuoviPreferiti);
                                        errorBar(getResources().getString(R.string.Op_fine), 3000);
                                    }
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void noInternetConnection() {
                                noInternetErrorBar();
                            }
                        }).execute(reqpref);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //associo il listener per aggiungerlo
            bottonePreferiti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mostro caricamento
                    setGone(bottoneRimuoviPreferiti, bottonePreferiti);
                    setVisible(caricamentoPreferiti);

                    JSONObject reqpref = new JSONObject();
                    try {
                        reqpref.put("id_elemento", elemento.getId());
                        reqpref.put("token", getToken());
                        reqpref.put("path", "add_preferito");

                        //richiesta aggiunta ai preferiti
                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(!a.getString("status").equals("ERROR")) {
                                        successBar(getResources().getString(R.string.Add_favorites), 3000);
                                        setGone(bottonePreferiti, caricamentoPreferiti);
                                        setVisible(bottoneRimuoviPreferiti);
                                    } else {
                                        setGone(caricamentoPreferiti);
                                        setVisible(bottonePreferiti);
                                        errorBar(getResources().getString(R.string.Op_fine), 3000);
                                    }
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void noInternetConnection() {

                            }
                        }).execute(reqpref);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            bottoneSegnala.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //instanzio un DialogFragment per ricevere la segnalazione dall'utente
                    FragmentManager fm = getSupportFragmentManager();
                    MyDialogFragment alertDialog = MyDialogFragment.newInstance(ctx.getResources().getString(R.string.titolo_segnalazione_elemento));
                    alertDialog.show(fm, "fragment_segnalazione");
                    //riceverò i risultati in onFinishEditDialog(testo inserito)
                }
            });
        }

        inserisciRecensione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isActivated()) {
                    if(getUser().canRecensione()) {
                        Intent intent = new Intent(ctx, A16_CreaRecensione.class);
                        startActivity(intent);
                    } else {
                        errorBar(getResources().getString(R.string.non_puoi_fare_altre_recensioni), 3000);
                    }
                }
            }
        });

        if(!(isActivated() && getUser().canRecensione())) {
            setGone(inserisciRecensione);
        }
    }


    //funzione che mi ritorna il testo inserito dall'utente nella segnalazione
    @Override
    public void onFinishEditDialog(String inputText) {
        JSONObject req = new JSONObject();
        try {
            req.put("id_elemento", elemento.getId());
            req.put("token", getToken());
            req.put("motivazione", inputText);
            req.put("path", "segnala_elemento");

            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(!a.getString("status").equals("ERROR")) {
                            successBar(getResources().getString(R.string.Report_succeed), 3000);
                        } else {
                            errorBar(getResources().getString(R.string.Report_error), 3000);
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

    //timer per lo scorrimento automatico delle foto
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % conteggioFoto);
                }
            });

        }
    }
}