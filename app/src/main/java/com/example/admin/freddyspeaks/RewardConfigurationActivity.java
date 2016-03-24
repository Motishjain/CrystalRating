package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.admin.adapter.SelectedRewardsBoxAdapter;
import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Reward;
import com.example.admin.database.SelectedReward;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

public class RewardConfigurationActivity extends AppCompatActivity {

    RecyclerView bronzeRewardsRecyclerView, silverRewardsRecyclerView, goldRewardsRecyclerView;
    Dao<Reward, Integer> rewardDao;
    Dao<SelectedReward, Integer> selectedRewardDao;
    QueryBuilder<Reward, Integer> rewardQueryBuilder;
    QueryBuilder<SelectedReward, Integer> selectedRewardQueryBuilder;
    SelectedRewardsBoxAdapter bronzeRewardsAdapter, silverRewardsAdapter, goldRewardsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_configuration);
        bronzeRewardsRecyclerView = (RecyclerView)findViewById(R.id.bronzeRewardsRecyclerView);
        silverRewardsRecyclerView = (RecyclerView) findViewById(R.id.silverRewardsRecyclerView);
        goldRewardsRecyclerView = (RecyclerView) findViewById(R.id.goldRewardsRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        bronzeRewardsRecyclerView.setLayoutManager(layoutManager);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        silverRewardsRecyclerView.setLayoutManager(layoutManager);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        goldRewardsRecyclerView.setLayoutManager(layoutManager);

        try {
            rewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Reward");
            selectedRewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("SelectedReward");
            rewardQueryBuilder = rewardDao.queryBuilder();
            selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();

            updateScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveRewardConfig(View v) {

    }

    public void addBronzeRewards(View v) {
        Intent selectRewardsPopup = new Intent(RewardConfigurationActivity.this, RewardSelectionActivity.class);
        String rewardCategory = AppConstants.BRONZE_CD;
        selectRewardsPopup.putExtra("rewardCategory", rewardCategory);
        startActivityForResult(selectRewardsPopup, 1);
    }

    private void addSilverRewards(View v) {
        Intent selectRewardsPopup = new Intent(RewardConfigurationActivity.this, RewardSelectionActivity.class);
        String rewardCategory = AppConstants.SILVER_CD;
        selectRewardsPopup.putExtra("rewardCategory", rewardCategory);
        startActivityForResult(selectRewardsPopup, 2);
    }

    private void addGoldRewards(View v) {
        Intent selectRewardsPopup = new Intent(RewardConfigurationActivity.this, RewardSelectionActivity.class);
        String rewardCategory = AppConstants.GOLD_CD;
        selectRewardsPopup.putExtra("rewardCategory", rewardCategory);
        startActivityForResult(selectRewardsPopup, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(data!=null) {
            boolean rewardsSelected = data.getBooleanExtra("rewardsSelected", false);

            if (rewardsSelected) {
                if (requestCode == 1) {
                    updateBronzeRewardList();
                } else if (requestCode == 2) {
                    updateSilverRewardList();
                } else if (requestCode == 3) {
                    updateGoldRewardList();
                }
            }
        }
    }

    public void updateBronzeRewardList() {
        try {
            selectedRewardQueryBuilder.reset();
            selectedRewardQueryBuilder.where().eq("rewardCategory", AppConstants.BRONZE_CD);
            rewardQueryBuilder.reset();

            QueryBuilder<Reward, Integer> selectedRewardsJoinQueryBuilder = rewardQueryBuilder.join(selectedRewardQueryBuilder);
            List<Reward> bronzeSelectedRewardList = selectedRewardsJoinQueryBuilder.query();
            if(bronzeRewardsRecyclerView.getAdapter()==null) {
                bronzeRewardsAdapter = new SelectedRewardsBoxAdapter(R.layout.selected_reward_item, bronzeSelectedRewardList);
                bronzeRewardsRecyclerView.setAdapter(bronzeRewardsAdapter);
            }
            else {
                bronzeRewardsAdapter.setRewardList(bronzeSelectedRewardList);
                bronzeRewardsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSilverRewardList() {
        try {
            selectedRewardQueryBuilder.reset();
            selectedRewardQueryBuilder.where().eq("rewardCategory", AppConstants.SILVER_CD);
            rewardQueryBuilder.reset();

            QueryBuilder<Reward, Integer> selectedRewardsJoinQueryBuilder = rewardQueryBuilder.join(selectedRewardQueryBuilder);
            List<Reward> silverSelectedRewardList = selectedRewardsJoinQueryBuilder.query();
            if(silverRewardsRecyclerView.getAdapter()==null) {
                silverRewardsAdapter = new SelectedRewardsBoxAdapter(R.layout.selected_reward_item, silverSelectedRewardList);
                silverRewardsRecyclerView.setAdapter(silverRewardsAdapter);
            }
            else {
                silverRewardsAdapter.setRewardList(silverSelectedRewardList);
                silverRewardsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGoldRewardList() {
        try {
            selectedRewardQueryBuilder.reset();
            selectedRewardQueryBuilder.where().eq("rewardCategory", AppConstants.GOLD_CD);
            rewardQueryBuilder.reset();

            QueryBuilder<Reward, Integer> selectedRewardsJoinQueryBuilder = rewardQueryBuilder.join(selectedRewardQueryBuilder);
            List<Reward> goldSelectedRewardList = selectedRewardsJoinQueryBuilder.query();
            if(goldRewardsRecyclerView.getAdapter()==null) {
                goldRewardsAdapter = new SelectedRewardsBoxAdapter(R.layout.selected_reward_item, goldSelectedRewardList);
                goldRewardsRecyclerView.setAdapter(goldRewardsAdapter);
            }
            else {
                goldRewardsAdapter.setRewardList(goldSelectedRewardList);
                goldRewardsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateScreen() {
        updateBronzeRewardList();
        updateSilverRewardList();
        updateGoldRewardList();
    }
}