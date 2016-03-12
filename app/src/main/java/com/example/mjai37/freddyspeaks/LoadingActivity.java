package com.example.mjai37.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.mjai37.database.DBHelper;
import com.example.mjai37.database.Outlet;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

public class LoadingActivity extends AppCompatActivity {


    Dao<Outlet, Integer> outletDao;
    QueryBuilder<Outlet, Integer> queryBuilder;
    List<Outlet> outletList; //Maximum one row will exist

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            outletDao = OpenHelperManager.getHelper(this, DBHelper.class).getOutletDao();
            queryBuilder = outletDao.queryBuilder();
            outletList = queryBuilder.query();
            if(outletList.size()>0){
                Intent homePage = new Intent(LoadingActivity.this,HomePageActivity.class);
                homePage.putExtra("outletCode",outletList.get(0).getOutletCode());
                startActivity(homePage);
            }
            else {

                Intent registerOutlet = new Intent(LoadingActivity.this,RegisterOutletActivity.class);
                startActivity(registerOutlet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}