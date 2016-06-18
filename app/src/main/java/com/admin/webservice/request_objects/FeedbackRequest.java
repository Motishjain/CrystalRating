package com.admin.webservice.request_objects;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by mjai37 on 1/21/2016.
 */

public class FeedbackRequest implements Serializable {

    private String outletCode;

    private String billNumber;

    private String billAmount;

    private String userPhoneNumber;

    private String userName;

    private Map<String,String> ratingsMap;

    private String rewardId;

    private String rewardCategory;

    private Date createdDate;

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

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public void setRewardCategory(String rewardCategory) {
        this.rewardCategory = rewardCategory;
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

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
