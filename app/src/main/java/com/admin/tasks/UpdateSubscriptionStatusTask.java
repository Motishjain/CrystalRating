package com.admin.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Subscription;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin on 01-05-2016.
 */
public class UpdateSubscriptionStatusTask extends AsyncTask<Void, Void, Void> implements FetchSubscriptionTask.OnTaskCompleted{

    Subscription subscription;
    Context context;
    Dao<Subscription, Integer> subscriptionDao;

    public UpdateSubscriptionStatusTask(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... input) {
        try {
            subscriptionDao = OpenHelperManager.getHelper(context, DBHelper.class).getCustomDao("Subscription");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String outletCode = sharedPreferences.getString("outletCode", null);
        FetchSubscriptionTask fetchSubscriptionTask = new FetchSubscriptionTask(context, this);
        fetchSubscriptionTask.doInBackground(outletCode);
        return null;
    }


    @Override
    public void onTaskCompleted(Subscription subscription) {
        if(subscription != null) {
            this.subscription = subscription;
            updateSubscriptionStatus();
        }
    }

    public void updateSubscriptionStatus() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        String newStatus = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date expirationDate = simpleDateFormat.parse(subscription.getExpiryDate());
            if(!subscription.getActivationStatus().equals(AppConstants.SUBSCRIPTION_EXPIRED)) {
                if(currentDate.after(expirationDate)) {
                    long diff = currentDate.getTime() - expirationDate.getTime();
                    int daysOverDue = (int) diff / (1000 * 60 * 60 * 24);
                    if(daysOverDue<15) {
                        newStatus = AppConstants.SUBSCRIPTION_PENDING;
                    }
                    else {
                        newStatus = AppConstants.SUBSCRIPTION_EXPIRED;
                    }
                }
            }
            if(newStatus!=null) {
                UpdateBuilder<Subscription, Integer> subscriptionUpdateBuilder = subscriptionDao.updateBuilder();
                subscriptionUpdateBuilder.updateColumnExpression("activationStatus",newStatus);
            }

        }
        catch (ParseException e) {
            Log.e("UpdateSubscription","Failed to parse subscription date",e);
        }
        catch (SQLException e) {
            Log.e("UpdateSubscription","Failed to update subscription",e);
        }
    }
}
