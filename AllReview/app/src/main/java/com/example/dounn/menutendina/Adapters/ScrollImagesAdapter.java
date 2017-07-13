package com.example.dounn.menutendina.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dounn.menutendina.A31_FullScreenImage;
import com.example.dounn.menutendina.Database.Foto;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.R;

import java.util.ArrayList;

/**
 * Created by Enrico on 12/06/2017.
 * <p>
 * Adapter che costruisce le immagini nello slider
 */

public class ScrollImagesAdapter extends PagerAdapter {

    Context context;
    ArrayList<Foto> fotos;

    //ricevo le stringhe contenenti le foto da visualizzare
    public ScrollImagesAdapter(Context context, ArrayList<Foto> fotos) {
        this.context = context;
        this.fotos = fotos;
    }

    @Override
    public int getCount() {
        return fotos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    //chiamato appena prima di mostrare ogni immagine che scorre
    public Object instantiateItem(ViewGroup parent, final int position) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.foto_layout, parent, false);

        //carico il layout dell'immmagine tramite l'id
        ImageViewLoading immagine_da_mostrare = (ImageViewLoading) view.findViewById(R.id.slider_immagini);

        immagine_da_mostrare.setFotoPath(fotos.get(position).getPath());

        //al click su ogni immagine questa viene aperta e mostrata su schermo intero in Full_Screen_Immage
        immagine_da_mostrare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passo la foto alla classe per zoomare
                Store.add("zoom_foto", fotos.get(position).getPath());
                Intent fullScreenIntent = new Intent(context, A31_FullScreenImage.class);
                fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                context.startActivity(fullScreenIntent);
            }
        });

        ViewPager vp = (ViewPager) parent;
        vp.addView(view, 0);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}








