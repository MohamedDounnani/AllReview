package com.example.dounn.menutendina;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class A30_InserisciDomanda extends LoggedActivity {

    Button invia;
    EditText domanda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a30_layout);

        invia = (Button) findViewById(R.id.invia_domanda);
        domanda = (EditText) findViewById(R.id.testo_domanda);
        final Elemento elemento = (Elemento) Store.get("elemento");

        domanda.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(!((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP));
                return false;
            }
        });

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(domanda.getText().length() < 40) {
                    errorBar(getResources().getString(R.string.Char_req), 2000);
                }
                JSONObject req = new JSONObject();
                try {
                    req.put("path", "add_domanda");
                    req.put("id_elemento", elemento.getId());
                    req.put("token", getToken());
                    req.put("testo", domanda.getText().toString());
                    startCaricamento(100, getResources().getString(R.string.Add_question));
                    new Request(new RequestCallback() {
                        @Override
                        public void inTheEnd(JSONObject a) {
                            stopCaricamento(200);
                            getUser().fattaDomanda();
                            finish();
                        }

                        @Override
                        public void noInternetConnection() {
                            noInternetErrorBar();
                        }
                    }).execute(req);

                } catch(JSONException e) {
                    errorBar(getString(R.string.errore_server), 2000);
                }
            }
        });

    }
}