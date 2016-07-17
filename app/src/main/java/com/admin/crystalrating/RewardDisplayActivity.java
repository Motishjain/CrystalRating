package com.admin.crystalrating;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Reward;
import com.admin.database.SelectedReward;
import com.admin.database.User;
import com.admin.dialogs.CustomDialogFragment;
import com.admin.tasks.FetchRewardImageTask;
import com.admin.util.RewardAllocationUtility;
import com.admin.view.CustomFontTextView;
import com.admin.webservice.WebServiceUtility;
import com.admin.webservice.request_objects.FeedbackRequest;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.util.HashMap;
import java.util.Map;

public class RewardDisplayActivity extends BaseActivity {

    FeedbackRequest feedback;
    Dao<SelectedReward, Integer> selectedRewardDao;
    SelectedReward allocatedReward;
    Dao<User, Integer> userDao;
    CustomFontTextView billAmtLabel,rewardDisplayExclaimer, rewardDisplayMessage, resultRewardName;
    CustomFontTextView thankYouMessage2;
    ImageView resultRewardImage;
    CustomDialogFragment dialogConfirmExit;
    public static Map<String,String> categoryMapping = new HashMap<>();


    static {
        categoryMapping.put(AppConstants.BRONZE_CD,"Bronze");
        categoryMapping.put(AppConstants.SILVER_CD,"Silver");
        categoryMapping.put(AppConstants.GOLD_CD,"Gold");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        billAmtLabel=(CustomFontTextView)findViewById(R.id.billAmt);
        rewardDisplayExclaimer = (CustomFontTextView) findViewById(R.id.rewardDisplayExclaimer);
        rewardDisplayMessage = (CustomFontTextView) findViewById(R.id.rewardDisplayMessage);
        thankYouMessage2 = (CustomFontTextView) findViewById(R.id.thankYouMessage2);
        resultRewardName = (CustomFontTextView) findViewById(R.id.resultRewardName);
        resultRewardImage = (ImageView) findViewById(R.id.resultRewardImage);

        Bundle extras = getIntent().getExtras();
        feedback = (FeedbackRequest)extras.get("feedback");

        if(feedback==null) {
            exit(null);
        }

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
            billAmtLabel.setText( "\u20B9"+feedback.getBillAmount());
            resultRewardName.setText("It's "+rewardResult.getName().toUpperCase());
        }
    }

    void allocateReward() {
        allocatedReward = RewardAllocationUtility.allocateReward(Double.parseDouble(feedback.getBillAmount()), selectedRewardDao);
        if (allocatedReward != null) {
            feedback.setRewardCategory(allocatedReward.getRewardCategory());
            feedback.setRewardId(allocatedReward.getReward().getRewardId());
        }
        WebServiceUtility.submitFeedback(this,feedback);
    }

    public void exit(View v) {
        Intent homePage = new Intent(RewardDisplayActivity.this, HomePageActivity.class);
        homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homePage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Intent homePage = new Intent(RewardDisplayActivity.this, HomePageActivity.class);
                homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homePage);
            }
        }, 3*60*1000);
    }

    @Override
    public void onBackPressed() {
        dialogConfirmExit = CustomDialogFragment.newInstance(R.layout.dialog_confirm_exit, new CustomDialogFragment.CustomDialogListener() {
            @Override
            public void onDialogPositiveClick() {
                dialogConfirmExit.dismiss();
                Intent homePage = new Intent(RewardDisplayActivity.this, HomePageActivity.class);
                homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homePage);
            }

            @Override
            public void onDialogNegativeClick() {
                dialogConfirmExit.dismiss();
            }
        });
        dialogConfirmExit.show(getSupportFragmentManager(), "");
    }

}