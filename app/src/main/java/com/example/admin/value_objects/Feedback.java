package com.example.admin.value_objects;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by mjai37 on 1/21/2016.
 */

public class Feedback implements Serializable {

    private String outletCode;

    private String billNumber;

    private String billAmount;

    private String userPhoneNumber;

    private Map<String,String> ratingsMap;

    private String rewardId;

    private int rewardCategory;

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public Map<String, String> getRatingsMap() {
        return ratingsMap;
    }

    public void setRatingsMap(Map<String, String> ratingsMap) {
        this.ratingsMap = ratingsMap;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public int getRewardCategory() {
        return rewardCategory;
    }

    public void setRewardCategory(int rewardCategory) {
        this.rewardCategory = rewardCategory;
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

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
