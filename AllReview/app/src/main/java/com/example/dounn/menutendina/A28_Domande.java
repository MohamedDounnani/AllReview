package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.dounn.menutendina.Adapters.DomandeFilterAdapter;
import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.Database.SuperDB;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dounn on 16/06/2017.
 */

public class A28_Domande extends SuperActivity {

    private RecyclerView recyclerView;
    private EditText etSearch;
    Button nuovaDomanda;
    Elemento elemento;
    private DomandeFilterAdapter adapter;
    LinearLayout sezioneDomanda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a28_layout);

        elemento = (Elemento) Store.get("elemento");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        nuovaDomanda = (Button) findViewById(R.id.nuova_domanda);
        sezioneDomanda = (LinearLayout) findViewById(R.id.sezione_domanda);
        etSearch = (EditText) findViewById(R.id.etSearch);

        adapter = new DomandeFilterAdapter(ctx, elemento.getDomande());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        // Add Text Change Listener to EditText
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if(!isActivated() || !getUser().canDomanda()) {
            setGone(sezioneDomanda);
        }

        nuovaDomanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store.add("elemento", elemento);
                startActivity(new Intent(ctx, A30_InserisciDomanda.class));
            }
        });

        enableOnScrollDownAction();
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        downloadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isActivated() || !getUser().canDomanda()) {
            nuovaDomanda.setVisibility(View.GONE);
        }
    }

    private void downloadData() {
        startCaricamento(200, getResources().getString(R.string.aggiorno));
        JSONObject req = new JSONObject();
        try {
            req.put("path", "elemento");
            req.put("id_elemento", elemento.getId());

            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(a.getString("status").equals("OK")) {
                            elemento = new Elemento(a.getJSONObject("result"));
                            Store.add("elemento", elemento);
                            adapter.update(elemento.getDomande());
                            etSearch.setText("");
                            stopCaricamento(200);
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                        stopCaricamento(100);
                    }
                }

                @Override
                public void noInternetConnection() {
                    noInternetErrorBar();
                    stopCaricamento(0);
                }
            }).execute(req);
        } catch(JSONException e) {
            errorBar(getResources().getString(R.string.Error), 2000);
        }
    }

}