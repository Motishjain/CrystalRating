package com.admin.tasks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Reward;
import com.admin.database.SelectedReward;
import com.admin.freddyspeaks.R;
import com.admin.freddyspeaks.RewardSelectionActivity;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.response_objects.RewardResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import layout.SelectRewardsBoxFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 4/14/2016.
 */
public class BuildSelectRewardFragmentsTask extends AsyncTask<RewardSelectionActivity, Void, Void> {

    Dao<Reward, Integer> rewardDao;
    Dao<SelectedReward, Integer> selectedRewardDao;
    QueryBuilder<Reward, Integer> rewardQueryBuilder;
    QueryBuilder<SelectedReward, Integer> selectedRewardQueryBuilder;

    ProgressDialog progressDialog;

    public BuildSelectRewardFragmentsTask(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(RewardSelectionActivity... input) {
        RewardSelectionActivity activity = input[0];
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        activity.setOutletCode(sharedPreferences.getString("outletCode", ""));

        Bundle extras = activity.getIntent().getExtras();
        activity.setRewardCategory(extras.getString("rewardCategory"));

        try {
            rewardDao = OpenHelperManager.getHelper(activity, DBHelper.class).getCustomDao("Reward");
            rewardQueryBuilder = rewardDao.queryBuilder();
            rewardQueryBuilder.orderBy("level", true);
            activity.setRewardsList(rewardQueryBuilder.query());

            //For first time
            if (activity.getRewardsList().size() == 0) {
                fetchRewards(activity);
            } else {
                createLevelWiseFragments(activity);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
    }

    public void fetchRewards(final RewardSelectionActivity activity) {
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<List<RewardResponse>> fetchRewardsCall = restEndpointInterface.fetchRewards(AppConstants.OUTLET_TYPE);
        fetchRewardsCall.enqueue(new Callback<List<RewardResponse>>() {
            @Override
            public void onResponse(Call<List<RewardResponse>> call, Response<List<RewardResponse>> response) {
                List<RewardResponse> rewardResponseList = response.body();
                try {
                    for (RewardResponse rewardResponse : rewardResponseList) {
                        final Reward dbReward = new Reward();
                        dbReward.setRewardId(rewardResponse.get_id());
                        dbReward.setName(rewardResponse.getName());
                        dbReward.setImageUrl(rewardResponse.getImage());
                        dbReward.setCost(rewardResponse.getCost());
                        dbReward.setLevel(rewardResponse.getLevel());
                        rewardDao.create(dbReward);
                    }
                    activity.setRewardsList(rewardQueryBuilder.query());
                    createLevelWiseFragments(activity);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<RewardResponse>> call, Throwable t) {
                Log.e("Reward Selection", "Not able to fetch rewards");
            }
        });
    }

    void createLevelWiseFragments(RewardSelectionActivity activity) {
        try {
            selectedRewardDao = OpenHelperManager.getHelper(activity, DBHelper.class).getCustomDao("SelectedReward");
            selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();
            selectedRewardQueryBuilder.where().eq("rewardCategory", activity.getRewardCategory());
            List<SelectedReward> savedRewardList = selectedRewardQueryBuilder.query();
            for (SelectedReward selectedReward : savedRewardList) {
                for (Reward reward : activity.getRewardsList()) {
                    if (selectedReward.getReward().getRewardId().equals(reward.getRewardId())) {
                        reward.setSelected(true);
                        activity.setSelectedLevel(reward.getLevel());
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            Log.e("RewardSelectionActivity", "Failed to fetch saved rewards");
        }
        //To find all the saved rewards and show it pre-selected
        activity.setLevelRewardsMap(new TreeMap<Integer, List<Reward>>());

        for (Reward reward : activity.getRewardsList()) {
            if (activity.getLevelRewardsMap().get(reward.getLevel()) == null) {
                List<Reward> rewardList = new ArrayList<>();
                rewardList.add(reward);
                activity.getLevelRewardsMap().put(reward.getLevel(), rewardList);
            } else {
                activity.getLevelRewardsMap().get(reward.getLevel()).add(reward);
            }
        }
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Integer level : activity.getLevelRewardsMap().keySet()) {
            SelectRewardsBoxFragment selectRewardsBoxFragment = SelectRewardsBoxFragment.newInstance(level, activity.getLevelRewardsMap().get(level), activity.getSelectedLevel());
            fragmentTransaction.add(R.id.rewardLevelBoxList, selectRewardsBoxFragment, "Level " + level + " rewards");
            activity.getFragmentList().add(selectRewardsBoxFragment);
        }
        fragmentTransaction.commit();
    }
}
