package com.admin.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.admin.freddyspeaks.R;

/**
 * Created by verona1024.
 */
public class CustomDialogFragment extends DialogFragment {

    int layoutResourceId;
    private CustomDialogListener customDialogListener;
    private String[] customMessages;

    /* @return A new instance of fragment CustomDialogFragment.
            */
    // TODO: Rename and change types and number of parameters

    public static CustomDialogFragment newInstance(int layoutResourceId, CustomDialogListener customDialogListener, String... customMessages) {
        CustomDialogFragment fragment = new CustomDialogFragment();
        fragment.layoutResourceId = layoutResourceId;
        fragment.customDialogListener = customDialogListener;
        fragment.customMessages = customMessages;
        return fragment;
    }

    public interface CustomDialogListener {
        void onDialogPositiveClick();
        void onDialogNegativeClick();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(layoutResourceId, null);

        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogListener.onDialogPositiveClick();
            }
        });

        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        if(buttonCancel!=null) {
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialogListener.onDialogNegativeClick();
                }
            });
        }

        if(customMessages!=null) {
            for(int i = 0;i < customMessages.length; i++) {
                String customMessage = customMessages[i];
                TextView customTextView = (TextView) dialog.findViewById(getActivity().getResources().getIdentifier("customTextView"+(i+1), "id", getActivity().getPackageName()));
                customTextView.setText(customMessage);
            }
        }

        // Inflate and set the layout for the dialog
        builder.setView(dialog);

        return builder.create();
    }
}
