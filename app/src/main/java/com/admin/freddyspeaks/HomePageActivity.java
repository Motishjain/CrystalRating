package com.admin.freddyspeaks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.admin.constants.AppConstants;
import com.admin.database.User;
import com.admin.util.ImageUtility;
import com.admin.util.KeyboardUtil;
import com.admin.util.ValidationUtil;
import com.admin.view.CustomFontButton;
import com.admin.webservice.request_objects.FeedbackRequest;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomePageActivity extends BaseActivity {

    ImageView backgroundRatingImage;
    CustomFontButton getStartedButton;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        executeDailyTasks();
        String activationStatus = sharedPreferences.getString("activationStatus", null);

        if(activationStatus!=null && activationStatus.equals(AppConstants.SUBSCRIPTION_EXPIRED)){
            Intent subscriptionInfo = new Intent(HomePageActivity.this, SubscriptionInfoActivity.class);
            startActivity(subscriptionInfo);
        }

        backgroundRatingImage = (ImageView) findViewById(R.id.backgroundRatingImage);
        backgroundRatingImage.setImageBitmap(ImageUtility.getImageBitmap(this,R.drawable.shopping_bg));
        getStartedButton = (CustomFontButton) findViewById(R.id.getStartedButton);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleRatingScreen();
            }
        });
    }

    void scheduleRatingScreen() {
        String outletCode = sharedPreferences.getString("outletCode", null);
        FeedbackRequest feedback = new FeedbackRequest();
        feedback.setOutletCode(outletCode);

        Intent getRating = new Intent(HomePageActivity.this, GetRatingActivity.class);
        getRating.putExtra("feedback", feedback);
        startActivity(getRating);

    }

    private void executeDailyTasks() {
        String dailyTaskExecutedDate = sharedPreferences.getString("dailyTaskExecutedDate", null);
        String currentDate = simpleDateFormat.format(new Date());
        if (dailyTaskExecutedDate == null || !dailyTaskExecutedDate.equals(currentDate)) {
            Intent intent = new Intent("com.admin.freddyspeaks.executedailytasks");
            sendBroadcast(intent);
        }
    }
}