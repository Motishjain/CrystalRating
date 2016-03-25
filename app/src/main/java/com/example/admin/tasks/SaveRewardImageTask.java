package com.example.admin.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.admin.database.DBHelper;
import com.example.admin.database.Reward;
import com.example.admin.util.ImageUtility;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;

/**
 * Created by Admin on 3/23/2016.
 */
public class SaveRewardImageTask extends AsyncTask<Reward,Void,Void>{

    Reward reward;
    Dao<Reward, Integer> rewardDao;
    UpdateBuilder<Reward, Integer> rewardUpdateBuilder;
    Context context;

    public SaveRewardImageTask(Context context,Reward reward) {
        this.context = context;
        this.reward = reward;
    }

    @Override
    protected Void doInBackground(Reward... rewards) {
        //If image bytes are not set, fetch the image and set the bytes

        if(rewards[0] != null) {
            try {
                rewardDao = OpenHelperManager.getHelper(context, DBHelper.class).getCustomDao("Reward");
                rewardUpdateBuilder = rewardDao.updateBuilder();
                rewardUpdateBuilder.where().eq("rewardId",rewards[0].getRewardId());
                rewardUpdateBuilder.updateColumnValue("image", rewards[0].getImage());
                rewardUpdateBuilder.update();
            }
            catch (SQLException e) {
                e.printStackTrace();
                //TODO handle failure
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.i("Image save- pre", "onPreExecute Called");
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i("Image save- pre", "onPostExecute Called");
    }


}
