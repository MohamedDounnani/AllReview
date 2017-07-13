package com.example.dounn.menutendina.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dounn.menutendina.Database.Riga;
import com.example.dounn.menutendina.R;

import java.util.ArrayList;

/**
 * Created by lucadiliello on 11/06/2017.
 */

public class TabellaAdapters extends RecyclerView.Adapter<TabellaAdapters.ViewHolder> {

    private final Context context;
    private final ArrayList<Riga> values;
    private final int level;

    public TabellaAdapters(Context context, ArrayList<Riga> values, int level) {
        this.context = context;
        this.values = values;
        this.level = level;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.punteggi_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setIsRecyclable(false);
        Riga riga = getItem(position);
        viewHolder.livello.setText(riga.getLivello());
        viewHolder.punteggio.setText(riga.getPunteggio());
        viewHolder.maxRec.setText(riga.getMaxRec());
        viewHolder.maxVoti.setText(riga.getMaxVoti());
        viewHolder.maxDom.setText(riga.getMaxDom());

        if(position == level) {
            viewHolder.livello.setBackgroundColor(ContextCompat.getColor(context, R.color.verdino));
            viewHolder.punteggio.setBackgroundColor(ContextCompat.getColor(context, R.color.verdino));
            viewHolder.maxRec.setBackgroundColor(ContextCompat.getColor(context, R.color.verdino));
            viewHolder.maxVoti.setBackgroundColor(ContextCompat.getColor(context, R.color.verdino));
            viewHolder.maxDom.setBackgroundColor(ContextCompat.getColor(context, R.color.verdino));
        } else if(position == 0) {
            viewHolder.livello.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            viewHolder.punteggio.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            viewHolder.maxRec.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            viewHolder.maxVoti.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            viewHolder.maxDom.setBackgroundColor(ContextCompat.getColor(context, R.color.black));

            viewHolder.livello.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.punteggio.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.maxRec.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.maxVoti.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.maxDom.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    private Riga getItem(int position) {
        return values.get(position);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView livello;
        TextView punteggio;
        TextView maxRec;
        TextView maxVoti;
        TextView maxDom;

        public ViewHolder(View itemView) {
            super(itemView);
            livello = (TextView) itemView.findViewById(R.id.livello);
            punteggio = (TextView) itemView.findViewById(R.id.punteggio);
            maxRec = (TextView) itemView.findViewById(R.id.max_rec);
            maxVoti = (TextView) itemView.findViewById(R.id.max_voti);
            maxDom = (TextView) itemView.findViewById(R.id.max_dom);
        }
    }
}
