package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;

import com.example.dounn.menutendina.Database.UtenteLogged;

/**
 * Created by lucadiliello on 16/06/2017.
 */

public class LoggedActivity extends SuperActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtenteLogged utente = getUser();
        if(utente == null) {
            startActivity(new Intent(ctx, A1_LoginIniziale.class));
        } else if(!utente.isAttivato()) {
            startActivity(new Intent(ctx, A4_AttivazioneAccountCodice.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtenteLogged utente = getUser();
        if(utente == null) {
            startActivity(new Intent(ctx, A1_LoginIniziale.class));
        } else if(!utente.isAttivato()) {
            startActivity(new Intent(ctx, A4_AttivazioneAccountCodice.class));
        }
    }
}

