package com.admin.crystalrating;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.admin.tasks.ApplicationStartupTask;
import com.admin.view.CustomProgressDialog;

public class LoadingActivity extends BaseActivity implements ApplicationStartupTask.OnTaskCompleted{

    ProgressDialog progress;
    String outletCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            progress = CustomProgressDialog.createCustomProgressDialog(this);
            progress.setMessage("Starting application");
            progress.show();
            ApplicationStartupTask applicationStartupTask = new ApplicationStartupTask(this);
            applicationStartupTask.execute(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTaskCompleted() {
        progress.dismiss();

        if(outletCode!=null){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Boolean areQuestionsFetched = sharedPreferences.getBoolean("areQuestionsFetched", false) ;

            if(areQuestionsFetched) {
                Intent homePage = new Intent(LoadingActivity.this,HomePageActivity.class);
                homePage.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(homePage);
            }
            else {
                Intent configureRewards = new Intent(LoadingActivity.this,RewardConfigurationActivity.class);
                configureRewards.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(configureRewards);

            }
        }
        else {
            Intent registerOutlet = new Intent(LoadingActivity.this,OutletDetailsActivity.class);
            registerOutlet.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(registerOutlet);
        }
    }

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }
}