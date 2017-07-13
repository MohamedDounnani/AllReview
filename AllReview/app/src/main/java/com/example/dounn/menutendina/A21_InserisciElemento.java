package com.example.dounn.menutendina;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.example.dounn.menutendina.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class A21_InserisciElemento extends SuperActivity {
    //DICHIARO TUTTI GLI ELEMENTI CHE DEVO USARE, REQUEST_CODE è PER CHIEDERE L'UTILIZZO DELLA FOTOCAMERA

    Spinner dropdown;

    TextView txtcategoria;
    TextView txtnomelemento;
    TextView txtdescrizione;
    TextView txtmaxfoto;
    EditText etNomeElemento;
    EditText etDescrizione;
    Button btnFoto;
    Button btnCreaElemento;
    ImageView iViewElemento1;
    ImageView iViewElemento2;
    ImageView iViewElemento3;
    ImageView iViewElemento4;
    ImageView iViewElemento5;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    //CREO UN ARRAY LIST PER USARE ITERATTIVAMENTE LE 5 FOTO
    ArrayList<ImageView> arrayList = new ArrayList<>();
    ArrayList<Bitmap> bitmaps;
    //PER MEMORIZZARE IL NUMERO DI FOTO MESSE
    int countviews = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a21_layout);

        /*
        ALERTDIALOG USATO PER I CONTROLLI
         */
        builder = new AlertDialog.Builder(this,R.style.Dialog_theme);
        builder.setCancelable(true);
        builder.setNeutralButton(
                getResources().getString(R.string.understand),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dropdown = (Spinner) findViewById(R.id.spinner_crea);

        //INIZIALIZZO TUTTI GLI OGGETTI
        inizializza();

        //AGGIUNGO I VARI IMAGEVIEW
        arrayList.add(iViewElemento1);
        arrayList.add(iViewElemento2);
        arrayList.add(iViewElemento3);
        arrayList.add(iViewElemento4);
        arrayList.add(iViewElemento5);
        bitmaps = new ArrayList<>();

        btnFoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(countviews < 5) {
                    //FUNZIONE CHE FA PARTIRE LA GALLERIA O FOTOCAMERA
                    openImageIntent();
                } else {
                    txtmaxfoto.setVisibility(View.VISIBLE);
                }
            }
        });
        //FUNZIONE CHE CONTROLLA SE TUTTI I PARAMENTRI SONO MESSI BENE, PER ORA DICE SOLO QUANDO TUTTO E' APPOSTO
        btnCreaElemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crea();

            }
        });

        dropdown = (Spinner) findViewById(R.id.spinner_crea);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Categorie, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
    }

    public void inizializza() {

        txtcategoria = (TextView) findViewById(R.id.txtcategoria);
        txtnomelemento = (TextView) findViewById(R.id.txtnomelemento);
        txtdescrizione = (TextView) findViewById(R.id.txtdescrizione);
        etNomeElemento = (EditText) findViewById(R.id.etNomeElemento);
        etDescrizione = (EditText) findViewById(R.id.etDescrizione);
        btnFoto = (Button) findViewById(R.id.btn_foto);
        btnCreaElemento = (Button) findViewById(R.id.btn_crea_elemento);
        iViewElemento1 = (ImageView) findViewById(R.id.imageView_elemento1);
        iViewElemento1.setId(0);
        iViewElemento2 = (ImageView) findViewById(R.id.imageView_elemento2);
        iViewElemento2.setId(1);
        iViewElemento3 = (ImageView) findViewById(R.id.imageView_elemento3);
        iViewElemento3.setId(2);
        iViewElemento4 = (ImageView) findViewById(R.id.imageView_elemento4);
        iViewElemento4.setId(3);
        iViewElemento5 = (ImageView) findViewById(R.id.imageView_elemento5);
        iViewElemento5.setId(4);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                builder = new AlertDialog.Builder(A21_InserisciElemento.this);
                builder.setCancelable(true);
                builder.setPositiveButton(
                        getResources().getString(R.string.Yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                v.setVisibility(View.GONE);
                                countviews = v.getId();
                                bitmaps.remove(countviews);
                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton(getResources().getString(R.string.No),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.setMessage(getResources().getString(R.string.Delete_element));
                alertDialog = builder.create();
                alertDialog.show();

            }
        };
        iViewElemento1.setOnClickListener(listener);
        iViewElemento2.setOnClickListener(listener);
        iViewElemento3.setOnClickListener(listener);
        iViewElemento4.setOnClickListener(listener);
        iViewElemento5.setOnClickListener(listener);
        etNomeElemento.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(!((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP));
                return false;
            }
        });

        //metto un listener per lo scorrimento del testo nell'inserimento descrizione
        etDescrizione.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(!((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP));
                return false;
            }
        });
    }


    //FUNZIONE PER I CONTROLLI DA FARE PER CREARE ELEMENTO
    public boolean controlliElementi() {
        if(iViewElemento1.getVisibility() != View.VISIBLE) {
            builder.setMessage(getResources().getString(R.string.Foto_insert));
            alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
        if(dropdown.getSelectedItemPosition() == 0) {
            builder.setMessage(getResources().getString(R.string.Category_insert));
            alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
        if(etNomeElemento.getText().toString().length() < 3) {
            builder.setMessage(getResources().getString(R.string.Char_insert_name));
            alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
        if(etDescrizione.getText().toString().length() < 5) {
            builder.setMessage(getResources().getString(R.string.Char_insert_descr));
            alertDialog = builder.create();
            alertDialog.show();
            return false;
        }

        return true;
    }


    //RISULTATO DELLA FUNZIONE SOPRA DESCRITTA, GESTISCO IL RISULTATO E LO TRASFORMO IN IMMAGINE
    @Override
    protected void onActivityResultImage(Bitmap bitmap) {
        bitmaps.add(bitmap);

        if(arrayList.get(countviews).getVisibility() == View.GONE) {
            arrayList.get(countviews).setVisibility(View.VISIBLE);
        }
        arrayList.get(countviews).setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
        countviews++;
    }

    public void crea() {
        if(controlliElementi()) {
            Utility.hideSoftKeyboard(A21_InserisciElemento.this);
            startCaricamento(0, getResources().getString(R.string.Loading_ele));
            JSONObject req = new JSONObject();
            try {
                req.put("token", getToken());
                req.put("nome", etNomeElemento.getText().toString());
                req.put("descr", etDescrizione.getText().toString());
                req.put("id_categoria", dropdown.getSelectedItemPosition());
                req.put("path", "add_elemento");
                Utility.addFotoJSON(req, bitmaps);
                //chiamo la richiesta asincrona al database per il controllo dei campi
                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        //controllo lo stato della richiesta
                        try {
                            if(!a.getString("status").equals("ERROR")) {

                                Elemento tmp = new Elemento(a.getJSONObject("result"));
                                Intent i = new Intent(ctx, A27_PaginaElemento.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Store.add("elemento", tmp);
                                stopCaricamento(0);
                                startActivity(i);
                                finish();
                            } else {
                                errorBar(getResources().getString(R.string.errore_server), 2000);
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void noInternetConnection() {
                        noInternetErrorBar();
                    }
                }).execute(req);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

    }

}