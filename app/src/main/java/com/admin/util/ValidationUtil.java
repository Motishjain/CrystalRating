package com.admin.util;

import android.support.design.widget.TextInputLayout;
import android.widget.TextView;

/**
 * Created by Admin on 4/14/2016.
 */
public class ValidationUtil {

    public static boolean isTextViewEmpty(TextView textView, TextInputLayout textInputLayout, String errorMessage) {
        if (textView.getText().toString().trim().equals("")) {
            textInputLayout.setError(errorMessage);
            textView.findFocus();
            return true;
        } else {
            textInputLayout.setError(null);
            return false;
        }
    }

    public static void resetForm(TextView textView, TextInputLayout textInputLayout) {
        textView.setText(null);
        textInputLayout.setError(null);
    }

    public static boolean isValidEmail(TextView textView, TextInputLayout textInputLayout, String errorMessage) {
        String emailId = textView.getText().toString();
        if (emailId.trim().equals("")) {
            return false;
        }
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            return true;
        } else {
            textInputLayout.setError(errorMessage);
            textView.findFocus();
            return false;
        }
    }

    public static boolean isValidCellNumber(TextView textView, TextInputLayout textInputLayout, String errorMessage) {
        String cellNumber = textView.getText().toString();
        if (cellNumber.trim().equals("")) {
            return false;
        }
        if (android.util.Patterns.PHONE.matcher(cellNumber).matches()) {
            return true;
        } else {
            textInputLayout.setError(errorMessage);
            textView.findFocus();
            return false;
        }
    }
    public static boolean isValidPincode(TextView textView, TextInputLayout textInputLayout, String errorMessage) {
        String pinCode = textView.getText().toString();
        if (pinCode.trim().equals("")) {
            return false;
        }
        if (pinCode.trim().length()==6) {
            return true;
        } else {
            textInputLayout.setError(errorMessage);
            textView.findFocus();
            return false;
        }
    }
}