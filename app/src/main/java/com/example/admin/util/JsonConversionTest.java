package com.example.admin.util;

import com.example.admin.constants.AppConstants;
import com.example.admin.webservice.request_objects.FeedbackRequest;
import com.example.admin.webservice.request_objects.OutletRequest;
import com.example.admin.webservice.request_objects.RewardSubmitRequest;
import com.example.admin.webservice.response_objects.FeedbackResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 3/26/2016.
 */
public class JsonConversionTest {

    public static void main(String args[]) {
        HashMap<String,List<String>> map = new HashMap<>();
        List<String> silverList = new ArrayList<>();
        silverList.add("1");
        silverList.add("2");
        map.put("SL", silverList);

        RewardSubmitRequest request = new RewardSubmitRequest();
        request.setOutletCode("AAB6G7H");
        request.setRewardCategory("SL");
        request.setRewardIdList(silverList);

        GsonBuilder builder = new GsonBuilder();
        Gson gson  = builder.create();
        System.out.println(gson.toJson(request));


        OutletRequest outletRequest = new OutletRequest();
        outletRequest.setOutletName("bhf");
        outletRequest.setAliasName("dnjd");
        outletRequest.setAddrLine1("bshs");
        outletRequest.setOutletType("RET");
        outletRequest.setAddrLine2("dndjd");
        outletRequest.setPinCode("777777");
        outletRequest.setEmail("motish@gh.com");
        outletRequest.setCellNumber("6464646464");
        System.out.println(gson.toJson(outletRequest));

        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setOutletCode("69cb68c7");
        feedbackRequest.setUserName("Motish");
        feedbackRequest.setUserPhoneNumber("7738657059");
        feedbackRequest.setBillNumber("23");
        feedbackRequest.setBillAmount("2333");
        System.out.println(gson.toJson(feedbackRequest));
    }
}
