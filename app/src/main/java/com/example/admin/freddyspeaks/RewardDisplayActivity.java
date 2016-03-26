package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.admin.database.SelectedReward;
import com.example.admin.database.User;
import com.example.admin.util.RewardAllocationUtility;
import com.example.admin.webservice.response_objects.FeedbackRequest;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.RequestParams;

public class RewardDisplayActivity extends AppCompatActivity {

    FeedbackRequest feedback;
    Dao<SelectedReward, Integer> selectedRewardDao;
    SelectedReward allocatedReward;
    Dao<User, Integer> userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_display);

        Bundle extras = getIntent().getExtras();
        allocatedReward = (SelectedReward) extras.get("allocatedReward");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    void allocateReward() {
        SelectedReward allocatedReward = RewardAllocationUtility.allocateReward(feedback.getUserPhoneNumber(),
                Integer.parseInt(feedback.getBillAmount()), selectedRewardDao, userDao);
        if (allocatedReward != null) {
            feedback.setRewardCategory(allocatedReward.getRewardCategory());
            feedback.setRewardId(allocatedReward.getReward().getRewardId());
        }
    }

}
