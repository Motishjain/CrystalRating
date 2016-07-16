package com.admin.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Admin on 4/12/2016.
 */
public class DialogBuilderUtil {

    public static void showPromptDialog(Context context, String title, String msg, DialogInterface.OnClickListener positiveButtonClickHandler, DialogInterface.OnClickListener negativeButtonClickHandler) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, positiveButtonClickHandler)
                .setNegativeButton(android.R.string.no, negativeButtonClickHandler)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showMessageDialog(Context context, String title, String msg, String okButton) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, okButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
