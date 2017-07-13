package com.example.dounn.menutendina.Utility;


import android.util.Log;

import org.json.JSONObject;

public abstract class RequestCallback {

    public void checkConnection(JSONObject a) {
        if(a == null) noInternetConnection();
        else {
            Log.e("Server Response",a.toString());
            inTheEnd(a);
        }
    }

    public abstract void inTheEnd(JSONObject a);
    public abstract void noInternetConnection();
}
