package com.example.dounn.menutendina.Database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 12/06/2017.
 */

public class Level {
    private int max_dom;
    private int points_up;
    private int max_rec;
    private int max_voti;
    private int livello;
    private int punteggio;
    private int points_down;

    public Level(JSONObject level) throws JSONException {
        max_dom = level.getInt("max_dom");
        points_up = level.getInt("points_up");
        max_rec = level.getInt("max_rec");
        max_voti = level.getInt("max_voti");
        livello = level.getInt("livello");
        punteggio = level.getInt("punteggio");
        points_down = level.getInt("points_down");

    }

    public int getMaxDom() {
        return max_dom;
    }

    public int getPointsUp() {
        return points_up;
    }

    public int getMaxRec() {
        return max_rec;
    }

    public int getMaxVoti() {
        return max_voti;
    }

    public int getLivello() {
        return livello;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public int getPointsDown() {
        return points_down;
    }
}
