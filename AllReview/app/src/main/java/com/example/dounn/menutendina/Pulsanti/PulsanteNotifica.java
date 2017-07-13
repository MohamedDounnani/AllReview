package com.example.dounn.menutendina.Pulsanti;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dounn.menutendina.A14_Notifiche;
import com.example.dounn.menutendina.R;

/**
 * Created by dounn on 21/05/2017.
 */
//CREAZIONE PULSANTE NOTIFICHE
public class PulsanteNotifica {
    private Context context = null;
    private MenuItem menuItem;
    private TextView textView;
    private ImageButton imageButton;

    public PulsanteNotifica(Menu menu, Context context) {
        this.context = context;

        menuItem = menu.findItem(R.id.badge);
        MenuItemCompat.setActionView(menuItem, R.layout.layout_campanella);
        RelativeLayout notifCount;
        notifCount = (RelativeLayout) MenuItemCompat.getActionView(menuItem);

        textView = (TextView) notifCount.findViewById(R.id.actionbar_notification_textview);

        imageButton = (ImageButton) notifCount.findViewById(R.id.action_bar_notification_imageview);

        textView.setText("0");
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PulsanteNotifica.this.context, A14_Notifiche.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                PulsanteNotifica.this.context.startActivity(intent);
            }
        });

    }


    public void setNotificationNumber(int i) {
        textView.setText(String.valueOf(i));
    }
}
