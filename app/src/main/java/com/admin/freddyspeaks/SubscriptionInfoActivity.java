package com.admin.freddyspeaks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.adapter.SubscriptionAdapter;
import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Subscription;
import com.admin.tasks.FetchSubscriptionTask;
import com.admin.view.CustomProgressDialog;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.WebServiceUtility;
import com.admin.webservice.request_objects.ExtendSubscriptionRequest;
import com.admin.webservice.response_objects.SaveServiceReponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionInfoActivity extends AppCompatActivity implements FetchSubscriptionTask.FetchSubscriptionTaskListener, SubscriptionAdapter.SubscriptionSelectionListener {

    RecyclerView paymentRecyclerView;

    Subscription subscription;
    SubscriptionAdapter.SubscriptionInfo subscriptionInfo;
    String outletCode;
    ProgressDialog progressDialog;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");

    boolean noFooterFlag;
    ImageView subscriptionStatusIndicator;
    TextView activationStatusTextView, activationStatusDescription, subscriptionInfoComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){public void onClick(View v){closeActivity(v);}});
        subscriptionStatusIndicator = (ImageView) findViewById(R.id.subscriptionStatusIndicator);
        activationStatusTextView = (TextView) findViewById(R.id.activationStatusTextView);
        activationStatusDescription = (TextView) findViewById(R.id.activationStatusDescription);
        paymentRecyclerView = (RecyclerView) findViewById(R.id.paymentRecyclerView);
        subscriptionInfoComment = (TextView) findViewById(R.id.subscriptionInfoComment);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        paymentRecyclerView.setLayoutManager(layoutManager);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        outletCode = sharedPreferences.getString("outletCode", null);

        progressDialog = CustomProgressDialog.createCustomProgressDialog(this);
        progressDialog.show();

        FetchSubscriptionTask fetchSubscriptionTask = new FetchSubscriptionTask(this, this, true, false);
        fetchSubscriptionTask.execute(outletCode);
    }

    public void setupSubscriptionScreen() {
        String activationStatus = subscription.getActivationStatus();
        String expiryDate = subscription.getExpiryDate();
        int daysRemaining = subscription.getDaysRemaining();

        if (activationStatus.equals(AppConstants.SUBSCRIPTION_TRIAL)) {
            subscriptionStatusIndicator.setImageResource(R.drawable.ic_done_green_48dp);
            activationStatusTextView.setText("Active (Trial)");
            activationStatusDescription.setText("Expires On:" + expiryDate+ ((daysRemaining<=3)?"("+daysRemaining+" "+((daysRemaining>1)?"days":"day")+" left)":""));
        } else if (activationStatus.equals(AppConstants.SUBSCRIPTION_ACTIVE)) {
            subscriptionStatusIndicator.setImageResource(R.drawable.ic_done_green_48dp);
            activationStatusTextView.setText("Active");
            activationStatusDescription.setText("Expires On:" + expiryDate+ ((daysRemaining<=3)?"("+daysRemaining+" "+((daysRemaining>1)?"days":"day")+" left)":""));
        } else if (activationStatus.equals(AppConstants.SUBSCRIPTION_PENDING)) {
            subscriptionStatusIndicator.setImageResource(R.drawable.ic_warning_red_500_48dp);
            activationStatusTextView.setText("Pending Renewal");
            activationStatusDescription.setText("Expired On:" + expiryDate);
            noFooterFlag = true;
            subscriptionInfoComment.setText("The application will continue to work till "+expiryDate+ ((daysRemaining<=7)?"("+daysRemaining+" "+((daysRemaining>1)?"days":"day")+")":"")+". Kindly renew your subscription");
        } else if (activationStatus.equals(AppConstants.SUBSCRIPTION_EXPIRED)) {
            subscriptionStatusIndicator.setImageResource(R.drawable.ic_error_outline_black_48dp);
            activationStatusTextView.setText("Expired");
            activationStatusDescription.setText("Expired On:" + expiryDate);
            noFooterFlag = true;
            subscriptionInfoComment.setText("Your service has expired, kindly renew the same to get going again !");
        }

        final SubscriptionAdapter subscriptionAdapter = new SubscriptionAdapter(this,
                R.layout.content_subscription_item, getSubscriptionData(), this);
        paymentRecyclerView.setAdapter(subscriptionAdapter);

    }


    private List<SubscriptionAdapter.SubscriptionInfo> getSubscriptionData() {
        List<SubscriptionAdapter.SubscriptionInfo> subscriptionInfoList = new ArrayList<>();

        subscriptionInfoList.add(new SubscriptionAdapter.SubscriptionInfo(
                "1 year subscription",
                "12",
                800
        ));

        subscriptionInfoList.add(new SubscriptionAdapter.SubscriptionInfo(
                "6 month subscription",
                "6",
                500
        ));

        subscriptionInfoList.add(new SubscriptionAdapter.SubscriptionInfo(
                "3 month subscription",
                "3",
                300
        ));

        return subscriptionInfoList;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i("SubscriptionInfo", "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                subscription.setActivationStatus(AppConstants.SUBSCRIPTION_ACTIVE);
                try {
                    Date date = simpleDateFormat.parse(subscription.getExpiryDate());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.MONTH,Integer.parseInt(subscriptionInfo.getMonths()));
                    subscription.setExpiryDate(simpleDateFormat.format(calendar.getTime()));
                }
                catch(Exception e) {
                    Log.e("Subscription","Failed to parse expiry date",e);
                }
                updateSubscription(subscription);
                setupSubscriptionScreen();

                final ExtendSubscriptionRequest extendSubscriptionRequest = new ExtendSubscriptionRequest();
                extendSubscriptionRequest.setOutletCode(outletCode);
                extendSubscriptionRequest.setPaymentId(paymentId);
                extendSubscriptionRequest.setAmount(subscriptionInfo.getPrice() + "");
                extendSubscriptionRequest.setSubscribedMonths(subscriptionInfo.getMonths());
                extendSubscriptionRequest.setPaymentDate(new Date());
                RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
                Call<SaveServiceReponse> extendSubscriptionCall = restEndpointInterface.extendSubscription(extendSubscriptionRequest);
                extendSubscriptionCall.enqueue(new Callback<SaveServiceReponse>() {
                    @Override
                    public void onResponse(Call<SaveServiceReponse> call, Response<SaveServiceReponse> response) {
                        SaveServiceReponse saveServiceReponse = response.body();
                        if (saveServiceReponse.isSuccess()) {
                            Log.i("Subscription Info", "Subscription extended successfully");
                        }
                    }

                    @Override
                    public void onFailure(Call<SaveServiceReponse> call, Throwable t) {
                        Log.e("Subscription Info", "Subscription extension failed", t);
                        WebServiceUtility.setRetryAlarm(SubscriptionInfoActivity.this, AppConstants.EXTEND_SUBSCRIPTION_FAILURE_SID, extendSubscriptionRequest);
                    }
                });

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
        if (success) {
        } else {

        }
    }

    public void closeActivity(View v) {
        Intent homePage = new Intent(SubscriptionInfoActivity.this, HomePageActivity.class);
        homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homePage);
    }

    @Override
    public void onTaskCompleted(Subscription subscription) {
        if (subscription != null) {
            this.subscription = subscription;
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupSubscriptionScreen();
                }
            });
        }
    }

    @Override
    public void onSubscriptionClicked(SubscriptionAdapter.SubscriptionInfo subscriptionInfo) {
        this.subscriptionInfo = subscriptionInfo;
    }

    private void updateSubscription(Subscription subscription) {
        try {
            Dao<Subscription, Integer> subscriptionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Subscription");
            UpdateBuilder<Subscription, Integer> subscriptionUpdateBuilder = subscriptionDao.updateBuilder();
            subscriptionUpdateBuilder.where().eq("id", subscription.getId());
            subscriptionUpdateBuilder.updateColumnValue("activationStatus", subscription.getActivationStatus());
            subscriptionUpdateBuilder.updateColumnValue("expiryDate", subscription.getExpiryDate());
            subscriptionUpdateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}