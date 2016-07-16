package com.admin.tasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.admin.crystalrating.LoadingActivity;
import com.admin.crystalrating.R;
import com.admin.util.ImageUtility;
import com.admin.webservice.RetrofitSingleton;

import java.text.SimpleDateFormat;

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
        for(Integer resId: resIdList) {
            ImageUtility.loadBitmap(loadingActivity,resId);
        }
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(loadingActivity);
            String outletCode = sharedPreferences.getString("outletCode", null) ;
            loadingActivity.setOutletCode(outletCode);
            //Load Retrofit API
            RetrofitSingleton.newInstance();

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
