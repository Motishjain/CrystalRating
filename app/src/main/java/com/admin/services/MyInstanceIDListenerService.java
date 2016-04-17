package com.admin.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by admin on 17-04-2016.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
