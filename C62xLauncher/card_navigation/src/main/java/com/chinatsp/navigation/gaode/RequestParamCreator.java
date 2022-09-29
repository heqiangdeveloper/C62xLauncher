package com.chinatsp.navigation.gaode;

import android.util.Log;

import com.autonavi.amapauto.jsonsdk.JsonProtocolManager;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestParamCreator {
    private String mVersionName;

    public RequestParamCreator() {
        JsonProtocolManager jsonProtocolManager = JsonProtocolManager.getInstance();
        mVersionName = jsonProtocolManager.getSDKVersion();
    }


    private JSONObject createCommonParamJson() {
        JSONObject request = new JSONObject();
        try {
            request.put("requestCode", "");
            request.put("responseCode", "");
            request.put("needResponse", true);
            request.put("versionName", mVersionName);
            request.put("requestAuthor", "Launcher");
            request.put("message", "");
            request.put("messageType", "request");
            request.put("statusCode", 0);
        } catch (JSONException e) {
            Log.e("request", e.getMessage(), e);
        }
        return request;
    }

    private static void putProtocolId(JSONObject jsonObject, int id) {
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.put("protocolId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String createMyLocation() {
        JSONObject jsonObject = createCommonParamJson();
        try {
            putProtocolId(jsonObject,ProtocolIds.MY_LOCATION);
            JSONObject dataObj = new JSONObject();
            dataObj.put("type", 0);
            jsonObject.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String createNavigationStatus() {
        JSONObject jsonObject = createCommonParamJson();
        try {
            putProtocolId(jsonObject,ProtocolIds.NAVIGATION_STATUS);
            JSONObject dataObj = new JSONObject();
            jsonObject.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    public String createNavigationToHomeOrCompany(int destType) {
        JSONObject jsonObject = createCommonParamJson();
        try {
            putProtocolId(jsonObject,ProtocolIds.NAVIGATION_HOME_OR_COMPANY);
            JSONObject dataObj = new JSONObject();
            jsonObject.put("destType", destType);
            jsonObject.put("directNavi", 0);
            jsonObject.put("dev", 0);
            jsonObject.put("strategy", 0);
            jsonObject.put("newStrategy", -100);
            jsonObject.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
