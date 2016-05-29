package com.admin.freddyspeaks;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.admin.animation.DepthPageTransformer;
import com.admin.animation.ViewPagerCustomDuration;
import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Question;
import com.admin.database.SelectedReward;
import com.admin.dialogs.CustomDialogFragment;
import com.admin.util.ImageUtility;
import com.admin.view.CustomFontButton;
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
import com.admin.tasks.SetRandomQuestionsTask;

public class GetRatingActivity extends BaseActivity implements RatingCardFragment.OnFragmentInteractionListener, CustomDialogFragment.CustomDialogListener {

    private AnimationDrawable nextButtonAnimation;
    Dao<Question, Integer> questionDao;
    Dao<SelectedReward, Integer> selectedRewardDao;
    ImageView backgroundRatingImage;
    LinearLayout footerButtons;
    CustomFontButton ratingPreviousButton,ratingNextButton;
    QueryBuilder<Question, Integer> questionQueryBuilder;
    QueryBuilder<SelectedReward, Integer> selectedRewardQueryBuilder;
    public List<Question> questionList;
    public List<SelectedReward> rewardList;
    int totalQuestions;
    Set<Integer> answeredQuestionIndexSet = new HashSet<>();
    int currentQuestionIndex;
    Map<String, String> ratingMap;
    boolean showPreviousButton, showNextButton;

    ViewPagerCustomDuration ratingBarPager;
    Map<Integer, RatingCardFragment> ratingFragmentMap;
    CustomDialogFragment dialogConfirmExit;
    FeedbackRequest feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ratingBarPager = (ViewPagerCustomDuration) findViewById(R.id.ratingBarPager);
        footerButtons = (LinearLayout) findViewById(R.id.footerButtons);
        ratingPreviousButton = (CustomFontButton) findViewById(R.id.ratingPreviousButton);
        ratingNextButton = (CustomFontButton) findViewById(R.id.ratingNextButton);
        backgroundRatingImage = (ImageView) findViewById(R.id.backgroundRatingImage);

        backgroundRatingImage.setImageBitmap(ImageUtility.getImageBitmap(R.drawable.shopping_bg));

        ratingFragmentMap = new HashMap<>();
        ratingMap = new HashMap<>();
        Bundle extras = getIntent().getExtras();
        feedback = (FeedbackRequest)extras.get("feedback");

        if (feedback != null && feedback.getRatingsMap() == null) {
            feedback.setRatingsMap(ratingMap);
        }

        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Question");
            questionQueryBuilder = questionDao.queryBuilder();
            questionQueryBuilder.where().eq("selected", "Y");
            questionQueryBuilder.orderBy("displayRank",true);
            questionList = questionQueryBuilder.query();
            setupRatingScreens();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            selectedRewardDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("SelectedReward");
            selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();
            rewardList = selectedRewardQueryBuilder.query();
            //updateScreen();

        } catch (Exception e) {
            Log.e("RewardConfiguration", "Unable to fetch selected rewards");
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

        if (currentQuestionIndex < (totalQuestions - 1)) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        } else {
            if(rewardList.size()>0) {
                Intent getBillDetails = new Intent(GetRatingActivity.this, BillDetailsActivity.class);
                getBillDetails.putExtra("feedback", feedback);
                startActivity(getBillDetails);
            }
            else{
                Intent noRewardThankYou = new Intent(GetRatingActivity.this, ThankYouActivity.class);
                noRewardThankYou.putExtra("feedback", feedback);
                startActivity(noRewardThankYou);
            }
        }
    }

    void displayQuestion(int questionIndex) {
        showPreviousButton=false;
        showNextButton=false;
        ratingBarPager.setCurrentItem(questionIndex);
        if (questionIndex > 0) {
            showPreviousButton=true;
        } else {
            showPreviousButton=false;
        }
        if (questionIndex <= (answeredQuestionIndexSet.size() - 1)) {
            showNextButton=true;
        } else {
            showNextButton=false;
        }
        updateFooterButtonsDisplay();

    }

    void updateFooterButtonsDisplay()
    {
        LinearLayout.LayoutParams previousButtonLayoutParams = (LinearLayout.LayoutParams) ratingPreviousButton.getLayoutParams();
        LinearLayout.LayoutParams nextButtonLayoutParams = (LinearLayout.LayoutParams) ratingNextButton.getLayoutParams();
        boolean isStarQuestion = questionList.get(currentQuestionIndex).getQuestionInputType().equals(AppConstants.STAR_RATING);
        if((isStarQuestion || (currentQuestionIndex == totalQuestions - 1)) && !showNextButton){
            showNextButton = true;
            ratingNextButton.setEnabled(false);
            //color here
            //setButtonState();
            ImageUtility.setButtonLook(this,false,ratingNextButton);

        }
        else {
            ratingNextButton.setEnabled(true);
            //color here
            ImageUtility.setButtonLook(this,true,ratingNextButton);

        }

        if(showPreviousButton) {
            ratingPreviousButton.setVisibility(View.VISIBLE);
        }
        else {
            ratingPreviousButton.setVisibility(View.GONE);
        }
        if(showNextButton) {
            ratingNextButton.setVisibility(View.VISIBLE);
        }
        else {
            ratingNextButton.setVisibility(View.GONE);
        }

        if(showPreviousButton && showNextButton) {
            footerButtons.setVisibility(View.VISIBLE);
            previousButtonLayoutParams.weight=1f;
            nextButtonLayoutParams.weight=1f;
        }
        else if(showPreviousButton || showNextButton) {
            footerButtons.setVisibility(View.VISIBLE);
            previousButtonLayoutParams.weight=2f;
            nextButtonLayoutParams.weight=2f;
        }
        else {
            footerButtons.setVisibility(View.INVISIBLE);
            ratingNextButton.setVisibility(View.INVISIBLE);
            ratingPreviousButton.setVisibility(View.INVISIBLE);
        }

        if (currentQuestionIndex == totalQuestions - 1) {
            ratingNextButton.setText("Done");
        }
        else {
            ratingNextButton.setText("Next");
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

        String selectedOption = questionList.get(currentQuestionIndex).getSelectedOption();
        answeredQuestionIndexSet.add(currentQuestionIndex);
        ratingMap.put(questionList.get(currentQuestionIndex).getQuestionId(), selectedOption);
        String questionInputType = questionList.get(currentQuestionIndex).getQuestionInputType();

        if (currentQuestionIndex == totalQuestions - 1 || questionInputType.equals(AppConstants.STAR_RATING)) {
            ratingNextButton.setEnabled(true);
            ImageUtility.setButtonLook(this,true,ratingNextButton);

        } else {
            Runnable getNextRatingTask = new Runnable() {
                    @Override
                    public void run() {
                        getNextRating(null);
                    }
                };
            Handler intervalHandler = new Handler();
            intervalHandler.postDelayed(getNextRatingTask, 400);
        }
    }


    void setupRatingScreens() {
        currentQuestionIndex = 0;
        totalQuestions = AppConstants.MAXIMUM_QUESTIONS;
        RatingFragmentsAdapter ratingFragmentsAdapter = new RatingFragmentsAdapter(getSupportFragmentManager(), this);
        ratingBarPager.setAdapter(ratingFragmentsAdapter);
        displayQuestion(currentQuestionIndex);
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
