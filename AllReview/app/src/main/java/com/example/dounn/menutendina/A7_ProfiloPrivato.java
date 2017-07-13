package com.example.dounn.menutendina;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dounn.menutendina.Database.Level;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.Utility.CallBack;
import com.example.dounn.menutendina.Utility.Images;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

public class A7_ProfiloPrivato extends LoggedActivity {

    private TextView infoMaxUtenteText;

    private TextView myTextProgress;
    private TextView myTextLevel;
    private TextView myTextProgressInit;
    private TextView myTextProgressFine;
    private TextView myTextLevelInit;
    private TextView myTextLevelFine;
    private TextView infoEmail;

    private TextView pointsAndLevel;

    private ProgressBar pointsProgress;
    private ProgressBar levelProgress;

    private GraphView graph;

    private TextView userNameProfile;

    private Button profileImageButton;
    private Button modificaProfilo;
    private ImageViewLoading viewLoading;

    private Button moreInfo;
    private Button classifica;
    private Button freespace;
    //Buttons

    private TextView dataTextview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a7_layout);
        navigationView.getMenu().getItem(pref.getInt("nav_profilo", 0)).setChecked(true);

        pointsAndLevel = (TextView) findViewById(R.id.points_and_level);

        infoMaxUtenteText = (TextView) findViewById(R.id.info_max_utente_text);
        infoEmail = (TextView) findViewById(R.id.info_email);

        myTextProgress = (TextView) findViewById(R.id.myTextProgress);
        myTextProgressInit = (TextView) findViewById(R.id.myTextProgressInit);
        myTextProgressFine = (TextView) findViewById(R.id.myTextProgressFine);

        myTextLevel = (TextView) findViewById(R.id.myTextLevel);
        myTextLevelInit = (TextView) findViewById(R.id.myTextLevelInit);
        myTextLevelFine = (TextView) findViewById(R.id.myTextLevelFine);

        pointsProgress = (ProgressBar) findViewById(R.id.progress_user_bar);
        levelProgress = (ProgressBar) findViewById(R.id.level_user_bar);

        userNameProfile = (TextView) findViewById(R.id.user_name_profile);
        dataTextview = (TextView) findViewById(R.id.data_textview);

        pointsProgress.setProgress(0);
        pointsProgress.setMax(1000);

        levelProgress.setProgress(0);
        levelProgress.setMax(30);

        graph = (GraphView) findViewById(R.id.points_graph);

        viewLoading = (ImageViewLoading) findViewById(R.id.image_profile);

        profileImageButton = (Button) findViewById(R.id.profile_image_button);
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        modificaProfilo = (Button) findViewById(R.id.modifica_profilo);

        modificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, A13_ModificaProfilo.class));
            }
        });

        moreInfo = (Button) findViewById(R.id.more_info);
        classifica = (Button) findViewById(R.id.classifica);
        freespace = (Button) findViewById(R.id.freespace);

        freespace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCaricamento(0,getResources().getString(R.string.freeing));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Images.freeUpSpace();
                                stopCaricamento(100);
                                successBar("Spazio liberato con successo", 3000);
                            }
                        });
                    }
                }).start();

            }
        });

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

        viewLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passo la foto alla classe per zoomare
                Store.add("zoom_foto", viewLoading.getFotoPath());
                Intent fullScreenIntent = new Intent(ctx, A31_FullScreenImage.class);
                fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(fullScreenIntent);
            }
        });

        enableOnScrollDownAction();

        showUser();
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        startCaricamento(200, getResources().getString(R.string.Up_user));
        updateUser(new CallBack() {
            @Override
            public void inTheEnd(Object o) {
                showUser();
                stopCaricamento(200);
            }
        });
    }

    public void showUser() {

        if(isActivated()) {
            setGone(dataTextview, myTextProgress, myTextProgressInit, myTextProgressFine, myTextLevel, myTextLevelInit, myTextLevelFine, pointsAndLevel);
            setGone(pointsProgress, levelProgress, userNameProfile, pointsProgress, levelProgress, graph, viewLoading, profileImageButton, infoMaxUtenteText);

            viewLoading.setVisibility(View.GONE);

            final String fotopath = getUser().getFotopath();
            if(fotopath == null) {
                profileImageButton.setText(getResources().getString(R.string.carica_immagine_profilo));
                viewLoading.setImageResource(R.mipmap.ic_profile);
            } else {
                viewLoading.setFotoPath(fotopath);
                profileImageButton.setText(getResources().getString(R.string.carica_nuova_immagine));
            }

            dataTextview.setText(getResources().getString(R.string.Registrato) + " " + getUser().completeData());

            // SETTING DEL BOTTONE CARICAMENTO FOTO

            Level livelli = getUser().getLevel();

            final int punteggio = livelli.getPunteggio();
            final int pointsUp = livelli.getPointsUp();
            final int pointsDown = livelli.getPointsDown();
            final int level = livelli.getLivello();
            final int finalLevel = 30;
            final int initLevel = 0;

            String maxString = getString(R.string.max_values_utente) + "";

            String maxVoti = "" + livelli.getMaxVoti();
            String maxRec = "" + livelli.getMaxRec();
            String maxDom = "" + livelli.getMaxDom();
            String doneVoti = "" + getUser().getVotiCounter();
            String doneRec = "" + getUser().getRecensioniCounter();
            String doneDom = "" + getUser().getDomandeCounter();

            maxString = java.text.MessageFormat.format(maxString, new String[]{maxRec, maxVoti, maxDom, doneRec, doneVoti, doneDom});

            infoMaxUtenteText.setText(maxString);
            infoEmail.setText(java.text.MessageFormat.format(getResources().getString(R.string.personal_email), getUser().getEmail()));

            myTextProgressInit.setText(String.valueOf(pointsDown));
            myTextProgressFine.setText(String.valueOf(pointsUp));

            myTextLevelInit.setText(String.valueOf(initLevel));
            myTextLevelFine.setText(String.valueOf(finalLevel));

            myTextLevel.setText(getResources().getString(R.string.Livello) + " " + level);
            myTextProgress.setText(punteggio + " " + getResources().getString(R.string.punti));

            final int maxState = 1000 * (punteggio - pointsDown) / (pointsUp - pointsDown);

            pointsProgress.setProgress(0);
            levelProgress.setProgress(0);

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getUser().getGrafico().getPunti());
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(10);
            series.setColor(getResources().getColor(R.color.barra));
            series.setThickness(8);
            graph.addSeries(series);

            // use static labels for horizontal and vertical labels
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            staticLabelsFormatter.setHorizontalLabels(getUser().getGrafico().getDates());
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();

            gridLabel.setLabelFormatter(staticLabelsFormatter);
            gridLabel.setVerticalAxisTitle(getResources().getString(R.string.Punteggio));
            gridLabel.setHorizontalAxisTitle(getResources().getString(R.string.Days));

            // SETTO USERNAME
            userNameProfile.setText(getUser().getUsername());
            setVisible(userNameProfile);

            setVisible(viewLoading, dataTextview, myTextProgress, myTextProgressInit, myTextProgressFine, myTextLevel, myTextLevelInit, myTextLevelFine);
            setVisible(pointsProgress, levelProgress, pointsAndLevel, infoMaxUtenteText, profileImageButton, graph);

            ObjectAnimator animation = ObjectAnimator.ofInt(pointsProgress, "progress", maxState);
            animation.setDuration(2500);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();

            animation = ObjectAnimator.ofInt(levelProgress, "progress", level);
            animation.setDuration(2500);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        } else noInternetErrorBar();
    }


    //RISULTATO DELLA FUNZIONE SOPRA DESCRITTA, GESTISCO IL RISULTATO E LO TRASFORMO IN IMMAGINE
    @Override
    protected void onActivityResultImage(Bitmap bitmap) {

        JSONObject req = new JSONObject();
        try {
            req.put("path", "change_foto_utente");
            req.put("token", getToken());
            req.put("img_data", Images.toEncodedString(bitmap));
            startCaricamento(100, getResources().getString(R.string.Up_image));

            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {

                    try {
                        stopCaricamento(0);
                        if(a.getString("status").equals("OK")) {
                            startCaricamento(0, getResources().getString(R.string.Up_user));
                            updateUser(new CallBack() {
                                @Override
                                public void inTheEnd(Object o) {
                                    stopCaricamento(100);
                                    showUser();
                                    successBar(getResources().getString(R.string.Loading_image), 6000);
                                }
                            });

                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                        stopCaricamento(0);
                    }
                }

                @Override
                public void noInternetConnection() {
                    noInternetErrorBar();
                }
            }).execute(req);
        } catch(JSONException e) {
            e.printStackTrace();
            stopCaricamento(300);
        }
    }
}