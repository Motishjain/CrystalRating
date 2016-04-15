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
import android.view.View;

import com.android.vending.billing.IInAppBillingService;
import com.admin.freddyspeaks.R;

public class SubscriptionInfoActivity extends AppCompatActivity {

    IInAppBillingService mService;
    ServiceConnection mServiceConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        establishConnection();
        setContentView(R.layout.activity_subscription_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    void establishConnection() {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    void subscribe(int months) {
        /*Bundle bundle = mService.getBuyIntent(3, "com.example.myapp",
                "", "subs", developerPayload);

        PendingIntent pendingIntent = bundle.getParcelable(RESPONSE_BUY_INTENT);
        if (bundle.getInt(RESPONSE_CODE) == BILLING_RESPONSE_RESULT_OK) {
            // Start purchase flow (this brings up the Google Play UI).
            // Result will be delivered through onActivityResult().
            startIntentSenderForResult(pendingIntent, RC_BUY, new Intent(),
                    Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
        }*/
    }
}
