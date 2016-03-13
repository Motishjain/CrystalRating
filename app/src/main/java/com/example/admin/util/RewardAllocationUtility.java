package com.example.admin.util;

import com.example.admin.database.RewardHistory;
import com.example.admin.database.SelectedReward;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 * Created by Admin on 3/13/2016.
 */

public class RewardAllocationUtility {

    public static SelectedReward allocateReward(String userPhoneNumber, int billAmount,Dao<SelectedReward, Integer> selectedRewardDao) {
        String rewardId = null;
        int bronzeRatio=0,silverRatio=0,goldRatio=0;
        int categoryAllocated = 0;


        if(billAmount<1000){
            bronzeRatio = 15;
        }
        else if(billAmount>=1000 && billAmount<3000){
            //Chances are in order Bronze (40% or more), Silver (5% or less), Gold (0%)
            bronzeRatio = 40;
            bronzeRatio += ((billAmount-1000)/1000)*10;
            silverRatio = (100-bronzeRatio) * (8/100);
            goldRatio = 0;
        }
        else if(billAmount>=3000 && billAmount<6000){
            //Chances are in order Silver (40% or more), Bronze (7% or less), Gold (6% or less)
            silverRatio = 40;
            silverRatio += ((silverRatio-1000)/1000)*10;
            bronzeRatio = (100-(silverRatio)) * (15/100);
            goldRatio = (100-(silverRatio+bronzeRatio))*(1/10);
        }
        else {
            //Chances are in order Gold (40% or more), Silver (20% or less), Bronze (12% or less)
            goldRatio = 40;
            goldRatio += ((goldRatio-1000)/1000)*10;
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
        if(categoryAllocated>0)
        {
            return getReward(selectedRewardDao,categoryAllocated);
        }
        else {
            return null;
        }
    }

    public static void main(String args[]) {
        Random randomGenerator = new SecureRandom();
        System.out.print(randomGenerator.nextInt(100));
    }

    private static SelectedReward getReward(Dao<SelectedReward, Integer> selectedRewardDao, int categoryAllocated) {
        QueryBuilder<SelectedReward,Integer> selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();
        try {
            selectedRewardQueryBuilder.where().eq("rewardCategory",categoryAllocated);
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