package com.example.dounn.menutendina;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;


public class A13_ModificaProfilo extends LoggedActivity {

    private Button sendChanges;
    private CheckBox modificaUsername;
    private CheckBox modificaEmail;
    private CheckBox modificaPassword;

    private EditText newUsername;
    private EditText newEmail;
    private EditText passwordOld;
    private EditText passwordNew;
    private int speed = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a13_layout);


        sendChanges = (Button) findViewById(R.id.send_changes);

        modificaEmail = (CheckBox) findViewById(R.id.modifica_email);
        modificaUsername = (CheckBox) findViewById(R.id.modifica_username);
        modificaPassword = (CheckBox) findViewById(R.id.modifica_password);

        newUsername = (EditText) findViewById(R.id.new_username);
        newEmail = (EditText) findViewById(R.id.new_email);
        passwordOld = (EditText) findViewById(R.id.password_old);
        passwordNew = (EditText) findViewById(R.id.password_new);

        modificaUsername.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                modificaUsername.setEnabled(false);

                ViewPropertyAnimator animation = newUsername.animate().setDuration(speed);

                if(isChecked) {
                    newUsername.setVisibility(View.VISIBLE);
                    animation.alpha(1f);
                } else {
                    animation.alpha(0f);
                }
                animation.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(!isChecked) {
                            newUsername.setVisibility(View.INVISIBLE);
                        }
                        modificaUsername.setEnabled(true);
                    }
                }).start();

            }
        });

        modificaEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                modificaEmail.setEnabled(false);

                ViewPropertyAnimator animation = newEmail.animate().setDuration(speed);

                if(isChecked) {
                    newEmail.setVisibility(View.VISIBLE);
                    animation.alpha(1f);
                    Toast.makeText(ctx, R.string.warning_email_pass, Toast.LENGTH_LONG).show();
                } else {
                    animation.alpha(0f);
                }
                animation.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(!isChecked) {
                            newEmail.setVisibility(View.INVISIBLE);
                        }
                        modificaEmail.setEnabled(true);
                    }
                }).start();

            }
        });

        modificaPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                modificaPassword.setEnabled(false);

                ViewPropertyAnimator animation_new = passwordOld.animate().setDuration(speed);
                ViewPropertyAnimator animation_old = passwordNew.animate().setDuration(speed);

                if(isChecked) {
                    passwordOld.setVisibility(View.VISIBLE);
                    passwordNew.setVisibility(View.VISIBLE);
                    animation_new.alpha(1f);
                    animation_old.alpha(1f);
                    Toast.makeText(ctx, R.string.warning_email_pass, Toast.LENGTH_LONG).show();
                } else {
                    animation_new.alpha(0f);
                    animation_old.alpha(0f);
                }
                animation_new.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(!isChecked) {
                            passwordOld.setVisibility(View.INVISIBLE);
                        }
                        modificaPassword.setEnabled(true);
                    }
                }).start();
                animation_new.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(!isChecked) {
                            passwordNew.setVisibility(View.INVISIBLE);
                        }
                        modificaPassword.setEnabled(true);
                    }
                }).start();
            }
        });

        //nome, email, password
        sendChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!modificaEmail.isChecked() && !modificaUsername.isChecked() && !modificaPassword.isChecked()) {
                    Toast.makeText(ctx, getResources().getString(R.string.Something_change), Toast.LENGTH_LONG).show();
                    return;
                } else {
                    sendChanges();
                }
            }
        });

    }

    void sendChanges() {

        startCaricamento(200, getResources().getString(R.string.Up_changes));

        boolean updateUsername = modificaUsername.isChecked();
        final boolean updateEmail = modificaEmail.isChecked();
        final boolean updatePassword = modificaPassword.isChecked();

        String username = null;
        String email = null;
        String newPassword = null;
        String oldPassword = null;

        JSONObject request = new JSONObject();
        try {
            request.put("token", getToken());
            request.put("path", "change_data");

            if(updateUsername) {
                if(!checkUsername(newUsername.getText())) {
                    Toast.makeText(ctx, getResources().getString(R.string.Invalid_username), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    username = newUsername.getText().toString();
                }
            }

            if(updateEmail) {
                if(!isValidEmail(newEmail.getText())) {
                    Toast.makeText(ctx, getResources().getString(R.string.Invalid_email), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    email = newEmail.getText().toString();
                }
            }

            if(updatePassword) {
                if(!checkPassWordAndConfirmPassword(passwordOld.getText().toString(), passwordNew.getText().toString())) {
                    Toast.makeText(ctx, getResources().getString(R.string.Invalid_password), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    newPassword = passwordNew.getText().toString();
                    oldPassword = passwordOld.getText().toString();
                }
            }

            request.put("nome", username);
            request.put("email", email);
            request.put("passwordOld", oldPassword);
            request.put("passwordNew", newPassword);

            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    try {
                        if(a.getString("status").equals("OK")) {
                            stopCaricamento(200);
                            successBar(getResources().getString(R.string.Update), 3000);
                            Intent i;
                            if(updateEmail || updatePassword) {
                                removeUser();
                                i = new Intent(ctx, A1_LoginIniziale.class);
                            } else i = new Intent(ctx, A7_ProfiloPrivato.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            errorBar(getResources().getString(R.string.Error_req), 3000);
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