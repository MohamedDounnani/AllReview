package com.example.dounn.menutendina;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dounn.menutendina.Utility.Utility;


public class A35_Assistenza extends SuperActivity {

    EditText problemaAssistenza;
    EditText domandaAssistenza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a35_layout);

        problemaAssistenza = (EditText) findViewById(R.id.etProblemaAssistenza);
        domandaAssistenza = (EditText) findViewById(R.id.edDomanda_Assistenza);

        problemaAssistenza.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(!((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP));
                return false;
            }
        });

        domandaAssistenza.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(!((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP));
                return false;
            }
        });

        final Button btnDomandaAssistenza = (Button) findViewById(R.id.btnOK);


        btnDomandaAssistenza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controlli()) {
                    Utility.hideSoftKeyboard(A35_Assistenza.this);
                    String subject = domandaAssistenza.getText().toString();
                    String body = problemaAssistenza.getText().toString();
                    String[] address = {"allreviewapp@gmail.com"};
                    composeEmail(address, subject, body);
                }
            }

        });
    }

    public void composeEmail(String[] address, String subject , String Text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT , Text);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.associa_email),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean controlli(){

        if (domandaAssistenza.getText().toString().length() < 3) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.titolo_breve),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (problemaAssistenza.getText().toString().length() < 5) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.descrizione_breve),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



}