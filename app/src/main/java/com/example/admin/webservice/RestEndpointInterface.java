package com.example.admin.webservice;

import com.example.admin.constants.AppConstants;
import com.example.admin.webservice.request_objects.OutletRequest;
import com.example.admin.webservice.request_objects.FeedbackRequest;
import com.example.admin.webservice.request_objects.RewardSubmitRequest;
import com.example.admin.webservice.response_objects.FeedbackResponse;
import com.example.admin.webservice.response_objects.PostServiceResponse;
import com.example.admin.webservice.response_objects.QuestionResponse;
import com.example.admin.webservice.response_objects.RewardResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Admin on 3/16/2016.
 */
public interface RestEndpointInterface {
    @GET(AppConstants.FETCH_REWARDS)
    Call<List<RewardResponse>> fetchRewards(@Query("outletType") String outletType);

    @GET(AppConstants.FETCH_QUESTIONS)
    Call<List<QuestionResponse>> fetchQuestions(@Query("outletType") String outletType);

    @POST(AppConstants.REGISTER_OUTLET)
    Call<PostServiceResponse> registerOutlet(@Body OutletRequest outletRequest);

    @POST(AppConstants.SAVE_REWARDS)
    Call<PostServiceResponse> saveRewards(@Body RewardSubmitRequest rewardSubmitRequest);

    @POST(AppConstants.SUBMIT_FEEDBACK)
    Call<PostServiceResponse> submitFeedback(@Body FeedbackRequest feedbackRequest);

    @GET(AppConstants.FETCH_FEEDBACK)
    Call<List<FeedbackResponse>> fetchFeedback(@Query("outletCode") String outletCode, @Query("fromDate") String fromDate, @Query("toDate") String toDate);

}
