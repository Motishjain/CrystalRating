package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Question;
import com.example.admin.database.SelectedReward;
import com.example.admin.database.User;
import com.example.admin.util.RewardAllocationUtility;
import com.example.admin.webservice.RestEndpointInterface;
import com.example.admin.webservice.RetrofitSingleton;
import com.example.admin.webservice.response_objects.Feedback;
import com.example.admin.webservice.response_objects.QuestionResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.loopj.android.http.RequestParams;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import layout.RatingCardFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetRatingActivity extends AppCompatActivity  implements RatingCardFragment.OnFragmentInteractionListener{


    Dao<Question, Integer> questionDao;
    Dao<SelectedReward, Integer> selectedRewardDao;
    Button ratingPreviousButton;
    Dao<User, Integer> userDao;
    int currentQuestionIndex ;
    ViewPager ratingBarPager;
    Map<String,String> ratingMap;
    Map<Integer,RatingCardFragment> ratingFragmentMap;

    QueryBuilder<Question, Integer> queryBuilder;
    public List<Question> questionList;
    int totalQuestions;
    Feedback feedback;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ratingBarPager = (ViewPager)findViewById(R.id.ratingBarPager);
        ratingPreviousButton = (Button) findViewById(R.id.ratingPreviousButton);
        ratingFragmentMap = new HashMap<>();

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        Bundle extras = getIntent().getExtras();
        ratingMap = new HashMap<>();
        if(extras!=null)
        {
            feedback = (Feedback)extras.get("feedback");
        }

        if(feedback!=null && feedback.getRatingsMap()==null){
            feedback.setRatingsMap(ratingMap);
        }

        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Question");
            selectedRewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("SelectedReward");
            userDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("User");
            queryBuilder = questionDao.queryBuilder();
            queryBuilder.where().eq("selected","Y");
            questionList = queryBuilder.query();
            //TODO needs to be added after reward configuration
            if(questionList==null || questionList.size()==0){
                fetchQuestions();
            }
            else {
                initializeRatingFragments();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getPreviousRating(View v) {
        currentQuestionIndex--;
        ratingBarPager.setCurrentItem(currentQuestionIndex);
        if(currentQuestionIndex == 0) {
            ratingPreviousButton.setVisibility(View.GONE);
        }
    }

    public void getNextRating(View v) {

        RatingCardFragment currentRatingFragment = ratingFragmentMap.get(currentQuestionIndex);

        TextView selectedOptionTextView = currentRatingFragment.getSelectedOptionTextView();

        if(selectedOptionTextView==null || selectedOptionTextView.toString().trim().equals("")){
            //TODO show alert
        }
        else {
            ratingMap.put(questionList.get(currentQuestionIndex).getQuestionId(), currentRatingFragment.getSelectedOptionTextView().getText().toString());

            currentQuestionIndex++;

            if (currentQuestionIndex < totalQuestions) {
                ratingBarPager.setCurrentItem(currentQuestionIndex);
                ratingPreviousButton.setVisibility(View.VISIBLE);

            } else {
                //Next screen
            }
        }

    }

    public class RatingFragmentsAdapter extends FragmentPagerAdapter{

        RatingCardFragment.OnFragmentInteractionListener onFragmentInteractionListener;

        public RatingFragmentsAdapter(FragmentManager fragmentManager, RatingCardFragment.OnFragmentInteractionListener onFragmentInteractionListener) {
            super(fragmentManager);
            this.onFragmentInteractionListener = onFragmentInteractionListener;
        }

        @Override
        public int getCount() {
            return questionList.size();
        }

        @Override
        public Fragment getItem(int position) {
            RatingCardFragment ratingCardFragment = RatingCardFragment.newInstance(questionList.get(position),onFragmentInteractionListener);
            ratingFragmentMap.put(position,ratingCardFragment);
            return ratingCardFragment;
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    void fetchQuestions() {
        questionList = new ArrayList<>();
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<List<QuestionResponse>> fetchQuestionsCall = restEndpointInterface.fetchQuestions(AppConstants.OUTLET_TYPE);
        fetchQuestionsCall.enqueue(new Callback<List<QuestionResponse>>() {
            @Override
            public void onResponse(Call<List<QuestionResponse>> call, Response<List<QuestionResponse>> response) {
                List<QuestionResponse> questionResponseList = response.body();
                try {
                    for (QuestionResponse questionResponse : questionResponseList) {
                        Question dbQuestion = new Question();
                        dbQuestion.setQuestionId(questionResponse.getQuestionId());
                        dbQuestion.setName(questionResponse.getQuestionName());
                        dbQuestion.setRatingValues(android.text.TextUtils.join(",", questionResponse.getOptionValues()));
                        dbQuestion.setSelected("Y");
                        questionDao.create(dbQuestion);
                        questionList.add(dbQuestion);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                initializeRatingFragments();
            }

            @Override
            public void onFailure(Call<List<QuestionResponse>> call, Throwable t) {

            }
        });
    }

    void allocateReward() {
        SelectedReward allocatedReward = RewardAllocationUtility.allocateReward(feedback.getUserPhoneNumber(), Integer.parseInt(feedback.getBillAmount()), selectedRewardDao,userDao);
        if(allocatedReward!=null) {
            feedback.setRewardCategory(allocatedReward.getRewardCategory());
            feedback.setRewardId(allocatedReward.getReward().getRewardId());
        }

        RequestParams params = new RequestParams();
        params.put("feedback", gson.toJson(feedback));

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

    void initializeRatingFragments() {
        currentQuestionIndex = 0;
        totalQuestions = questionList.size();
        ratingPreviousButton.setVisibility(View.GONE);
        RatingFragmentsAdapter ratingFragmentsAdapter = new RatingFragmentsAdapter(getSupportFragmentManager(),this);
        ratingBarPager.setAdapter(ratingFragmentsAdapter);
    }


}