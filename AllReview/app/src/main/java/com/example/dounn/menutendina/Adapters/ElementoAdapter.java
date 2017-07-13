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

import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.R;
import com.example.dounn.menutendina.Utility.Utility;

import java.util.ArrayList;

/**
 * Created by lucadiliello on 21/06/2017.
 */

public class ElementoAdapter extends RecyclerView.Adapter<ElementoAdapter.ViewHolder> {

    Context ctx;
    ArrayList<Elemento> values;
    Intent intent;

    public ElementoAdapter(@NonNull Context context, ArrayList<Elemento> values, Intent i) {
        this.ctx = context;
        this.values = values;
        this.intent = i;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_layout, parent, false);

        final ElementoAdapter.ViewHolder res = new ElementoAdapter.ViewHolder(convertView);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(res.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    Store.add("elemento", getItem(res.getAdapterPosition()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
            }
        });

        return res;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ElementoAdapter.ViewHolder viewHolder, int i) {
        Elemento elemento = getItem(viewHolder.getAdapterPosition());

        viewHolder.txtNomeRicerca.setText(Utility.capitalize(elemento.getNome()));

        viewHolder.ratingBar.setMax(100);
        if(elemento.getRating() == -1) {
            viewHolder.ratingBar.setVisibility(View.GONE);
            viewHolder.votazione.setTextColor(ctx.getResources().getColor(R.color.bottone_anonimo));
            viewHolder.votazione.setText(ctx.getResources().getString(R.string.recensioni_non_presenti));
        } else {
            viewHolder.ratingBar.setProgress((int) (elemento.getRating() * 20));
            viewHolder.votazione.setText(String.format("%.1f", (elemento.getRating())));
        }

        viewHolder.descrizioneElemento.setText(elemento.getDescr());
        viewHolder.categoria.setText(Utility.catchCategoria(elemento.getCategoria()));
        if(elemento.getFotoNumber() > 0) {
            viewHolder.image.setFotoPath(elemento.getFotos().get(0).getPath());
        }
    }


    public Elemento getItem(int position) {
        return values.get(position);
    }

    public void update(ArrayList<Elemento> elementi) {
        this.values = elementi;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageViewLoading image;
        RatingBar ratingBar;
        TextView votazione;
        TextView txtNomeRicerca;
        TextView descrizioneElemento;
        TextView categoria;

        public ViewHolder(View convertView) {
            super(convertView);
            this.image = (ImageViewLoading) convertView.findViewById(R.id.image);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar_adapter_elemento);
            votazione = (TextView) convertView.findViewById(R.id.valutazione_stelle_adapter_elemento);
            txtNomeRicerca = (TextView) convertView.findViewById(R.id.txtNome_Ricerca);
            descrizioneElemento = (TextView) convertView.findViewById(R.id.descrizione_elemento_adapter);
            categoria = (TextView) convertView.findViewById(R.id.categoria_elemento_adapter);
        }
    }
}