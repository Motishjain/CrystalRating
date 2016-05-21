package com.admin.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.admin.freddyspeaks.R;
import com.admin.freddyspeaks.SubscriptionInfoActivity;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by admin on 17-04-2016.
 */
public class NodeGcmListenerService extends GcmListenerService {



    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("", "From: " + from);
        Log.d("", "Message: " + message);
    }


}
