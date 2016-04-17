package com.admin.freddyspeaks;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.admin.database.Reward;
import com.admin.tasks.BuildSelectRewardFragmentsTask;
import com.admin.tasks.SaveRewardsTask;
import com.admin.util.DialogBuilderUtil;
import com.admin.view.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import layout.SelectRewardsBoxFragment;

public class RewardSelectionActivity extends AppCompatActivity implements SelectRewardsBoxFragment.OnFragmentInteractionListener {

    List<Reward> rewardsList;
    Map<Integer, List<Reward>> levelRewardsMap;
    private ProgressDialog progressDialog;
    private String rewardCategory;
    private String outletCode;
    private int selectedLevel;

    List<SelectRewardsBoxFragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_selection);
        progressDialog = CustomProgressDialog.createCustomProgressDialog(this);
        progressDialog.setMessage("Loading Rewards...");
        progressDialog.show();
        try {
            BuildSelectRewardFragmentsTask buildSelectRewardFragmentsTask = new BuildSelectRewardFragmentsTask(progressDialog);
            buildSelectRewardFragmentsTask.execute(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void rewardClicked(int level, int index, boolean checked) {
        List<Reward> levelRewards = levelRewardsMap.get(level);
        levelRewards.get(index).setSelected(checked);

        //Check if this is the first checkbox_off_background being ticked, i.e. a level is being selected first time
        if (checked && selectedLevel == 0) {
            selectedLevel = level;
            for (SelectRewardsBoxFragment fragment : fragmentList) {
                if (fragment.getLevel() != selectedLevel) {
                    fragment.setSelectedLevel(selectedLevel);
                }
            }
        } else {
            boolean isAnySelected = false;
            for (Reward reward : levelRewards) {
                if (reward.isSelected()) {
                    isAnySelected = true;
                    break;
                }
            }
            //If all the checkboxes of current level are unchecked
            if (!isAnySelected) {
                selectedLevel = 0;
                for (SelectRewardsBoxFragment fragment : fragmentList) {
                    if (fragment.getLevel() != selectedLevel) {
                        fragment.setSelectedLevel(selectedLevel);
                    }
                }
            }
        }
    }

    @Override
    public void fragmentCreated(int level) {
    }

    public void saveSelectedRewards(View v) {
        progressDialog.setMessage("Saving Rewards...");
        progressDialog.show();
        SaveRewardsTask saveRewardsTask = new SaveRewardsTask(progressDialog);
        saveRewardsTask.execute(this);
    }

    public void close(View v) {
        finish();
    }

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public String getRewardCategory() {
        return rewardCategory;
    }

    public void setRewardCategory(String rewardCategory) {
        this.rewardCategory = rewardCategory;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(int selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public List<SelectRewardsBoxFragment> getFragmentList() {
        return fragmentList;
    }

    public List<Reward> getRewardsList() {
        return rewardsList;
    }

    public void setRewardsList(List<Reward> rewardsList) {
        this.rewardsList = rewardsList;
    }

    public Map<Integer, List<Reward>> getLevelRewardsMap() {
        return levelRewardsMap;
    }

    public void setLevelRewardsMap(Map<Integer, List<Reward>> levelRewardsMap) {
        this.levelRewardsMap = levelRewardsMap;
    }
}
