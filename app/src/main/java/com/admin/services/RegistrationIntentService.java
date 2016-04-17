package com.admin.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.admin.freddyspeaks.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by admin on 17-04-2016.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            sendRegistrationToServer(token);
        }
        catch(IOException e) {
            Log.e(TAG,"Unable to register device",e);
        }
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

}
