package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.admin.tasks.AsyncImageSetterTask;

public class HomePageActivity extends BaseActivity {

    ImageView backgroundRatingImage;
    Button addRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backgroundRatingImage = (ImageView) findViewById(R.id.backgroundRatingImage);
        AsyncImageSetterTask asyncImageSetterTask = new AsyncImageSetterTask(this,backgroundRatingImage);
        asyncImageSetterTask.execute(R.drawable.bags);

        addRating = (Button) findViewById(R.id.add_rating);

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getRating = new Intent(HomePageActivity.this, GetRatingActivity.class);
                startActivity(getRating);
            }
        });
    }
}