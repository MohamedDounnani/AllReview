package com.example.dounn.menutendina;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dounn.menutendina.Adapters.ScrollImagesAdapter;
import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Database.Recensione;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.Utility.Images;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.example.dounn.menutendina.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.dounn.menutendina.Utility.Images.toEncodedString;

/**
 * Created by Enrico on 12/06/2017.
 */

public class A16_CreaRecensione extends SuperActivity {
    //usati per upload foto

    ViewPager viewPager;
    TextView titolo;
    RatingBar stelle;
    EditText editTitolo;
    TextView erroreTitolo;
    EditText descrizione;
    TextView erroreDescrizione;
    Button allega;
    ImageView immagineAllega;
    Bitmap resizedBitmap;
    Button pubblica;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    //elementi per i puntini sotto le immagini scorrevoli
    LinearLayout sliderDotspanel;
    private int conteggio_foto;
    private ImageView[] dots;

    Elemento elemento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a16_layout);

        viewPager = (ViewPager) findViewById(R.id.viewPager_Crea_recensione);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);
        titolo = (TextView) findViewById(R.id.titolo_articolo_da_recensire);
        stelle = (RatingBar) findViewById(R.id.ratingBarCreaRecensione);
        editTitolo = (EditText) findViewById(R.id.inserisci_titolo_crea_recensione);
        descrizione = (EditText) findViewById(R.id.inserisci_descrizione_crea_recensione);
        allega = (Button) findViewById(R.id.bottone_allega_immagine);
        pubblica = (Button) findViewById(R.id.bottone_pubblica_recensione);
        immagineAllega = (ImageView) findViewById(R.id.immagine_crea_recensione);

        elemento = (Elemento) Store.get("elemento");
        //setto il titolo con il nome dell'elemento
        titolo.setText(elemento.getNome());


        //metto un listener per lo scorrimento del testo nell'inserimento descrizione
        descrizione.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(!((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP));
                return false;
            }
        });

        //conto il numero di foto dell'elemento e instanzio il vettore di immagini
        conteggio_foto = elemento.getFotos().size();
        viewPager.setAdapter(new ScrollImagesAdapter(ctx, elemento.getFotos()));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dots = new ImageView[conteggio_foto];
        for(int i = 0; i < conteggio_foto; i++) {
            dots[i] = new ImageView(ctx);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
            params.setMargins(12, 0, 12, 0);
            sliderDotspanel.addView(dots[i], params);
        }
        //setto il primo punto attivo
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        //chiamato quando la foto cambia o è scrollata
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //metto tutti i punti vuoti
                for(int i = 0; i < conteggio_foto; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }
                //metto quello giusto selezionato
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        //creo la barra dei punti sotto allo scorrimento delle foto


        //setto un timer temporizzato che fa girare in automatico le foto
        Timer timer = new Timer();
        //2o param:tempo entro quale partirà il timer
        //30 param:tempo tra un intervallo e l'altro
        timer.scheduleAtFixedRate(new MyTimerTask(), 5000, 7000);

        //listener che manda al server i dati della recensione fatta
        pubblica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //se la lunghezza di titolo e descrizione vanno bene invio tutto al server
                if(editTitolo.getText().toString().length() >= 4 && descrizione.getText().toString().length() >= 20) {
                    Utility.hideSoftKeyboard(A16_CreaRecensione.this);
                    startCaricamento(0, getResources().getString(R.string.caricamento_recensione));
                    JSONObject req = new JSONObject();
                    //richiesta asincrona per controllo se utente ha già recensito elemento
                    try {
                        req.put("id_elemento", elemento.getId());
                        req.put("token", getToken());
                        req.put("path", "just_recensito");

                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(!a.getString("status").equals("ERROR")) {
                                        if(a.getString("result").equals("si")) {
                                            //la recensione è già stata caricata dall'utente
                                            stopCaricamento(0);
                                            Utility.hideSoftKeyboard(A16_CreaRecensione.this);
                                            errorBar(getResources().getString(R.string.recensione_gia_fatta), 3000);
                                        } else {
                                            //ok posso caricare la recensione
                                            JSONObject req = new JSONObject();
                                            //richiesta asincrona per invio della recensione al server
                                            try {
                                                req.put("id_elemento", elemento.getId());
                                                req.put("token", getToken());
                                                //controllo se è stata inserita o no una immagine
                                                if(resizedBitmap != null)
                                                    req.put("foto", toEncodedString(resizedBitmap));
                                                else
                                                    req.put("foto", "no_foto");
                                                req.put("titolo", editTitolo.getText().toString());
                                                req.put("testo", descrizione.getText().toString());
                                                req.put("voto", String.valueOf(stelle.getRating()));
                                                req.put("path", "add_recensione");

                                                new Request(new RequestCallback() {
                                                    @Override
                                                    public void inTheEnd(JSONObject a) {
                                                        try {
                                                            if(!a.getString("status").equals("ERROR")) {
                                                                //ricevo la recensione dal server per pssarla alla prossima activity e salvare la foto
                                                                Recensione recensione = new Recensione(a.getJSONObject("result"));
                                                                Store.add("recensione", recensione);
                                                                Images.addImage(recensione.getFotopath(), resizedBitmap);
                                                                getUser().fattaRecensione();
                                                                Intent i = new Intent(ctx, A24_Recensione.class);
                                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        } catch(JSONException e) {
                                                            errorBar(getResources().getString(R.string.Server_error), 2000);
                                                            e.printStackTrace();
                                                        } finally {
                                                            stopCaricamento(200);
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
                                } catch(JSONException e) {
                                    errorBar(getResources().getString(R.string.errore_server), 3000);
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
                } else {
                    //se le lunghezze non vanno bene mostro gli errori e setto i listener per la loro scomparsa
                    if(editTitolo.getText().toString().length() < 4) {
                        erroreTitolo = (TextView) findViewById(R.id.errore_titolo);
                        erroreTitolo.setVisibility(View.VISIBLE);
                        cancellaMessaggioErroreTitoloNonValido();
                    }
                    if(descrizione.getText().toString().length() < 30) {
                        erroreDescrizione = (TextView) findViewById(R.id.errore_descrizione);
                        erroreDescrizione.setVisibility(View.VISIBLE);
                        cancellaMessaggioErroreDescrizioneNonValida();
                    }
                    stopCaricamento(0);
                }
            }
        });

        allega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });
        immagineAllega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(A16_CreaRecensione.this);
                builder.setCancelable(true);
                builder.setPositiveButton(
                        getResources().getString(R.string.Yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                resizedBitmap = null;
                                immagineAllega.setImageBitmap(null);
                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton(getResources().getString(R.string.No),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.setMessage(getResources().getString(R.string.Delete_element));
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResultImage(Bitmap bitmap) {
        resizedBitmap = bitmap;
        immagineAllega.setImageBitmap(resizedBitmap);
    }

    //timer per lo scorrimento automatico delle foto
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem() == conteggio_foto - 1)
                        viewPager.setCurrentItem(0);
                    else
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            });

        }

    }


    public void cancellaMessaggioErroreTitoloNonValido() {
        //listener che ascoltano il cambio del testo
        editTitolo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                erroreTitolo.setVisibility(View.GONE);
            }
        });
    }

    public void cancellaMessaggioErroreDescrizioneNonValida() {
        //listener che ascoltano il cambio del testo
        descrizione.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                erroreDescrizione.setVisibility(View.GONE);
            }
        });
    }
}