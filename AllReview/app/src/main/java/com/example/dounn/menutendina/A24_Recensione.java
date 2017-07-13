package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dounn.menutendina.Animations.CircleAnimation;
import com.example.dounn.menutendina.Database.Recensione;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.CircleView;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.OtherView.MyDialogFragment;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.example.dounn.menutendina.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Enrico on 20/06/2017.
 */

public class A24_Recensione extends SuperActivity implements MyDialogFragment.RispostaDialog {

    private TextView titoloElemento;
    private ImageViewLoading immagineRecensione;

    private RatingBar votazioneRecensione;
    private TextView titoloRecensione;
    private TextView descrizioneRecensione;
    private TextView recensore;
    private TextView dataRecensione;
    private ImageButton bottonepositivi;
    private ImageButton segnala;
    private ImageButton bottonenegativi;
    private ProgressBar progresspositivi;
    private ProgressBar progressnegativi;
    private Recensione recensione;
    //di default non ho niente votato
    private int votoFatto = 0;
    private TextView textvieVotazioneStelleA24;

    CircleView pointAnimationPositivi;
    CircleView pointAnimationNegativi;

    private LinearLayout sezioneVoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a24_layout);

        recensione = (Recensione) Store.get("recensione");

        //dichiaro gli elementi necessari
        titoloElemento = (TextView) findViewById(R.id.titolo_articolo_della_recensione_a24);
        immagineRecensione = (ImageViewLoading) findViewById(R.id.immagine_recensione_a24);
        votazioneRecensione = (RatingBar) findViewById(R.id.votazione_recensione_a24);
        titoloRecensione = (TextView) findViewById(R.id.titolo_recensione_a24);
        descrizioneRecensione = (TextView) findViewById(R.id.descrizione_recensione_a24);
        recensore = (TextView) findViewById(R.id.utente_recensore_a24);
        dataRecensione = (TextView) findViewById(R.id.data_recensione_a24);
        sezioneVoti = (LinearLayout) findViewById(R.id.sezione_voti);
        textvieVotazioneStelleA24 = (TextView) findViewById(R.id.textvie_votazione_stelle_a24);

        pointAnimationPositivi = (CircleView) findViewById(R.id.voti_positivi);
        pointAnimationNegativi = (CircleView) findViewById(R.id.voti_negativi);
        bottonepositivi = (ImageButton) findViewById(R.id.voto_positivo_button);
        bottonenegativi = (ImageButton) findViewById(R.id.voto_negativo_button);
        progressnegativi = (ProgressBar) findViewById(R.id.progress_negativi_a24);
        progresspositivi = (ProgressBar) findViewById(R.id.progress_positivi_a24);
        segnala = (ImageButton) findViewById(R.id.segnala_recensione);

        //setto gli elementi visibili da tutti
        titoloElemento.setText(recensione.getElemento().getNome());
        votazioneRecensione.setRating((float) recensione.getVoto());
        titoloRecensione.setText(recensione.getTitolo());
        descrizioneRecensione.setText(recensione.getDescr());
        recensore.setText(recensione.getUtente().getUsername());
        dataRecensione.setText(recensione.reduceData());

        pointAnimationPositivi.setNumber(recensione.getVotiPositivi());
        pointAnimationNegativi.setNumber(recensione.getVotiNegativi());

        init();
        enableOnScrollDownAction();
    }


    void update() {
        if(recensione == null) return;
        int id_recensione = recensione.getId();
        startCaricamento(0, getResources().getString(R.string.caricamento_recensione));
        JSONObject req = new JSONObject();
        try {
            req.put("path", "recensione");
            req.put("id_recensione", id_recensione);
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(a.getString("status").equals("OK")) {
                            recensione = new Recensione(a.getJSONObject("result"));
                            stopCaricamento(100);
                            init();
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

    void init() {
        textvieVotazioneStelleA24.setText(String.format("%.1f", (recensione.getVoto())));

        immagineRecensione.setFotoPath(recensione.getFotopath());
        immagineRecensione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passo la foto alla classe per zoomare
                Store.add("zoom_foto", immagineRecensione.getFotoPath());
                Intent fullScreenIntent = new Intent(ctx, A31_FullScreenImage.class);
                fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(fullScreenIntent);
            }
        });

        //al click vado nel profilo dell'utente
        recensore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, A20_ProfiloPubblico.class);
                intent.putExtra("id_utente", recensione.getUtente().getId());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });

        titoloElemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, A27_PaginaElemento.class);
                Store.add("elemento", recensione.getElemento());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });

        //controllo di essere un utente loggato per vedere tutte le funzionalità
        if(!isActivated()) {
            setGone(bottonepositivi, bottonenegativi, segnala, progressnegativi, progresspositivi);
        } else {
            //se la recensione è mia nascondo tutto
            if(recensione.getUtente().getId() == getUser().getId()) {
                setGone(bottonepositivi, bottonenegativi, segnala, progressnegativi, progresspositivi);
            } else {

                //controllo anche di non aver esaurito i voti, se l'avevo già votata, l'onresume me la farà vedere
                if(!getUser().canVoto() && votoFatto == 0) {
                    setGone(bottonepositivi, bottonenegativi, progressnegativi, progresspositivi);
                    errorBar(getResources().getString(R.string.terminati_voti), 3000);
                }
            }
        }

        bottonepositivi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(votoFatto == 1) {
                    errorBar(getResources().getString(R.string.giapositivo), 3000);
                } else {

                    JSONObject req = new JSONObject();
                    try {
                        req.put("voto", 1);
                        req.put("id_recensione", recensione.getId());
                        req.put("path", "vota_recensione");
                        req.put("token", getToken());


                        //mostro i caricamenti dei pulsanti
                        setGone(bottonepositivi, bottonenegativi);
                        setVisible(progresspositivi, progressnegativi);

                        //chiedo una richiesta veloce al server per la votazione
                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(a.getString("status").equals("OK")) {
                                        successBar("Voto Aggiunto correttamente", 3000);

                                        //se non avevo votato niente
                                        if(votoFatto == 0) {
                                            pointAnimationPositivi.setNumber(pointAnimationPositivi.getNumber() + 1);
                                            recensione.setVoti(pointAnimationPositivi.getNumber(), pointAnimationNegativi.getNumber());
                                        } else {
                                            //se l'utente aveva già votato negativo
                                            if(votoFatto == -1) {
                                                pointAnimationPositivi.setNumber(pointAnimationPositivi.getNumber() + 1);
                                                pointAnimationNegativi.setNumber(pointAnimationNegativi.getNumber() - 1);
                                                recensione.setVoti(pointAnimationPositivi.getNumber(), pointAnimationNegativi.getNumber());
                                            }
                                        }

                                        bottonenegativi.setEnabled(true);
                                        bottonepositivi.setBackgroundResource(R.drawable.bottone_grigio_anonimo);
                                        bottonepositivi.setEnabled(false);
                                        bottonenegativi.setBackgroundResource(R.drawable.segnala_elemento);

                                        //mostro i bottoni
                                        setGone(progresspositivi, progressnegativi);
                                        setVisible(bottonepositivi, bottonenegativi);

                                        votoFatto = 1;
                                        Store.add("recensione", recensione);
                                        getUser().fattoVoto();
                                        onScrollDownAction();
                                    }
                                } catch(JSONException e) {
                                    errorBar("Errore", 3000);
                                }
                                stopCaricamento(200);
                            }

                            @Override
                            public void noInternetConnection() {
                                noInternetErrorBar();
                                stopCaricamento(200);
                            }
                        }).execute(req);
                    } catch(JSONException e) {

                    }
                }
            }
        });

        bottonenegativi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(votoFatto == -1) {
                    errorBar(getResources().getString(R.string.gianegativo), 3000);
                } else {
                    JSONObject req = new JSONObject();
                    try {
                        req.put("voto", -1);
                        req.put("id_recensione", recensione.getId());
                        req.put("path", "vota_recensione");
                        req.put("token", getToken());

                        //mostro i caricamenti dei pulsanti
                        setGone(bottonepositivi, bottonenegativi);
                        setVisible(progresspositivi, progressnegativi);

                        //chiedo una richiesta veloce al server per la votazione
                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(a.getString("status").equals("OK")) {
                                        successBar("Voto Aggiunto correttamente", 3000);

                                        //se non avevo votato niente
                                        if(votoFatto == 0) {
                                            pointAnimationNegativi.setNumber(pointAnimationNegativi.getNumber() + 1);
                                            recensione.setVoti(pointAnimationPositivi.getNumber(), pointAnimationNegativi.getNumber());
                                        } else {
                                            //se l'utente aveva già votato positivo
                                            if(votoFatto == 1) {
                                                pointAnimationPositivi.setNumber(pointAnimationPositivi.getNumber() - 1);
                                                pointAnimationNegativi.setNumber(pointAnimationNegativi.getNumber() + 1);
                                                recensione.setVoti(pointAnimationPositivi.getNumber(), pointAnimationNegativi.getNumber());
                                            }
                                        }

                                        bottonepositivi.setBackgroundResource(R.drawable.bottone_verdino);
                                        bottonepositivi.setEnabled(true);
                                        bottonenegativi.setBackgroundResource(R.drawable.bottone_grigio_anonimo);
                                        bottonenegativi.setEnabled(false);

                                        //mostro i bottoni
                                        setGone(progresspositivi, progressnegativi);
                                        setVisible(bottonepositivi, bottonenegativi);

                                        votoFatto = -1;
                                        Store.add("recensione", recensione);
                                        getUser().fattoVoto();
                                        onScrollDownAction();
                                    } else {
                                        errorBar("Già votato", 3000);
                                    }
                                } catch(JSONException e) {
                                    errorBar("Errore", 3000);
                                }
                                stopCaricamento(200);
                            }

                            @Override
                            public void noInternetConnection() {
                                noInternetErrorBar();
                                stopCaricamento(200);
                            }
                        }).execute(req);

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        //permetto la segnalazione
        segnala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //instanzio un DialogFragment per ricevere la segnalazione dall'utente
                FragmentManager fm = getSupportFragmentManager();
                MyDialogFragment alertDialog = MyDialogFragment.newInstance(ctx.getResources().getString(R.string.titolo_segnalazione_recensione));
                alertDialog.show(fm, "fragment_segnalazione");
                //riceverò i risultati in onFinishEditDialog(testo inserito)
            }
        });

        pointAnimationPositivi.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean mMeasured = false;

            @Override
            public void onGlobalLayout() {
                if(!mMeasured) {
                    pointAnimationPositivi.setColor(getResources().getColor(R.color.bottone_login));
                    CircleAnimation animation = new CircleAnimation(pointAnimationPositivi, true);
                    animation.setDuration(2000);
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setInterpolator(new LinearInterpolator());
                    pointAnimationPositivi.startAnimation(animation);
                    mMeasured = true; // Some optional flag to mark, that we already got the sizes
                }
            }
        });


        pointAnimationNegativi.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean mMeasured = false;

            @Override
            public void onGlobalLayout() {
                if(!mMeasured) {
                    pointAnimationNegativi.setColor(getResources().getColor(R.color.colore_rosso_segnalazione));
                    CircleAnimation animation = new CircleAnimation(pointAnimationNegativi, false);
                    animation.setDuration(2000);
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setInterpolator(new LinearInterpolator());
                    pointAnimationNegativi.startAnimation(animation);
                    mMeasured = true; // Some optional flag to mark, that we already got the sizes
                }
            }
        });

        if(getIntent().hasExtra("focus")) {
            Utility.highLight(sezioneVoti);
        }
    }

    //funzione che mi ritorna il testo inserito dall'utente nella segnalazione
    @Override
    public void onFinishEditDialog(String inputText) {
        JSONObject req = new JSONObject();
        try {
            req.put("id_recensione", recensione.getId());
            req.put("token", getToken());
            req.put("motivazione", inputText);
            req.put("path", "segnala_elemento");

            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(!a.getString("status").equals("ERROR")) {
                            successBar("Segnalazione avvenuta correttamente", 3000);
                        } else {
                            errorBar("Errore nell'invio della segnalazione", 3000);
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void noInternetConnection() {
                    errorBar("Connessione assente", 2000);
                }
            }).execute(req);
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recensione = (Recensione) Store.get("recensione");

        //prima controllo che sia attivato
        if(isActivated()) {
            //poi controllo che la recensione non sia sua
            if(!(recensione.getUtente().getId() == getUser().getId())) {
                JSONObject req = new JSONObject();
                try {
                    req.put("id_recensione", recensione.getId());
                    req.put("path", "has_voto");
                    req.put("token", getToken());
                    //chiedo una richiesta veloce al server per la votazione
                    new Request(new RequestCallback() {
                        @Override
                        public void inTheEnd(JSONObject a) {
                            try {
                                if(a.getString("status").equals("OK")) {
                                    if(getUser().canVoto() || a.getInt("result") != 0) {
                                        if(a.getInt("result") == 1) {
                                            votoFatto = 1;
                                            bottonepositivi.setBackgroundResource(R.drawable.bottone_grigio_anonimo);
                                            bottonepositivi.setEnabled(false);
                                        } else {
                                            if(a.getInt("result") == -1) {
                                                votoFatto = -1;
                                                bottonenegativi.setBackgroundResource(R.drawable.bottone_grigio_anonimo);
                                                bottonenegativi.setEnabled(false);
                                            }
                                        }
                                        setGone(progressnegativi, progresspositivi);
                                        setVisible(bottonenegativi, bottonepositivi);
                                    }
                                } else {
                                    errorBar(getResources().getString(R.string.errore_server), 3000);
                                }
                            } catch(JSONException e) {
                                errorBar(getResources().getString(R.string.errore_server), 3000);
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
    }
}