package com.admin.tasks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Question;
import com.admin.freddyspeaks.R;
import com.admin.freddyspeaks.RatingSummaryActivity;
import com.admin.view.CustomProgressDialog;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.response_objects.FeedbackResponse;
import com.admin.webservice.response_objects.Rating;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import layout.RatingChartFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 22-04-2016.
 */
public class FetchAndFragmentFeedbackTask extends AsyncTask<RatingSummaryActivity, Void, Void> {

    private ProgressDialog progressDialog;
    Dao<Question, Integer> questionDao;
    QueryBuilder<Question, Integer> questionQueryBuilder;

    //List of feedbacks for a given time frame
    List<FeedbackResponse> feedbackResponseList;

    /*Map to save ratings for each question id (String). In turn, for every question id there is a map
    storing list of feedbackId indexes for each ratingOption*/
    Map<String,Map<Integer, List<Integer>>> questionWiseRatingFeedbackIndexList;
    SimpleDateFormat webServiceDateFormat;
    Date fromDate, toDate;

    public FetchAndFragmentFeedbackTask(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    protected Void doInBackground(RatingSummaryActivity... input) {
        RatingSummaryActivity ratingSummaryActivity = input[0];

        progressDialog = CustomProgressDialog.createCustomProgressDialog(ratingSummaryActivity);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        fetchFeedback(ratingSummaryActivity,fromDate,toDate);
        return null;
    }

    public void fetchFeedback(final RatingSummaryActivity ratingSummaryActivity, Date fromDate, Date toDate) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ratingSummaryActivity);
        String outletCode = sharedPreferences.getString("outletCode", null);
        feedbackResponseList = new ArrayList<>();
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<List<FeedbackResponse>> fetchRewardsCall = restEndpointInterface.fetchFeedback(outletCode, webServiceDateFormat.format(fromDate), webServiceDateFormat.format(toDate));
        fetchRewardsCall.enqueue(new Callback<List<FeedbackResponse>>() {
            @Override
            public void onResponse(Call<List<FeedbackResponse>> call, Response<List<FeedbackResponse>> response) {
                if (response.isSuccess()) {
                    feedbackResponseList = response.body();
                    divideRatingsByQuestion();
                    createFragments(ratingSummaryActivity);
                }
            }

            @Override
            public void onFailure(Call<List<FeedbackResponse>> call, Throwable t) {
                Log.e("RatingSummary", "Unable to fetch feedback", t);
                progressDialog.dismiss();
            }
        });
    }

    public void createFragments(RatingSummaryActivity activity){
        try {
            questionDao = OpenHelperManager.getHelper(activity, DBHelper.class).getCustomDao("Question");
            questionQueryBuilder = questionDao.queryBuilder();
            List<Question> questionList = questionQueryBuilder.query();
            List<Question> productQuestionList = new ArrayList<>();
            List<Question> serviceQuestionList = new ArrayList<>();
            List<Question> miscQuestionList = new ArrayList<>();

            for (Question question : questionList) {
                if (question.getQuestionType().equals(AppConstants.PRODUCT_QUESTION_TYPE)) {
                    productQuestionList.add(question);
                } else if (question.getQuestionType().equals(AppConstants.SERVICE_QUESTION_TYPE)) {
                    serviceQuestionList.add(question);
                } else if (question.getQuestionType().equals(AppConstants.MISCELLENOUS_QUESTION_TYPE)) {
                    miscQuestionList.add(question);
                }
            }

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            RatingChartFragment productRatingChartFragment = RatingChartFragment.newInstance("Product ratings",
                    productQuestionList,feedbackResponseList, questionWiseRatingFeedbackIndexList);
            fragmentTransaction.add(R.id.ratingCategoryFragments, productRatingChartFragment, "Product chart fragment");
            RatingChartFragment serviceRatingChartFragment = RatingChartFragment.newInstance("Service ratings",
                    serviceQuestionList,feedbackResponseList, questionWiseRatingFeedbackIndexList);
            fragmentTransaction.add(R.id.ratingCategoryFragments, serviceRatingChartFragment, "Service chart fragment");
            RatingChartFragment miscRatingChartFragment = RatingChartFragment.newInstance("Misc. ratings",
                    miscQuestionList,feedbackResponseList, questionWiseRatingFeedbackIndexList);
            fragmentTransaction.add(R.id.ratingCategoryFragments, miscRatingChartFragment, "Misc. chart fragment");

            fragmentTransaction.commit();
        }
        catch (SQLException e) {

        }
    }

    public void divideRatingsByQuestion() {

        int feedbackIndex = 0;
        //Loop over all the feedbacks for the outlet and create
        for (FeedbackResponse feedbackResponse : feedbackResponseList) {

            for (Rating rating : feedbackResponse.getRatings()) {
                if (questionWiseRatingFeedbackIndexList.get(rating.getQuestionId()) == null) {
                    questionWiseRatingFeedbackIndexList.put(rating.getQuestionId(), new HashMap<Integer, List<Integer>>());
                }
                Map<Integer,List<Integer>> ratingWiseFeedbackList = questionWiseRatingFeedbackIndexList.get(rating.getQuestionId());

                if (ratingWiseFeedbackList.get(rating.getSelectedOptionIndex()) == null) {
                    ratingWiseFeedbackList.put(rating.getSelectedOptionIndex(), new ArrayList<Integer>());
                }
                ratingWiseFeedbackList.get(rating.getSelectedOptionIndex()).add(feedbackIndex);
            }
            feedbackIndex++;
        }
    }
}
