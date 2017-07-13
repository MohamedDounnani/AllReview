package com.example.dounn.menutendina;

import android.app.Application;

import com.example.dounn.menutendina.Utility.Images;
import com.example.dounn.menutendina.Utility.Utility;

/**
 * Created by lucadiliello on 28/06/2017.
 */

public class AllReview extends Application {
    public AllReview(){
        Images.setContext(this);
        Utility.setContext(this);
        Utility.notifiche = new Utility.Notifiche();
    }
}
