package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.webservice.request_objects.FeedbackRequest;

public class BillDetailsActivity extends AppCompatActivity {

    TextInputLayout inputBillAmountLayout, inputBillNumberLayout;
    TextView billNumber, billAmount;
    Button continueButton, resetButton;
    FeedbackRequest feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        feedback = (FeedbackRequest)extras.get("feedback");

        inputBillNumberLayout = (TextInputLayout) findViewById(R.id.inputBillNumberLayout);
        inputBillAmountLayout = (TextInputLayout) findViewById(R.id.inputBillAmountLayout);

        billNumber = (TextView) findViewById(R.id.inputBillNumberText);
        billAmount = (TextView) findViewById(R.id.inputBillAmountText);

        continueButton = (Button) findViewById(R.id.billInfoContinueButton);
        resetButton = (Button) findViewById(R.id.billInfoResetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billAmount.setText("");
                billNumber.setText("");
                inputBillAmountLayout.setError(null);
                inputBillNumberLayout.setError(null);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allFieldsEntered = true;
                if (billNumber.getText().toString().equals("")) {
                    inputBillNumberLayout.setError("Please enter bill number");
                    billNumber.findFocus();
                    allFieldsEntered = false;
                }
                if (billAmount.getText().toString().equals("")) {
                    inputBillAmountLayout.setError("Please enter bill amount");
                    billAmount.findFocus();
                    allFieldsEntered = false;
                }
                if(allFieldsEntered) {
                    feedback.setBillAmount(billAmount.getText().toString());
                    feedback.setBillNumber(billNumber.getText().toString());
                    Intent displayReward = new Intent(BillDetailsActivity.this, RewardDisplayActivity.class);
                    displayReward.putExtra("feedback",feedback);
                    startActivity(displayReward);
                }
            }
        });
    }
}