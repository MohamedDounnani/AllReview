package com.example.dounn.menutendina;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.dounn.menutendina.Adapters.UtenteAdapter;
import com.example.dounn.menutendina.Database.Utente;
import com.example.dounn.menutendina.Utility.GenericRequest;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class A19_PersoneSeguite extends LoggedActivity {

    RecyclerView recyclerSeguiti;
    ArrayList<Utente> utenti;
    UtenteAdapter adapter;
    LinearLayoutManager llm = new LinearLayoutManager(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a19_layout);
        navigationView.getMenu().getItem(pref.getInt("nav_persone_seguo",0)).setChecked(true);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerSeguiti = (RecyclerView) findViewById(R.id.recycler_seguiti);
        recyclerSeguiti.setLayoutManager(llm);
        utenti = new ArrayList<>();
        adapter = new UtenteAdapter(ctx, utenti);
        recyclerSeguiti.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                JSONObject req = new JSONObject();
                try {
                    req.put("id_utente", utenti.get(position).getId());
                    req.put("token", getToken());
                    req.put("path", "remove_seguito");
                    utenti.remove(position);
                    adapter.update(utenti);
                    new GenericRequest().execute(req);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(200);

            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerSeguiti);
        enableOnScrollDownAction();
        onScrollDownAction();
    }


    @Override
    protected void onResume() {
        super.onResume();
        onScrollDownAction();
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();

        JSONObject req = new JSONObject();
        try {
            req.put("path", "seguiti");
            req.put("token", getToken());
            startCaricamento(0, "Loading ...");
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(a.getString("status").equals("OK")) {
                            JSONArray result = a.getJSONArray("result");
                            utenti = new ArrayList<>();
                            for(int i = 0; i < result.length(); i++) {
                                utenti.add(new Utente(result.getJSONObject(i)));
                            }
                            adapter.update(utenti);
                        } else throw new JSONException("Parsing error " + a.toString());
                    } catch(JSONException e) {
                        e.printStackTrace();
                    } finally {
                        stopCaricamento(0);
                    }
                }

                @Override
                public void noInternetConnection() {
                    stopCaricamento(0);
                    noInternetErrorBar();
                }
            }).execute(req);


        } catch(JSONException e) {
            e.printStackTrace();
            stopCaricamento(0);
        }

    }
}
