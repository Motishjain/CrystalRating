package com.admin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.admin.database.Subscription;
import com.admin.tasks.FetchSubscriptionTask;
import com.admin.tasks.SetRandomQuestionsTask;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 4/6/2016.
 */
public class DailyAlarmReceiver extends BroadcastReceiver implements FetchSubscriptionTask.FetchSubscriptionTaskListener{

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DailyAlarmReceiver", "called");
        this.context = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String outletCode = sharedPreferences.getString("outletCode", null);
        SetRandomQuestionsTask setRandomQuestionsTask = new SetRandomQuestionsTask(context,null);
        setRandomQuestionsTask.execute();
        FetchSubscriptionTask fetchSubscriptionTask = new FetchSubscriptionTask(context, null, false);
        fetchSubscriptionTask.execute(outletCode);
    }


    @Override
    public void onTaskCompleted(Subscription subscription) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(new Date());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dailyTaskExecutedDate", currentDate);
        editor.putString("activationStatus", subscription.getActivationStatus());
        editor.commit();
    }
}
