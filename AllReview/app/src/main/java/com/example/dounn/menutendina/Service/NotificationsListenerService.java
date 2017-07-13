package com.example.dounn.menutendina.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.dounn.menutendina.A14_Notifiche;
import com.example.dounn.menutendina.Database.Notifica;
import com.example.dounn.menutendina.Database.UtenteLogged;
import com.example.dounn.menutendina.R;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 02/07/2017.
 */

public class NotificationsListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);

        try {
            final Context ctx = this;
            SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
            String json = pref.getString("user", "");
            if(json.equals("")) throw new Exception("prefError");
            Gson gson = new Gson();
            final UtenteLogged user = gson.fromJson(json, UtenteLogged.class);

            final int id_not = Integer.valueOf(bundle.getString("id_notifica"));
            final int id_utente = Integer.valueOf(bundle.getString("id_utente"));

            if(user.getId() != id_utente) return;

            try {
                JSONObject req = new JSONObject();
                req.put("token", user.getToken());
                req.put("path", "notifica_from_id");
                req.put("id_notifica", id_not);
                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        try {
                            if(a.getString("status").equals("OK")) {
                                Notifica notifica = new Notifica(a.getJSONObject("result"), ctx);

                                Intent intent = new Intent(ctx, A14_Notifiche.class);
                                PendingIntent intent2 = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(ctx)
                                                .setSmallIcon(R.mipmap.logo2)
                                                .setContentTitle("AllReview")
                                                .setWhen(System.currentTimeMillis())
                                                .setContentText(notifica.toString())
                                                .setContentIntent(intent2)
                                                .setTicker("All Review Notification")
                                                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
                                mBuilder.setAutoCancel(true);

                                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(1, mBuilder.build());

                            }

                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void noInternetConnection() {

                    }
                }).execute(req);
            } catch(JSONException e) {
                e.printStackTrace();
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}