package com.example.dounn.menutendina;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class A18_ModificaElemento extends SuperActivity {


    private Spinner dropdown;
    private Elemento elemento;
    private CheckBox modificaCategoria;
    private LinearLayout categoria;
    private CheckBox modificaNomeElemento;
    private EditText etNomeElemento;
    private CheckBox modificaDescrElemento;
    private EditText etDescrizione;
    private Button btnCreaElemento;

    AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    int speed = 250;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a18_layout);

        elemento = (Elemento) Store.get("elemento");

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setNeutralButton(
                "Ho Capito",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        modificaCategoria = (CheckBox) findViewById(R.id.modifica_categoria);
        modificaNomeElemento = (CheckBox) findViewById(R.id.modifica_nome_elemento);
        etNomeElemento = (EditText) findViewById(R.id.etNomeElemento);
        modificaDescrElemento = (CheckBox) findViewById(R.id.modifica_descr_elemento);
        etDescrizione = (EditText) findViewById(R.id.etDescrizione);
        btnCreaElemento = (Button) findViewById(R.id.btn_crea_elemento);
        categoria = (LinearLayout) findViewById(R.id.categoria);


        dropdown = (Spinner) findViewById(R.id.spinner_crea);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Categorie, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);


        modificaCategoria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                modificaCategoria.setEnabled(false);

                ViewPropertyAnimator animation = categoria.animate().setDuration(speed);

                if(isChecked) {
                    categoria.setVisibility(View.VISIBLE);
                    animation.alpha(1f);
                } else {
                    animation.alpha(0f);
                }
                animation.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(!isChecked) {
                            categoria.setVisibility(View.INVISIBLE);
                        }
                        modificaCategoria.setEnabled(true);
                    }
                }).start();

            }
        });

        modificaNomeElemento.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                modificaNomeElemento.setEnabled(false);

                ViewPropertyAnimator animation = etNomeElemento.animate().setDuration(speed);

                if(isChecked) {
                    etNomeElemento.setVisibility(View.VISIBLE);
                    animation.alpha(1f);
                } else {
                    animation.alpha(0f);
                }
                animation.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(!isChecked) {
                            etNomeElemento.setVisibility(View.INVISIBLE);
                        }
                        modificaNomeElemento.setEnabled(true);
                    }
                }).start();

            }
        });

        modificaDescrElemento.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                modificaDescrElemento.setEnabled(false);

                ViewPropertyAnimator animation = etDescrizione.animate().setDuration(speed);

                if(isChecked) {
                    etDescrizione.setVisibility(View.VISIBLE);
                    animation.alpha(1f);
                } else {
                    animation.alpha(0f);
                }
                animation.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(!isChecked) {
                            etDescrizione.setVisibility(View.INVISIBLE);
                        }
                        modificaDescrElemento.setEnabled(true);
                    }
                }).start();
            }
        });

        btnCreaElemento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendChanges();
            }
        });
    }

    void sendChanges() {
        if(!modificaCategoria.isChecked() && !modificaNomeElemento.isChecked() && !modificaDescrElemento.isChecked()) {
            Toast.makeText(ctx, getResources().getString(R.string.Something_change), Toast.LENGTH_LONG).show();
            return;
        }

        startCaricamento(200, "Uploading changes");

        boolean updateCategoria = modificaCategoria.isChecked();
        boolean updateNome = modificaNomeElemento.isChecked();
        boolean updateDescrizione = modificaDescrElemento.isChecked();

        int newCategoria = 0;
        String newNome = null;
        String newDescrizione = null;

        JSONObject request = new JSONObject();
        try {
            request.put("token", getToken());
            request.put("path", "modifica_elemento");
            request.put("id_elemento", elemento.getId());

            if(updateCategoria) {
                if(dropdown.getSelectedItemPosition() == 0) {
                    builder.setMessage(getResources().getString(R.string.errore_categoria));
                    alertDialog = builder.create();
                    alertDialog.show();
                    return;
                } else {
                    newCategoria = dropdown.getSelectedItemPosition();
                }
            }

            if(updateNome) {
                if(etNomeElemento.getText().length() < 4) {
                    builder.setMessage(getResources().getString(R.string.Char_insert_name));
                    alertDialog = builder.create();
                    alertDialog.show();
                    return;
                } else {
                    newNome = etNomeElemento.getText().toString();
                }
            }

            if(updateDescrizione) {
                if(etDescrizione.getText().length() < 20) {
                    builder.setMessage(getResources().getString(R.string.Char_insert_descr));
                    alertDialog = builder.create();
                    alertDialog.show();
                    return;
                } else {
                    newDescrizione = etDescrizione.getText().toString();
                }
            }

            request.put("categoria", newCategoria);
            request.put("nome", newNome);
            request.put("descrizione", newDescrizione);

            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(a.getString("status").equals("OK")) {
                            stopCaricamento(200);
                            Intent i = new Intent(ctx, A27_PaginaElemento.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            errorBar(getResources().getString(R.string.errore_server), 3000);
                            stopCaricamento(200);
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void noInternetConnection() {
                    noInternetErrorBar();
                    stopCaricamento(200);
                }
            }).execute(request);

        } catch(JSONException e) {
            e.printStackTrace();
            stopCaricamento(200);
        }
    }

}

