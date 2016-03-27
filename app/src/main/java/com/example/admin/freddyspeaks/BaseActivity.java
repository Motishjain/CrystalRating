package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Admin on 3/27/2016.
 */
public class BaseActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

            case R.id.actionManageRewards:
                Intent configureRewards = new Intent(BaseActivity.this,RewardConfigurationActivity.class);
                configureRewards.putExtra("editMode",true);
                startActivity(configureRewards);
                return true;

            case R.id.actionSubscriptionInfo:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void setActionBar(String heading) {
        // TODO Auto-generated method stub

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerBg,null)));
        actionBar.setTitle("FreddySpeaks");
        actionBar.show();

    }
}
