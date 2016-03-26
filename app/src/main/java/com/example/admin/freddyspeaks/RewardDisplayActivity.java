package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.example.admin.database.Reward;
import com.example.admin.database.SelectedReward;
import com.example.admin.database.User;
import com.example.admin.tasks.FetchRewardImageTask;
import com.example.admin.util.RewardAllocationUtility;
import com.example.admin.view.CustomFontTextView;
import com.example.admin.webservice.response_objects.FeedbackRequest;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.RequestParams;

public class RewardDisplayActivity extends AppCompatActivity {

    FeedbackRequest feedback;
    Dao<SelectedReward, Integer> selectedRewardDao;
    SelectedReward allocatedReward;
    Dao<User, Integer> userDao;
    CustomFontTextView rewardDisplayHeader, rewardResultText, resultRewardName;
    ImageView resultRewardImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_display);

        rewardDisplayHeader = (CustomFontTextView) findViewById(R.id.rewardDisplayHeader);
        rewardResultText = (CustomFontTextView) findViewById(R.id.rewardResultText);
        resultRewardName = (CustomFontTextView) findViewById(R.id.resultRewardName);
        resultRewardImage = (ImageView) findViewById(R.id.resultRewardImage);

        Bundle extras = getIntent().getExtras();
        feedback = (FeedbackRequest)extras.get("feedback");

        allocateReward();

        if(allocatedReward.getReward()!=null) {
            Reward rewardResult = allocatedReward.getReward();
            FetchRewardImageTask fetchRewardImageTask = new FetchRewardImageTask(resultRewardImage);
            fetchRewardImageTask.execute(rewardResult.getImage());
            resultRewardName.setText(rewardResult.getName());
        }
        else {

        }
    }

    void allocateReward() {
        allocatedReward = RewardAllocationUtility.allocateReward(feedback.getUserPhoneNumber(),
                Integer.parseInt(feedback.getBillAmount()), selectedRewardDao, userDao);
        if (allocatedReward != null) {
            feedback.setRewardCategory(allocatedReward.getRewardCategory());
            feedback.setRewardId(allocatedReward.getReward().getRewardId());
        }
    }

}
