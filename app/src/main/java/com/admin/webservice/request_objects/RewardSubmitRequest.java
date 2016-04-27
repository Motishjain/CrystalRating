package com.admin.webservice.request_objects;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 3/25/2016.
 */
public class RewardSubmitRequest {

    private String outletCode;

    private String rewardCategory;

    private List<String> rewardIdList;

    private String createdDate;

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public String getRewardCategory() {
        return rewardCategory;
    }

    public void setRewardCategory(String rewardCategory) {
        this.rewardCategory = rewardCategory;
    }

    public List<String> getRewardIdList() {
        return rewardIdList;
    }

    public void setRewardIdList(List<String> rewardIdList) {
        this.rewardIdList = rewardIdList;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
