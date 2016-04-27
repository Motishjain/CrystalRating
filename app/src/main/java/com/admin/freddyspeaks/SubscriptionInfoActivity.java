package com.admin.freddyspeaks;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Outlet;
import com.android.vending.billing.IInAppBillingService;
import com.admin.freddyspeaks.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionInfoActivity extends BaseActivity {

    Button payNowButton;
    Spinner subscriptionSpinner;
    Dao<Outlet, Integer> outletDao;
    List<String> subscriptionList;
    List<Integer> subscriptionAmountList;
    TextView subscriptionSummary,transactionResultMessage;

    boolean active,activeTrial,subscriptionPending,subscriptionExpired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_info);

        payNowButton = (Button) findViewById(R.id.payNowButton);
        subscriptionSpinner = (Spinner) findViewById(R.id.subscriptionSpinner);
        subscriptionSummary = (TextView) findViewById(R.id.subscriptionSummary);
        transactionResultMessage = (TextView) findViewById(R.id.transactionResultMessage);


        if(active) {

        }
        else if(activeTrial) {

        }
        else if(subscriptionPending) {

        }
        else if(subscriptionExpired) {

        }

        subscriptionSummary.setText("Free trial till ");

        subscriptionList = new ArrayList<>();
        subscriptionList.add("3 months (Rs. 500)");
        subscriptionList.add("6 months (Rs. 800)");
        subscriptionList.add("1 year (Rs. 1200)");
        subscriptionAmountList = new ArrayList<>();
        subscriptionAmountList.add(500);
        subscriptionAmountList.add(800);
        subscriptionAmountList.add(1200);

        try {
            outletDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Outlet");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, subscriptionList);
        subscriptionSpinner.setAdapter(dataAdapter);

        payNowButton.setVisibility(View.VISIBLE);
    }

    public void payNow(View v) {
        Outlet currentOutlet = null;
        int selectedSubscriptionIndex = subscriptionSpinner.getSelectedItemPosition();
        QueryBuilder<Outlet, Integer> outletQueryBuilder = outletDao.queryBuilder();
        try {
            currentOutlet = outletQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e("OutletDetailsActivity", "Outlet details fetch error");
        }

        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();

        builder.setKey(""); //Put your live KEY here
        builder.setSalt(""); //Put your live SALT here
        builder.setMerchantId(AppConstants.MERCHANT_ID);


        builder.setIsDebug(true);
        builder.setDebugKey("F4Vvyz");// Debug Key
        builder.setDebugMerchantId("4828127");// Debug Merchant ID
        builder.setDebugSalt("Z6cEj6SP");// Debug Salt

        builder.setAmount(subscriptionAmountList.get(selectedSubscriptionIndex));

        builder.setTnxId("0nf7");


        builder.setPhone(currentOutlet.getCellNumber());

        builder.setProductName("Subscription for"+subscriptionList.get(selectedSubscriptionIndex));

        builder.setFirstName(currentOutlet.getOutletName());

        builder.setEmail(currentOutlet.getEmail());

        builder.setsUrl("https://mobiletest.payumoney.com/mobileapp/payumoney/success.php");
        builder.setfUrl("https://mobiletest.payumoney.com/mobileapp/payumoney/failure.php");
        builder.setUdf1("Outlet code - "+currentOutlet.getOutletCode());
        builder.setUdf2("");
        builder.setUdf3("");
        builder.setUdf4("");
        builder.setUdf5("");

        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();

        PayUmoneySdkInitilizer.startPaymentActivityForResult(this, paymentParam);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                Log.i("SubscriptionInfo", "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                showMessage(true, "Payment Success Id : " + paymentId);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("SubscriptionInfo", "failure");
                showMessage(false, "Transaction was cancelled");
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                Log.i("app_activity", "failure");

                if (data != null) {
                    if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {
                        showMessage(false, "Transaction was cancelled");
                    } else {
                        showMessage(false, "Transaction failure");
                    }
                }
                //Write your code if there's no result
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
                Log.i("SubscriptionInfo", "User returned without login");
                showMessage(false, "User returned without login");
            }
        }

    }

    void showMessage(boolean success, String message) {
        if(success) {
            transactionResultMessage.setVisibility(View.VISIBLE);
            transactionResultMessage.setText(message);
            payNowButton.setVisibility(View.GONE);
        }
        else {

        }
    }

    public void closeActivity(View v) {
        this.finish();
    }
}