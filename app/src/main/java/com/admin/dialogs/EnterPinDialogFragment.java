package com.admin.dialogs;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.admin.constants.AppConstants;
import com.admin.freddyspeaks.R;
import com.admin.util.KeyboardUtil;

/**
 * Created by Admin on 26-06-2016.
 */
public class EnterPinDialogFragment extends DialogFragment  {

    EnterPinDialogFragment.EnterPinDialogListener enterPinDialogListener;
    String title;
    TextInputLayout inputPinLayout;
    EditText inputPinText;

    public static EnterPinDialogFragment newInstance(String title, EnterPinDialogFragment.EnterPinDialogListener enterPinDialogListener) {
        EnterPinDialogFragment fragment = new EnterPinDialogFragment();
        fragment.enterPinDialogListener = enterPinDialogListener;
        fragment.title = title;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_enter_pin, null);

        inputPinLayout = (TextInputLayout) dialog.findViewById(R.id.inputPinLayout);
        inputPinText = (EditText) dialog.findViewById(R.id.inputPinText);

        KeyboardUtil.showKeyboard(getActivity(),inputPinText);
        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.hideKeyboard(getActivity(), inputPinText);
                if(inputPinText.getText().toString().equals(AppConstants.OUTLET_PIN)) {
                    enterPinDialogListener.onDialogPositiveClick();
                }
                else {
                    inputPinLayout.setError("Incorrect pin. Try again!");
                }

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPinDialogListener.onDialogNegativeClick();
            }
        });

        // Inflate and set the layout for the dialog
        builder.setView(dialog);

        return builder.create();
    }

    public interface EnterPinDialogListener {
        void onDialogPositiveClick();
        void onDialogNegativeClick();
    }
}
