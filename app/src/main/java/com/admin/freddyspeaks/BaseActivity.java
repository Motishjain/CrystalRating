package com.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Admin on 3/27/2016.
 */
public class BaseActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionEditProfile:
                Intent outletDetails = new Intent(BaseActivity.this,OutletDetailsActivity.class);
                outletDetails.putExtra("editMode",true);
                startActivity(outletDetails);
                return true;

            case R.id.actionRewards:
                Intent configureRewards = new Intent(BaseActivity.this,RewardConfigurationActivity.class);
                configureRewards.putExtra("editMode",true);
                startActivity(configureRewards);
                return true;

            case R.id.actionSubscriptionInfo:
                Intent subscriptionInfo = new Intent(BaseActivity.this,SubscriptionInfoActivity.class);
                startActivity(subscriptionInfo);
                return true;

            case R.id.actionFeedbackSummary:
                Intent ratingSummary = new Intent(BaseActivity.this,FeedbackAnalysisActivity.class);
                startActivity(ratingSummary);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
