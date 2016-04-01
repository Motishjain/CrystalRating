package com.example.admin.webservice.response_objects;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by Admin on 3/31/2016.
 */
public class FeedbackResponse {

    public FeedbackResponse(Map<String,Integer> ratingsMap,String userName,String userPhoneNumber,Timestamp createdTs,String billNumber,String billAmount, String rewardId){
        this.ratingsMap = ratingsMap;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.createdTs = createdTs;
        this.billNumber = billNumber;
        this.billAmount = billAmount;
        this.rewardId = rewardId;
    }

    private Map<String,Integer> ratingsMap;
    private String userName;
    private String userPhoneNumber;
    private Timestamp createdTs;
    private String billNumber;
    private String billAmount;
    private String rewardId;

    public Map<String, Integer> getRatingsMap() {
        return ratingsMap;
    }

    public void setRatingsMap(Map<String, Integer> ratingsMap) {
        this.ratingsMap = ratingsMap;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public Timestamp getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(Timestamp createdTs) {
        this.createdTs = createdTs;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }
}
