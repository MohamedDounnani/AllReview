package com.example.dounn.menutendina;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dounn.menutendina.Database.Domanda;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.example.dounn.menutendina.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class A32_InserisciRisposta extends SuperActivity {

    TextView domandaTesto;
    TextView testoRisposta;
    Button inviaRisposta;
    Domanda domanda;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a32_layout);
        builder = new AlertDialog.Builder(A32_InserisciRisposta.this);
        builder.setCancelable(true);
        builder.setNeutralButton(
                getResources().getString(R.string.understand),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        domandaTesto = (TextView) findViewById(R.id.domanda_testo);
        testoRisposta = (EditText) findViewById(R.id.testo_risposta);
        inviaRisposta = (Button) findViewById(R.id.invia_risposta);

        testoRisposta.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(!((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP));
                return false;
            }
        });

        domanda = (Domanda) Store.get("domanda");
        domandaTesto.setText(domanda.getTesto());

        inviaRisposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testoRisposta.getText().length() < 20) {
                    builder.setMessage( getResources().getString(R.string.Answer_limit));
                    alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Utility.hideSoftKeyboard(A32_InserisciRisposta.this);
                    startCaricamento(0,  getResources().getString(R.string.Uploading));
                    JSONObject req = new JSONObject();
                    try {
                        req.put("id_domanda", domanda.getId());
                        req.put("testo", testoRisposta.getText());
                        req.put("token", getToken());
                        req.put("path", "add_risposta");
                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(a.getString("status").equals("OK")) {
                                        finish();
                                    } else throw new JSONException("else");
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
                        errorBar(getResources().getString(R.string.Error), 3000);
                    }
                }
            }
        });


    }
}