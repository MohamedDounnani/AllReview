package com.example.dounn.menutendina.Database;

/**
 * Created by lucadiliello on 11/06/2017.
 */

public class Riga {
    private int livello;
    private int punteggio;
    private int max_rec;
    private int max_voti;
    private int max_dom;

    public Riga(int livello, int punteggio, int max_rec, int max_voti, int max_dom) {
        this.livello = livello;
        this.punteggio = punteggio;
        this.max_rec = max_rec;
        this.max_voti = max_voti;
        this.max_dom = max_dom;
    }

    public String getLivello() {
        return String.valueOf(livello);
    }

    public String getPunteggio() {
        return String.valueOf(punteggio);
    }

    public String getMaxRec() {
        return String.valueOf(max_rec);
    }

    public String getMaxVoti() {
        return String.valueOf(max_voti);
    }

    public String getMaxDom() {
        return String.valueOf(max_dom);
    }

    @Override
    public String toString() {
        return "(" + getLivello() + "," + getPunteggio() + "," + getMaxRec() + "," + getMaxVoti() + "," + getMaxDom() + ")";
    }
}
