package com.example.dounn.menutendina.Utility;

/**
 * Created by lucadiliello on 08/06/2017.
 */

public class NoInternetConnectionException extends Exception {

    @Override
    public String getMessage() {
        return "No internet connection";
    }
}
