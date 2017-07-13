package com.example.dounn.menutendina;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.dounn.menutendina.Adapters.TabellaAdapters;
import com.example.dounn.menutendina.Database.Riga;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class A5_InfoPunteggi extends SuperActivity {

    RecyclerView recyclerList;
    LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a5_layout);

        ArrayList<Riga> newRighe = readCsv("tabella.csv");

        //Riga particolare con Override per poter stampare l'header sullo schermo
        Riga header = new Riga(0, 0, 0, 0, 0) {
            @Override
            public String getLivello() {
                return getResources().getString(R.string.Livello);
            }

            @Override
            public String getPunteggio() {
                return getResources().getString(R.string.Punteggio);
            }

            @Override
            public String getMaxRec() {
                return getResources().getString(R.string.Recensioni_giorno);
            }

            @Override
            public String getMaxVoti() {
                return getResources().getString(R.string.Voti_giorno);
            }

            @Override
            public String getMaxDom() {
                return getResources().getString(R.string.Domande_giorno);
            }
        };

        //aggiungo l'header come primo nei dati che darò all'adapter
        newRighe.add(0, header);

        recyclerList = (RecyclerView) findViewById(R.id.recyclerList);
        recyclerList.setLayoutManager(llm);
        //creo un adapter e lo attacco a listView
        TabellaAdapters adapter = new TabellaAdapters(this, newRighe, getUser().getLevel().getLivello());

        recyclerList.setAdapter(adapter);

    }

    //leggo csv e lo metto in un arraylist di riga
    public final ArrayList<Riga> readCsv(String csvTabellaPath) {
        ArrayList<Riga> res = new ArrayList<>();

        InputStream csvStream;
        BufferedReader reader;
        try {
            csvStream = getAssets().open(csvTabellaPath);

            reader = new BufferedReader(new InputStreamReader(csvStream));

            String line;
            while((line = reader.readLine()) != null) {
                String[] RowData = line.split(",");
                int livello = Integer.parseInt(RowData[0]);
                int punteggio = Integer.parseInt(RowData[1]);
                int max_rec = Integer.parseInt(RowData[2]);
                int max_voti = Integer.parseInt(RowData[3]);
                int max_dom = Integer.parseInt(RowData[4]);

                res.add(new Riga(livello, punteggio, max_rec, max_voti, max_dom));
                // do something with "data" and "value"
            }
            reader.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }
}