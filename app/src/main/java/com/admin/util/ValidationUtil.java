package com.admin.util;

import android.renderscript.Double2;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
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
            textInputLayout.setError("Please enter your Email Id");
            textView.findFocus();
            return false;
        }
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            textInputLayout.setError(null);
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
            textInputLayout.setError("Please enter your Mobile Number");
            textView.findFocus();
            return false;
        }
        if (cellNumber.trim().length() == 10) {
            textInputLayout.setError(null);
            return true;
        }
        else {
            textInputLayout.setError(errorMessage);
            textView.findFocus();
            return false;
        }
    }

    public static boolean isValidPincode(TextView textView, TextInputLayout textInputLayout, String errorMessage) {
        String pinCode = textView.getText().toString();
        if (pinCode.trim().equals("")) {
            textInputLayout.setError("Please enter Pin Code");
            textView.findFocus();
            return false;
        }
        if (pinCode.trim().length()==6) {
            textInputLayout.setError(null);
            return true;
        } else {
            textInputLayout.setError(errorMessage);
            textView.findFocus();
            return false;
        }
    }

    public static boolean isValidAmount(TextView textView, TextInputLayout textInputLayout, String errorMessage) {
        String amt=textView.getText().toString();

        if (amt.trim().equals("")) {
            textInputLayout.setError("Please enter Bill Amount");
            textView.findFocus();
            return false;
        }
        Double amtValue=Double.parseDouble(amt);
        if (amtValue<=0) {
            textInputLayout.setError(null);
            textInputLayout.setError(errorMessage);
            textView.findFocus();
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

}