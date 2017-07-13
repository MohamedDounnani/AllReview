package com.example.dounn.menutendina;

import android.os.Bundle;

import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ZoomOnTouch;

/**
 * Created by Enrico on 12/06/2017.
 * <p>
 * Activity vuota a parte per mostrare solo l'immagine ingrandita.
 * Richiamerà Zoom_On_Touch che permetterà di zoomare l'immagine.
 */

public class A31_FullScreenImage extends SuperActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //istanzio la classe per zoomare
        ZoomOnTouch zoomOnTouch = new ZoomOnTouch(this);

        //prendo la foto dalle preferenze e la converto
        zoomOnTouch.setFotoPath((String) Store.get("zoom_foto"));

        //setto lo zoom massimo consentito
        zoomOnTouch.setMaxZoom(10f);
        setContentView(zoomOnTouch);
    }
}
