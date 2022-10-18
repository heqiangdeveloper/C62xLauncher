package com.chinatsp.navigation.gaode.bean;

import android.util.Log;

import com.chinatsp.navigation.gaode.ProtocolIds;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ResponseConvert<T> {
    public GaoDeResponse<T> convertFromJson(String json) {
        GaoDeResponse<T> gaoDeResponse = new GaoDeResponse();
        try {
            JSONObject jsonObject = new JSONObject(json);
            gaoDeResponse.setRequestCode(jsonObject.optString("requestCode"));
            gaoDeResponse.setResponseCode(jsonObject.optString("responseCode"));
            gaoDeResponse.setNeedResponse(jsonObject.optBoolean("needResponse"));
            gaoDeResponse.setProtocolId(jsonObject.optInt("protocolId"));
            gaoDeResponse.setVersionName(jsonObject.optString("versionName"));
            gaoDeResponse.setRequestAuthor(jsonObject.optString("requestAuthor"));
            gaoDeResponse.setMessageType(jsonObject.optString("messageType"));
            JSONObject dataJson = jsonObject.optJSONObject("data");
            if (dataJson != null) {
                Object data = createData(dataJson, gaoDeResponse.getProtocolId());
                gaoDeResponse.setData((T) data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gaoDeResponse;
    }

    private Object createData(JSONObject jsonObject, int protocolId) {
        if (jsonObject == null) {
            return null;
        }
        switch (protocolId) {
            case ProtocolIds.MY_LOCATION:
                return Address.parseFrom(jsonObject);
            case ProtocolIds.NAVIGATION_STATUS:
                return NavigationStatus.parseFrom(jsonObject);
            case ProtocolIds.CURRENT_ROAD_NAME:
                return RoadInfo.parseFrom(jsonObject);
            case ProtocolIds.NAVI_GUIDE_INFO:
                return GuideInfo.parseFrom(jsonObject);
            case ProtocolIds.MAP_STATUS:
                return MapStatus.parseFrom(jsonObject);
            case ProtocolIds.TRAFFIC_LANE_INFO:
                return TrafficLaneModel.parseFrom(jsonObject);
        }
        return null;
    }

}
