package com.chinatsp.driveinfo;

public class DriveInfo {
    private float drivingMileage;
    private int drivingTime;
    private float oilConsumption;
    private int maintenanceMileage;
    private String healthyLevel;
    private int ranking;


    public DriveInfo(float drivingMileage, int drivingTime, float oilConsumption, int maintenanceMileage, String healthyLevel, int ranking) {
        this.drivingMileage = drivingMileage;
        this.drivingTime = drivingTime;
        this.oilConsumption = oilConsumption;
        this.maintenanceMileage = maintenanceMileage;
        this.healthyLevel = healthyLevel;
        this.ranking = ranking;
    }

    public float getDrivingMileage() {
        return drivingMileage;
    }

    public void setDrivingMileage(float drivingMileage) {
        this.drivingMileage = drivingMileage;
    }

    public int getDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(int drivingTime) {
        this.drivingTime = drivingTime;
    }

    public int getMaintenanceMileage() {
        return maintenanceMileage;
    }

    public void setMaintenanceMileage(int maintenanceMileage) {
        this.maintenanceMileage = maintenanceMileage;
    }

    public String getHealthyLevel() {
        return healthyLevel;
    }

    public void setHealthyLevel(String healthyLevel) {
        this.healthyLevel = healthyLevel;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public float getOilConsumption() {
        return oilConsumption;
    }

    public void setOilConsumption(float oilConsumption) {
        this.oilConsumption = oilConsumption;
    }
}
