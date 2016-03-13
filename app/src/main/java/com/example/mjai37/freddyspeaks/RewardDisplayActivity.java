package com.example.mjai37.freddyspeaks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.mjai37.constants.AppConstants;
import com.example.mjai37.database.DBHelper;
import com.example.mjai37.database.RewardHistory;
import com.example.mjai37.value_objects.Feedback;
import com.example.mjai37.webservice.RestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RewardDisplayActivity extends AppCompatActivity {


    Dao<RewardHistory, Integer> rewardHistoryDao;
    QueryBuilder<RewardHistory, Integer> queryBuilder;
    UpdateBuilder<RewardHistory, Integer> updateBuilder;
    List<RewardHistory> rewardHistoryList = new ArrayList<>();
    Feedback feedback;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_display);

        Bundle extras = getIntent().getExtras();
        feedback = (Feedback)extras.get("feedback");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            rewardHistoryDao = OpenHelperManager.getHelper(this, DBHelper.class).getRewardHistoryDao();
            queryBuilder = rewardHistoryDao.queryBuilder();
            updateBuilder = rewardHistoryDao.updateBuilder();
            rewardHistoryList = queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    public void submitFeedback(View v) {
        feedback.setRewardId("1");
        feedback.setRewardCategory("1");
        RequestParams params = new RequestParams();

        params.put("feedback", gson.toJson(feedback));

        RestClient.post(AppConstants.SUBMIT_FEEDBACK, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
                        // Hide Progress Dialog
                        try {
                            String str = new String(responseBytes, "UTF-8");

                            JSONObject response = new JSONObject(str);
                            // When the JSON response has status boolean value assigned with true
                            if (response.getBoolean("success")) {
                                //TODO do something
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    // When the response returned by REST has Http response code other than '200'
                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(), "Device might not be connected to Internet", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "Not able to register now! Please try again later.", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(getApplicationContext(), "Device might not be connected to Internet", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

}
