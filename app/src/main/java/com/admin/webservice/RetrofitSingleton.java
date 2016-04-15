package com.admin.webservice;

import com.admin.constants.AppConstants;
import com.admin.database.Reward;
import com.admin.webservice.response_objects.QuestionResponse;
import com.admin.webservice.response_objects.RewardResponse;
import com.j256.ormlite.dao.Dao;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 3/23/2016.
 */
public class RetrofitSingleton {

    static RestEndpointInterface restEndpointInterface;

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restEndpointInterface = retrofit.create(RestEndpointInterface.class);
    }

    public static RestEndpointInterface newInstance(){
        return restEndpointInterface;
    }
}
