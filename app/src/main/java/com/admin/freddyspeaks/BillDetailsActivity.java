package com.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.dialogs.CustomDialogFragment;
import com.admin.util.ImageUtility;
import com.admin.util.KeyboardUtil;
import com.admin.util.ValidationUtil;
import com.admin.webservice.request_objects.FeedbackRequest;

public class BillDetailsActivity extends BaseActivity {

    TextInputLayout inputBillAmountLayout;
    TextView billAmount;
    FeedbackRequest feedback;
    ImageView backgroundBillDetailsImage;
    CustomDialogFragment dialogConfirmExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        feedback = (FeedbackRequest) extras.get("feedback");

        inputBillAmountLayout = (TextInputLayout) findViewById(R.id.inputBillAmountLayout);
        billAmount = (TextView) findViewById(R.id.inputBillAmountText);
        billAmount.setHint("\u20B9" + " " + "Bill Amount");
        backgroundBillDetailsImage = (ImageView) findViewById(R.id.backgroundBillDetailsImage);
        backgroundBillDetailsImage.setImageBitmap(ImageUtility.getImageBitmap(this,R.drawable.shopping_bg));
    }

    public void nextButtonClickHandler(View v) {
        if (ValidationUtil.isValidAmount(billAmount, inputBillAmountLayout, "Enter valid bill amount")) {
            KeyboardUtil.hideKeyboard(this);
            feedback.setBillAmount(Double.parseDouble(billAmount.getText().toString())+"");
            Intent displayReward = new Intent(BillDetailsActivity.this, RewardDisplayActivity.class);
            displayReward.putExtra("feedback", feedback);
            startActivity(displayReward);

        }
    }

    @Override
    public void onBackPressed() {
        dialogConfirmExit = CustomDialogFragment.newInstance(R.layout.dialog_confirm_exit, new CustomDialogFragment.CustomDialogListener() {
            @Override
            public void onDialogPositiveClick() {
                dialogConfirmExit.dismiss();
                Intent homePage = new Intent(BillDetailsActivity.this, HomePageActivity.class);
                homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homePage);
            }

            @Override
            public void onDialogNegativeClick() {
                dialogConfirmExit.dismiss();
            }
        });
        dialogConfirmExit.show(getSupportFragmentManager(), "");
    }
}