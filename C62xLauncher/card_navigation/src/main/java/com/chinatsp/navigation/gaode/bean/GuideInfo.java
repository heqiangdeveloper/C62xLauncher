package com.chinatsp.navigation.gaode.bean;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Author: Steven.Yang
 * Description:
 */
public class GuideInfo {
    private String packageName;
    private String clientPackageName;
    private int callbackId;
    private int timeStamp;
    private String var1;
    private int type;
    private String curRoadName;
    private String nextRoadName;
    private int cameraDist;
    private int cameraType;
    private int cameraSpeed;
    private int cameraIndex;
    private int icon;
    private int newIcon;
    private int routeRemainDis;
    private int routeRemainTime;
    private int segRemainDis;
    private int segRemainTime;
    private int carDirection;
    private int carLatitude;
    private int carLongitude;
    private int limitedSpeed;
    private int curSegNum;
    private int curPointNum;
    private int roundAboutNum;
    private int roundAllNum;
    private int routeAllDis;
    private int routeAllTime;
    private int curSpeed;
    private int trafficLightNum;
    private int sapaDist;
    private int nextSapaDist;
    private int sapaType;
    private int nextSapaType;
    private int sapaNum;
    private int roadType;
    private int currentRoadTotalDis;
    private String routeRemainDistanceAuto;
    private String routeRemainTimeAuto;
    private String segRemainDisAuto;
    private String nextNextRoadName;
    private int nextNextTurnIcon;
    private int nextSegRemainDis;
    private int nextSegRemainTime;
    private int segAssistantAction;
    private int roundaboutOutAngle;
    private String etaText;
    private int nextRoadProgressPrecent;
    private int turnIconWeight;
    private int turnIconHeight;
    private boolean cameraPenalty;
    private boolean nextRoadNOAOrNot;
    private boolean newCamera;
    private long cameraID;
    private String endPOIName;
    private String endPOIAddr;
    private String endPOIType;
    private double endPOILongitude;
    private double endPOILatitude;
    private String arrivePOIType;
    private double arrivePOILongitude;
    private double arrivePOILatitude;
    private int viaPOItime;
    private int viaPOIdistance;
    private String endPOICityName;
    private String endPOIDistrictName;
    private String viaPOIArrivalTime;
    private String addIcon;
    private String nextNextAddIcon;
    private String nextSegRemainDisAuto;
    private int resultCode;



    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setClientPackageName(String clientPackageName) {
        this.clientPackageName = clientPackageName;
    }

    public String getClientPackageName() {
        return clientPackageName;
    }

    public void setCallbackId(int callbackId) {
        this.callbackId = callbackId;
    }

    public int getCallbackId() {
        return callbackId;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setVar1(String var1) {
        this.var1 = var1;
    }

    public String getVar1() {
        return var1;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setCurRoadName(String curRoadName) {
        this.curRoadName = curRoadName;
    }

    public String getCurRoadName() {
        return curRoadName;
    }

    public void setNextRoadName(String nextRoadName) {
        this.nextRoadName = nextRoadName;
    }

    public String getNextRoadName() {
        return nextRoadName;
    }

    public void setCameraDist(int cameraDist) {
        this.cameraDist = cameraDist;
    }

    public int getCameraDist() {
        return cameraDist;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraSpeed(int cameraSpeed) {
        this.cameraSpeed = cameraSpeed;
    }

    public int getCameraSpeed() {
        return cameraSpeed;
    }

    public void setCameraIndex(int cameraIndex) {
        this.cameraIndex = cameraIndex;
    }

    public int getCameraIndex() {
        return cameraIndex;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setNewIcon(int newIcon) {
        this.newIcon = newIcon;
    }

    public int getNewIcon() {
        return newIcon;
    }

    public void setRouteRemainDis(int routeRemainDis) {
        this.routeRemainDis = routeRemainDis;
    }

    public int getRouteRemainDis() {
        return routeRemainDis;
    }

    public void setRouteRemainTime(int routeRemainTime) {
        this.routeRemainTime = routeRemainTime;
    }

    public int getRouteRemainTime() {
        return routeRemainTime;
    }

    public void setSegRemainDis(int segRemainDis) {
        this.segRemainDis = segRemainDis;
    }

    public int getSegRemainDis() {
        return segRemainDis;
    }

    public void setSegRemainTime(int segRemainTime) {
        this.segRemainTime = segRemainTime;
    }

    public int getSegRemainTime() {
        return segRemainTime;
    }

    public void setCarDirection(int carDirection) {
        this.carDirection = carDirection;
    }

    public int getCarDirection() {
        return carDirection;
    }

    public void setCarLatitude(int carLatitude) {
        this.carLatitude = carLatitude;
    }

    public int getCarLatitude() {
        return carLatitude;
    }

    public void setCarLongitude(int carLongitude) {
        this.carLongitude = carLongitude;
    }

    public int getCarLongitude() {
        return carLongitude;
    }

    public void setLimitedSpeed(int limitedSpeed) {
        this.limitedSpeed = limitedSpeed;
    }

    public int getLimitedSpeed() {
        return limitedSpeed;
    }

    public void setCurSegNum(int curSegNum) {
        this.curSegNum = curSegNum;
    }

    public int getCurSegNum() {
        return curSegNum;
    }

    public void setCurPointNum(int curPointNum) {
        this.curPointNum = curPointNum;
    }

    public int getCurPointNum() {
        return curPointNum;
    }

    public void setRoundAboutNum(int roundAboutNum) {
        this.roundAboutNum = roundAboutNum;
    }

    public int getRoundAboutNum() {
        return roundAboutNum;
    }

    public void setRoundAllNum(int roundAllNum) {
        this.roundAllNum = roundAllNum;
    }

    public int getRoundAllNum() {
        return roundAllNum;
    }

    public void setRouteAllDis(int routeAllDis) {
        this.routeAllDis = routeAllDis;
    }

    public int getRouteAllDis() {
        return routeAllDis;
    }

    public void setRouteAllTime(int routeAllTime) {
        this.routeAllTime = routeAllTime;
    }

    public int getRouteAllTime() {
        return routeAllTime;
    }

    public void setCurSpeed(int curSpeed) {
        this.curSpeed = curSpeed;
    }

    public int getCurSpeed() {
        return curSpeed;
    }

    public void setTrafficLightNum(int trafficLightNum) {
        this.trafficLightNum = trafficLightNum;
    }

    public int getTrafficLightNum() {
        return trafficLightNum;
    }

    public void setSapaDist(int sapaDist) {
        this.sapaDist = sapaDist;
    }

    public int getSapaDist() {
        return sapaDist;
    }

    public void setNextSapaDist(int nextSapaDist) {
        this.nextSapaDist = nextSapaDist;
    }

    public int getNextSapaDist() {
        return nextSapaDist;
    }

    public void setSapaType(int sapaType) {
        this.sapaType = sapaType;
    }

    public int getSapaType() {
        return sapaType;
    }

    public void setNextSapaType(int nextSapaType) {
        this.nextSapaType = nextSapaType;
    }

    public int getNextSapaType() {
        return nextSapaType;
    }

    public void setSapaNum(int sapaNum) {
        this.sapaNum = sapaNum;
    }

    public int getSapaNum() {
        return sapaNum;
    }

    public void setRoadType(int roadType) {
        this.roadType = roadType;
    }

    public int getRoadType() {
        return roadType;
    }

    public void setCurrentRoadTotalDis(int currentRoadTotalDis) {
        this.currentRoadTotalDis = currentRoadTotalDis;
    }

    public int getCurrentRoadTotalDis() {
        return currentRoadTotalDis;
    }

    public void setRouteRemainDistanceAuto(String routeRemainDistanceAuto) {
        this.routeRemainDistanceAuto = routeRemainDistanceAuto;
    }

    public String getRouteRemainDistanceAuto() {
        return routeRemainDistanceAuto;
    }

    public void setRouteRemainTimeAuto(String routeRemainTimeAuto) {
        this.routeRemainTimeAuto = routeRemainTimeAuto;
    }

    public String getRouteRemainTimeAuto() {
        return routeRemainTimeAuto;
    }

    public void setSegRemainDisAuto(String segRemainDisAuto) {
        this.segRemainDisAuto = segRemainDisAuto;
    }

    public String getSegRemainDisAuto() {
        return segRemainDisAuto;
    }

    public void setNextNextRoadName(String nextNextRoadName) {
        this.nextNextRoadName = nextNextRoadName;
    }

    public String getNextNextRoadName() {
        return nextNextRoadName;
    }

    public void setNextNextTurnIcon(int nextNextTurnIcon) {
        this.nextNextTurnIcon = nextNextTurnIcon;
    }

    public int getNextNextTurnIcon() {
        return nextNextTurnIcon;
    }

    public void setNextSegRemainDis(int nextSegRemainDis) {
        this.nextSegRemainDis = nextSegRemainDis;
    }

    public int getNextSegRemainDis() {
        return nextSegRemainDis;
    }

    public void setNextSegRemainTime(int nextSegRemainTime) {
        this.nextSegRemainTime = nextSegRemainTime;
    }

    public int getNextSegRemainTime() {
        return nextSegRemainTime;
    }

    public void setSegAssistantAction(int segAssistantAction) {
        this.segAssistantAction = segAssistantAction;
    }

    public int getSegAssistantAction() {
        return segAssistantAction;
    }

    public void setRoundaboutOutAngle(int roundaboutOutAngle) {
        this.roundaboutOutAngle = roundaboutOutAngle;
    }

    public int getRoundaboutOutAngle() {
        return roundaboutOutAngle;
    }

    public void setEtaText(String etaText) {
        this.etaText = etaText;
    }

    public String getEtaText() {
        return etaText;
    }

    public void setNextRoadProgressPrecent(int nextRoadProgressPrecent) {
        this.nextRoadProgressPrecent = nextRoadProgressPrecent;
    }

    public int getNextRoadProgressPrecent() {
        return nextRoadProgressPrecent;
    }

    public void setTurnIconWeight(int turnIconWeight) {
        this.turnIconWeight = turnIconWeight;
    }

    public int getTurnIconWeight() {
        return turnIconWeight;
    }

    public void setTurnIconHeight(int turnIconHeight) {
        this.turnIconHeight = turnIconHeight;
    }

    public int getTurnIconHeight() {
        return turnIconHeight;
    }

    public void setCameraPenalty(boolean cameraPenalty) {
        this.cameraPenalty = cameraPenalty;
    }

    public boolean getCameraPenalty() {
        return cameraPenalty;
    }

    public void setNextRoadNOAOrNot(boolean nextRoadNOAOrNot) {
        this.nextRoadNOAOrNot = nextRoadNOAOrNot;
    }

    public boolean getNextRoadNOAOrNot() {
        return nextRoadNOAOrNot;
    }

    public void setNewCamera(boolean newCamera) {
        this.newCamera = newCamera;
    }

    public boolean getNewCamera() {
        return newCamera;
    }

    public void setCameraID(long cameraID) {
        this.cameraID = cameraID;
    }

    public long getCameraID() {
        return cameraID;
    }

    public void setEndPOIName(String endPOIName) {
        this.endPOIName = endPOIName;
    }

    public String getEndPOIName() {
        return endPOIName;
    }

    public void setEndPOIAddr(String endPOIAddr) {
        this.endPOIAddr = endPOIAddr;
    }

    public String getEndPOIAddr() {
        return endPOIAddr;
    }

    public void setEndPOIType(String endPOIType) {
        this.endPOIType = endPOIType;
    }

    public String getEndPOIType() {
        return endPOIType;
    }

    public void setEndPOILongitude(double endPOILongitude) {
        this.endPOILongitude = endPOILongitude;
    }

    public double getEndPOILongitude() {
        return endPOILongitude;
    }

    public void setEndPOILatitude(double endPOILatitude) {
        this.endPOILatitude = endPOILatitude;
    }

    public double getEndPOILatitude() {
        return endPOILatitude;
    }

    public void setArrivePOIType(String arrivePOIType) {
        this.arrivePOIType = arrivePOIType;
    }

    public String getArrivePOIType() {
        return arrivePOIType;
    }

    public void setArrivePOILongitude(double arrivePOILongitude) {
        this.arrivePOILongitude = arrivePOILongitude;
    }

    public double getArrivePOILongitude() {
        return arrivePOILongitude;
    }

    public void setArrivePOILatitude(double arrivePOILatitude) {
        this.arrivePOILatitude = arrivePOILatitude;
    }

    public double getArrivePOILatitude() {
        return arrivePOILatitude;
    }

    public void setViaPOItime(int viaPOItime) {
        this.viaPOItime = viaPOItime;
    }

    public int getViaPOItime() {
        return viaPOItime;
    }

    public void setViaPOIdistance(int viaPOIdistance) {
        this.viaPOIdistance = viaPOIdistance;
    }

    public int getViaPOIdistance() {
        return viaPOIdistance;
    }

    public void setEndPOICityName(String endPOICityName) {
        this.endPOICityName = endPOICityName;
    }

    public String getEndPOICityName() {
        return endPOICityName;
    }

    public void setEndPOIDistrictName(String endPOIDistrictName) {
        this.endPOIDistrictName = endPOIDistrictName;
    }

    public String getEndPOIDistrictName() {
        return endPOIDistrictName;
    }

    public void setViaPOIArrivalTime(String viaPOIArrivalTime) {
        this.viaPOIArrivalTime = viaPOIArrivalTime;
    }

    public String getViaPOIArrivalTime() {
        return viaPOIArrivalTime;
    }

    public void setAddIcon(String addIcon) {
        this.addIcon = addIcon;
    }

    public String getAddIcon() {
        return addIcon;
    }

    public void setNextNextAddIcon(String nextNextAddIcon) {
        this.nextNextAddIcon = nextNextAddIcon;
    }

    public String getNextNextAddIcon() {
        return nextNextAddIcon;
    }

    public void setNextSegRemainDisAuto(String nextSegRemainDisAuto) {
        this.nextSegRemainDisAuto = nextSegRemainDisAuto;
    }

    public String getNextSegRemainDisAuto() {
        return nextSegRemainDisAuto;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }
    public static Object parseFrom(JSONObject jsonObject) {
        GuideInfo roadInfo = new GuideInfo();
        if (jsonObject == null) {
            return roadInfo;
        }
        roadInfo.icon = jsonObject.optInt("icon");
        roadInfo.type = jsonObject.optInt("type");
        roadInfo.curRoadName = jsonObject.optString("curRoadName");
        roadInfo.nextRoadName = jsonObject.optString("nextRoadName");
        roadInfo.endPOIName = jsonObject.optString("endPOIName");
        roadInfo.endPOIAddr = jsonObject.optString("endPOIAddr");
        roadInfo.endPOICityName = jsonObject.optString("endPOICityName");
        roadInfo.etaText = jsonObject.optString("etaText");
        roadInfo.routeRemainDistanceAuto = jsonObject.optString("routeRemainDistanceAuto");
        roadInfo.routeRemainDis = jsonObject.optInt("routeRemainDis");
        roadInfo.routeRemainTime = jsonObject.optInt("routeRemainTime");
        roadInfo.routeRemainTimeAuto = jsonObject.optString("routeRemainTimeAuto");
        roadInfo.segRemainDisAuto = jsonObject.optString("segRemainDisAuto");
        roadInfo.segRemainDis = jsonObject.optInt("segRemainDis");
        return roadInfo;
    }
}
