package com.admin.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.admin.crystalrating.R;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.response_objects.SaveServiceReponse;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String outletCode = sharedPreferences.getString("outletCode", null);

        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<SaveServiceReponse> saveGCMTokenCall = restEndpointInterface.saveGCMToken(outletCode, token);

        saveGCMTokenCall.enqueue(new Callback<SaveServiceReponse>() {
            @Override
            public void onResponse(Call<SaveServiceReponse> call, Response<SaveServiceReponse> response) {
                Log.i("RegistrationService","Token saved successfully");
            }

            @Override
            public void onFailure(Call<SaveServiceReponse> call, Throwable t) {
                Log.e("RegistrationService","Unable to save token");
            }
        });
    }

}
