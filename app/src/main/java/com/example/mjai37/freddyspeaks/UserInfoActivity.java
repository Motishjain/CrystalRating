package com.example.mjai37.freddyspeaks;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserInfoActivity extends AppCompatActivity {

    EditText userName,userPhoneNumber;
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName = (EditText) findViewById(R.id.inputUserNameText);
        userPhoneNumber = (EditText) findViewById(R.id.inputUserPhoneNumberText);
        continueButton = (Button) findViewById(R.id.user_info_continue_button);


    }

}
