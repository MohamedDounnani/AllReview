package com.example.dounn.menutendina.Database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 12/06/2017.
 */


public class Impostazioni {
    private boolean NuovaRecensioneUtenteCheSeguo;
    private boolean NuovaRecensioneOggettoCheSeguo;
    private boolean NuovoVotoMiaRecensione;
    private boolean NuovaRispostaMiaDomanda;
    private boolean MiaMigliorRisposta;

    Impostazioni(JSONObject impostazioni) throws JSONException {
        NuovaRecensioneUtenteCheSeguo = impostazioni.getInt("NuovaRecensioneUtenteCheSeguo") > 0;
        NuovaRecensioneOggettoCheSeguo = impostazioni.getInt("NuovaRecensioneOggettoCheSeguo") > 0;
        NuovoVotoMiaRecensione = impostazioni.getInt("NuovoVotoMiaRecensione") > 0;
        NuovaRispostaMiaDomanda = impostazioni.getInt("NuovaRispostaMiaDomanda") > 0;
        MiaMigliorRisposta = impostazioni.getInt("MiaMigliorRisposta") > 0;
    }

    public boolean isNuovaRecensioneUtenteCheSeguo() {
        return NuovaRecensioneUtenteCheSeguo;
    }

    public boolean isNuovaRecensioneOggettoCheSeguo() {
        return NuovaRecensioneOggettoCheSeguo;
    }

    public boolean isNuovoVotoMiaRecensione() {
        return NuovoVotoMiaRecensione;
    }

    public boolean isNuovaRispostaMiaDomanda() {
        return NuovaRispostaMiaDomanda;
    }

    public boolean isMiaMigliorRisposta() {
        return MiaMigliorRisposta;
    }
}
