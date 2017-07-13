package com.example.dounn.menutendina;

/**
 * Created by dounn on 31/05/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dounn.menutendina.Database.UtenteLogged;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;
import com.example.dounn.menutendina.Utility.Utility;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

public class A1_LoginIniziale extends SuperActivity {

    //Dialog box
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    //CardBoard
    private CardView bordoLogin;
    private CardView bordoRegistrazione;

    // TextView
    private TextView usernamePasswordErratiLogin;
    private TextView passwordDimenticataLogin;
    private TextView erroreEmailNonPresente;
    private TextView usernameRegistrazioneGiaEsistente;
    private TextView usernameRegistrazioneNonValido;
    private TextView emailNonValida;
    private TextView emailGiaUsata;
    private TextView errorePassword;
    private TextView passwordTroppoCorta;

    // EditText
    private EditText usernameRegistrazione;
    private EditText usernameLogin;
    private EditText passwordLogin;
    private EditText emailRegistrazione;
    private EditText password1Registrazione;
    private EditText password2Registrazione;

    // Button
    private Button registrati;
    private Button accedi;
    private Button fbbutton;
    private Button accediDaAnonimo;
    private Button login;
    private Button registrazione;

    // Creating Facebook CallbackManager Value
    public static CallbackManager callbackmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a1_layout);
        navigationView.getMenu().getItem(1).setChecked(true);


        // Initialize SDK before setContentView(Layout ID)
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Initialize layout button
        fbbutton = (Button) findViewById(R.id.facebook);

        fbbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Call private method
                onFblogin();
            }
        });

        accediDaAnonimo = (Button) findViewById(R.id.anonimo);

        //Carico elementi per il LOGIN **
        login = (Button) findViewById(R.id.login);
        bordoLogin = (CardView) findViewById(R.id.card_login);
        usernameLogin = (EditText) findViewById(R.id.username_login);
        passwordLogin = (EditText) findViewById(R.id.password_login);
        usernamePasswordErratiLogin = (TextView) findViewById(R.id.username_password_errati);
        passwordDimenticataLogin = (TextView) findViewById(R.id.password_dimenticata);
        erroreEmailNonPresente = (TextView) findViewById(R.id.email_non_presente);

        accedi = (Button) findViewById(R.id.accedi);

        //carico elementi per la REGISTRAZIONE **
        registrazione = (Button) findViewById(R.id.registrazione);
        bordoRegistrazione = (CardView) findViewById(R.id.card_registrazione);
        usernameRegistrazione = (EditText) findViewById(R.id.username_registrazione);
        usernameRegistrazioneGiaEsistente = (TextView) findViewById(R.id.username_gia_esistente);
        usernameRegistrazioneNonValido = (TextView) findViewById(R.id.username_non_valido);
        emailRegistrazione = (EditText) findViewById(R.id.email_registrazione);
        emailNonValida = (TextView) findViewById(R.id.errore_email_registrazione);
        emailGiaUsata = (TextView) findViewById(R.id.email_gia_usata);
        password1Registrazione = (EditText) findViewById(R.id.password_registrazione1);
        password2Registrazione = (EditText) findViewById(R.id.password_registrazione2);
        errorePassword = (TextView) findViewById(R.id.password_differenti);
        passwordTroppoCorta = (TextView) findViewById(R.id.password_corta);
        registrati = (Button) findViewById(R.id.registrati);

        // LOGIN
        //listener per comparsa/scomparsa elementi login alla pressione del bottone
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameLogin.getVisibility() == View.GONE) {
                    setVisible(bordoLogin, accedi, usernameLogin, passwordLogin, passwordDimenticataLogin);
                    usernameLogin.requestFocus();
                } else {
                    setGone(bordoLogin, accedi, usernameLogin, passwordLogin, passwordDimenticataLogin, usernamePasswordErratiLogin);
                }
            }
        });

        // ACCEDI
        //listener per l'INVIO di dati del login
        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //controllo che i campi username e password non siano nulli o vuoti
                if(usernameLogin.getText().toString().equals("") || passwordLogin.getText().toString().equals("") ||
                        usernameLogin == null || passwordLogin == null) {
                    //rendo visibile errore
                    usernamePasswordErratiLogin.setVisibility(View.VISIBLE);
                    //attivo i listener per la sua scomparsa
                    cancellaMessaggioErroreLogin();

                } else {
                    Utility.hideSoftKeyboard(A1_LoginIniziale.this);
                    startCaricamento(200, getResources().getString(R.string.Loading));
                    //richiesta accesso
                    JSONObject req = new JSONObject();
                    try {
                        req.put("username", usernameLogin.getText().toString());
                        req.put("password", passwordLogin.getText().toString());
                        req.put("path", "login");

                        //Log.d("richieste:/n", usernameLogin.getText() + " " + passwordLogin.getText().toString() + " " + passwordLogin.getText().toString().length());
                        //chiamo la richiesta asincrona al database per il controllo dei campi
                        new Request(new RequestCallback() {
                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {

                                    //controllo lo stato della richiesta
                                    if(!a.getString("status").equals("ERROR")) {
                                        setUser(new UtenteLogged(a.getJSONObject("result").getJSONObject("utente")));
                                        Intent intent;

                                        if(getUser().isAttivato()) {
                                            intent = new Intent(ctx, A10_HomePage.class);
                                        } else {
                                            intent = new Intent(ctx, A4_AttivazioneAccountCodice.class);
                                        }

                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        //rendo visibile errore se la richiesta da ERROR
                                        usernamePasswordErratiLogin.setVisibility(View.VISIBLE);
                                        //attivo i listener per la sua scomparsa
                                        cancellaMessaggioErroreLogin();
                                    }
                                    stopCaricamento(100);
                                } catch(JSONException e) {
                                    stopCaricamento(100);
                                    errorBar(getResources().getString(R.string.errore_server), 2000);
                                }
                            }

                            @Override
                            public void noInternetConnection() {
                                noInternetErrorBar();
                                stopCaricamento(200);
                            }
                        }).execute(req);
                    } catch(JSONException e) {
                        noInternetErrorBar();
                        stopCaricamento(200);
                    }
                }

            }
        });

        // PASSWORD DIMENTICATA
        //invio nuova password per email se l'utente clicca nel link di password dimenticata
        passwordDimenticataLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordDimenticataLogin.setTextColor(Color.parseColor("#030bf7"));
                passwordDimenticataLogin.setPaintFlags(passwordDimenticataLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                //cancello eventuali errori
                setGone(erroreEmailNonPresente, usernamePasswordErratiLogin);

                //abbasso la tastiera al click
                Utility.hideSoftKeyboard(A1_LoginIniziale.this);

                JSONObject req = new JSONObject();
                try {
                    req.put("email", usernameLogin.getText().toString());
                    req.put("path", "nuova_password");
                } catch(JSONException e) {
                }

                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {

                        if(a != null) {
                            //ridò al testo il colore spento
                            passwordDimenticataLogin.setTextColor(Color.parseColor("#295d9e"));
                            passwordDimenticataLogin.setPaintFlags(0);

                            try {
                                if(!a.getString("status").equals("ERROR")) {
                                    //se la richiesta è andata a buon mostro Toast di invio email
                                    Toast.makeText(ctx, getResources().getString(R.string.allert_invio_messaggio), Toast.LENGTH_LONG).show();
                                } else {
                                    //altrimenti mostro il messaggio di errore della email non esistente
                                    erroreEmailNonPresente.setVisibility(View.VISIBLE);
                                    //attivo listener per la scomparsa errore
                                    cancellaMessaggioErroreLogin();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //ridò al testo il colore spento
                            passwordDimenticataLogin.setTextColor(Color.parseColor("#295d9e"));
                            passwordDimenticataLogin.setPaintFlags(0);
                        }

                    }

                    @Override
                    public void noInternetConnection() {
                        noInternetErrorBar();
                    }
                }).execute(req);
            }
        });


        //listener per la comparsa/scomparsa elementi registrazione
        registrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameRegistrazione.getVisibility() == View.GONE) {
                    setVisible(bordoRegistrazione, usernameRegistrazione, emailRegistrazione, password1Registrazione, password2Registrazione, registrati);
                } else {
                    setGone(bordoRegistrazione, usernameRegistrazione, emailRegistrazione, password1Registrazione, password2Registrazione, registrati);
                }
            }
        });

        //listener per l'INVIO di dati della registazione
        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //controllo che le password coincidano e l'email sia valida
                if(checkPassWordAndConfirmPassword(password1Registrazione.getText().toString(),
                        password2Registrazione.getText().toString()) && isValidEmail(emailRegistrazione.getText().toString())) {
                    //faccio un ulteriore controllo sulla lunghezza della password
                    if(password1Registrazione.getText().toString().length() >= 8
                            && usernameRegistrazione.getText().toString().length() >= 3) {
                        Utility.hideSoftKeyboard(A1_LoginIniziale.this);

                        //creo la richiesta JSON

                        JSONObject req = new JSONObject();
                        try {
                            startCaricamento(0, "Registering");
                            //req.put("username","Luca1995");
                            //req.put("password","ziocan");
                            req.put("nome", usernameRegistrazione.getText().toString());
                            req.put("email", emailRegistrazione.getText().toString());
                            req.put("password", password1Registrazione.getText().toString());
                            req.put("lang", Locale.getDefault().getDisplayLanguage());
                            req.put("path", "registra");


                            new Request(new RequestCallback() {
                                @Override
                                public void inTheEnd(JSONObject a) {
                                    if(a != null) {
                                        try {
                                            if(!a.getString("status").equals("ERROR")) {

                                                setUser(new UtenteLogged(a.getJSONObject("result").getJSONObject("utente")));

                                                //Faccio partire pagina inserimento codice A4
                                                Intent myIntent = new Intent(ctx, A4_AttivazioneAccountCodice.class);
                                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(myIntent);

                                            } else {

                                                //se è stato inserito un username già presente
                                                if(a.getString("message").equals("nome_usato")) {
                                                    usernameRegistrazioneGiaEsistente.setVisibility(View.VISIBLE);
                                                    cancellaMessaggioErroreRegistrazioneUsernameDoppio();
                                                }
                                                //se è stata inserita una email già esistente
                                                if(a.getString("message").equals("email_usata") || a.getString("message").equals("email_not_exists")) {
                                                    emailGiaUsata.setVisibility(View.VISIBLE);
                                                    cancellaMessaggioErroreRegistrazioneEmailGiaUsata();
                                                }
                                            }
                                        } catch(JSONException e) {
                                            e.printStackTrace();
                                        } finally {
                                            stopCaricamento(200);
                                        }
                                    } else {
                                        stopCaricamento(200);
                                    }
                                }

                                @Override
                                public void noInternetConnection() {
                                    stopCaricamento(200);
                                    noInternetErrorBar();
                                }
                            }).execute(req);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                    } else //se la password è più corta di 8 caratteri mostro errore
                    {
                        if(password1Registrazione.getText().toString().length() < 8) {
                            passwordTroppoCorta.setVisibility(View.VISIBLE);
                            cancellaMessaggioErroreRegistrazionePassword();
                        }
                        if(usernameRegistrazione.getText().toString().length() < 3) {
                            usernameRegistrazioneNonValido.setVisibility(View.VISIBLE);
                            cancellaMessaggioErroreRegistrazioneUsernameNonValido();
                        }
                    }
                } else //se le password non coincidono o/e l'email non è valida
                {
                    if(!checkPassWordAndConfirmPassword(password1Registrazione.getText().toString(),
                            password2Registrazione.getText().toString())) {
                        errorePassword.setVisibility(View.VISIBLE);
                        cancellaMessaggioErroreRegistrazionePassword();
                    }
                    if(!isValidEmail(emailRegistrazione.getText().toString())) {
                        emailNonValida.setVisibility(View.VISIBLE);
                        cancellaMessaggioErroreRegistrazioneEmail();
                    }
                }
            }
        });

        accediDaAnonimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(A1_LoginIniziale.this, R.style.Dialog_theme);
                builder.setCancelable(true);
                builder.setTitle(getResources().getString(R.string.title_allert_anonimo));

                builder.setPositiveButton(
                        getResources().getString(R.string.prosegui),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeUser();
                                startActivity(new Intent(ctx, A10_HomePage.class));
                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.setMessage(getResources().getString(R.string.allert_anonimo));
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isActivated()) {
            Intent i = new Intent(ctx, A10_HomePage.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //chiudo tutte le tendine precedentemente aperte
        setGone(bordoRegistrazione, usernameRegistrazione, emailRegistrazione, password1Registrazione, password2Registrazione, registrati);
        setGone(bordoLogin, accedi, usernameLogin, passwordLogin, passwordDimenticataLogin);

        //check delle richieste di registrazione o login per aperturna tendine appropriate
        if(pref.getString("previous", null) != null) {
            if(pref.getString("previous", null).equals("REG")) {
                setVisible(bordoRegistrazione, usernameRegistrazione, emailRegistrazione, password1Registrazione, password2Registrazione, registrati);
                usernameRegistrazione.requestFocus();
                pref.edit().remove("previous").apply();
            } else {
                if(pref.getString("previous", null).equals("LOGIN")) {
                    setVisible(accedi, bordoLogin, usernameLogin, passwordLogin, passwordDimenticataLogin);
                    usernameLogin.requestFocus();
                    pref.edit().remove("previous").apply();
                }
            }
        }
    }

    // Private method to handle Facebook login and callback
    private void onFblogin() {
        callbackmanager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if(response.getError() != null) {
                                            errorBar(getResources().getString(R.string.Error_Facebook), 3000);
                                        } else {
                                            try {

                                                final String strEmail = json.getString("email");
                                                String strId = json.getString("id");
                                                final String name = json.getString("first_name") + " " + json.getString("last_name");

                                                final String password = Utility.generate_sha(strEmail + strId + name);
                                                final String language = Locale.getDefault().getDisplayLanguage();

                                                JSONObject req1 = new JSONObject();
                                                req1.put("path", "login");
                                                req1.put("username", strEmail);
                                                req1.put("password", password);
                                                startCaricamento(0, "Logging with facebook");
                                                new Request(new RequestCallback() {
                                                    @Override
                                                    public void inTheEnd(JSONObject a) {
                                                        try {
                                                            stopCaricamento(0);
                                                            if(a.getString("status").equals("ERROR")) {
                                                                JSONObject req2 = new JSONObject();
                                                                req2.put("path", "facebook");
                                                                req2.put("nome", name);
                                                                req2.put("email", strEmail);
                                                                req2.put("password", password);
                                                                req2.put("lang", language);
                                                                startCaricamento(0, "Setting up first signing");
                                                                new Request(new RequestCallback() {
                                                                    @Override
                                                                    public void inTheEnd(JSONObject a) {
                                                                        try {
                                                                            if(a.getString("status").equals("OK")) {
                                                                                setUser(new UtenteLogged(a.getJSONObject("result").getJSONObject("utente")));
                                                                                Intent intent = new Intent(ctx, A10_HomePage.class);
                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                                                stopCaricamento(200);
                                                                                startActivity(intent);
                                                                            } else {
                                                                                if(a.getString("message").equals("nome_usato") || a.getString("message").equals("email_usata")) {
                                                                                    errorBar(getResources().getString(R.string.Facebook_Email_Used), 3000);
                                                                                } else
                                                                                    errorBar(getResources().getString(R.string.Facebook_Fail), 3000);
                                                                            }
                                                                        } catch(JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void noInternetConnection() {
                                                                        noInternetErrorBar();
                                                                    }
                                                                }).execute(req2);
                                                            } else {
                                                                setUser(new UtenteLogged(a.getJSONObject("result").getJSONObject("utente")));
                                                                Intent intent = new Intent(ctx, A10_HomePage.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                                startActivity(intent);
                                                            }
                                                        } catch(JSONException e) {
                                                            e.printStackTrace();
                                                            noInternetErrorBar();
                                                        }
                                                    }

                                                    @Override
                                                    public void noInternetConnection() {
                                                        noInternetErrorBar();
                                                    }
                                                }).execute(req1);

                                            } catch(JSONException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                        );
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        noInternetErrorBar();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        noInternetErrorBar();
                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }

    //Vari listener per la scomparsa dei messaggi di errore
    public void cancellaMessaggioErroreLogin() {
        //listener che ascoltano il cambio del testo
        usernameLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernamePasswordErratiLogin.setVisibility(View.GONE);
                erroreEmailNonPresente.setVisibility(View.GONE);
            }
        });

        passwordLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernamePasswordErratiLogin.setVisibility(View.GONE);
            }
        });
    }

    public void cancellaMessaggioErroreRegistrazioneUsernameNonValido() {
        //listener che ascoltano il cambio del testo
        usernameRegistrazione.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameRegistrazioneNonValido.setVisibility(View.GONE);
            }
        });
    }

    public void cancellaMessaggioErroreRegistrazionePassword() {
        //listener che ascoltano il cambio del testo
        password1Registrazione.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                errorePassword.setVisibility(View.GONE);
                passwordTroppoCorta.setVisibility(View.GONE);
            }
        });

        password2Registrazione.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                errorePassword.setVisibility(View.GONE);
                passwordTroppoCorta.setVisibility(View.GONE);
            }
        });
    }

    public void cancellaMessaggioErroreRegistrazioneEmail() {
        //listener che ascoltano il cambio del testo
        emailRegistrazione.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailNonValida.setVisibility(View.GONE);
            }
        });
    }

    public void cancellaMessaggioErroreRegistrazioneUsernameDoppio() {
        //listener che ascoltano il cambio del testo
        usernameRegistrazione.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameRegistrazioneGiaEsistente.setVisibility(View.GONE);
            }
        });
    }

    public void cancellaMessaggioErroreRegistrazioneEmailGiaUsata() {
        //listener che ascoltano il cambio del testo
        emailRegistrazione.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailGiaUsata.setVisibility(View.GONE);
            }
        });
    }
}