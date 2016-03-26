package com.example.admin.util;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.SelectedReward;
import com.example.admin.database.User;
import com.example.admin.webservice.request_objects.RewardSubmitRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Admin on 3/13/2016.
 */

public class RewardAllocationUtility {

    public static Map<Integer,String> categoryMapping = new HashMap<>();
    static {
        categoryMapping.put(1, AppConstants.BRONZE_CD);
        categoryMapping.put(2, AppConstants.SILVER_CD);
        categoryMapping.put(3, AppConstants.GOLD_CD);
    }

    public static SelectedReward allocateReward(String userPhoneNumber, int billAmount,Dao<SelectedReward, Integer> selectedRewardDao, Dao<User, Integer> userDao) {

        int bronzeRatio=0,silverRatio=0,goldRatio=0;
        int categoryAllocated = 0, carryForwardAmount = 0, targetAmount;
        try {
            QueryBuilder<User, Integer> userQueryBuilder = userDao.queryBuilder();
            userQueryBuilder.where().eq("phoneNumber", userPhoneNumber);
            User currentUser = userQueryBuilder.queryForFirst();
            carryForwardAmount = currentUser.getCarryForwardAmount();
        }
        catch(SQLException e) {
            //TODO handle error
        }

        targetAmount = billAmount + carryForwardAmount;

        if(targetAmount<1000){
            bronzeRatio = 15;
        }
        else if(targetAmount>=1000 && targetAmount<3000){
            //Chances are in order Bronze (40% or more), Silver (5% or less), Gold (0%)
            bronzeRatio = 40;
            bronzeRatio += ((targetAmount-1000)/1000)*10;
            silverRatio = (100-bronzeRatio) * (8/100);
            goldRatio = 0;
        }
        else if(targetAmount>=3000 && targetAmount<6000){
            //Chances are in order Silver (40% or more), Bronze (7% or less), Gold (6% or less)
            silverRatio = 40;
            silverRatio += ((targetAmount-3000)/1000)*10;
            bronzeRatio = (100-(silverRatio)) * (15/100);
            goldRatio = (100-(silverRatio+bronzeRatio))*(1/10);
        }
        else {
            //Chances are in order Gold (40% or more), Silver (20% or less), Bronze (12% or less)
            goldRatio = 40;
            goldRatio += ((targetAmount-6000)/1000)*10;
            silverRatio = (100-(goldRatio)) * (30/100);
            bronzeRatio = (100-(silverRatio+goldRatio))*(2/10);
        }

        Random randomGenerator = new SecureRandom();

        int randomNumber = randomGenerator.nextInt(100);

        if(randomNumber<bronzeRatio){
            //Bronze allocated
            categoryAllocated = 1;
        }
        else if(randomNumber<(bronzeRatio+silverRatio)){
            //Silver allocated
            categoryAllocated = 2;
        }
        else if(randomNumber<(bronzeRatio+silverRatio+goldRatio)){
            //Gold allocated
            categoryAllocated = 3;
        }
        System.out.println(categoryAllocated);
        if(categoryAllocated>0 && selectedRewardDao!=null)
        {
            return getReward(selectedRewardDao,categoryAllocated);
        }
        else {
            return null;
        }
    }

    public static void main(String args[]) {
        HashMap<String,List<String>> map = new HashMap<>();
        List<String> bronzeList = new ArrayList<>();
        bronzeList.add("1");
        bronzeList.add("2");
        map.put("BZ", bronzeList);
        List<String> silverList = new ArrayList<>();
        silverList.add("1");
        silverList.add("2");
        map.put("SL", silverList);

        RewardSubmitRequest request = new RewardSubmitRequest();
        request.setOutletCode("AAB6G7H");
        request.setRewardsMap(map);

        GsonBuilder builder = new GsonBuilder();
        Gson gson  = builder.create();
        System.out.println(gson.toJson(request));
    }

    private static SelectedReward getReward(Dao<SelectedReward, Integer> selectedRewardDao, int categoryAllocated) {
        QueryBuilder<SelectedReward,Integer> selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();
        try {
            selectedRewardQueryBuilder.where().eq("rewardCategory",categoryMapping.get(categoryAllocated));
            List<SelectedReward> possibleRewards = selectedRewardQueryBuilder.query();
            if(possibleRewards.size()>0){
                Random randomGenerator = new SecureRandom();
                int randomRewardIndex = randomGenerator.nextInt(possibleRewards.size());
                return possibleRewards.get(randomRewardIndex);
            }
            else if (categoryAllocated>1){
                return getReward(selectedRewardDao,categoryAllocated-1);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}