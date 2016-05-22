package com.admin.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.admin.database.DBHelper;
import com.admin.database.Outlet;
import com.admin.freddyspeaks.LoadingActivity;
import com.admin.freddyspeaks.R;
import com.admin.util.ImageUtility;
import com.admin.webservice.RetrofitSingleton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 4/15/2016.
 */
public class ApplicationStartupTask extends AsyncTask<LoadingActivity, Void, Void> {

    Integer[] resIdList = new Integer[]{R.drawable.shopping_bg};
    Dao<Outlet, Integer> outletDao;
    ApplicationStartupTask.OnTaskCompleted onTaskCompleted;

    public ApplicationStartupTask(ApplicationStartupTask.OnTaskCompleted onTaskCompleted) {
        this.onTaskCompleted = onTaskCompleted;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(LoadingActivity... input) {
        LoadingActivity activity = input[0];
        Map<Integer,Bitmap> imageBitmapCollection= ImageUtility.getImageBitmapCollection();
        for(Integer resId: resIdList) {
            imageBitmapCollection.put(resId, (ImageUtility.decodeSampledBitmapFromResource(activity.getResources(), resId)));
        }
        try {
            outletDao = OpenHelperManager.getHelper(activity, DBHelper.class).getCustomDao("Outlet");
            QueryBuilder<Outlet,Integer> outletQueryBuilder = outletDao.queryBuilder();
            List<Outlet> outletList = outletQueryBuilder.query();
            if(outletList.size()>0) {
                activity.setOutletCode(outletList.get(0).getOutletCode());
            }
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
