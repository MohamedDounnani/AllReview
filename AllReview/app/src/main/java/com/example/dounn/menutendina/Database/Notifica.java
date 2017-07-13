package com.example.dounn.menutendina.Database;

import android.content.Context;

import com.example.dounn.menutendina.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

import static com.example.dounn.menutendina.Database.Notifica.Tipo.MiaMigliorRisposta;
import static com.example.dounn.menutendina.Database.Notifica.Tipo.NuovaRecensioneOggettoCheSeguo;
import static com.example.dounn.menutendina.Database.Notifica.Tipo.NuovaRecensioneUtenteCheSeguo;
import static com.example.dounn.menutendina.Database.Notifica.Tipo.NuovaRispostaMiaDomanda;
import static com.example.dounn.menutendina.Database.Notifica.Tipo.NuovoVotoMiaRecensione;

/**
 * Created by lucadiliello on 16/06/2017.
 */

public class Notifica extends SuperDB {

    private final Context context;

    public enum Tipo {
        NuovaRecensioneUtenteCheSeguo,
        NuovaRecensioneOggettoCheSeguo,
        NuovoVotoMiaRecensione,
        NuovaRispostaMiaDomanda,
        MiaMigliorRisposta
    }

    private int id;

    private Risposta risposta;
    private Recensione recensione;
    private Voto voto;
    private Elemento elemento;
    private int id_proprietario;

    private Tipo tipo;

    public Notifica(JSONObject o, Context ctx) throws JSONException {
        super(o.getInt("data"));
        this.context = ctx;
        id = o.getInt("id");
        id_proprietario = o.getInt("id_utente");
        switch(o.getInt("tipo")) {
            case 0:
                tipo = NuovaRecensioneUtenteCheSeguo;
                recensione = new Recensione(o.getJSONObject("recensione"));
                break;
            case 1:
                tipo = NuovaRecensioneOggettoCheSeguo;
                recensione = new Recensione(o.getJSONObject("recensione"));
                break;
            case 2:
                tipo = NuovoVotoMiaRecensione;
                voto = new Voto(o.getJSONObject("voto"));
                break;
            case 3:
                tipo = NuovaRispostaMiaDomanda;
                risposta = new Risposta(o.getJSONObject("risposta"));
                elemento = new Elemento(o.getJSONObject("elemento"));
                break;
            case 4:
                tipo = MiaMigliorRisposta;
                risposta = new Risposta(o.getJSONObject("risposta"));
                elemento = new Elemento(o.getJSONObject("elemento"));
                break;
        }
    }

    public int getDataInt() {
        return data;
    }

    public int getId() {
        return id;
    }

    public int getIdProprietario() {
        return id_proprietario;
    }

    public Risposta getRisposta() {
        return risposta;
    }

    public Recensione getRecensione() {
        return recensione;
    }

    public Voto getVoto() {
        return voto;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getFoto() {
        switch(tipo) {
            case NuovaRecensioneUtenteCheSeguo:
            case NuovaRecensioneOggettoCheSeguo:
                return recensione.getFotopath();
            case NuovoVotoMiaRecensione:
                return voto.getRecensione().getFotopath();
            case NuovaRispostaMiaDomanda:
            case MiaMigliorRisposta:
                if(elemento.getFotos().size() > 0)
                    return elemento.getFotos().get(0).getPath();
            default:
                return null;
        }
    }

    public String toString() {
        String message;
        switch(getTipo()) {
            case NuovaRecensioneOggettoCheSeguo:
                message = MessageFormat.format(context.getResources().getString(R.string.nuovaRecOggetto), new String[]{getRecensione().getElemento().getNome(),getRecensione().getUtente().getUsername()});
                break;
            case NuovaRecensioneUtenteCheSeguo:
                message = MessageFormat.format(context.getResources().getString(R.string.nuovaRecUtente), new String[]{getRecensione().getUtente().getUsername(), getRecensione().getElemento().getNome()});
                break;
            case NuovoVotoMiaRecensione:
                message = MessageFormat.format(context.getResources().getString(R.string.nuovoVotoMiaRec),
                        new String[]{getVoto().getRecensione().getElemento().getNome(),getVoto().getUtente().getUsername(), getVoto().getVoto() > 0 ? context.getResources().getString(R.string.positivo) : context.getResources().getString(R.string.negativo)});
                break;
            case NuovaRispostaMiaDomanda:
                message = MessageFormat.format(context.getResources().getString(R.string.nuovaRispDom), getRisposta().getUtente().getUsername());
                break;
            case MiaMigliorRisposta:
                message = context.getResources().getString(R.string.miaRispTop);
                break;
            default:
                message = context.getResources().getString(R.string.notificaDefault);
                break;
        }
        return message;
    }
}
