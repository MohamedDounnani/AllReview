package com.example.dounn.menutendina.Database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lucadiliello on 03/07/2017.
 * Modified by lucadiliello on 03/07/2017
 */

public class SuperDB {

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    static SimpleDateFormat completeFormatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault());
    static SimpleDateFormat simpleFormatter = new SimpleDateFormat("MM-dd", Locale.getDefault());

    int data;

    SuperDB(int data) {
        this.data = data;
    }

    public String reduceData() {
        return formatter.format(new Date(data * 1000L));
    }

    public String completeData() {
        return completeFormatter.format(new Date(data * 1000L));
    }

}
