package com.example.dounn.menutendina.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.dounn.menutendina.A20_ProfiloPubblico;
import com.example.dounn.menutendina.A29_Risposte;
import com.example.dounn.menutendina.Database.Domanda;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.R;

import java.util.ArrayList;

// Adapter Class
public class DomandeFilterAdapter extends RecyclerView.Adapter<DomandeFilterAdapter.ViewHolder> implements Filterable {

    private ArrayList<Domanda> mOriginalValues; // Original Values
    private ArrayList<Domanda> mDisplayedValues; // Values to be displayed
    Context ctx;

    public DomandeFilterAdapter(Context context, ArrayList<Domanda> domande) {
        this.ctx = context;
        this.mOriginalValues = domande;
        this.mDisplayedValues = domande;
    }

    public void update(ArrayList<Domanda> newDomande) {
        mOriginalValues = newDomande;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDisplayedValues.size();
    }

    public Domanda getItem(int position) {
        return mDisplayedValues.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {

        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.domanda_layout, parent, false);

        final DomandeFilterAdapter.ViewHolder res = new DomandeFilterAdapter.ViewHolder(convertView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = res.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Store.add("domanda", getItem(position));
                    Intent i = new Intent(ctx, A29_Risposte.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                }
            }
        });

        res.nomeProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = res.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    int id_utente = getItem(position).getUtente().getId();
                    Intent i = new Intent(ctx, A20_ProfiloPubblico.class);
                    i.putExtra("id_utente", id_utente);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                }
            }
        });

        res.utenteMigliorRisposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = res.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && getItem(position).getRispostaTop() != null) {
                    int id_utente = getItem(position).getRispostaTop().getUtente().getId();
                    Intent i = new Intent(ctx, A20_ProfiloPubblico.class);
                    i.putExtra("id_utente", id_utente);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                }
            }
        });

        return res;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Domanda domanda = getItem(i);
        viewHolder.domandaUtente.setText(domanda.getTesto());
        viewHolder.nomeProfilo.setText(domanda.getUtente().getUsername());
        viewHolder.orario.setText(domanda.reduceData());

        String fotopath = domanda.getUtente().getFotopath();
        viewHolder.fotoProfilo.setFotoPath(fotopath);

        if(domanda.getRispostaTop() != null) {

            String fotopathRisp = domanda.getRispostaTop().getUtente().getFotopath();
            viewHolder.fotoProfiloRisposta.setFotoPath(fotopathRisp);
            viewHolder.utenteMigliorRisposta.setText(domanda.getRispostaTop().getUtente().getUsername());
            viewHolder.migliorRisposta.setText(domanda.getRispostaTop().getTesto());
            viewHolder.orario_risposta.setText(domanda.getRispostaTop().reduceData());
        } else {
            viewHolder.settoreMigliorRisposta.setVisibility(View.GONE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mDisplayedValues = (ArrayList<Domanda>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Domanda> FilteredArrList = new ArrayList<>();

                if(mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Domanda>(mDisplayedValues); // saves the original data in mOriginalValues
                }


                if(constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for(int i = 0; i < mOriginalValues.size(); i++) {
                        Domanda d = mOriginalValues.get(i);
                        if(d.getTesto().toLowerCase().contains(constraint)) {
                            FilteredArrList.add(d);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageViewLoading fotoProfilo;
        TextView nomeProfilo;
        TextView orario;
        TextView domandaUtente;
        CardView settoreMigliorRisposta;
        ImageViewLoading fotoProfiloRisposta;
        TextView utenteMigliorRisposta;
        TextView migliorRisposta;
        TextView orario_risposta;

        public ViewHolder(View convertView) {
            super(convertView);
            fotoProfilo = (ImageViewLoading) convertView.findViewById(R.id.foto_profilo);
            nomeProfilo = (TextView) convertView.findViewById(R.id.nome_profilo);
            orario = (TextView) convertView.findViewById(R.id.orario);
            domandaUtente = (TextView) convertView.findViewById(R.id.domanda_utente);
            settoreMigliorRisposta = (CardView) convertView.findViewById(R.id.settore_miglior_risposta);
            fotoProfiloRisposta = (ImageViewLoading) convertView.findViewById(R.id.foto_profilo_risposta);
            utenteMigliorRisposta = (TextView) convertView.findViewById(R.id.utente_miglior_risposta);
            migliorRisposta = (TextView) convertView.findViewById(R.id.miglior_risposta);
            orario_risposta = (TextView) convertView.findViewById(R.id.orario_risposta);
        }
    }
}