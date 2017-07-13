package com.example.dounn.menutendina.OtherView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.dounn.menutendina.R;

/**
 * Created by Enrico on 30/06/2017.
 */

public class MyDialogFragment extends DialogFragment {

    public MyDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface RispostaDialog {
        void onFinishEditDialog(String inputText);
    }

    public static MyDialogFragment newInstance(String title) {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.Dialog_theme);

        //prendo e setto il titolo
        String title = getArguments().getString("title");
        alertDialogBuilder.setTitle(title);

        //creo edittext e linear layout per la formattazione
        final EditText mEditText = new EditText(getContext());
        //edittext scrollabile
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(! ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP));
                return false;
            }
        });
        mEditText.setVerticalScrollBarEnabled(true);
        mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mEditText.setSingleLine(false);
        mEditText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);


        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 300);
        layoutParams.setMargins(50,20,50,0);
        ll.addView(mEditText,layoutParams);

        //aggiunto l'elemento creato al dialog box
        alertDialogBuilder.setView(ll);

        //setto azione per pressione ok
        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RispostaDialog listener = (RispostaDialog) getActivity();
                listener.onFinishEditDialog(mEditText.getText().toString());
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });

        //chiudo il dialog box se si preme cancell
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });

        return alertDialogBuilder.create();
    }


}