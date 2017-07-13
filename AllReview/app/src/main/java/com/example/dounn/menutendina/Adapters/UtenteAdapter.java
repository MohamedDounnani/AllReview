package com.example.dounn.menutendina.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dounn.menutendina.A20_ProfiloPubblico;
import com.example.dounn.menutendina.Database.Utente;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.R;

import java.util.ArrayList;

/**
 * Created by lucadiliello on 13/06/2017.
 */

public class UtenteAdapter extends RecyclerView.Adapter<UtenteAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<Utente> values;

    public UtenteAdapter(@NonNull Context context, ArrayList<Utente> values) {
        ctx = context;
        this.values = values;
    }

    public Utente getItem(int position) {
        return values.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.utente_layout, parent, false);

        final ViewHolder viewHolder = new ViewHolder(convertView);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Intent i = new Intent(ctx, A20_ProfiloPubblico.class);
                    i.putExtra("id_utente", getItem(position).getId());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Utente utente_red = getItem(i);

        viewHolder.textUser.setText(utente_red.getUsername());
        viewHolder.subTextUser.setText(ctx.getResources().getString(R.string.Punteggio) + ": " + utente_red.getLevel().getPunteggio());
        viewHolder.progressUser.setMax(30);
        viewHolder.progressUser.setProgress(utente_red.getLevel().getLivello());
        viewHolder.textLevel.setText(ctx.getResources().getString(R.string.Livello) + " " + utente_red.getLevel().getLivello());

        if(utente_red.getFotopath() == null) {
            viewHolder.imageViewLoading.setImageResource(R.mipmap.ic_face_black_24dp);
        } else {
            viewHolder.imageViewLoading.setFotoPath(utente_red.getFotopath());
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void update(ArrayList<Utente> utenti) {
        values = utenti;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageViewLoading imageViewLoading;
        TextView textUser;
        TextView subTextUser;
        ProgressBar progressUser;
        TextView textLevel;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewLoading = (ImageViewLoading) itemView.findViewById(R.id.immagine_profilo);
            textUser = (TextView) itemView.findViewById(R.id.text_user);
            subTextUser = (TextView) itemView.findViewById(R.id.sub_text_user);
            progressUser = (ProgressBar) itemView.findViewById(R.id.progress_user);
            textLevel = (TextView) itemView.findViewById(R.id.text_level);
        }
    }
}