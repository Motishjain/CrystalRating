package com.admin.util;

import android.support.design.widget.TextInputLayout;
import android.widget.TextView;

/**
 * Created by Admin on 4/14/2016.
 */
public class ValidationUtil {

    public static boolean isTextViewEmpty(TextView textView, TextInputLayout textInputLayout,String errorMessage){
        if(textView.getText().toString().equals("")) {
            textInputLayout.setError(errorMessage);
            textView.findFocus();
            return true;
        }
        return false;
    }
}
