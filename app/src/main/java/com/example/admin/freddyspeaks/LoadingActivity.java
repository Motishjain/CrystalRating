package com.example.admin.freddyspeaks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.admin.database.DBHelper;
import com.example.admin.database.Outlet;
import com.example.admin.tasks.BitmapPreLoadTask;
import com.example.admin.util.ImageUtility;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

public class LoadingActivity extends BaseActivity implements BitmapPreLoadTask.OnTaskCompleted{

    Integer[] resIdList = new Integer[]{R.drawable.bags};

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            progress=new ProgressDialog(this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
            ImageUtility.loadBitmapInBackground(getApplicationContext(),resIdList,this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTaskCompleted() {
        progress.dismiss();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String outletCode = sharedPreferences.getString("outletCode", null) ;

        if(outletCode!=null){
            Boolean areQuestionsFetched = sharedPreferences.getBoolean("areQuestionsFetched", false) ;
            if(areQuestionsFetched) {
                Intent homePage = new Intent(LoadingActivity.this,HomePageActivity.class);
                startActivity(homePage);
            }
            else {
                Intent homePage = new Intent(LoadingActivity.this,RewardConfigurationActivity.class);
                startActivity(homePage);
            }
        }
        else {
            Intent registerOutlet = new Intent(LoadingActivity.this,OutletDetailsActivity.class);
            startActivity(registerOutlet);
        }
    }
}