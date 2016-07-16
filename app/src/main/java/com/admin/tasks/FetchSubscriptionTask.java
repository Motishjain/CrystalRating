package com.admin.tasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Subscription;
import com.admin.crystalrating.R;
import com.admin.crystalrating.SubscriptionInfoActivity;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.response_objects.SubscriptionResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 30-04-2016.
 */
public class FetchSubscriptionTask extends AsyncTask<String, Void, Void> {

    Dao<Subscription, Integer> subscriptionDao;
    Context context;
    FetchSubscriptionTaskListener fetchSubscriptionTaskListener;
    boolean useLocalCopy;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    boolean showNotification;

    public FetchSubscriptionTask (Context context, FetchSubscriptionTaskListener fetchSubscriptionTaskListener, boolean useLocalCopy, boolean showNotification) {
        this.context = context;
        this.fetchSubscriptionTaskListener = fetchSubscriptionTaskListener;
        this.useLocalCopy = useLocalCopy;
        this.showNotification = showNotification;
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
            QueryBuilder<Subscription,Integer> subscriptionQueryBuilder = subscriptionDao.queryBuilder();
            final List<Subscription> subscriptionList = subscriptionQueryBuilder.query();

            if(useLocalCopy){
                if(subscriptionList.size()>0) {
                    if(fetchSubscriptionTaskListener!=null) {
                        fetchSubscriptionTaskListener.onTaskCompleted(subscriptionList.get(0));
                    }
                    return null;
                }
            }

            RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
            Call<SubscriptionResponse> fetchSubscriptionCall = restEndpointInterface.fetchSubscription(outletCode);
            fetchSubscriptionCall.enqueue(new Callback<SubscriptionResponse>() {
                @Override
                public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {
                    if (response.isSuccess()) {
                        SubscriptionResponse subscriptionResponse = response.body();
                        Subscription subscription;
                        try {
                            Date expiryDate = simpleDateFormat.parse(subscriptionResponse.getExpiryDate());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            Integer daysRemaining = -1;
                            if(subscriptionResponse.getActivationStatus().equals(AppConstants.SUBSCRIPTION_ACTIVE) || subscriptionResponse.getActivationStatus().equals(AppConstants.SUBSCRIPTION_TRIAL)) {
                                daysRemaining = (int) Math.ceil(((expiryDate.getTime() - calendar.getTimeInMillis())*1.0)/(1000*60*60*24));
                                if(daysRemaining<=3 && showNotification) {
                                    Intent resultIntent = new Intent(context, SubscriptionInfoActivity.class);
                                    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    createNotification(context,"Renew Subscription","Your subscription expires in "+daysRemaining+" "+((daysRemaining>1)?"days":"day"),resultPendingIntent);
                                }
                            }
                            else if (subscriptionResponse.getActivationStatus().equals(AppConstants.SUBSCRIPTION_PENDING)) {
                                daysRemaining = (int) (Math.ceil((((expiryDate.getTime() - calendar.getTimeInMillis())*1.0)/(1000*60*60*24)))) + 7;
                                Intent resultIntent = new Intent(context, SubscriptionInfoActivity.class);
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                if(showNotification)
                                {
                                    createNotification(context,"Subscription Expired","Kindly renew your subscription",resultPendingIntent);
                                }
                            }
                            if(subscriptionList.size()==0) {
                                subscription = new Subscription();
                                subscription.setExpiryDate(subscriptionResponse.getExpiryDate());
                                subscription.setActivationStatus(subscriptionResponse.getActivationStatus());
                                subscription.setDaysRemaining(daysRemaining);
                                subscriptionDao.create(subscription);
                            }
                            else {
                                subscription = subscriptionList.get(0);
                                subscription.setExpiryDate(subscriptionResponse.getExpiryDate());
                                subscription.setActivationStatus(subscriptionResponse.getActivationStatus());
                                subscription.setDaysRemaining(daysRemaining);
                                UpdateBuilder<Subscription,Integer> subscriptionUpdateBuilder = subscriptionDao.updateBuilder();
                                subscriptionUpdateBuilder.where().eq("id",subscription.getId());
                                subscriptionUpdateBuilder.updateColumnValue("activationStatus",subscription.getActivationStatus());
                                subscriptionUpdateBuilder.updateColumnValue("expiryDate",subscription.getExpiryDate());
                                subscriptionUpdateBuilder.updateColumnValue("daysRemaining",subscription.getDaysRemaining());
                                subscriptionUpdateBuilder.update();
                            }
                            if(fetchSubscriptionTaskListener!=null){
                                fetchSubscriptionTaskListener.onTaskCompleted(subscription);
                            }
                        }
                        catch (SQLException e) {
                            Log.e("FetchSubscriptionTask","Failed to save subscription",e);
                        }
                        catch (ParseException e) {
                            Log.e("FetchSubscriptionTask","Failed to parse date",e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SubscriptionResponse> call, Throwable t) {
                    Log.e("RatingSummary", "Unable to fetch feedback", t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void createNotification(Context context, String title, String body, PendingIntent resultPendingIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.application_icon).setContentTitle(title)
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

    public interface FetchSubscriptionTaskListener {
        void onTaskCompleted(Subscription subscription);
    }
}
