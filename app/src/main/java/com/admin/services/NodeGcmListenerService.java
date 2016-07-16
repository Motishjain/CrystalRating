package com.admin.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.admin.crystalrating.R;
import com.admin.crystalrating.SubscriptionInfoActivity;
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
        Bundle notification  = data.getBundle("notification");

        Intent resultIntent = new Intent(this, SubscriptionInfoActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SubscriptionInfoActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        createNotification(notification.getString("title"), notification.getString("body"),resultPendingIntent);

    }

    private void createNotification(String title, String body, PendingIntent resultPendingIntent) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.application_icon).setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}
