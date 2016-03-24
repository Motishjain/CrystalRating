package com.example.admin.webservice.request_objects;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 3/25/2016.
 */
public class RewardSubmitRequest {

    private String outletCode;

    private Map<String,List<String>> rewardsMap;

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public Map<String, List<String>> getRewardsMap() {
        return rewardsMap;
    }

    public void setRewardsMap(Map<String, List<String>> rewardsMap) {
        this.rewardsMap = rewardsMap;
    }
}
