package com.example.admin.util;

import com.example.admin.webservice.request_objects.RewardSubmitRequest;
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
}
