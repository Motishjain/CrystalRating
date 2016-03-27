package com.example.admin.freddyspeaks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.admin.database.DBHelper;
import com.example.admin.database.Reward;
import com.example.admin.database.SelectedReward;
import com.example.admin.database.User;
import com.example.admin.tasks.FetchRewardImageTask;
import com.example.admin.util.RewardAllocationUtility;
import com.example.admin.view.CustomFontTextView;
import com.example.admin.webservice.RestEndpointInterface;
import com.example.admin.webservice.RetrofitSingleton;
import com.example.admin.webservice.request_objects.FeedbackRequest;
import com.example.admin.webservice.response_objects.PostServiceResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardDisplayActivity extends BaseActivity {

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

        try {
            userDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("User");
            selectedRewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("SelectedReward");
        } catch (Exception e) {
            e.printStackTrace();
        }

        allocateReward();

        if(allocatedReward!=null) {
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
        submitFeedback();
    }

    void submitFeedback() {
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<PostServiceResponse> submitFeedbackCall = restEndpointInterface.submitFeedback(feedback);
        submitFeedbackCall.enqueue(new Callback<PostServiceResponse>() {
            @Override
            public void onResponse(Call<PostServiceResponse> call, Response<PostServiceResponse> response) {
                PostServiceResponse postServiceResponse = response.body();

                if (postServiceResponse.isSuccess()) {
                    //TODO handle
                }
            }

            @Override
            public void onFailure(Call<PostServiceResponse> call, Throwable t) {
                //TODO Handle failure
            }
        });
    }
}