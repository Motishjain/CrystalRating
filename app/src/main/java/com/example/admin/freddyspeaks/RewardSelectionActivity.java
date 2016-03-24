package com.example.admin.freddyspeaks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Reward;
import com.example.admin.database.SelectedReward;
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
    Dao<SelectedReward, Integer> selectedRewardDao;
    QueryBuilder<Reward, Integer> rewardQueryBuilder;
    QueryBuilder<SelectedReward, Integer> selectedRewardQueryBuilder;
    List<Reward> rewardsList;
    Map<Integer,List<Reward>> levelRewardsMap;
    private ProgressDialog progress;
    private String rewardCategory;
    private int selectedLevel;
    private int selections;

    List<SelectRewardsBoxFragment> fragmentList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_selection);

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

            if(rewardsList.size()==0) {
                fetchRewards();
            }

            selectedRewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("SelectedReward");
            selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();
            selectedRewardQueryBuilder.where().eq("rewardCategory",rewardCategory);


            //To find all the saved rewards and show it pre-selected
            List<SelectedReward> savedRewardList = selectedRewardQueryBuilder.query();
            selections = savedRewardList.size();

            for(SelectedReward selectedReward:savedRewardList) {
                for(Reward reward: rewardsList) {
                    if(selectedReward.getReward().getRewardId().equals(reward.getRewardId())){
                        reward.setSelected(true);
                        selectedLevel = reward.getLevel();
                        break;
                    }
                }
            }

            levelRewardsMap = new TreeMap<>();

            //For first time

            createLevelWiseFragments();


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
                fragment.setSelectedLevel(selectedLevel);
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
                    fragment.setSelectedLevel(selectedLevel);
                }
            }
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
                    int i = 0;
                    for (RewardResponse rewardResponse : rewardResponseList) {
                        final Reward dbReward = new Reward();
                        dbReward.setRewardId((i++)+"");
                        //dbReward.setRewardId((rewardResponse.getRewardId());
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
        Intent rewardsSaved = new Intent();
        rewardsSaved.putExtra("rewardsSelected", true);
        setResult(200,rewardsSaved);
    }
}
