package com.admin.freddyspeaks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.admin.animation.DepthPageTransformer;
import com.admin.animation.ViewPagerCustomDuration;
import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Question;
import com.admin.dialogs.CustomDialogFragment;
import com.admin.util.DialogBuilderUtil;
import com.admin.util.ImageUtility;
import com.admin.webservice.request_objects.FeedbackRequest;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import layout.RatingCardFragment;

public class GetRatingActivity extends BaseActivity implements RatingCardFragment.OnFragmentInteractionListener, CustomDialogFragment.CustomDialogListener {


    Dao<Question, Integer> questionDao;
    ImageView backgroundRatingImage, ratingBackArrow, ratingNextArrow;
    QueryBuilder<Question, Integer> questionQueryBuilder;
    public List<Question> questionList;
    int totalQuestions;
    Set<Integer> answeredQuestionIndexSet = new HashSet<>();
    int currentQuestionIndex;
    Map<String, String> ratingMap;

    ViewPagerCustomDuration ratingBarPager;
    Map<Integer, RatingCardFragment> ratingFragmentMap;
    Button ratingDoneButton;
    CustomDialogFragment dialogConfirmExit;
    FeedbackRequest feedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ratingBarPager = (ViewPagerCustomDuration) findViewById(R.id.ratingBarPager);
        ratingDoneButton = (Button) findViewById(R.id.ratingDoneButton);
        ratingBackArrow = (ImageView) findViewById(R.id.ratingBackArrow);
        ratingNextArrow = (ImageView) findViewById(R.id.ratingNextArrow);
        backgroundRatingImage = (ImageView) findViewById(R.id.backgroundRatingImage);

        backgroundRatingImage.setImageBitmap(ImageUtility.getImageBitmap(R.drawable.bags));

        ratingFragmentMap = new HashMap<>();
        ratingMap = new HashMap<>();


        feedback = new FeedbackRequest();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String outletCode = sharedPreferences.getString("outletCode", null);
        feedback.setOutletCode(outletCode);

        if (feedback != null && feedback.getRatingsMap() == null) {
            feedback.setRatingsMap(ratingMap);
        }

        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Question");
            questionQueryBuilder = questionDao.queryBuilder();
            questionQueryBuilder.where().eq("selected", "Y");
            questionList = questionQueryBuilder.query();
            setupRatingScreens();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ratingBarPager.setPageTransformer(true, new DepthPageTransformer());

        ratingBarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //Flag to check if swipe is user initiated or programmed (via previous/next buttons)
            boolean userInitiatedScroll;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Call the handlers in case of swipe as well
                if (userInitiatedScroll) {
                    if (position < currentQuestionIndex) {
                        getPreviousRating(null);
                    } else if (position > currentQuestionIndex) {
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

        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        }
    }

    public void getNextRating(View v) {

        String selectedOption = questionList.get(currentQuestionIndex).getSelectedOption();

        answeredQuestionIndexSet.add(currentQuestionIndex);
        ratingMap.put(questionList.get(currentQuestionIndex).getQuestionId(), selectedOption);
        if (currentQuestionIndex < (totalQuestions - 1)) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        } else {
            Intent getBillDetails = new Intent(GetRatingActivity.this, BillDetailsActivity.class);
            getBillDetails.putExtra("feedback", feedback);
            startActivity(getBillDetails);
        }
    }

    void displayQuestion(int questionIndex) {
        ratingBarPager.setCurrentItem(questionIndex);
        if (questionIndex > 0) {
            ratingBackArrow.setVisibility(View.VISIBLE);
        } else {
            ratingBackArrow.setVisibility(View.INVISIBLE);
        }
        if (questionIndex <= (answeredQuestionIndexSet.size() - 1)) {
            ratingNextArrow.setVisibility(View.VISIBLE);
        } else {
            ratingNextArrow.setVisibility(View.INVISIBLE);
        }

    }

    public class RatingFragmentsAdapter extends FragmentPagerAdapter {

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
            RatingCardFragment ratingCardFragment = RatingCardFragment.newInstance(position + 1, questionList.get(position), onFragmentInteractionListener, totalQuestions);
            ratingFragmentMap.put(position, ratingCardFragment);
            return ratingCardFragment;
        }

    }

    @Override
    public void onQuestionAnswered() {
        if (currentQuestionIndex == totalQuestions - 1) {
            ratingDoneButton.setVisibility(View.VISIBLE);
        } else {
            Runnable getNextRatingTask = new Runnable() {
                @Override
                public void run() {
                    getNextRating(null);
                }
            };
            Handler intervalHandler = new Handler();
            intervalHandler.postDelayed(getNextRatingTask,400);

        }
    }

    void setupRatingScreens() {
        currentQuestionIndex = 0;
        totalQuestions = AppConstants.MAXIMUM_QUESTIONS;
        ratingBackArrow.setVisibility(View.INVISIBLE);
        ratingNextArrow.setVisibility(View.INVISIBLE);
        RatingFragmentsAdapter ratingFragmentsAdapter = new RatingFragmentsAdapter(getSupportFragmentManager(), this);
        ratingBarPager.setAdapter(ratingFragmentsAdapter);
    }

    @Override
    public void onBackPressed() {
        dialogConfirmExit = CustomDialogFragment.newInstance(R.layout.dialog_confirm_exit, this);
        dialogConfirmExit.show(getFragmentManager(), "");
    }


    @Override
    public void onDialogPositiveClick() {
        dialogConfirmExit.dismiss();
        Intent homePage = new Intent(GetRatingActivity.this, HomePageActivity.class);
        homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homePage);
    }

    @Override
    public void onDialogNegativeClick() {
        dialogConfirmExit.dismiss();
    }

}