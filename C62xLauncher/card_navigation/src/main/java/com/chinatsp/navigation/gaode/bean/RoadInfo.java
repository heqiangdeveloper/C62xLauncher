package com.chinatsp.navigation.gaode.bean;

import android.text.TextUtils;

import org.json.JSONObject;

public class RoadInfo {
    private String curRoadName;



    public String getCurRoadName() {
        return curRoadName;
    }

    public static RoadInfo parseFrom(JSONObject jsonObject) {
        RoadInfo roadInfo = new RoadInfo();
        String roadName = null;
        if (jsonObject != null) {
            roadName = jsonObject.optString("curRoadName");
        }
        if (TextUtils.isEmpty(roadName)) {
            roadName = "未知道路";
        }
        roadInfo.curRoadName = roadName;
        return roadInfo;
    }
}
