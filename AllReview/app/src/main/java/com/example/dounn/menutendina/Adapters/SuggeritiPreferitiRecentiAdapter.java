package com.example.dounn.menutendina.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.R;

import java.util.ArrayList;

/**
 * Created by dounn on 22/06/2017.
 */

public class SuggeritiPreferitiRecentiAdapter extends RecyclerView.Adapter<SuggeritiPreferitiRecentiAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<Elemento> values;
    private Intent intent;

    public SuggeritiPreferitiRecentiAdapter(Context context, ArrayList<Elemento> values, Intent intent) {
        this.ctx = context;
        this.values = values;
        this.intent = intent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_card, parent, false);
        final ViewHolder holder = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Store.add("elemento", getItem(position));
                    ctx.startActivity(intent);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Elemento elemento = getItem(position);
        holder.tvTitle.setText(elemento.getNome());
        if(elemento.getFotos().size() > 0) {
            holder.itemImages.setFotoPath(elemento.getFotos().get((int) (Math.random() * elemento.getFotos().size())).getPath());
        }

    }

    private Elemento getItem(int position) {
        return values.get(position);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageViewLoading itemImages;
        TextView tvTitle;

        ViewHolder(View view) {
            super(view);
            this.itemImages = (ImageViewLoading) view.findViewById(R.id.itemImages);
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        }

    }
}

