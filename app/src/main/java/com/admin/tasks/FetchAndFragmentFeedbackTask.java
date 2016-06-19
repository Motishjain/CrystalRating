package com.admin.tasks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Question;
import com.admin.freddyspeaks.R;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import layout.RatingChartFragment;
import layout.RatingSummaryFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 22-04-2016.
 */
public class FetchAndFragmentFeedbackTask extends AsyncTask<RatingSummaryFragment, Void, Void> {

    Dao<Question, Integer> questionDao;
    QueryBuilder<Question, Integer> questionQueryBuilder;

    //List of feedbacks for a given time frame
    List<FeedbackResponse> feedbackResponseList;
    List<Question> productQuestionList;
    List<Question> serviceQuestionList;
    List<Question> miscQuestionList;

    /*Map to save ratings for each question id (String). In turn, for every question id there is a map
    storing list of feedbackId indexes for each ratingOption*/
    Map<String,Map<Integer, List<Integer>>> questionWiseRatingFeedbackIndexList = new HashMap<>();

    double productAverageRating, serviceAverageRating, miscAverageRating;

    SimpleDateFormat webServiceDateFormat;
    @Override
    protected Void doInBackground(RatingSummaryFragment... input) {
        RatingSummaryFragment ratingSummaryFragment = input[0];
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        fetchFeedback(ratingSummaryFragment,ratingSummaryFragment.getFromDate(),ratingSummaryFragment.getToDate());
        return null;
    }

    public void fetchFeedback(final RatingSummaryFragment ratingSummaryFragment, Date fromDate, Date toDate) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ratingSummaryFragment.getActivity());
        String outletCode = sharedPreferences.getString("outletCode", null);
        feedbackResponseList = new ArrayList<>();
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        //Add one day to toDate as the time saved is greater than the date. If feedback is saved on 23rd Apr, timestamp is saved as 23rd Apr + (time)
        Calendar c = Calendar.getInstance();
        c.setTime(toDate);
        c.add(Calendar.DAY_OF_MONTH,1);

        Call<List<FeedbackResponse>> fetchRewardsCall = restEndpointInterface.fetchFeedback(outletCode, webServiceDateFormat.format(fromDate), webServiceDateFormat.format(c.getTime()));

        fetchRewardsCall.enqueue(new Callback<List<FeedbackResponse>>() {
            @Override
            public void onResponse(Call<List<FeedbackResponse>> call, Response<List<FeedbackResponse>> response) {
                if (response.isSuccess()) {
                    ratingSummaryFragment.getServerNotReachableView().setVisibility(View.GONE);
                    ratingSummaryFragment.getRatingCategoryFragments().setVisibility(View.VISIBLE);
                    feedbackResponseList = response.body();
                    divideRatingsByQuestion();
                    createFragments(ratingSummaryFragment);
                }
            }

            @Override
            public void onFailure(Call<List<FeedbackResponse>> call, Throwable t) {
                Log.e("RatingSummary", "Unable to fetch feedback", t);
                ratingSummaryFragment.getProgressDialog().dismiss();
                ratingSummaryFragment.getServerNotReachableView().setVisibility(View.VISIBLE);
                ratingSummaryFragment.getRatingCategoryFragments().setVisibility(View.GONE);
            }
        });
    }

    public void createFragments(RatingSummaryFragment fragment){
        try {
            questionDao = OpenHelperManager.getHelper(fragment.getActivity(), DBHelper.class).getCustomDao("Question");
            questionQueryBuilder = questionDao.queryBuilder();
            List<Question> questionList = questionQueryBuilder.query();
            productQuestionList = new ArrayList<>();
            serviceQuestionList = new ArrayList<>();
            miscQuestionList = new ArrayList<>();

            for (Question question : questionList) {
                if (question.getQuestionType().equals(AppConstants.PRODUCT_QUESTION_TYPE)) {
                    productQuestionList.add(question);
                } else if (question.getQuestionType().equals(AppConstants.SERVICE_QUESTION_TYPE)) {
                    serviceQuestionList.add(question);
                } else if (question.getQuestionType().equals(AppConstants.MISCELLENOUS_QUESTION_TYPE)) {
                    miscQuestionList.add(question);
                }
            }

            productAverageRating = calculateAverageRating(productQuestionList);
            serviceAverageRating = calculateAverageRating(serviceQuestionList);
            miscAverageRating = calculateAverageRating(miscQuestionList);

            FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment productRatingFragment = fragmentManager.findFragmentByTag("Product chart fragment");
            Fragment serviceRatingFragment = fragmentManager.findFragmentByTag("Service chart fragment");
            Fragment miscRatingFragment = fragmentManager.findFragmentByTag("Misc. chart fragment");

            if(productRatingFragment!=null) {
                fragmentTransaction.remove(productRatingFragment);
            }
            if(serviceRatingFragment!=null) {
                fragmentTransaction.remove(serviceRatingFragment);
            }
            if(miscRatingFragment!=null) {
                fragmentTransaction.remove(miscRatingFragment);
            }

            fragmentTransaction.commit();

            RatingChartFragment productRatingChartFragment = RatingChartFragment.newInstance("Product ratings",productAverageRating,
                    productQuestionList,feedbackResponseList, questionWiseRatingFeedbackIndexList);
            RatingChartFragment serviceRatingChartFragment = RatingChartFragment.newInstance("Service ratings", serviceAverageRating,
                    serviceQuestionList,feedbackResponseList, questionWiseRatingFeedbackIndexList);
            RatingChartFragment miscRatingChartFragment = RatingChartFragment.newInstance("Misc. ratings", miscAverageRating,
                    miscQuestionList,feedbackResponseList, questionWiseRatingFeedbackIndexList);

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.ratingCategoryFragments, productRatingChartFragment, "Product chart fragment");
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.ratingCategoryFragments, serviceRatingChartFragment, "Service chart fragment");
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.ratingCategoryFragments, miscRatingChartFragment, "Misc. chart fragment");
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
            fragment.getProgressDialog().dismiss();
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

    public double calculateAverageRating(List<Question> questionList) {
        double sumRatingValue = 0;
        List<Question> answeredQuestionList = new ArrayList<>();
        for(Question question:questionList) {
            Map<Integer, List<Integer>> ratingWiseFeedbackList = questionWiseRatingFeedbackIndexList.get(question.getQuestionId());
            int options = question.getRatingValues().split(";").length;
            double sumQuestionRatingValue = 0;
            int sumQuestionRatings = 0;
            if(ratingWiseFeedbackList!=null) {
                for (Integer optionValue : ratingWiseFeedbackList.keySet()) {
                    //If question input type is star, rating is same as selected star position
                    int rating = optionValue;
                    //In case of options, higher the position, lower is the rating
                    if(question.getQuestionInputType().equals(AppConstants.OPTION_RATING)) {
                        rating = options - optionValue + 1;
                    }

                    sumQuestionRatingValue += ratingWiseFeedbackList.get(optionValue).size() * rating;
                    sumQuestionRatings += ratingWiseFeedbackList.get(optionValue).size();
                }
                //Normalizing the rating as out of 5
                sumRatingValue += ((sumQuestionRatingValue / sumQuestionRatings) / options) * 5;
                answeredQuestionList.add(question);
            }
        }
        questionList.clear();
        questionList.addAll(answeredQuestionList);
        return sumRatingValue/answeredQuestionList.size();
    }
}
