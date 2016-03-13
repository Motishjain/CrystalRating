package com.example.mjai37.freddyspeaks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.mjai37.database.DBHelper;
import com.example.mjai37.value_objects.Feedback;
import com.example.mjai37.database.Question;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.util.HashMap;
import java.util.List;

import layout.RatingCardFragment;

public class GetRatingActivity extends AppCompatActivity {


    Dao<Question, Integer> questionDao;
    int currentQuestionIndex ;
    RatingCardFragment currentRatingFragment;

    QueryBuilder<Question, Integer> queryBuilder;
    UpdateBuilder<Feedback, Integer> updateBuilder;
    List<Question> questionList;
    int totalQuestions;
    FragmentManager fragmentManager;
    Feedback feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        feedback = (Feedback)extras.get("feedback");

        if(feedback.getRatingsMap()==null){
            feedback.setRatingsMap(new HashMap<String, String>());
        }

        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getQuestionDao();
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
            feedback.setRewardCategory("1");
            feedback.setRewardId("1");
            Intent rewardDisplay = new Intent(GetRatingActivity.this, RewardDisplayActivity.class);
            rewardDisplay.putExtra("feedback", feedback);
            startActivity(rewardDisplay);
        }
    }
}