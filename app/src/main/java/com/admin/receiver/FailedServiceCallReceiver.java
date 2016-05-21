package com.admin.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.FailedServiceCall;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.WebServiceUtility;
import com.admin.webservice.request_objects.ExtendSubscriptionRequest;
import com.admin.webservice.request_objects.FeedbackRequest;
import com.admin.webservice.response_objects.SaveServiceReponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 14-05-2016.
 */
public class FailedServiceCallReceiver extends BroadcastReceiver {

    Dao<FailedServiceCall, Integer> failedServiceCallDao;
    QueryBuilder<FailedServiceCall, Integer> failedServiceCallQueryBuilder;
    DeleteBuilder<FailedServiceCall, Integer> failedServiceCallDeleteBuilder;
    int failedServiceCallCount;
    RestEndpointInterface restEndpointInterface;
    Gson gson = new Gson();
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        try {
            failedServiceCallDao = OpenHelperManager.getHelper(context, DBHelper.class).getCustomDao("FailedServiceCall");
            failedServiceCallQueryBuilder = failedServiceCallDao.queryBuilder();
            List<FailedServiceCall> failedServiceCalls = failedServiceCallQueryBuilder.query();
            failedServiceCallCount = failedServiceCalls.size();

            if (failedServiceCallCount > 0) {
                failedServiceCallDeleteBuilder = failedServiceCallDao.deleteBuilder();
                restEndpointInterface = RetrofitSingleton.newInstance();
                for (final FailedServiceCall failedServiceCall : failedServiceCalls) {
                    switch (failedServiceCall.getServiceId()) {
                        case AppConstants.SUBMIT_FEEDBACK_FAILURE_SID:
                            submitFeedback(failedServiceCall);
                            break;
                        case AppConstants.EXTEND_SUBSCRIPTION_FAILURE_SID:
                            extendSubscription(failedServiceCall);
                            break;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    void submitFeedback(final FailedServiceCall failedServiceCall) {
        final FeedbackRequest feedback = gson.fromJson(failedServiceCall.getParametersJsonString(),
                new TypeToken<FeedbackRequest>() {}.getType());
        Call<SaveServiceReponse> submitFeedbackCall = restEndpointInterface.submitFeedback(feedback);

        submitFeedbackCall.enqueue(new Callback<SaveServiceReponse>() {
            @Override
            public void onResponse(Call<SaveServiceReponse> call, Response<SaveServiceReponse> response) {
                SaveServiceReponse saveServiceReponse = response.body();
                if (saveServiceReponse.isSuccess()) {
                    Log.i("FailedServiceCall", "Service call retried successfully");
                    removeFailedCall(failedServiceCall.getId());
                }
            }

            @Override
            public void onFailure(Call<SaveServiceReponse> call, Throwable t) {
                Log.e("FailedServiceCall", "Service call retry failed", t);
            }
        });
    }

    void extendSubscription(final FailedServiceCall failedServiceCall) {
        final ExtendSubscriptionRequest extendSubscriptionRequest = gson.fromJson(failedServiceCall.getParametersJsonString()
                , new TypeToken<ExtendSubscriptionRequest>() {}.getType());

        Call<SaveServiceReponse> extendSubscriptionCall = restEndpointInterface.extendSubscription(extendSubscriptionRequest);
        extendSubscriptionCall.enqueue(new Callback<SaveServiceReponse>() {
            @Override
            public void onResponse(Call<SaveServiceReponse> call, Response<SaveServiceReponse> response) {
                SaveServiceReponse saveServiceReponse = response.body();
                if (saveServiceReponse.isSuccess()) {
                    Log.i("FailedServiceCall", "Service call retried successfully");
                    removeFailedCall(failedServiceCall.getId());
                }
            }

            @Override
            public void onFailure(Call<SaveServiceReponse> call, Throwable t) {
                Log.e("FailedServiceCall", "Service call retry failed", t);
            }
        });
    }


    void removeFailedCall(Integer failedServiceCallId) {
        try {
            failedServiceCallDeleteBuilder.reset();
            failedServiceCallDeleteBuilder.where().eq("id", failedServiceCallId);
            failedServiceCallDeleteBuilder.delete();
            failedServiceCallCount--;
        } catch (SQLException e) {
            Log.e("FailedServiceCall", "Service call delete failed", e);
        }
        if (failedServiceCallCount == 0) {
            Intent failedServiceIntent = new Intent(context, FailedServiceCallReceiver.class);
            PendingIntent failedServicePendingIntent = PendingIntent.getBroadcast(context, 0, failedServiceIntent, 0);
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.cancel(failedServicePendingIntent);
        }
    }
}