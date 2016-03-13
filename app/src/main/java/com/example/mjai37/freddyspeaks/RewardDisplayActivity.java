package com.example.mjai37.freddyspeaks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.mjai37.database.DBHelper;
import com.example.mjai37.database.RewardHistory;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.util.ArrayList;
import java.util.List;

public class RewardDisplayActivity extends AppCompatActivity {


    Dao<RewardHistory, Integer> rewardHistoryDao;
    QueryBuilder<RewardHistory, Integer> queryBuilder;
    UpdateBuilder<RewardHistory, Integer> updateBuilder;
    List<RewardHistory> rewardHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            rewardHistoryDao = OpenHelperManager.getHelper(this, DBHelper.class).getRewardHistoryDao();
            queryBuilder = rewardHistoryDao.queryBuilder();
            updateBuilder = rewardHistoryDao.updateBuilder();
            rewardHistoryList = queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
