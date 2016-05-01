package com.admin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.admin.tasks.UpdateSubscriptionStatusTask;

/**
 * Created by Admin on 01-05-2016.
 */
public class CheckSubscriptionAlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        UpdateSubscriptionStatusTask updateSubscriptionStatusTask = new UpdateSubscriptionStatusTask(context);
        updateSubscriptionStatusTask.execute();
    }
}
