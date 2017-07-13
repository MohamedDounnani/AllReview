package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by lucadiliello on 20/06/2017.
 */

public class A17_ModificaElementoAvviso extends LoggedActivity {

    Button buttonIndietro;
    Button buttonAvanti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a17_layout);

        buttonIndietro = (Button) findViewById(R.id.button_indietro);
        buttonAvanti = (Button) findViewById(R.id.button_avanti);

        buttonAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, A18_ModificaElemento.class));
            }
        });

        buttonIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
