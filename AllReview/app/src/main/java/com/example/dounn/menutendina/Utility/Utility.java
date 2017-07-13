package com.example.dounn.menutendina.Utility;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.dounn.menutendina.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by lucadiliello on 31/05/2017.
 */

public class Utility {

    public static Context context;
    public static Notifiche notifiche = new Notifiche();

    public static void setContext(Context c) {
        context = c;
    }

    public static void addFotoJSON(JSONObject r, ArrayList<Bitmap> a) throws JSONException {
        int i;
        for(i = 0; i < a.size(); i++) {
            Bitmap bt = a.get(i);
            r.put("foto" + i, Images.toEncodedString(bt));
        }
        r.put("foto_number", i);
    }

    public static String generate_sha(String a) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String text = "This is some text";

        md.update(a.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();
        a = String.format("%064x", new java.math.BigInteger(1, digest));
        return a.substring(0, 12);
    }

    //abbassa la tastiera quando è chiamato il metodo
    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static String capitalize(String t) {
        String[] parts = t.split("\\s+");
        for(String a : parts) {
            if(a.length() > 1) {
                a = Character.toUpperCase(a.charAt(0)) + a.substring(1);
            } else {
                a = String.valueOf(Character.toUpperCase(a.charAt(0)));
            }
        }
        return TextUtils.join(" ", parts);
    }

    public static SimpleDateFormat format_grafico = new SimpleDateFormat("dd/MM", Locale.ITALIAN);

    public static void highLight(View v) {
        int exColor = v.getSolidColor();
        ObjectAnimator.ofObject(v, "backgroundColor", new ArgbEvaluator(), context.getResources().getColor(R.color.azzurrino), exColor)
                .setDuration(2000)
                .start();
    }

    public static class Notifiche {
        int numeroNotifiche = 0;
        //data dell'ultima chiamata al server per aggiornamento in secondi
        int vita = (int) (System.currentTimeMillis() / 1000);

        public int getNotificationNumber(String token) {
            //controllo se sono passati almeno 30 secondi dall'ultima chiamata al server
            if(System.currentTimeMillis() / 1000 > vita + Constants.notificaUpdateTime) {
                //resetto il tempo
                vita = (int) (System.currentTimeMillis() / 1000);

                //faccio una nuova richiesta al server tramite le nostre
                // classi Request e RequestCallBack
                JSONObject req = new JSONObject();
                try {
                    //inserisco path e token per autenticarmi sul server
                    req.put("path", "numero_notifiche");
                    req.put("token", token);
                    new Request(new RequestCallback() {
                        @Override
                        public void inTheEnd(JSONObject a) {
                            try {
                                //se lo status di ritorno è OK aggiorno il numero
                                if(a.getString("status").equals("OK")) {
                                    numeroNotifiche = a.getInt("result");
                                } else throw new JSONException("else");
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void noInternetConnection() {
                            //non gestisco l'errore di nessuna connessione
                            //semplicemente si spera che la prossima richiesta abbia successo
                        }
                    }).execute(req);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
            return numeroNotifiche;
        }
    }

    public static String catchCategoria(String Categoria) {
        String traduzione;
        switch (Categoria) {
            case "Abbigliamento":
                traduzione = context.getResources().getString(R.string.Clothing);
                break;
            case "Arte & Antiquariato":
                traduzione = context.getResources().getString(R.string.ArtAndAntiques);
                break;
            case "Auto e Moto":
                traduzione = context.getResources().getString(R.string.CarsAndMotorcycles);
                break;
            case "Bellezza & Salute":
                traduzione =  context.getResources().getString(R.string.BeautyAndHealth);
                break;
            case "Biglietti ed eventi":
                traduzione = context.getResources().getString(R.string.TicketAndEvents);
                break;
            case "Casa, Arredamento & Bricolage":
                traduzione = context.getResources().getString(R.string.HomeFurnitureAndDIY);
                break;
            case "Collezionismo":
                traduzione = context.getResources().getString(R.string.Collectibles);
                break;
            case "Commercio ed industria":
                traduzione = context.getResources().getString(R.string.TradeAndIndustry);
                break;
            case "Elettrodomestici":
                traduzione = context.getResources().getString(R.string.HomeApplication);
                break;
            case "Film, Fotografia e Video":
                traduzione = context.getResources().getString(R.string.FilmPhotographyAndVideo);
                break;
            case "Giocattoli & Modellismo":
                traduzione = context.getResources().getString(R.string.ToysAndModelling);
                break;
            case "Libri, Fumetti e Lettori elettronici":
                traduzione = context.getResources().getString(R.string.BooksComicsAndEletronic);
                break;
            case "Informatica e Mondo Online":
                traduzione = context.getResources().getString(R.string.ComputerScience);
                break;
            case "Orologi e Gioielli":
                traduzione = context.getResources().getString(R.string.Watches);
                break;
            case "Attrezzatura Sportiva":
                traduzione =  context.getResources().getString(R.string.Sport);
                break;
            case "Telefonia":
                traduzione =  context.getResources().getString(R.string.Smartphone);
                break;
            case "Televisori":
                traduzione =  context.getResources().getString(R.string.Televisions);
                break;
            case "HiFi, Amplificatori e Casse":
                traduzione =  context.getResources().getString(R.string.HiFi);
                break;
            case "Videogiochi e Console":
                traduzione =  context.getResources().getString(R.string.Games);
                break;
            case "Cucina":
                traduzione =  context.getResources().getString(R.string.Kitchen);
                break;
            case "Elettronica e componentistica":
                traduzione = context.getResources().getString(R.string.Eletronics);
                break;
            case "Hotel & Alloggi":
                traduzione = context.getResources().getString(R.string.Hotels);
                break;
            case "Infanzia":
                traduzione = context.getResources().getString(R.string.Child);
                break;
            case "Scuola, Università ed Ufficio":
                traduzione = context.getResources().getString(R.string.School);
                break;
            case "Computer & Accessori":
                traduzione = context.getResources().getString(R.string.Computer);
                break;
            default:
                traduzione = "Default";
        }
        return traduzione;

    }
}
