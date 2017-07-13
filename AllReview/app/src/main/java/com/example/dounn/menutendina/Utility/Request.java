package com.example.dounn.menutendina.Utility;

import org.json.JSONObject;

public class Request extends GenericRequest {

    private RequestCallback callback;

    public Request(RequestCallback ac) {
        this.callback = ac;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject){
        callback.checkConnection(jsonObject);
    }
}