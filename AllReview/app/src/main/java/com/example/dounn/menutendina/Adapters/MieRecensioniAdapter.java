package com.example.dounn.menutendina.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dounn.menutendina.A27_PaginaElemento;
import com.example.dounn.menutendina.Database.Recensione;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.R;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by lucadiliello on 21/06/2017.
 */

public class MieRecensioniAdapter extends RecyclerView.Adapter<MieRecensioniAdapter.ViewHolder> {

    private Context ctx;
    private ArrayList<Recensione> values;
    private Intent intent;

    public MieRecensioniAdapter(@NonNull Context context, ArrayList<Recensione> values, Intent intent) {
        this.ctx = context;
        this.values = values;
        this.intent = intent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {

        final View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mie_recensioni_layout, parent, false);

        final ViewHolder viewHolder = new ViewHolder(convertView);

        //al click sull'elemento porto alla pagina dell'elemento
        viewHolder.elementoRecensione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    Recensione recensione = getItem(position);
                    Store.add("elemento", recensione.getElemento());
                    Intent newintent = new Intent(ctx, A27_PaginaElemento.class);
                    newintent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(newintent);
                }
            }
        });

        //al click sulla scheda totale porto alla recensione
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    Recensione recensione = getItem(position);
                    Store.add("recensione", recensione);
                    ctx.startActivity(intent);
                }
            }
        });

        return viewHolder;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(MieRecensioniAdapter.ViewHolder viewHolder, int i) {
        Recensione recensione = getItem(i);

        viewHolder.elementoRecensione.setText(recensione.getElemento().getNome());
        viewHolder.ratingBar.setRating((float) recensione.getVoto());
        viewHolder.valutazioneStelle.setText(String.format("%.1f", (recensione.getVoto()) + 0.05));
        viewHolder.titoloRecensione.setText(recensione.getTitolo());
        viewHolder.descrizione.setText(recensione.getDescr());
        viewHolder.imageGenerale.setFotoPath(recensione.getFotopath());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    private Recensione getItem(int position) {
        return values.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageViewLoading imageGenerale;
        TextView elementoRecensione;
        RatingBar ratingBar;
        TextView valutazioneStelle;
        TextView titoloRecensione;
        public TextView descrizione;

        ViewHolder(View convertView) {
            super(convertView);
            imageGenerale = (ImageViewLoading) convertView.findViewById(R.id.image_generale);
            elementoRecensione = (TextView) convertView.findViewById(R.id.titolo_elemento_mie_recensioni);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar_mie_rec);
            valutazioneStelle = (TextView) convertView.findViewById(R.id.valutazione_stelle_adapter_mie_recensioni);
            titoloRecensione = (TextView) convertView.findViewById(R.id.titolo_mie_recensioni);
            descrizione = (TextView) convertView.findViewById(R.id.descrizione_mie_recensioni);
        }
    }
}