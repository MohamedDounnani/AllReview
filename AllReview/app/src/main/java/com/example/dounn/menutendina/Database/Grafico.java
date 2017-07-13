package com.example.dounn.menutendina.Database;

import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 12/06/2017.
 */

public class Grafico extends SuperDB {

    private DataPoint[] punti;
    private int[] date;

    Grafico(JSONArray result) throws JSONException {
        super(0);
        punti = new DataPoint[result.length() > 2 ? result.length() : 3];
        date = new int[result.length() > 2 ? result.length() : 3];

        if(result.length() > 2) {
            for(int i = 0; i < result.length(); i++) {
                JSONObject o = result.getJSONObject(i);
                punti[i] = new DataPoint(i, o.getInt("points"));
                date[i] = o.getInt("data");
            }
        } else if(result.length() > 0) {
            int i;
            JSONObject o = result.getJSONObject(0);
            for(i = 0; i < result.length(); i++) {
                o = result.getJSONObject(i);
                punti[i] = new DataPoint(i, o.getInt("points"));
                date[i] = o.getInt("data");
            }
            for(; i < punti.length; i++) {
                punti[i] = new DataPoint(i, o.getInt("points"));
                date[i] = o.getInt("data");
            }
        } else {
            for(int i = 0; i < 3; i++) {
                punti[i] = new DataPoint(i, 0);
                date[i] = (int) (System.currentTimeMillis() / 1000);
            }
        }
    }


    public DataPoint[] getPunti() {
        return punti;
    }

    public String[] getDates() {
        String[] res = new String[punti.length];
        if(res.length < 5) {

            for(int j = 0; j < res.length; j++) res[j] = "";

            res[0] = simpleFormatter.format(date[0] * 1000L);
            res[punti.length / 2] = simpleFormatter.format(date[punti.length / 2] * 1000L);
            res[punti.length - 1] = simpleFormatter.format(date[punti.length - 1] * 1000L);

            return res;
        } else {

            for(int j = 0; j < res.length; j++) res[j] = "";

            res[1] = simpleFormatter.format(date[1] * 1000L);
            res[punti.length / 2] = simpleFormatter.format(date[punti.length / 2] * 1000L);
            res[punti.length - 2] = simpleFormatter.format(date[punti.length - 2] * 1000L);

            return res;
        }
    }
}
