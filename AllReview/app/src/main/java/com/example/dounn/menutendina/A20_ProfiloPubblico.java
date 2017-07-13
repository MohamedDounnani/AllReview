package com.example.dounn.menutendina;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dounn.menutendina.Database.Level;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.Database.Utente;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;


public class A20_ProfiloPubblico extends SuperActivity {

    private Utente utenteRed;
    private int id_utente;

    private TextView myTextProgress;
    private TextView myTextLevel;
    private TextView myTextProgressInit;
    private TextView myTextProgressFine;
    private TextView myTextLevelInit;
    private TextView myTextLevelFine;

    private TextView pointsAndLevel;

    private ProgressBar pointsProgress;
    private ProgressBar levelProgress;

    private GraphView graph;

    private TextView userNameProfile;

    ImageViewLoading imageProfile;

    private TextView dataTextView;

    private Button seguiAdd;
    private ProgressBar progressSegui;
    private Button seguiRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a20_layout);
        try {
            id_utente = getIntent().getIntExtra("id_utente", 0);
            if(id_utente == getUser().getId()) {
                startActivity(new Intent(ctx, A7_ProfiloPrivato.class));
                finish();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        pointsAndLevel = (TextView) findViewById(R.id.points_and_level);

        myTextProgress = (TextView) findViewById(R.id.myTextProgress);
        myTextProgressInit = (TextView) findViewById(R.id.myTextProgressInit);
        myTextProgressFine = (TextView) findViewById(R.id.myTextProgressFine);

        myTextLevel = (TextView) findViewById(R.id.myTextLevel);
        myTextLevelInit = (TextView) findViewById(R.id.myTextLevelInit);
        myTextLevelFine = (TextView) findViewById(R.id.myTextLevelFine);

        pointsProgress = (ProgressBar) findViewById(R.id.progress_user_bar);
        levelProgress = (ProgressBar) findViewById(R.id.level_user_bar);

        userNameProfile = (TextView) findViewById(R.id.user_name_profile);
        dataTextView = (TextView) findViewById(R.id.data_textview);

        pointsProgress.setProgress(0);
        pointsProgress.setMax(1000);

        levelProgress.setProgress(0);
        levelProgress.setMax(30);

        graph = (GraphView) findViewById(R.id.points_graph);

        imageProfile = (ImageViewLoading) findViewById(R.id.image_profile);

        Button moreInfo = (Button) findViewById(R.id.more_info);
        Button classifica = (Button) findViewById(R.id.classifica);

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, A5_InfoPunteggi.class));
            }
        });

        classifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, A12_Classifica.class));
            }
        });

        Button vedi_recensioni = (Button) findViewById(R.id.vedi_recensioni);
        vedi_recensioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, A25_RecensioniUtente.class);
                i.putExtra("id_utente", utenteRed.getId());
                startActivity(i);
            }
        });

        progressSegui = (ProgressBar) findViewById(R.id.progress_segui);
        seguiAdd = (Button) findViewById(R.id.segui_add);
        seguiRemove = (Button) findViewById(R.id.segui_remove);


        if(id_utente < 1) {
            errorBar(getResources().getString(R.string.Err_user), 5000);
        } else updateUser(id_utente);

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passo la foto alla classe per zoomare
                Store.add("zoom_foto", imageProfile.getFotoPath());
                Intent fullScreenIntent = new Intent(ctx, A31_FullScreenImage.class);
                fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(fullScreenIntent);
            }
        });

        if(isActivated()) {
            //associo il listener al bottone per la rimozione
            seguiRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mostro caricamento
                    setGone(seguiAdd, seguiRemove);
                    setVisible(progressSegui);

                    JSONObject reqpref = new JSONObject();
                    try {
                        reqpref.put("id_utente", id_utente);
                        reqpref.put("token", getToken());
                        reqpref.put("path", "remove_seguito");

                        //richiesta rimossione dai preferiti
                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(!a.getString("status").equals("ERROR")) {
                                        successBar(getResources().getString(R.string.Remove_favorites), 3000);
                                        setGone(seguiRemove, progressSegui);
                                        setVisible(seguiAdd);
                                    } else {
                                        setGone(progressSegui);
                                        setVisible(seguiRemove);
                                        errorBar(getResources().getString(R.string.Op_fine), 3000);
                                    }
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                    errorBar(getResources().getString(R.string.Error), 2000);
                                }
                            }

                            @Override
                            public void noInternetConnection() {
                                noInternetErrorBar();
                            }
                        }).execute(reqpref);
                    } catch(JSONException e) {
                        errorBar(getResources().getString(R.string.Error), 2000);
                        e.printStackTrace();
                    }
                }
            });

            //associo il listener per aggiungerlo
            seguiAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mostro caricamento
                    setGone(seguiRemove, seguiAdd);
                    setVisible(progressSegui);

                    JSONObject reqpref = new JSONObject();
                    try {
                        reqpref.put("id_utente", id_utente);
                        reqpref.put("token", getToken());
                        reqpref.put("path", "add_seguito");

                        //richiesta aggiunta ai preferiti
                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(!a.getString("status").equals("ERROR")) {
                                        successBar(getResources().getString(R.string.Add_favorites), 3000);
                                        setGone(seguiAdd, progressSegui);
                                        setVisible(seguiRemove);
                                    } else {
                                        setGone(progressSegui);
                                        setVisible(seguiAdd);
                                        errorBar(getResources().getString(R.string.Op_fine), 3000);
                                    }
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                    errorBar(getResources().getString(R.string.Error), 2000);
                                }
                            }

                            @Override
                            public void noInternetConnection() {
                                noInternetErrorBar();
                            }
                        }).execute(reqpref);
                    } catch(JSONException e) {
                        errorBar(getResources().getString(R.string.Error), 2000);
                        e.printStackTrace();
                    }
                }
            });
        }

        enableOnScrollDownAction();
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        updateUser(id_utente);
    }


    @Override
    protected void onResume() {
        super.onResume();
        onScrollDownAction();
    }

    public void showUser() {

        setGone(dataTextView, myTextProgress, myTextProgressInit, myTextProgressFine, myTextLevel, myTextLevelInit, myTextLevelFine, pointsAndLevel);
        setGone(pointsProgress, levelProgress, userNameProfile, pointsProgress, levelProgress, graph, imageProfile);

        imageProfile.setVisibility(View.GONE);

        final String fotopath = utenteRed.getFotopath();
        if(fotopath == null) {
            imageProfile.setImageResource(R.mipmap.ic_profile);
        } else {
            imageProfile.setFotoPath(fotopath);
        }

        dataTextView.setText(getResources().getString(R.string.Registrato) + " " + utenteRed.completeData());

        // SETTING DEL BOTTONE CARICAMENTO FOTO

        Level livelli = utenteRed.getLevel();

        final int punteggio = livelli.getPunteggio();
        final int points_up = livelli.getPointsUp();
        final int points_down = livelli.getPointsDown();
        final int level = livelli.getLivello();
        final int finalLevel = 30;
        final int initLevel = 0;

        myTextProgressInit.setText(String.valueOf(points_down));
        myTextProgressFine.setText(String.valueOf(points_up));

        myTextLevelInit.setText(String.valueOf(initLevel));
        myTextLevelFine.setText(String.valueOf(finalLevel));

        myTextLevel.setText(getResources().getString(R.string.Livello) + " " + level);
        myTextProgress.setText(punteggio + "  " + getResources().getString(R.string.punti));

        final int max_state = 1000 * (punteggio - points_down) / (points_up - points_down);

        pointsProgress.setProgress(0);
        levelProgress.setProgress(0);

        //se non sono loggato non vedo preferiti e segnala

        setGone(seguiAdd, seguiRemove, progressSegui);
        if(isActivated()) {
            //mostro un breve caricamento
            progressSegui.setVisibility(View.VISIBLE);

            //richiesta per controllare il bottone dei preferiti da mostrare
            JSONObject reqpref = new JSONObject();
            try {
                reqpref.put("id_utente", id_utente);
                reqpref.put("token", getToken());
                reqpref.put("path", "is_seguito");

                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        try {
                            if(!a.getString("status").equals("ERROR")) {
                                progressSegui.setVisibility(View.GONE);
                                if(a.getString("result").equals("si")) {
                                    //se è già preferito mostro bottone per rimuoverlo
                                    seguiRemove.setVisibility(View.VISIBLE);
                                } else {
                                    //altrimenti mostro bottone per poterlo aggiungere
                                    seguiAdd.setVisibility(View.VISIBLE);
                                }
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
                errorBar(getResources().getString(R.string.Error), 2000);
            }
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(utenteRed.getGrafico().getPunti());
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setColor(getResources().getColor(R.color.barra));
        series.setThickness(8);
        graph.addSeries(series);

        // use static labels for horizontal and vertical labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(utenteRed.getGrafico().getDates());
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();

        gridLabel.setLabelFormatter(staticLabelsFormatter);
        gridLabel.setVerticalAxisTitle(getResources().getString(R.string.Punteggio));
        gridLabel.setHorizontalAxisTitle(getResources().getString(R.string.Days));

        // SETTO USERNAME
        userNameProfile.setText(utenteRed.getUsername());
        setVisible(userNameProfile);

        setVisible(imageProfile, dataTextView, myTextProgress, myTextProgressInit, myTextProgressFine, myTextLevel, myTextLevelInit, myTextLevelFine);
        setVisible(pointsProgress, levelProgress, pointsAndLevel, graph);

        ObjectAnimator animation = ObjectAnimator.ofInt(pointsProgress, "progress", max_state);
        animation.setDuration(2500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        animation = ObjectAnimator.ofInt(levelProgress, "progress", level);
        animation.setDuration(2500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }


    void updateUser(final int id_utente) {
        synchronized(lockUpdateUser) {
            startCaricamento(100, getResources().getString(R.string.Get_user));

            try {
                JSONObject req = new JSONObject();
                req.put("id_utente", id_utente);
                req.put("path", "utente");

                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        try {
                            if(a.getString("status").equals("OK")) {
                                // INIZIALIZZAZIONE DATI SCARICATI
                                utenteRed = new Utente(a.getJSONObject("result"));
                                showUser();
                                stopCaricamento(200);
                            } else {
                                noInternetErrorBar();
                                stopCaricamento(200);
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                            stopCaricamento(200);
                        }
                    }

                    @Override
                    public void noInternetConnection() {
                        noInternetErrorBar();
                        stopCaricamento(200);

                    }
                }).execute(req);

            } catch(JSONException e) {
                e.printStackTrace();
                stopCaricamento(200);
            }
        }
    }
}