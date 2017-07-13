package com.example.dounn.menutendina;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.dounn.menutendina.Database.Impostazioni;
import com.example.dounn.menutendina.Utility.CallBack;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class A6_Impostazioni extends LoggedActivity {

    Switch nuovaRecensioneUtenteCheSeguo;
    Switch nuovaRecensioneOggettoCheSeguo;
    Switch nuovoVotoMiaRecensione;
    Switch nuovaRispostaMiaDomanda;
    Switch miaMigliorRisposta;

    Impostazioni impostazioni;

    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a6_layout);
        navigationView.getMenu().getItem(pref.getInt("nav_settings", 0)).setChecked(true);


        nuovaRecensioneUtenteCheSeguo = (Switch) findViewById(R.id.simpleSwitch1);
        nuovaRecensioneOggettoCheSeguo = (Switch) findViewById(R.id.simpleSwitch2);
        nuovoVotoMiaRecensione = (Switch) findViewById(R.id.simpleSwitch3);
        nuovaRispostaMiaDomanda = (Switch) findViewById(R.id.simpleSwitch4);
        miaMigliorRisposta = (Switch) findViewById(R.id.simpleSwitch5);

        nuovaRecensioneUtenteCheSeguo.setText(getResources().getString(R.string.n1));
        nuovaRecensioneOggettoCheSeguo.setText(getResources().getString(R.string.n2));
        nuovoVotoMiaRecensione.setText(getResources().getString(R.string.n3));
        nuovaRispostaMiaDomanda.setText(getResources().getString(R.string.n4));
        miaMigliorRisposta.setText(getResources().getString(R.string.n5));

        save = (Button) findViewById(R.id.save_impostazioni);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCaricamento(200, "Saving settings");
                JSONObject req = new JSONObject();
                try {
                    req.put("path", "nuove_impostazioni");
                    req.put("NuovaRecensioneUtenteCheSeguo", nuovaRecensioneUtenteCheSeguo.isChecked() ? 1 : 0);
                    req.put("NuovaRecensioneOggettoCheSeguo", nuovaRecensioneOggettoCheSeguo.isChecked() ? 1 : 0);
                    req.put("NuovoVotoMiaRecensione", nuovoVotoMiaRecensione.isChecked() ? 1 : 0);
                    req.put("NuovaRispostaMiaDomanda", nuovaRispostaMiaDomanda.isChecked() ? 1 : 0);
                    req.put("MiaMigliorRisposta", miaMigliorRisposta.isChecked() ? 1 : 0);
                    req.put("token", getToken());

                } catch(JSONException e) {
                }

                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        stopCaricamento(200);
                        successBar(getResources().getString(R.string.Settings_Saved), 3000);
                    }

                    @Override
                    public void noInternetConnection() {

                    }
                }).execute(req);
            }
        });

        enableOnScrollDownAction();
        update();
    }

    @Override
    public void onScrollDownAction() {
        super.onScrollDownAction();
        startCaricamento(0, getResources().getString(R.string.Updating_data));
        updateUser(new CallBack() {
            @Override
            public void inTheEnd(Object o) {
                stopCaricamento(100);
                update();
            }
        });
    }

    private void update() {
        impostazioni = getUser().getImpostazioni();
        nuovaRecensioneUtenteCheSeguo.setChecked(impostazioni.isNuovaRecensioneUtenteCheSeguo());
        nuovaRecensioneOggettoCheSeguo.setChecked(impostazioni.isNuovaRecensioneOggettoCheSeguo());
        nuovoVotoMiaRecensione.setChecked(impostazioni.isNuovoVotoMiaRecensione());
        nuovaRispostaMiaDomanda.setChecked(impostazioni.isNuovaRispostaMiaDomanda());
        miaMigliorRisposta.setChecked(impostazioni.isMiaMigliorRisposta());
    }
}