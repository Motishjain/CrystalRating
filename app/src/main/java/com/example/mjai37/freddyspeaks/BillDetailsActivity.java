package com.example.mjai37.freddyspeaks;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BillDetailsActivity extends AppCompatActivity {

    TextInputLayout inputBillAmountLayout, inputBillNumberLayout;
    TextView billNumber, billAmount;
    Button continueButton, resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputBillNumberLayout = (TextInputLayout) findViewById(R.id.inputBillNumberLayout);
        inputBillAmountLayout = (TextInputLayout) findViewById(R.id.inputBillAmountLayout);

        billNumber = (TextView) findViewById(R.id.inputBillNumberText);
        billAmount = (TextView) findViewById(R.id.inputBillAmountText);

    }

}
