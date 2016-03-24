package com.example.admin.freddyspeaks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Reward;
import com.example.admin.webservice.RestEndpointInterface;
import com.example.admin.webservice.RetrofitSingleton;
import com.example.admin.webservice.response_objects.RewardResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import layout.SelectRewardsBoxFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardSelectionActivity extends AppCompatActivity implements SelectRewardsBoxFragment.OnFragmentInteractionListener{

    Dao<Reward, Integer> rewardDao;
    QueryBuilder<Reward, Integer> queryBuilder;
    List<Reward> rewardsList;
    List<Reward> selectedRewardList;
    Map<Integer,List<Reward>> levelRewardsMap;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_selection);

        try {
            rewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Reward");
            queryBuilder = rewardDao.queryBuilder();
            queryBuilder.orderBy("level",true);
            rewardsList = queryBuilder.query();
            selectedRewardList = new ArrayList<>();
            levelRewardsMap = new TreeMap<>();

            //For first time
            if(rewardsList.size()==0) {
                fetchRewards();
            }
            else {
                createLevelWiseFragments();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void rewardClicked(int level,int index, boolean checked) {
        if(checked) {
            selectedRewardList.add(levelRewardsMap.get(level).get(index));
        }
        else {
            selectedRewardList.remove(levelRewardsMap.get(level).get(index));
        }
    }

    public void fetchRewards() {
        showProgressDialog();
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<List<RewardResponse>> fetchRewardsCall = restEndpointInterface.fetchRewards(AppConstants.OUTLET_TYPE);
        fetchRewardsCall.enqueue(new Callback<List<RewardResponse>>() {
            @Override
            public void onResponse(Call<List<RewardResponse>> call, Response<List<RewardResponse>> response) {
                List<RewardResponse> rewardResponseList = response.body();
                try {
                    for (RewardResponse rewardResponse : rewardResponseList) {
                        final Reward dbReward = new Reward();
                        dbReward.setName(rewardResponse.getName());
                        dbReward.setImageUrl(rewardResponse.getImage());
                        dbReward.setCost(rewardResponse.getCost());
                        dbReward.setLevel(rewardResponse.getLevel());
                        rewardDao.create(dbReward);
                    }
                    rewardsList = queryBuilder.query();
                    createLevelWiseFragments();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<RewardResponse>> call, Throwable t) {
                //TODO handle failure
            }
        });
    }

    void showProgressDialog(){
        progress=new ProgressDialog(this);
        progress.setMessage("Fetching Rewards");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
    }

    void createLevelWiseFragments() {
        for(Reward reward:rewardsList) {
            if(levelRewardsMap.get(reward.getLevel())==null){
                List<Reward> rewardList = new ArrayList<>();
                rewardList.add(reward);
                levelRewardsMap.put(reward.getLevel(),rewardList);
            }
            else {
                levelRewardsMap.get(reward.getLevel()).add(reward);
            }
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for(Integer level:levelRewardsMap.keySet()) {
            SelectRewardsBoxFragment selectRewardsBoxFragment = SelectRewardsBoxFragment.newInstance(level,levelRewardsMap.get(level));
            fragmentTransaction.add(R.id.rewardLevelBoxList, selectRewardsBoxFragment, "Level " + level + " rewards");
        }
        fragmentTransaction.commit();
        progress.dismiss();
    }

    public void saveSelectedRewards(View v) {

    }
}
