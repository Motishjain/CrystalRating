package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.admin.animation.DepthPageTransformer;
import com.example.admin.animation.ViewPagerCustomDuration;
import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Question;
import com.example.admin.util.DialogBuilderUtil;
import com.example.admin.util.ImageUtility;
import com.example.admin.webservice.request_objects.FeedbackRequest;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import layout.RatingCardFragment;

public class GetRatingActivity extends BaseActivity implements RatingCardFragment.OnFragmentInteractionListener{


    Dao<Question, Integer> questionDao;
    ImageView backgroundRatingImage,ratingBackArrow,ratingNextArrow;
    QueryBuilder<Question, Integer> questionQueryBuilder;
    public List<Question> questionList;
    int totalQuestions;
    int currentQuestionIndex;
    Map<String,String> ratingMap;

    ViewPagerCustomDuration ratingBarPager;
    Map<Integer,RatingCardFragment> ratingFragmentMap;
    Button ratingPreviousButton;
    Button ratingNextButton;

    FeedbackRequest feedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long T1 = android.os.SystemClock.uptimeMillis();
        Log.d("GetRatingT1",T1+"");
        setContentView(R.layout.activity_get_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ratingBarPager = (ViewPagerCustomDuration)findViewById(R.id.ratingBarPager);
        ratingPreviousButton = (Button) findViewById(R.id.ratingPreviousButton);
        ratingNextButton = (Button) findViewById(R.id.ratingNextButton);
        ratingBackArrow = (ImageView) findViewById(R.id.ratingBackArrow);
        ratingNextArrow = (ImageView) findViewById(R.id.ratingNextArrow);
        backgroundRatingImage = (ImageView) findViewById(R.id.backgroundRatingImage);

        backgroundRatingImage.setImageBitmap(ImageUtility.getImageBitmap(R.drawable.bags));

        long T2 = android.os.SystemClock.uptimeMillis();
        Log.d("GetRatingT2",T2+"");
        ratingFragmentMap = new HashMap<>();
        ratingMap = new HashMap<>();

/*      TextToSpeechConversionTask textToSpeechConversionTask = new TextToSpeechConversionTask(getApplicationContext());
        textToSpeechConversionTask.execute(AppConstants.USER_WELCOME_MSG);*/

        feedback = new FeedbackRequest();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String outletCode = sharedPreferences.getString("outletCode", null) ;
        feedback.setOutletCode(outletCode);

        if(feedback!=null && feedback.getRatingsMap()==null) {
            feedback.setRatingsMap(ratingMap);
        }

        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Question");
            questionQueryBuilder = questionDao.queryBuilder();
            questionQueryBuilder.where().eq("selected","Y");
            questionList = questionQueryBuilder.query();
            long T3 = android.os.SystemClock.uptimeMillis();
            Log.d("GetRatingT3",T3+"");
            setupRatingScreens();
            long T4 = android.os.SystemClock.uptimeMillis();
            Log.d("GetRatingT4",T4+"");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ratingBarPager.setPageTransformer(true,new DepthPageTransformer());

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
            displayQuestion(currentQuestionIndex);
        }
    }

    public void getNextRating(View v) {

        String selectedOption = questionList.get(currentQuestionIndex).getSelectedOption();

        if(selectedOption == null || selectedOption.trim().equals("")){
            //TODO show alert
            DialogBuilderUtil.showMessageDialog(this,"","Please select an option","Close");
        }
        else {
            ratingMap.put(questionList.get(currentQuestionIndex).getQuestionId(), selectedOption);
            if (currentQuestionIndex < (totalQuestions-1)) {
                currentQuestionIndex++;
                displayQuestion(currentQuestionIndex);
            } else {
                Intent getBillDetails = new Intent(GetRatingActivity.this, BillDetailsActivity.class);
                getBillDetails.putExtra("feedback",feedback);
                startActivity(getBillDetails);
            }
        }
    }

    void displayQuestion(int questionIndex) {
        ratingBarPager.setCurrentItem(questionIndex);
        if(questionIndex == 0) {
            ratingPreviousButton.setVisibility(View.GONE);
            ratingBackArrow.setVisibility(View.INVISIBLE);
            ratingNextArrow.setVisibility(View.VISIBLE);
            ratingNextButton.setText("Next");
        }
        else if(questionIndex == totalQuestions - 1) {
            ratingPreviousButton.setVisibility(View.VISIBLE);
            ratingBackArrow.setVisibility(View.VISIBLE);
            ratingNextArrow.setVisibility(View.INVISIBLE);
            ratingNextButton.setText("Done!");
        }
        else {
            ratingPreviousButton.setVisibility(View.VISIBLE);
            ratingBackArrow.setVisibility(View.VISIBLE);
            ratingNextArrow.setVisibility(View.VISIBLE);
            ratingNextButton.setText("Next");
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

    void setupRatingScreens() {
        currentQuestionIndex = 0;
        totalQuestions = AppConstants.MAXIMUM_QUESTIONS;
        ratingPreviousButton.setVisibility(View.INVISIBLE);
        ratingBackArrow.setVisibility(View.GONE);
        RatingFragmentsAdapter ratingFragmentsAdapter = new RatingFragmentsAdapter(getSupportFragmentManager(),this);
        ratingBarPager.setAdapter(ratingFragmentsAdapter);
    }

}