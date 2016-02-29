package com.example.mjai37.freddyspeaks;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.mjai37.database.DBHelper;
import com.example.mjai37.database.GoodieHistory;
import com.example.mjai37.database.User;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.util.ArrayList;
import java.util.List;

public class GoodieDisplayActivity extends AppCompatActivity {


    Dao<GoodieHistory, Integer> goodieHistoryDao;
    QueryBuilder<GoodieHistory, Integer> queryBuilder;
    UpdateBuilder<GoodieHistory, Integer> updateBuilder;
    List<GoodieHistory> goodieHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodie_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            goodieHistoryDao = OpenHelperManager.getHelper(this, DBHelper.class).getGoodieHistoryDao();
            queryBuilder = goodieHistoryDao.queryBuilder();
            updateBuilder = goodieHistoryDao.updateBuilder();
            goodieHistoryList = queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
