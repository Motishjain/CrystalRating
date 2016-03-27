package com.example.admin.freddyspeaks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Reward;
import com.example.admin.database.SelectedReward;
import com.example.admin.webservice.RestEndpointInterface;
import com.example.admin.webservice.RetrofitSingleton;
import com.example.admin.webservice.request_objects.RewardSubmitRequest;
import com.example.admin.webservice.response_objects.PostServiceResponse;
import com.example.admin.webservice.response_objects.RewardResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import layout.SelectRewardsBoxFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardSelectionActivity extends AppCompatActivity implements SelectRewardsBoxFragment.OnFragmentInteractionListener{

    Dao<Reward, Integer> rewardDao;
    Dao<SelectedReward, Integer> selectedRewardDao;
    QueryBuilder<Reward, Integer> rewardQueryBuilder;
    QueryBuilder<SelectedReward, Integer> selectedRewardQueryBuilder;
    private List<Reward> rewardsList;
    Map<Integer,List<Reward>> levelRewardsMap;
    private ProgressDialog progress;
    private String rewardCategory;
    private String outletCode;
    private int selectedLevel;

    List<SelectRewardsBoxFragment> fragmentList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_selection);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        outletCode = sharedPreferences.getString("outletCode", "") ;

        Bundle extras = getIntent().getExtras();
        rewardCategory = extras.getString("rewardCategory");

        try {
            if(progress == null){
                showProgressDialog();
            }
            rewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Reward");
            rewardQueryBuilder = rewardDao.queryBuilder();
            rewardQueryBuilder.orderBy("level",true);
            rewardsList = rewardQueryBuilder.query();

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
        List<Reward> levelRewards = levelRewardsMap.get(level);
        levelRewards.get(index).setSelected(checked);

        //Check if this is the first checkbox being ticked, i.e. a level is being selected first time
        if(checked && selectedLevel == 0) {
            selectedLevel = level;
            for(SelectRewardsBoxFragment fragment:fragmentList) {
                if(fragment.getLevel()!=selectedLevel)
                {
                    fragment.setSelectedLevel(selectedLevel);
                }
            }
        }
        else {
            boolean isAnySelected = false;
            for(Reward reward: levelRewards) {
                if(reward.isSelected()){
                    isAnySelected = true;
                    break;
                }
            }
            //If all the checkboxes of current level are unchecked
            if(!isAnySelected){
                selectedLevel = 0;
                for(SelectRewardsBoxFragment fragment:fragmentList) {
                    if(fragment.getLevel()!=selectedLevel)
                    {
                        fragment.setSelectedLevel(selectedLevel);
                    }
                }
            }
        }
    }

    public void fetchRewards() {
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<List<RewardResponse>> fetchRewardsCall = restEndpointInterface.fetchRewards(AppConstants.OUTLET_TYPE);
        fetchRewardsCall.enqueue(new Callback<List<RewardResponse>>() {
            @Override
            public void onResponse(Call<List<RewardResponse>> call, Response<List<RewardResponse>> response) {
                List<RewardResponse> rewardResponseList = response.body();
                try {
                    int i = 0;
                    for (RewardResponse rewardResponse : rewardResponseList) {
                        final Reward dbReward = new Reward();
                        dbReward.setRewardId(rewardResponse.getId());
                        dbReward.setName(rewardResponse.getName());
                        dbReward.setImageUrl(rewardResponse.getImage());
                        dbReward.setCost(rewardResponse.getCost());
                        dbReward.setLevel(rewardResponse.getLevel());
                        rewardDao.create(dbReward);
                    }
                    rewardsList = rewardQueryBuilder.query();
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
        try {
            selectedRewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("SelectedReward");
            selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();
            selectedRewardQueryBuilder.where().eq("rewardCategory", rewardCategory);
            List<SelectedReward> savedRewardList = selectedRewardQueryBuilder.query();
            for(SelectedReward selectedReward:savedRewardList) {
                for(Reward reward: rewardsList) {
                    if(selectedReward.getReward().getRewardId().equals(reward.getRewardId())){
                        reward.setSelected(true);
                        selectedLevel = reward.getLevel();
                        break;
                    }
                }
            }
        }
        catch(SQLException e) {
            Log.e("RewardSelectionActivity","Failed to fetch saved rewards");
        }

        //To find all the saved rewards and show it pre-selected


        levelRewardsMap = new TreeMap<>();

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
            SelectRewardsBoxFragment selectRewardsBoxFragment = SelectRewardsBoxFragment.newInstance(level,levelRewardsMap.get(level), selectedLevel);
            fragmentTransaction.add(R.id.rewardLevelBoxList, selectRewardsBoxFragment, "Level " + level + " rewards");
            fragmentList.add(selectRewardsBoxFragment);
        }
        fragmentTransaction.commit();
        progress.dismiss();
    }

    public void saveSelectedRewards(View v) {

        try {
            DeleteBuilder<SelectedReward,Integer> selectedRewardDeleteBuilder = selectedRewardDao.deleteBuilder();
            selectedRewardDeleteBuilder.where().eq("rewardCategory", rewardCategory);
            selectedRewardDeleteBuilder.delete();

            Map<String,List<String>> rewardsMap = new HashMap<>();
            rewardsMap.put(rewardCategory,new ArrayList<String>());

            for(Reward reward:rewardsList) {
                if(reward.isSelected()) {
                    SelectedReward selectedReward = new SelectedReward();
                    selectedReward.setReward(reward);
                    selectedReward.setRewardCategory(rewardCategory);
                    selectedRewardDao.create(selectedReward);
                    rewardsMap.get(rewardCategory).add(reward.getRewardId());
                }
            }
            RewardSubmitRequest rewardSubmitRequest = new RewardSubmitRequest();
            rewardSubmitRequest.setOutletCode(outletCode);
            rewardSubmitRequest.setRewardsMap(rewardsMap);

            RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
            Call<PostServiceResponse> saveRewardsCall = restEndpointInterface.saveRewards(rewardSubmitRequest);
            saveRewardsCall.enqueue(new Callback<PostServiceResponse>() {
                @Override
                public void onResponse(Call<PostServiceResponse> call, Response<PostServiceResponse> response) {
                    PostServiceResponse postServiceResponse = response.body();

                    if (postServiceResponse.isSuccess()) {
                        Intent rewardsSaved = new Intent();
                        rewardsSaved.putExtra("rewardsSelected", true);
                        setResult(200, rewardsSaved);
                        RewardSelectionActivity.this.finish();
                    }
                }

                @Override
                public void onFailure(Call<PostServiceResponse> call, Throwable t) {
                    //TODO Handle failure
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
