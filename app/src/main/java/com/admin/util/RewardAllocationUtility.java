package com.admin.util;

import android.util.Log;

import com.admin.constants.AppConstants;
import com.admin.database.SelectedReward;
import com.admin.database.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.PrintWriter;
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

    public static SelectedReward allocateReward(double billAmount, Dao<SelectedReward, Integer> selectedRewardDao) {
        double rewardAmountMean = (1.5 * billAmount) / 100;
        double rewardAmountLowerBound = (1 * billAmount) / 100;
        double rewardAmountUpperBound = (2 * billAmount) / 100;
        double minDiff = 9999;
        int selectedRewardIndex = -1;
        List<Integer> selectedRewardIndexList = new ArrayList<>();

        QueryBuilder<SelectedReward, Integer> selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();
        selectedRewardQueryBuilder.orderBy("rewardCost",true);
        try {
            List<SelectedReward> possibleRewards = selectedRewardQueryBuilder.query();
            for(int i = 0;i < possibleRewards.size();i++) {
                SelectedReward possibleReward = possibleRewards.get(i);
                double cost = Double.parseDouble(possibleReward.getRewardCost());
                if(Math.abs(cost-rewardAmountMean)<minDiff) {
                    selectedRewardIndex = i;
                    minDiff = Math.abs(cost-rewardAmountMean);
                }
                if(rewardAmountLowerBound<=cost && cost<=rewardAmountUpperBound) {
                    selectedRewardIndexList.add(i);
                }
            }
            if(selectedRewardIndexList.size()>0) {
                Random randomGenerator = new SecureRandom();
                selectedRewardIndex = selectedRewardIndexList.get(randomGenerator.nextInt(selectedRewardIndexList.size()));
            }
            return possibleRewards.get(selectedRewardIndex);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("D:/freddy/algo-analysis.txt", "UTF-8");
            for (int i = 1; i < 500; i++) {
                Random r = new Random();
                int low = 200;
                int high = 10000;
                int result = r.nextInt(high - low) + low;
                writer.append(i + "," + result + "," + (allocateReward(result, null)));
                writer.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}