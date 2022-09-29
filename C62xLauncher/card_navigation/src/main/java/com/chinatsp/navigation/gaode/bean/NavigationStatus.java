package com.chinatsp.navigation.gaode.bean;

import android.util.Log;

import org.json.JSONObject;

public class NavigationStatus {

    public static final int STATUS_CRUISE = 0;
    public static final int STATUS_IN_NAVIGATION = 1;
    public static final int STATUS_IN_NAVIGATION_MOCK = 2;

    private int status;
    private String errorMessage;
    private int resultCode;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public static NavigationStatus parseFrom(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        NavigationStatus status = new NavigationStatus();
        status.status = jsonObject.optInt("status");
        status.errorMessage = jsonObject.optString("errorMessage");
        status.resultCode = jsonObject.optInt("resultCode");
        return status;
    }
}
