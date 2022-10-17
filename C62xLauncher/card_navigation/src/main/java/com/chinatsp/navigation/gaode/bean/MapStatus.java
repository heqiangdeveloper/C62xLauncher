package com.chinatsp.navigation.gaode.bean;

import org.json.JSONObject;

/**
 * Author: Steven.Yang
 * Description:
 */
public class MapStatus {
    private int autoStatus;
    private int statusDetails;

    public static final int START_NAVIGATION = 16;
    public static final int STOP_NAVIGATION = 17;
    public static final int ARRIVED_NAVIGATION = 18; // 在STOP_NAVIGATION之前就会透出


    public int getAutoStatus() {
        return autoStatus;
    }

    public void setAutoStatus(int autoStatus) {
        this.autoStatus = autoStatus;
    }

    public int getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(int statusDetails) {
        this.statusDetails = statusDetails;
    }


    public static MapStatus parseFrom(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        MapStatus mapStatus = new MapStatus();
        mapStatus.autoStatus = jsonObject.optInt("autoStatus");
        mapStatus.statusDetails = jsonObject.optInt("statusDetails");
        return mapStatus;
    }

    @Override
    public String toString() {
        return "MapStatus{" +
                "autoStatus=" + autoStatus +
                ", statusDetails=" + statusDetails +
                '}';
    }
}
