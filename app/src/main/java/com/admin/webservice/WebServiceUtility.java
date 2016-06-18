package com.admin.webservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.FailedServiceCall;
import com.admin.receiver.FailedServiceCallReceiver;
import com.admin.util.DateTimeUtility;
import com.admin.webservice.request_objects.FeedbackRequest;
import com.admin.webservice.response_objects.SaveServiceReponse;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 14-05-2016.
 */
public class WebServiceUtility {

    static Dao<FailedServiceCall, Integer> failedServiceCallDao;

    public static void submitFeedback(final Context context, final FeedbackRequest feedback) {
        feedback.setCreatedDate(DateTimeUtility.getLocalDate().toString());
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<SaveServiceReponse> submitFeedbackCall = restEndpointInterface.submitFeedback(feedback);
        submitFeedbackCall.enqueue(new Callback<SaveServiceReponse>() {
            @Override
            public void onResponse(Call<SaveServiceReponse> call, Response<SaveServiceReponse> response) {
                SaveServiceReponse saveServiceReponse = response.body();
                if (saveServiceReponse.isSuccess()) {
                    Log.i("Reward display", "Feedback sent successfully");
                }
            }

            @Override
            public void onFailure(Call<SaveServiceReponse> call, Throwable t) {
                Log.e("Reward display", "Failed to submit feedback");
                setRetryAlarm(context, AppConstants.SUBMIT_FEEDBACK_FAILURE_SID, feedback);
            }
        });
    }

    public static void setRetryAlarm(Context context,String serviceId, Object parameterObject) {
        try {
            failedServiceCallDao = OpenHelperManager.getHelper(context, DBHelper.class).getCustomDao("FailedServiceCall");
            QueryBuilder<FailedServiceCall, Integer> failedServiceCallQueryBuilder = failedServiceCallDao.queryBuilder();
            List<FailedServiceCall> failedServiceCalls = failedServiceCallQueryBuilder.query();
            if (failedServiceCalls.size() == 0) {
                Intent failedServiceIntent = new Intent(context, FailedServiceCallReceiver.class);
                PendingIntent failedServicePendingIntent = PendingIntent.getBroadcast(context, 0, failedServiceIntent, 0);
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        3 * AlarmManager.INTERVAL_HOUR, failedServicePendingIntent);
            }

            FailedServiceCall failedServiceCall = new FailedServiceCall();
            failedServiceCall.setServiceId(serviceId);
            Gson gson = new Gson();
            failedServiceCall.setParametersJsonString(gson.toJson(parameterObject));
            failedServiceCallDao.create(failedServiceCall);

            //new FailedServiceCallReceiver().onReceive(RewardDisplayActivity.this,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
