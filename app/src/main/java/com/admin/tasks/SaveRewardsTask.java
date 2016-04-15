package com.admin.tasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.admin.database.DBHelper;
import com.admin.database.Reward;
import com.admin.database.SelectedReward;
import com.admin.freddyspeaks.RewardSelectionActivity;
import com.admin.util.ImageUtility;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.request_objects.RewardSubmitRequest;
import com.admin.webservice.response_objects.PostServiceResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 4/14/2016.
 */
public class SaveRewardsTask extends AsyncTask<RewardSelectionActivity, Void, Void> {

    Dao<SelectedReward, Integer> selectedRewardDao;
    ProgressDialog progressDialog;

    public SaveRewardsTask(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(RewardSelectionActivity... input) {
        final RewardSelectionActivity activity = input[0];
        List<String> rewardIdList = new ArrayList<>();
        try {
            selectedRewardDao = OpenHelperManager.getHelper(activity, DBHelper.class).getCustomDao("SelectedReward");
            DeleteBuilder<SelectedReward,Integer> selectedRewardDeleteBuilder = selectedRewardDao.deleteBuilder();
            selectedRewardDeleteBuilder.where().eq("rewardCategory", activity.getRewardCategory());
            selectedRewardDeleteBuilder.delete();

            for(Reward reward:activity.getRewardsList()) {
                if(reward.isSelected()) {
                    SelectedReward selectedReward = new SelectedReward();
                    selectedReward.setReward(reward);
                    selectedReward.setRewardCategory(activity.getRewardCategory());
                    selectedRewardDao.create(selectedReward);
                    rewardIdList.add(reward.getRewardId());
                }
            }
        }
        catch(SQLException e) {
            Log.e("RewardSelectionActivity", "Failed to fetch saved rewards");
        }

        RewardSubmitRequest rewardSubmitRequest = new RewardSubmitRequest();
        rewardSubmitRequest.setOutletCode(activity.getOutletCode());
        rewardSubmitRequest.setRewardCategory(activity.getRewardCategory());
        rewardSubmitRequest.setRewardIdList(rewardIdList);

        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<PostServiceResponse> saveRewardsCall = restEndpointInterface.saveRewards(rewardSubmitRequest);
        saveRewardsCall.enqueue(new Callback<PostServiceResponse>() {
            @Override
            public void onResponse(Call<PostServiceResponse> call, Response<PostServiceResponse> response) {
                PostServiceResponse postServiceResponse = response.body();
                progressDialog.dismiss();
                if (postServiceResponse.isSuccess()) {
                    Intent rewardsSaved = new Intent();
                    rewardsSaved.putExtra("rewardsSelected", true);
                    activity.setResult(200, rewardsSaved);
                    activity.finish();
                }
            }

            @Override
            public void onFailure(Call<PostServiceResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Reward Configuration","Unable to save rewards");
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}
