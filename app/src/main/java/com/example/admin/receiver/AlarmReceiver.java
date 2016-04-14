package com.example.admin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.admin.tasks.SetRandomQuestionsTask;

/**
 * Created by Admin on 4/6/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SetRandomQuestionsTask setRandomQuestionsTask = new SetRandomQuestionsTask(context,null);
        setRandomQuestionsTask.execute();
    }


}
