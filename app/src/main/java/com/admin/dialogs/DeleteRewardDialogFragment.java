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
public class DeleteRewardDialogFragment extends DialogFragment {

    public interface DeleteRewardDialogListener {
        public void onDeleteDialogPositiveClick(DialogFragment dialog);
        public void onDeleteDialogNegativeClick(DialogFragment dialog);
    }

    private DeleteRewardDialogListener deleteRewardDialogListener;
    private DeleteRewardDialogFragment deleteRewardDialogFragment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            deleteRewardDialogListener = (DeleteRewardDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement deleteRewardDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        deleteRewardDialogFragment = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_rewards_delete, null);

        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRewardDialogListener.onDeleteDialogPositiveClick(deleteRewardDialogFragment);
            }
        });

        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRewardDialogListener.onDeleteDialogNegativeClick(deleteRewardDialogFragment);
            }
        });

        // Inflate and set the layout for the dialog
        builder.setView(dialog);

        return builder.create();
    }
}
