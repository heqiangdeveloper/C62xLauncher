package com.chinatsp.weaher.repository;

public class WeatherBean {

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_SUNNY = 1;
    public static final int TYPE_CLOUDY = 2;
    public static final int TYPE_RAIN = 3;
    public static final int TYPE_FOG = 4;
    public static final int TYPE_SNOW = 5;
    public static final int TYPE_THUNDER_SHOWER = 6;
    public static final int TYPE_SMOG = 7;
    public static final int TYPE_OVERCAST = 8;
    public static final int TYPE_WINDY = 9;

    private int type;
    private String temperatureDesc;
    private String date;
    private String city;

    public WeatherBean(int type, String temperatureDesc, String date, String city) {
        this.type = type;
        this.temperatureDesc = temperatureDesc;
        this.date = date;
        this.city = city;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTemperatureDesc() {
        return temperatureDesc;
    }

    public void setTemperatureDesc(String temperatureDesc) {
        this.temperatureDesc = temperatureDesc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
