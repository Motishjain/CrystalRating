package com.example.admin.freddyspeaks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.admin.database.DBHelper;
import com.example.admin.database.Question;
import com.example.admin.database.Reward;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import layout.SelectRewardsBoxFragment;

public class RewardSelectionActivity extends AppCompatActivity {

    Dao<Reward, Integer> rewardDao;
    QueryBuilder<Reward, Integer> queryBuilder;
    List<Reward> rewardsList;
    Map<Integer,List<Reward>> levelRewardsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_reward_selection);

        try {
            rewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Reward");
            queryBuilder = rewardDao.queryBuilder();
            queryBuilder.orderBy("level",true);
            rewardsList = queryBuilder.query();
            levelRewardsMap = new HashMap<>();

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
