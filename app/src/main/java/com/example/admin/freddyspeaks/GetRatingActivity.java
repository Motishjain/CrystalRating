package com.example.admin.freddyspeaks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Question;
import com.example.admin.database.SelectedReward;
import com.example.admin.database.User;
import com.example.admin.util.RewardAllocationUtility;
import com.example.admin.webservice.RestClient;
import com.example.admin.webservice.response_objects.Feedback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import layout.RatingCardFragment;

public class GetRatingActivity extends AppCompatActivity {


    Dao<Question, Integer> questionDao;
    Dao<SelectedReward, Integer> selectedRewardDao;
    Dao<User, Integer> userDao;
    int currentQuestionIndex ;
    RatingCardFragment currentRatingFragment;

    QueryBuilder<Question, Integer> queryBuilder;
    List<Question> questionList;
    int totalQuestions;
    FragmentManager fragmentManager;
    Feedback feedback;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        Bundle extras = getIntent().getExtras();
        feedback = (Feedback)extras.get("feedback");

        if(feedback.getRatingsMap()==null){
            feedback.setRatingsMap(new HashMap<String, String>());
        }

        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Question");
            selectedRewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("SelectedReward");
            userDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("User");
            queryBuilder = questionDao.queryBuilder();
            queryBuilder.where().eq("selected","Y");
            questionList = queryBuilder.query();
            totalQuestions = questionList.size();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        currentQuestionIndex = 0;
        Question question = questionList.get(currentQuestionIndex);
        currentRatingFragment = RatingCardFragment.newInstance(question.getName(),question.getRatingValues().split(","));
        fragmentTransaction.add(R.id.ratingCardHolder, currentRatingFragment);
        fragmentTransaction.commit();
    }

    public void submitRating(View v) {
        if(currentRatingFragment.getSelectedOptionValue().getText().toString().trim().equals("")){
            //TODO show alert
        }
        feedback.getRatingsMap().put(questionList.get(currentQuestionIndex).getQuestionId(),currentRatingFragment.getSelectedOptionValue().getText().toString());
        currentQuestionIndex++;

        if(currentQuestionIndex<totalQuestions) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Question question = questionList.get(currentQuestionIndex);
            currentRatingFragment = RatingCardFragment.newInstance(question.getName(),question.getRatingValues().split(","));
            fragmentTransaction.replace(R.id.ratingCardHolder, currentRatingFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else {
            SelectedReward allocatedReward = RewardAllocationUtility.allocateReward(feedback.getUserPhoneNumber(), Integer.parseInt(feedback.getBillAmount()), selectedRewardDao,userDao);
            if(allocatedReward!=null) {
                feedback.setRewardCategory(allocatedReward.getRewardCategory());
                feedback.setRewardId(allocatedReward.getRewardId());
            }

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

            //TODO move this in success block
            if(allocatedReward!=null){
                Intent rewardDisplay = new Intent(GetRatingActivity.this, RewardDisplayActivity.class);
                rewardDisplay.putExtra("allocatedReward", allocatedReward);
                startActivity(rewardDisplay);
            }
            else {
                Intent thanksScreen = new Intent(GetRatingActivity.this, ThanksActivity.class);
                startActivity(thanksScreen);
            }

        }
    }
}