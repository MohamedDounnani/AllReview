package com.example.dounn.menutendina.Database;

import android.util.Log;

import com.example.dounn.menutendina.Utility.Constants;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by lucadiliello on 20/06/2017.
 */

public class AutoComplete implements Iterable<String> {
    private static ArrayList<String> nomiElementi;
    private static long vita;

    public AutoComplete() {
        nomiElementi = new ArrayList<>();
        vita = System.currentTimeMillis();
        update();
    }

    public synchronized ArrayList<String> getNames(){
        //se son passati più di 30 secondi, aggiorno
        if(System.currentTimeMillis() - 1000 * Constants.autoCompleteUpdateTime > vita){
            vita = System.currentTimeMillis();
            update();
        }
        return nomiElementi;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < nomiElementi.size();
            }

            @Override
            public String next() {
                return nomiElementi.get(index++);
            }
        };
    }

    private void update() {
        JSONObject req = new JSONObject();
        try {
            req.put("path", "autocomplete");
            //chiamo la richiesta asincrona al database
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    //controllo lo stato della richiesta

                    try {
                        if(a.getString("status").equals("OK")) {
                            nomiElementi = new ArrayList<>();
                            JSONArray autocomplete = a.getJSONArray("result");
                            for(int i = 0; i < autocomplete.length(); i++) {
                                nomiElementi.add(autocomplete.get(i).toString());
                            }
                        } else {
                            Log.e("Superato", "Qualcosa è andato storto nell'inserimento elemento");
                        }
                    } catch(JSONException e) {
                        Log.e("Superato json login", " Oggetto json:" + a.toString() + "\nErrore:\n" + e.toString());
                    } catch(NullPointerException e) {
                        Log.e("Connessione", "Non connesso");
                    }
                }
                @Override
                public void noInternetConnection() {
                }
            }).execute(req);
        } catch(JSONException e) {
        }
    }
}
