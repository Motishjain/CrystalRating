package com.admin.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.admin.freddyspeaks.R;

/**
 * Created by verona1024.
 */
public class CustomDialogFragment extends DialogFragment {

    int layoutResourceId;
    private CustomDialogListener customDialogListener;

    /* @return A new instance of fragment CustomDialogFragment.
            */
    // TODO: Rename and change types and number of parameters

    public static CustomDialogFragment newInstance(int layoutResourceId) {
        CustomDialogFragment fragment = new CustomDialogFragment();
        fragment.layoutResourceId = layoutResourceId;
        return fragment;
    }

    public interface CustomDialogListener {
        void onDialogPositiveClick();
        void onDialogNegativeClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            customDialogListener = (CustomDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement customDialogListener");
        }
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
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogListener.onDialogNegativeClick();
            }
        });

        // Inflate and set the layout for the dialog
        builder.setView(dialog);

        return builder.create();
    }
}
