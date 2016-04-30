package com.admin.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.admin.database.DBHelper;
import com.admin.database.Subscription;
import com.admin.freddyspeaks.R;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.response_objects.SubscriptionResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

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
    OnTaskCompleted onTaskCompleted;

    public FetchSubscriptionTask (Context context, OnTaskCompleted onTaskCompleted) {
        this.context = context;
        this.onTaskCompleted = onTaskCompleted;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... input) {
        String outletCode = input[0];
        try {
            subscriptionDao = OpenHelperManager.getHelper(context, DBHelper.class).getCustomDao("Outlet");
            QueryBuilder<Subscription,Integer> subscriptionQueryBuilder = subscriptionDao.queryBuilder();
            List<Subscription> subscriptionList = subscriptionQueryBuilder.query();
            if(subscriptionList.size()>0) {
                onTaskCompleted.onTaskCompleted(subscriptionList.get(0));
                return null;
            }
            //Load Retrofit API
            RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
            Call<SubscriptionResponse> fetchSubscriptionCall = restEndpointInterface.fetchSubscription(outletCode);
            fetchSubscriptionCall.enqueue(new Callback<SubscriptionResponse>() {
                @Override
                public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {
                    if (response.isSuccess()) {
                        SubscriptionResponse subscriptionResponse = response.body();
                        Subscription subscription = new Subscription();
                        subscription.setExpiryDate(subscriptionResponse.getExpiryDate());
                        subscription.setActivationStatus(subscriptionResponse.getActivationStatus());
                        try {
                            subscriptionDao.create(subscription);
                            onTaskCompleted.onTaskCompleted(subscription);
                        }
                        catch (SQLException e) {
                            Log.e("FetchSubscriptionTask","Failed to save subscription",e);
                            //TODO show error dialog
                            onTaskCompleted.onTaskCompleted(null);
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

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(Subscription subscription);
    }
}
