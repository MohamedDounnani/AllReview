package com.example.dounn.menutendina.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dounn.menutendina.A20_ProfiloPubblico;
import com.example.dounn.menutendina.Database.Risposta;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.R;
import com.example.dounn.menutendina.SuperActivity;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.example.dounn.menutendina.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class RisposteAdapter extends RecyclerView.Adapter<RisposteAdapter.ViewHolder> {

    private boolean scegliTopRisposta;
    private ArrayList<Risposta> risposte;
    private int focusId;
    SuperActivity context;

    public RisposteAdapter(SuperActivity context, ArrayList<Risposta> risposte) {
        this.context = context;
        this.risposte = risposte;
        this.scegliTopRisposta = false;
        this.focusId = -1;
    }

    public RisposteAdapter(SuperActivity context, ArrayList<Risposta> risposte, int focusId) {
        this.context = context;
        this.risposte = risposte;
        this.focusId = focusId;
        this.scegliTopRisposta = false;
    }

    public RisposteAdapter(SuperActivity context, ArrayList<Risposta> risposte, int focusId, boolean scegliTopRisposta) {
        this.scegliTopRisposta = scegliTopRisposta;
        this.context = context;
        this.risposte = risposte;
        this.focusId = focusId;
    }

    public void update(ArrayList<Risposta> risposte) {
        this.risposte = risposte;
        this.scegliTopRisposta = false;
        notifyDataSetChanged();
    }

    public void update(ArrayList<Risposta> risposte, boolean scegli_top_risposta) {
        this.risposte = risposte;
        this.scegliTopRisposta = scegli_top_risposta;
        notifyDataSetChanged();
    }


    public Risposta getItem(int position) {
        return risposte.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.risposta_layout, parent, false);

        final ViewHolder res = new ViewHolder(convertView);

        res.utenteNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = res.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, A20_ProfiloPubblico.class);
                    intent.putExtra("id_utente", getItem(position).getUtente().getId());
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        return res;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        i = viewHolder.getAdapterPosition();
        Risposta risp = getItem(i);
        viewHolder.utenteNome.setText(risp.getUtente().getUsername());
        viewHolder.rispostaTesto.setText(risp.getTesto());
        viewHolder.fotoProfilo.setFotoPath(risp.getUtente().getFotopath());
        viewHolder.data.setText(risp.reduceData());

        if(i == focusId) {
            focusId = -1;
            Utility.highLight(viewHolder.view);
        }

        if(scegliTopRisposta) {
            viewHolder.setTop.setVisibility(View.VISIBLE);
            final int finalI = i;
            viewHolder.setTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject req = new JSONObject();
                        req.put("path", "set_top_risposta");
                        req.put("token", context.getUser().getToken());
                        req.put("id_risposta", getItem(finalI).getId());
                        context.startCaricamento(0, context.getResources().getString(R.string.Application));
                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(a.getString("status").equals("OK")) {
                                        context.onScrollDownAction();
                                    } else throw new JSONException("else");
                                } catch(JSONException e) {
                                    context.errorBar(context.getResources().getString(R.string.Error), 2000);
                                }
                                context.stopCaricamento(100);
                            }

                            @Override
                            public void noInternetConnection() {
                                context.noInternetErrorBar();
                                context.stopCaricamento(100);
                            }
                        }).execute(req);

                    } catch(JSONException e) {
                        context.errorBar(context.getResources().getString(R.string.Error), 2000);
                        context.stopCaricamento(100);
                    }
                }
            });


        } else viewHolder.setTop.setVisibility(View.GONE);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return risposte.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageViewLoading fotoProfilo;
        TextView utenteNome;
        TextView rispostaTesto;
        View view;
        ImageButton setTop;
        TextView data;

        public ViewHolder(View convertView) {
            super(convertView);
            this.view = convertView;
            fotoProfilo = (ImageViewLoading) convertView.findViewById(R.id.foto_profilo);
            utenteNome = (TextView) convertView.findViewById(R.id.utente_nome);
            rispostaTesto = (TextView) convertView.findViewById(R.id.risposta_testo);
            setTop = (ImageButton) convertView.findViewById(R.id.set_top);
            data = (TextView) convertView.findViewById((R.id.orario));
        }
    }
}