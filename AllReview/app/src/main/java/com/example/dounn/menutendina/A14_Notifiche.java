package com.example.dounn.menutendina;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.dounn.menutendina.Adapters.NotificheAdapter;
import com.example.dounn.menutendina.Database.Notifica;
import com.example.dounn.menutendina.Utility.GenericRequest;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.example.dounn.menutendina.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class A14_Notifiche extends SuperActivity {

    ArrayList<Notifica> notifiche = new ArrayList<>();
    private NotificheAdapter mAdapter;
    private RecyclerView mRecyclerView;

    ItemTouchHelper itemTouchHelper;

    SuperActivity support = this;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a14_layout);
        setMenuChecked("nav_notifiche");

        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mAdapter = new NotificheAdapter(ctx, notifiche);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                JSONObject req = new JSONObject();
                try {
                    req.put("path", "remove_notifica");
                    req.put("id_notifica", notifiche.get(position).getId());
                    req.put("token", getToken());
                    notifiche.remove(position);
                    mAdapter.notifyDataSetChanged();
                    new GenericRequest().execute(req);
                    Utility.notifiche.getNotificationNumber(getToken());
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(200);

            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        enableOnScrollDownAction();
        downloadData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        onScrollDownAction();
    }


    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        downloadData();
    }

    void downloadData() {
        startCaricamento(300, getResources().getString(R.string.Down_notifiche));
        JSONObject req = new JSONObject();
        try {
            req.put("path", "notifica");
            req.put("token", getToken());
        } catch(JSONException e) {
            e.printStackTrace();
        }

        new Request(new RequestCallback() {
            @Override
            public void inTheEnd(JSONObject a) {
                try {
                    if(a.getString("status").equals("OK")) {
                        JSONArray arrayNotifiche = a.getJSONArray("result");
                        notifiche = new ArrayList<>();
                        for(int i = 0; i < arrayNotifiche.length(); i++)
                            notifiche.add(new Notifica(arrayNotifiche.getJSONObject(i),ctx));
                        mAdapter.update(notifiche);
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                stopCaricamento(200);
            }

            @Override
            public void noInternetConnection() {
                noInternetErrorBar();
                stopCaricamento(200);
            }
        }).execute(req);
    }
}