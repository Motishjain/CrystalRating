package com.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.admin.adapter.SubscriptionAdapter;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;

import java.util.ArrayList;

public class SubscriptionInfoActivity extends BaseActivity {

    ListView listViewPayment;

    boolean active = false,activeTrial = false,subscriptionPending = false,subscriptionExpired = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_info);

        listViewPayment = (ListView) findViewById(R.id.listViewPayment);

        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.content_subscription_header, listViewPayment, false);
        View footer = inflater.inflate(R.layout.content_subscription_footer, listViewPayment, false);

        LinearLayout linearLayoutActiveOrTrial = (LinearLayout)
                header.findViewById(R.id.linearLayoutActiveTrial);
        LinearLayout linearLayoutPending = (LinearLayout)
                header.findViewById(R.id.linearLayoutPendingReneval);
        LinearLayout linearLayoutExpired = (LinearLayout)
                header.findViewById(R.id.linearLayoutPaymentExpired);

        boolean noFooterFlag = false;

        if(activeTrial) {
            linearLayoutActiveOrTrial.setVisibility(View.VISIBLE);
            linearLayoutPending.setVisibility(View.GONE);
            linearLayoutExpired.setVisibility(View.GONE);

            TextView head = (TextView) header.findViewById(R.id.textViewHead);
            head.setText("Active (Trial)");
            TextView subhead = (TextView) header.findViewById(R.id.textViewSubhead);
            subhead.setText("Expires On: 25 May 2016 (30 days)");

            noFooterFlag = true;
        }
        else if(active) {
            linearLayoutActiveOrTrial.setVisibility(View.VISIBLE);
            linearLayoutPending.setVisibility(View.GONE);
            linearLayoutExpired.setVisibility(View.GONE);

            TextView head = (TextView) header.findViewById(R.id.textViewHead);
            head.setText("Active");
            TextView subhead = (TextView) header.findViewById(R.id.textViewSubhead);
            subhead.setText("Expires On: 25 May 2016 (30 days)");

            noFooterFlag = true;
        }
        else if(subscriptionPending) {
            linearLayoutActiveOrTrial.setVisibility(View.GONE);
            linearLayoutPending.setVisibility(View.VISIBLE);
            linearLayoutExpired.setVisibility(View.GONE);

            TextView head = (TextView) header.findViewById(R.id.textViewPendingRenewal);
            head.setText("Pending Renewal");
            TextView subhead = (TextView) header.findViewById(R.id.textViewSubheadPendingRenewal);
            subhead.setText("Expired On: 25 April 2016");

            TextView footerMessage = (TextView) footer.findViewById(R.id.textViewFooter);
            footerMessage.setText("The application will continue to work till 2 June 2016(7 days). Kindly renew your subscription");
        }
        else if(subscriptionExpired) {
            linearLayoutActiveOrTrial.setVisibility(View.GONE);
            linearLayoutPending.setVisibility(View.GONE);
            linearLayoutExpired.setVisibility(View.VISIBLE);

            TextView head = (TextView) header.findViewById(R.id.textViewExpired);
            head.setText("Expired");
            TextView subhead = (TextView) header.findViewById(R.id.textViewSubheadExpired);
            subhead.setText("Expired On: 25 May 2016");

            TextView footerMessage = (TextView) footer.findViewById(R.id.textViewFooter);
            footerMessage.setText("Your service has expired, kindly renew the same to get going again !");
        }

        SubscriptionAdapter subscriptionAdapter = new SubscriptionAdapter(this,
                R.layout.content_subscription_info, getSubscriptionData());

        listViewPayment.setAdapter(subscriptionAdapter);

        listViewPayment.addHeaderView(header);

        if (!noFooterFlag){
            listViewPayment.addFooterView(footer);
        }
    }

    private ArrayList<SubscriptionAdapter.SubscriptionInfo> getSubscriptionData(){
        ArrayList<SubscriptionAdapter.SubscriptionInfo> subscriptionInfos = new ArrayList<>();

        subscriptionInfos.add(new SubscriptionAdapter.SubscriptionInfo(
                "1 year subscription",
                2500
        ));

        subscriptionInfos.add(new SubscriptionAdapter.SubscriptionInfo(
                "6 month subscription",
                1500
        ));

        subscriptionInfos.add(new SubscriptionAdapter.SubscriptionInfo(
                "3 month subscription",
                800
        ));

        return  subscriptionInfos;
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
        }
        else {

        }
    }

    public void closeActivity(View v) {
        this.finish();
    }
}