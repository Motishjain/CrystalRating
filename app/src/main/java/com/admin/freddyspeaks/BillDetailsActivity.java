package com.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.util.ImageUtility;
import com.admin.util.ValidationUtil;
import com.admin.webservice.request_objects.FeedbackRequest;

public class BillDetailsActivity extends BaseActivity {

    TextInputLayout inputBillAmountLayout, inputBillNumberLayout;
    TextView billNumber, billAmount;
    FeedbackRequest feedback;
    ImageView backgroundBillDetailsImage;

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
        backgroundBillDetailsImage = (ImageView) findViewById(R.id.backgroundBillDetailsImage);
        backgroundBillDetailsImage.setImageBitmap(ImageUtility.getImageBitmap(R.drawable.bags));

    }

    public void resetButtonClickHandler(View v) {
        billAmount.setText("");
        billNumber.setText("");
        inputBillAmountLayout.setError(null);
        inputBillNumberLayout.setError(null);
    }

    public void nextButtonClickHandler(View v) {
        inputBillNumberLayout.setError(null);
        inputBillAmountLayout.setError(null);
        if (!ValidationUtil.isTextViewEmpty(billNumber,inputBillNumberLayout,"Please enter bill number") &&
                !ValidationUtil.isTextViewEmpty(billAmount,inputBillAmountLayout,"Please enter bill amount")) {
            feedback.setBillAmount(billAmount.getText().toString());
            feedback.setBillNumber(billNumber.getText().toString());
            Intent displayReward = new Intent(BillDetailsActivity.this, RewardDisplayActivity.class);
            displayReward.putExtra("feedback",feedback);
            startActivity(displayReward);
        }
    }
}