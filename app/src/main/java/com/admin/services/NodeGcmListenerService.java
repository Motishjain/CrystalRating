package com.admin.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.admin.freddyspeaks.R;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by admin on 17-04-2016.
 */
public class NodeGcmListenerService extends GcmListenerService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("", "From: " + from);
        Log.d("", "Message: " + message);

        if (from.startsWith("/topics/")) {
            createNotification(from, message);
        } else {
            // normal downstream message.
        }
    }

    private void createNotification(String title, String body) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}
