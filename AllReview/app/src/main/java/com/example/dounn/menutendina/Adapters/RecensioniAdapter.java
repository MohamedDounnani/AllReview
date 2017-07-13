package com.example.dounn.menutendina.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dounn.menutendina.A20_ProfiloPubblico;
import com.example.dounn.menutendina.A24_Recensione;
import com.example.dounn.menutendina.Database.Recensione;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.R;
import com.example.dounn.menutendina.Utility.Utility;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Enrico on 16/06/2017.
 */

public class RecensioniAdapter extends RecyclerView.Adapter<RecensioniAdapter.ViewHolder> {

    Context ctx;
    ArrayList<Recensione> recensioni;
    int focusId;

    public RecensioniAdapter(Context context, ArrayList<Recensione> values) {
        this.focusId = -1;
        this.ctx = context;
        this.recensioni = values;
    }

    public RecensioniAdapter(Context context, ArrayList<Recensione> values, int focusId) {
        this.focusId = focusId;
        this.ctx = context;
        this.recensioni = values;
    }

    // inserisci le giuste informazioni nel viewHolder
    @Override
    public void onBindViewHolder(RecensioniAdapter.ViewHolder viewHolder, int i) {
        final Recensione recensione = getItem(i);

        viewHolder.ratingBarRecensione.setRating((float) recensione.getVoto());
        viewHolder.titoloRecensione.setText(recensione.getTitolo());
        viewHolder.utenteRecensore.setText(recensione.getUtente().getUsername());
        viewHolder.descrizioneRecensione.setText(recensione.getDescr());
        viewHolder.immagineRecensione.setFotoPath(recensione.getFotopath());

        if(i == focusId) {
            focusId = -1;
            Utility.highLight(viewHolder.view);
        }
    }


    // costruisci il viewHolder e metti già a scaricare le foto
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {

        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recensione_layout, parent, false);

        final ViewHolder res = new ViewHolder(convertView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = res.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Store.add("recensione", getItem(position));
                    Intent intent = new Intent(ctx, A24_Recensione.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
            }
        });

        res.utenteRecensore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = res.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(ctx, A20_ProfiloPubblico.class);
                    intent.putExtra("id_utente", getItem(position).getUtente().getId());
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
            }
        });

        return res;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return recensioni.size();
    }

    private Recensione getItem(int position) {
        return recensioni.get(position);
    }

    public void update(ArrayList<Recensione> recensioni) {
        this.recensioni = recensioni;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageViewLoading immagineRecensione;
        public RatingBar ratingBarRecensione;
        public TextView utenteRecensore;
        public TextView titoloRecensione;
        public TextView descrizioneRecensione;
        public View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.immagineRecensione = (ImageViewLoading) view.findViewById(R.id.immagine_recensione);
            this.ratingBarRecensione = (RatingBar) view.findViewById(R.id.rating_bar_recensione);
            this.utenteRecensore = (TextView) view.findViewById(R.id.utente_recensore);
            this.titoloRecensione = (TextView) view.findViewById(R.id.titolo_recensione);
            this.descrizioneRecensione = (TextView) view.findViewById(R.id.descrizione_recensione);
        }
    }
}

