package com.admin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.admin.database.DBHelper;
import com.admin.database.FailedServiceCall;
import com.admin.database.User;
import com.admin.tasks.UpdateSubscriptionStatusTask;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 14-05-2016.
 */
public class FailedServiceCallReceiver extends BroadcastReceiver {

    Dao<FailedServiceCall, Integer> failedServiceCallDao;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            failedServiceCallDao = OpenHelperManager.getHelper(context, DBHelper.class).getCustomDao("FailedServiceCall");
            QueryBuilder<FailedServiceCall, Integer> failedServiceCallQueryBuilder = failedServiceCallDao.queryBuilder();
            List<FailedServiceCall> failedServiceCalls = failedServiceCallQueryBuilder.query();

            if(failedServiceCalls.size()>0) {
                final DeleteBuilder<FailedServiceCall, Integer> failedServiceCallDeleteBuilder = failedServiceCallDao.deleteBuilder();
                RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
                Gson gson = new Gson();
                for(final FailedServiceCall failedServiceCall:failedServiceCalls) {
                    List<Object> parameters = gson.fromJson(failedServiceCall.getParametersJsonString(),new TypeToken<List<Object>>(){}.getType());
                    Method method = restEndpointInterface.getClass().getMethod(failedServiceCall.getServiceName(),Object.class);
                    Call serviceCall = (Call) method.invoke(restEndpointInterface,parameters);
                    serviceCall.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            Log.i("FailedServiceCall","Service call retried successfully");
                            try {
                                failedServiceCallDeleteBuilder.reset();
                                failedServiceCallDeleteBuilder.where().eq("id",failedServiceCall.getId());
                                failedServiceCallDeleteBuilder.delete();
                            }
                            catch (SQLException e) {
                                Log.e("FailedServiceCall","Service call delete failed",e);
                            }

                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            Log.e("FailedServiceCall","Service call retry failed",t);
                        }
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}