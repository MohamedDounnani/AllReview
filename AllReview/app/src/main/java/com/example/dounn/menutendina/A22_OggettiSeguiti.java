package com.example.dounn.menutendina;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.dounn.menutendina.Adapters.ElementoAdapter;
import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Utility.GenericRequest;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class A22_OggettiSeguiti extends LoggedActivity {

    private RecyclerView recyclerView;
    private ArrayList<Elemento> elementi;
    private ElementoAdapter adapter;
    private LinearLayoutManager llm = new LinearLayoutManager(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a22_layout);
        navigationView.getMenu().getItem(pref.getInt("nav_oggetti_seguo",0)).setChecked(true);

        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_seguiti);
        recyclerView.setLayoutManager(llm);
        elementi = new ArrayList<>();
        Intent i = new Intent(ctx,A27_PaginaElemento.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        adapter = new ElementoAdapter(ctx, elementi,i);
        recyclerView.setAdapter(adapter);

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
                    req.put("id_elemento", elementi.get(position).getId());
                    req.put("token", getToken());
                    req.put("path", "remove_preferito");
                    elementi.remove(position);
                    adapter.notifyDataSetChanged();
                    new GenericRequest().execute(req);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(200);

            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
            req.put("path", "preferiti");
            req.put("token", getToken());
            startCaricamento(0, getResources().getString(R.string.Loading));
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(a.getString("status").equals("OK")) {
                            JSONArray result = a.getJSONArray("result");

                            elementi = new ArrayList<>();
                            for(int i = 0; i < result.length(); i++) {
                                elementi.add(new Elemento(result.getJSONObject(i)));
                            }
                            adapter.update(elementi);
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