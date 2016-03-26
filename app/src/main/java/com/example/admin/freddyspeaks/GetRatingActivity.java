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
import com.example.admin.webservice.RestEndpointInterface;
import com.example.admin.webservice.RetrofitSingleton;
import com.example.admin.webservice.request_objects.FeedbackRequest;
import com.example.admin.webservice.response_objects.QuestionResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import layout.RatingCardFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetRatingActivity extends AppCompatActivity  implements RatingCardFragment.OnFragmentInteractionListener{


    Dao<Question, Integer> questionDao;
    QueryBuilder<Question, Integer> questionQueryBuilder;
    public List<Question> questionList;
    int totalQuestions;
    int currentQuestionIndex;
    Map<String,String> ratingMap;

    ViewPager ratingBarPager;
    Map<Integer,RatingCardFragment> ratingFragmentMap;
    Button ratingPreviousButton;

    FeedbackRequest feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ratingBarPager = (ViewPager)findViewById(R.id.ratingBarPager);
        ratingPreviousButton = (Button) findViewById(R.id.ratingPreviousButton);
        ratingFragmentMap = new HashMap<>();

        Bundle extras = getIntent().getExtras();
        ratingMap = new HashMap<>();
        if(extras!=null)
        {
            feedback = (FeedbackRequest)extras.get("feedback");
        }

        if(feedback!=null && feedback.getRatingsMap()==null) {
            feedback.setRatingsMap(ratingMap);
        }

        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Question");
            questionQueryBuilder = questionDao.queryBuilder();
            questionQueryBuilder.where().eq("selected","Y");
            questionList = questionQueryBuilder.query();

            //TODO needs to be added after reward configuration
            if(questionList==null || questionList.size()==0){
                fetchQuestions();
            }
            else {
                setupRatingScreens();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ratingBarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //Flag to check if swipe is user initiated or programmed (via previous/next buttons)
            boolean userInitiatedScroll;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Call the handlers in case of swipe as well
                if(userInitiatedScroll) {
                    if(position < currentQuestionIndex) {
                        getPreviousRating(null);
                    }
                    else if(position > currentQuestionIndex){
                        getNextRating(null);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Check if its user initiated swipe
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    userInitiatedScroll = true;
                }
            }
        });

    }

    public void getPreviousRating(View v) {

        if(currentQuestionIndex > 0) {
            currentQuestionIndex--;
            ratingBarPager.setCurrentItem(currentQuestionIndex);
        }
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

            if (currentQuestionIndex < totalQuestions) {
                currentQuestionIndex++;
                ratingBarPager.setCurrentItem(currentQuestionIndex);
                ratingPreviousButton.setVisibility(View.VISIBLE);

            } else {
                Intent getUserInfo = new Intent(GetRatingActivity.this, UserInfoActivity.class);
                getUserInfo.putExtra("feedback",feedback);
                startActivity(getUserInfo);
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
                setupRatingScreens();
            }

            @Override
            public void onFailure(Call<List<QuestionResponse>> call, Throwable t) {

            }
        });
    }

    void setupRatingScreens() {
        filterQuestions();
        initializeRatingFragments();
    }

    void filterQuestions() {
        List<Question> filteredList = new ArrayList<>();
        try {
            questionQueryBuilder.reset();
            questionQueryBuilder.where().eq("selected", "Y");
            questionQueryBuilder.where().eq("questionType", AppConstants.PRODUCT_QUESTION_TYPE);
            questionList = questionQueryBuilder.query();
            Random randomGenerator = new SecureRandom();
            int randomQuestionIndex = randomGenerator.nextInt(questionList.size());
            filteredList.add(questionList.get(randomQuestionIndex));

            questionQueryBuilder.reset();
            questionQueryBuilder.where().eq("selected", "Y");
            questionQueryBuilder.where().eq("questionType", AppConstants.SERVICE_QUESTION_TYPE);
            questionList = questionQueryBuilder.query();
            randomQuestionIndex = randomGenerator.nextInt(questionList.size());
            filteredList.add(questionList.get(randomQuestionIndex));

            questionQueryBuilder.reset();
            questionQueryBuilder.where().eq("selected", "Y");
            questionQueryBuilder.where().eq("questionType", AppConstants.MISCELLENOUS_QUESTION_TYPE);
            questionList = questionQueryBuilder.query();
            randomQuestionIndex = randomGenerator.nextInt(questionList.size());
            filteredList.add(questionList.get(randomQuestionIndex));

            questionList = filteredList;
        }
        catch (SQLException e) {

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