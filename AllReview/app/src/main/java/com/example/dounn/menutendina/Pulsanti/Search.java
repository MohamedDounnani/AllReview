package com.example.dounn.menutendina.Pulsanti;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.provider.BaseColumns;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.dounn.menutendina.A15_InserisciRecensione;
import com.example.dounn.menutendina.A26_RisultatiRicerca;
import com.example.dounn.menutendina.Database.AutoComplete;

import com.example.dounn.menutendina.R;


/**
 * Created by dounn on 21/05/2017.
 */

public class Search {
    private SimpleCursorAdapter myAdapter;

    private static AutoComplete names = new AutoComplete();

    private Intent intent;
    private Context ctx;
    //PER isInserisciRecensione VEDI SUPERACTIVITY
    private boolean isInserisciRecensione;

    private final String[] from = new String[]{"Search"};
    private final int[] to = new int[]{android.R.id.text1};

    private SearchView searchView;

    public Search(Context context, String token) {
        this.ctx = context;
        myAdapter = new android.support.v4.widget.SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null,
                from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.BLACK);
                return view;
            }
        };

    }

    public void setIsRecensione(boolean v){
        isInserisciRecensione = v;
    }

    //FUNZIONE PER TROVARE QUELLO CHE SCRIVI NELLA BARRA DI RICERCA, UNA SPECIE DI AUTOCOMPL
    private void populateAdapter(String query) {

        //FUNZIONE PER PRENDERE I NOMI DEGLI ELEMENTI DA USARE NELL'AUTOCOMPLITE
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "Search"});
        for (int i = 0; i < names.getNames().size(); i++) {
            if (names.getNames().get(i).toLowerCase().contains(query.toLowerCase())) {
                c.addRow(new Object[]{i, names.getNames().get(i)});
            }
        }
        if (c.getCount() == 0) {
            c.addRow(new Object[]{0, ctx.getResources().getString(R.string.elemento_ndisp)});
        }
        myAdapter.changeCursor(c);
    }


    //SearchView
    public void search_v(Menu menu, final Context context, ComponentName name) {

        //aggiungo nel search l'adapter per l'autocompile e il listener
        SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSuggestionsAdapter(myAdapter);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(name));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            //QUANDO CLICCO SUL SUGGERIMENTO MI PORTA NELL'ACTIVITY DI RICERCA
            @Override
            public boolean onSuggestionClick(int position) {
                if(!isInserisciRecensione) {
                    intent = new Intent(context, A26_RisultatiRicerca.class);
                    Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                    String query = cursor.getString(1);
                    intent.putExtra("query", query);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                }
                else{
                    intent = new Intent(context, A15_InserisciRecensione.class);
                    Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                    String query = cursor.getString(1);
                    intent.putExtra("query", query);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        //aggiungo un listener quando sto scrivendo sulla barra di ricerca
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //listener che parte quando clicco  il pulsante ricerca sulla tastiera
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!isInserisciRecensione) {
                    intent = new Intent(context, A26_RisultatiRicerca.class);
                    intent.putExtra("query", query);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                }
                else{
                    intent = new Intent(context, A15_InserisciRecensione.class);
                    intent.putExtra("query", query);
                    //LA ACTIVITY CHIAMA NON FA NE L'ANIMAZIONE NE VIENE TENUTA NELLA STORIA
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                }
                return false;
            }

            /* listener dell'autocomplite vero e proprio, quando sto scrivendo mi escono suggerimenti richiamando
              la funzione populateAdapter
            */
            @Override
            public boolean onQueryTextChange(String newText) {
                populateAdapter(newText);
                return true;
            }
        });
    }
}