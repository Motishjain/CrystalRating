package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.admin.webservice.request_objects.FeedbackRequest;

public class HomePageActivity extends AppCompatActivity {

    Button addRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addRating = (Button) findViewById(R.id.add_rating);

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent billDetails = new Intent(HomePageActivity.this, GetRatingActivity.class);
                FeedbackRequest feedback = new FeedbackRequest();
                billDetails.putExtra("feedback",feedback);
                startActivity(billDetails);
            }
        });
    }
}