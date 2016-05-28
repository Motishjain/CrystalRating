package com.admin.tasks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.admin.database.DBHelper;
import com.admin.database.Outlet;
import com.admin.freddyspeaks.LoadingActivity;
import com.admin.freddyspeaks.R;
import com.admin.util.ImageUtility;
import com.admin.webservice.RetrofitSingleton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 4/15/2016.
 */
public class ApplicationStartupTask extends AsyncTask<LoadingActivity, Void, Void> {

    Integer[] resIdList = new Integer[]{R.drawable.shopping_bg};
    ApplicationStartupTask.OnTaskCompleted onTaskCompleted;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ApplicationStartupTask(ApplicationStartupTask.OnTaskCompleted onTaskCompleted) {
        this.onTaskCompleted = onTaskCompleted;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(LoadingActivity... input) {
        LoadingActivity loadingActivity = input[0];
        Map<Integer,Bitmap> imageBitmapCollection= ImageUtility.getImageBitmapCollection();
        for(Integer resId: resIdList) {
            imageBitmapCollection.put(resId, (ImageUtility.decodeSampledBitmapFromResource(loadingActivity.getResources(), resId)));
        }
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(loadingActivity);
            String outletCode = sharedPreferences.getString("outletCode", null) ;
            loadingActivity.setOutletCode(outletCode);
            //Load Retrofit API
            RetrofitSingleton.newInstance();

            boolean areQuestionsFetched = sharedPreferences.getBoolean("areQuestionsFetched", false) ;
            if(areQuestionsFetched) {
                String dailyTaskExecutedDate = sharedPreferences.getString("dailyTaskExecutedDate", null);
                String currentDate = simpleDateFormat.format(new Date());
                if (dailyTaskExecutedDate == null || !dailyTaskExecutedDate.equals(currentDate)) {
                    Intent intent = new Intent("com.admin.freddyspeaks.executedailytasks");
                    loadingActivity.sendBroadcast(intent);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("dailyTaskExecutedDate", currentDate);
                    editor.commit();
                }
            }

            if(onTaskCompleted!=null){
                onTaskCompleted.onTaskCompleted();
            }
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
        void onTaskCompleted();
    }
}
