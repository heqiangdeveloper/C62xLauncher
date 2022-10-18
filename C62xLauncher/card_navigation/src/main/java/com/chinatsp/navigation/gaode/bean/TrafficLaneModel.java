package com.chinatsp.navigation.gaode.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class TrafficLaneModel {
    private boolean trafficLaneEnabled;
    private List<LaneInfo> trafficLaneInfos;
    private int trafficLaneSize;


    public boolean isTrafficLaneEnabled() {
        return trafficLaneEnabled;
    }

    public List<LaneInfo> getTrafficLaneInfos() {
        return trafficLaneInfos;
    }

    public int getTrafficLaneSize() {
        return trafficLaneSize;
    }

    public static class LaneInfo {
        private int trafficLaneNo;
        private int trafficLaneIcon;
        private int trafficLaneExtended;
        private boolean trafficLaneAdvised;

        public int getTrafficLaneNo() {
            return trafficLaneNo;
        }

        public int getTrafficLaneIcon() {
            return trafficLaneIcon;
        }

        public int getTrafficLaneExtended() {
            return trafficLaneExtended;
        }

        public boolean isTrafficLaneAdvised() {
            return trafficLaneAdvised;
        }
    }

    public static TrafficLaneModel parseFrom(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        TrafficLaneModel trafficLaneModel = new TrafficLaneModel();
        trafficLaneModel.trafficLaneEnabled = jsonObject.optBoolean("trafficLaneEnabled");
        trafficLaneModel.trafficLaneSize = jsonObject.optInt("trafficLaneSize");
        JSONArray trafficLaneArray = jsonObject.optJSONArray("trafficLaneInfos");
        if (trafficLaneArray != null) {
            List<LaneInfo> laneInfoList = new LinkedList<>();
            for (int i = 0; i < trafficLaneArray.length(); i++) {
                JSONObject laneJsonObj = trafficLaneArray.optJSONObject(i);
                if (laneJsonObj != null) {
                    LaneInfo laneInfo = new LaneInfo();
                    laneInfo.trafficLaneAdvised = laneJsonObj.optBoolean("trafficLaneAdvised");
                    laneInfo.trafficLaneExtended = laneJsonObj.optInt("trafficLaneExtended");
                    laneInfo.trafficLaneIcon = laneJsonObj.optInt("trafficLaneIcon");
                    laneInfo.trafficLaneNo = laneJsonObj.optInt("trafficLaneNo");
                }
            }
        }
        return trafficLaneModel;
    }
}
