package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Admin on 3/27/2016.
 */
public class BaseActivity  extends AppCompatActivity {


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
                return true;

            case R.id.actionManageRewards:
                Intent configureRewards = new Intent(BaseActivity.this,RewardConfigurationActivity.class);
                configureRewards.putExtra("editMode",true);
                return true;

            case R.id.actionSubscriptionInfo:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
