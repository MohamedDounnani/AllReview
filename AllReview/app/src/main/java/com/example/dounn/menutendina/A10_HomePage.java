package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.dounn.menutendina.Adapters.SuggeritiPreferitiRecentiAdapter;
import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/*

1.Ho fatto in modo che si le richieste al server siano sempre 5, suggeriti ecc most seen and best
i primi che trova con degli elementi li sceglie, da sempre la precedenza a suggeriti recenti e piu visti
se non trova uno di questi allora sceglie most seen e best.

2.Utente anonimo: Avrà un layout non ancora completo per invogliarlo a registrarsi
3.Utente non registrato: Avrà come liste: most seen and best , perchè troverà sugg ecc vuoti
4.Utente registrato: segue la regola scritta al punto 1.
 */
public class A10_HomePage extends SuperActivity {

    //Lista che prende tutti gli elementi recenti
    /*
    dichiato tutti i componenti di cui ho bisogno
     */
    ArrayList<Elemento> elementi;
    //le liste della homepage
    RecyclerView suggeriti;
    RecyclerView preferiti;
    RecyclerView vistiRecente;
    //i nomi delle liste
    TextView itemTitle1;
    TextView itemTitle2;
    TextView itemTitle3;

    ArrayList<RecyclerView> arrayListeLayout;
    ArrayList<TextView> arrayText;
    ArrayList<String> nomi;
    //Hashtable per mappare i nomi delle richieste in nomi liste suggeriti -> Suggeriti
    Hashtable<String, String> tabellaNomi;
    int count;

    //massimo di list che puo prendere
    int NUMERO_LISTE = 3;
    int NUMERO_ELEMENTI = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.a10_layout);

        navigationView.getMenu().getItem(0).setChecked(true);
        creazioneHashTable();
        arrayListeLayout = new ArrayList<>();
        //CREO LA LISTA RECENTI ECC...
        if(!isLogged()) {

            inizializzaAccountNonAttivatoAnonimo();

        } else {
            if(!isActivated()) {
                inizializzaAccountNonAttivatoAnonimo();
            } else {
                inizializzaAccountAttivato();
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        createLista();
    }


    public void creazioneHashTable() {
        tabellaNomi = new Hashtable<>();
        tabellaNomi.put("most_seen", "Most Seen");
        tabellaNomi.put("best_of", "Best Of");

        tabellaNomi.put("suggeriti", getResources().getString(R.string.suggeriti));
        tabellaNomi.put("preferiti", getResources().getString(R.string.preferiti));
        tabellaNomi.put("recenti", getResources().getString(R.string.recenti));
    }

    public void inizializzaAccountNonAttivatoAnonimo() {
        nomi = new ArrayList<>();
        arrayText = new ArrayList<>();
        nomi.add("most_seen");
        nomi.add("best_of");
        itemTitle1 = (TextView) findViewById(R.id.itemTitle);
        arrayText.add(itemTitle1);
        itemTitle2 = (TextView) findViewById(R.id.itemTitle2);
        arrayText.add(itemTitle2);
        suggeriti = (RecyclerView) findViewById(R.id.list1);
        arrayListeLayout.add(suggeriti);
        preferiti = (RecyclerView) findViewById(R.id.list2);
        arrayListeLayout.add(preferiti);
        preferiti.setItemAnimator(new DefaultItemAnimator());
        preferiti.setHasFixedSize(true);
        preferiti.setLayoutManager(new LinearLayoutManager(A10_HomePage.this, LinearLayoutManager.HORIZONTAL, false));
        suggeriti.setItemAnimator(new DefaultItemAnimator());
        suggeriti.setHasFixedSize(true);
        suggeriti.setLayoutManager(new LinearLayoutManager(A10_HomePage.this, LinearLayoutManager.HORIZONTAL, false));

    }

    public void inizializzaAccountAttivato() {
        nomi = new ArrayList<>();
        arrayText = new ArrayList<>();
        nomi.add("suggeriti");
        nomi.add("preferiti");
        nomi.add("recenti");
        nomi.add("most_seen");
        nomi.add("best_of");
        itemTitle1 = (TextView) findViewById(R.id.itemTitle);
        arrayText.add(itemTitle1);
        itemTitle2 = (TextView) findViewById(R.id.itemTitle2);
        arrayText.add(itemTitle2);
        itemTitle3 = (TextView) findViewById(R.id.itemTitle3);
        arrayText.add(itemTitle3);
        suggeriti = (RecyclerView) findViewById(R.id.list1);
        arrayListeLayout.add(suggeriti);
        preferiti = (RecyclerView) findViewById(R.id.list2);
        arrayListeLayout.add(preferiti);
        vistiRecente = (RecyclerView) findViewById(R.id.list3);
        arrayListeLayout.add(vistiRecente);
        preferiti.setItemAnimator(new DefaultItemAnimator());
        preferiti.setHasFixedSize(true);
        preferiti.setLayoutManager(new LinearLayoutManager(A10_HomePage.this, LinearLayoutManager.HORIZONTAL, false));
        suggeriti.setItemAnimator(new DefaultItemAnimator());
        suggeriti.setHasFixedSize(true);
        suggeriti.setLayoutManager(new LinearLayoutManager(A10_HomePage.this, LinearLayoutManager.HORIZONTAL, false));
        vistiRecente.setItemAnimator(new DefaultItemAnimator());
        vistiRecente.setHasFixedSize(true);
        vistiRecente.setLayoutManager(new LinearLayoutManager(A10_HomePage.this, LinearLayoutManager.HORIZONTAL, false));


    }

    //CREO LA LISTA SUGGERITI PREFERITI E VISTI DI RECENTE
    public void createLista() {
        startCaricamento(150, getResources().getString(R.string.Homepage_Loading));
        count = -1;
        for(int i = 0; i < nomi.size(); i++) {
            JSONObject req = new JSONObject();
            try {
                if(isActivated()) {
                    req.put("token", getToken());
                }
                req.put("path", nomi.get(i));
                final String names = nomi.get(i);
                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        //controllo lo stato della richiesta
                        try {
                            if(!a.getString("status").equals("ERROR")) {

                                if(a.getJSONArray("result").length() > 0) {
                                    count++;
                                    stopCaricamento(200);
                                    if(count <= NUMERO_LISTE - 1) {
                                        elementi = new ArrayList<>();
                                        for(int j = 0; j < a.getJSONArray("result").length(); j++) {
                                            //NUMERO ELEMENTI ANALIZZATO
                                            if(elementi.size() < NUMERO_ELEMENTI) {

                                                Elemento elemento = new Elemento(a.getJSONArray("result").getJSONObject(j));

                                                arrayText.get(count).setText(tabellaNomi.get(names));
                                                arrayText.get(count).setVisibility(View.VISIBLE);
                                                elementi.add(elemento);
                                            }
                                        }
                                        Intent intent = new Intent(ctx, A27_PaginaElemento.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        arrayListeLayout.get(count).setAdapter(new SuggeritiPreferitiRecentiAdapter(ctx, elementi, intent));
                                    }
                                }

                            } else {
                                errorBar(getResources().getString(R.string.errore_server),2000);
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void noInternetConnection() {
                        noInternetErrorBar();
                    }
                }).execute(req);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

    }


}