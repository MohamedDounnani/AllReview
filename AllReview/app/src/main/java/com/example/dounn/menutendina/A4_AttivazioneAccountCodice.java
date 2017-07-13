package com.example.dounn.menutendina;

/**
 * Created by dounn on 31/05/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;


public class A4_AttivazioneAccountCodice extends SuperActivity {

    private EditText inserimentoCodice;
    private TextView erroreInvioCodice;
    private TextView erroreReinviaEmail;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a4_layout);

        builder = new AlertDialog.Builder(this, R.style.Dialog_theme);
        builder.setCancelable(true);
        builder.setNeutralButton(getResources().getString(R.string.Procedi),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ctx, A10_HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

        inserimentoCodice = (EditText) findViewById(R.id.inserisci_codice);
        Button inviaCodice = (Button) findViewById(R.id.bottone_invia_codice);
        TextView inviaNuovamenteMail = (TextView) findViewById(R.id.testo_reinvia_email);


        //listener per l'invio del codice
        inviaCodice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogged()) {
                    //creo oggetto JSON con dentro il codice inserito dall'utente
                    JSONObject req = new JSONObject();
                    try {
                        //inserisco nella richiesta il token in sessione e il codice inviatomi per e-mail
                        req.put("code", inserimentoCodice.getText());
                        req.put("token", getToken());
                        req.put("path", "attiva");

                        new Request(new RequestCallback() {

                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(a != null) {
                                        if(!a.getString("status").equals("ERROR")) {
                                            //inserisco il token nella sessione
                                            attivaUser();
                                            builder.setMessage(getResources().getString(R.string.registrazione_succ));
                                            alertDialog = builder.create();
                                            alertDialog.show();
                                        } else {
                                            //se ci sono stati problemi mostro la stringa di errore
                                            erroreInvioCodice = (TextView) findViewById(R.id.errore_conferma_codice);
                                            erroreInvioCodice.setVisibility(View.VISIBLE);
                                            //attivo il listener per cancellare l'errore
                                            cancellaMessaggioErroreCodice();
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
                        }).execute(req);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //se ci sono stati problemi con il token mostro un errore
                    erroreInvioCodice = (TextView) findViewById(R.id.errore_conferma_codice);
                    erroreInvioCodice.setVisibility(View.VISIBLE);
                    //attivo il listener per cancellare l'errore
                    cancellaMessaggioErroreCodice();
                }
            }
        });

        //richiesta reinvio email
        inviaNuovamenteMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject req = new JSONObject();
                try {
                    //inserisco nella richiesta il token in sessione
                    req.put("token", getToken());
                    req.put("path", "reinvio_email");


                    new Request(new RequestCallback() {
                        @Override
                        public void inTheEnd(JSONObject a) {
                            try {
                                if(a != null) {
                                    //Log.d("Risultati reinviao email:", a.toString());
                                    //se non ci sono stati errori mostro un toast
                                    if(!a.getString("status").equals("ERROR")) {
                                        successBar(getResources().getString(R.string.Success_email), 3000);
                                    } else {
                                        //altrimenti faccio comparire messaggio di errore
                                        erroreReinviaEmail = (TextView) findViewById(R.id.errore_reinvio_email);
                                        erroreReinviaEmail.setVisibility(View.VISIBLE);
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
                    }).execute(req);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void cancellaMessaggioErroreCodice() {

        //listener che ascoltano il cambio del testo
        inserimentoCodice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                erroreInvioCodice.setVisibility(View.GONE);
            }
        });
    }
}