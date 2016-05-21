package com.admin.tasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.admin.database.DBHelper;
import com.admin.database.Subscription;
import com.admin.freddyspeaks.R;
import com.admin.freddyspeaks.SubscriptionInfoActivity;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.response_objects.SubscriptionResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 30-04-2016.
 */
public class FetchSubscriptionTask extends AsyncTask<String, Void, Void> {

    Integer[] resIdList = new Integer[]{R.drawable.bags};
    Dao<Subscription, Integer> subscriptionDao;
    Context context;
    ProgressDialog progressDialog;
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    public FetchSubscriptionTask (Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... input) {
        String outletCode = input[0];
        try {
            subscriptionDao = OpenHelperManager.getHelper(context, DBHelper.class).getCustomDao("Subscription");
            RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
            Call<SubscriptionResponse> fetchSubscriptionCall = restEndpointInterface.fetchSubscription(outletCode);
            fetchSubscriptionCall.enqueue(new Callback<SubscriptionResponse>() {
                @Override
                public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {
                    if (response.isSuccess()) {
                        SubscriptionResponse subscriptionResponse = response.body();
                        try {
                            QueryBuilder<Subscription,Integer> subscriptionQueryBuilder = subscriptionDao.queryBuilder();
                            List<Subscription> subscriptionList = subscriptionQueryBuilder.query();
                            if(subscriptionList.size()==0) {
                                Subscription subscription = new Subscription();
                                subscription.setExpiryDate(subscriptionResponse.getExpiryDate());
                                subscription.setActivationStatus(subscriptionResponse.getActivationStatus());
                                subscriptionDao.create(subscription);
                            }
                            else {
                                UpdateBuilder<Subscription,Integer> subscriptionUpdateBuilder = subscriptionDao.updateBuilder();
                                subscriptionUpdateBuilder.where().eq("id",subscriptionList.get(0).getId());
                                subscriptionUpdateBuilder.updateColumnExpression("activationStatus",subscriptionResponse.getActivationStatus());
                                subscriptionUpdateBuilder.update();
                            }
                        }
                        catch (SQLException e) {
                            Log.e("FetchSubscriptionTask","Failed to save subscription",e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SubscriptionResponse> call, Throwable t) {
                    Log.e("RatingSummary", "Unable to fetch feedback", t);
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void createNotification(Context context, String title, String body, PendingIntent resultPendingIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(Subscription subscription);
    }
}
