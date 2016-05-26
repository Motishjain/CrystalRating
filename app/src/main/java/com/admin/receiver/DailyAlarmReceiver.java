package com.admin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.admin.tasks.FetchSubscriptionTask;
import com.admin.tasks.SetRandomQuestionsTask;

/**
 * Created by Admin on 4/6/2016.
 */
public class DailyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("DailyAlarmReceiver ---", "called");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String outletCode = sharedPreferences.getString("outletCode", null);
        SetRandomQuestionsTask setRandomQuestionsTask = new SetRandomQuestionsTask(context,null);
        setRandomQuestionsTask.execute();
        FetchSubscriptionTask fetchSubscriptionTask = new FetchSubscriptionTask(context, null, false);
        fetchSubscriptionTask.execute(outletCode);
    }


}
