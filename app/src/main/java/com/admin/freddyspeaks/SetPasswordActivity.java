package com.admin.freddyspeaks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.admin.database.DBHelper;
import com.admin.database.Outlet;
import com.admin.util.ValidationUtil;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class SetPasswordActivity extends AppCompatActivity {

    TextInputLayout inputPinLayout, inputPin2Layout;
    EditText inputPinText, inputPin2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        inputPinLayout = (TextInputLayout) findViewById(R.id.inputPinLayout);
        inputPin2Layout = (TextInputLayout) findViewById(R.id.inputPin2Layout);
        inputPinText = (EditText) findViewById(R.id.inputPinText);
        inputPin2Text = (EditText) findViewById(R.id.inputPin2Text);
    }

    public void configureRewards(View v) {
        String pin = inputPinText.getText().toString();

        if(!ValidationUtil.isTextViewEmpty(inputPinText,inputPinLayout,"Please enter 4-digit pin")
                && !ValidationUtil.isTextViewEmpty(inputPin2Text,inputPin2Layout,"Please re-enter pin")
                &&  ValidationUtil.isValidPin(inputPinText,inputPin2Text,inputPinLayout,inputPin2Layout)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("outletPin", pin);
            editor.commit();
            Intent configureRewards = new Intent(SetPasswordActivity.this, RewardConfigurationActivity.class);
            configureRewards.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(configureRewards);
        }
    }

}
