package com.example.admin.freddyspeaks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.admin.database.SelectedReward;
import com.example.admin.webservice.response_objects.Feedback;

public class RewardDisplayActivity extends AppCompatActivity {

    Feedback feedback;
    SelectedReward allocatedReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_display);

        Bundle extras = getIntent().getExtras();
        allocatedReward = (SelectedReward)extras.get("allocatedReward");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
