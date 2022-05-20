package com.chinatsp.weaher.weekday;

public class DayWeatherBean {
    private String dayOfWeek;
    private int type;
    private String word;
    private String temperatureDesc;

    public DayWeatherBean(String dayOfWeek, int type, String word, String temperatureDesc) {
        this.dayOfWeek = dayOfWeek;
        this.type = type;
        this.word = word;
        this.temperatureDesc = temperatureDesc;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTemperatureDesc() {
        return temperatureDesc;
    }

    public void setTemperatureDesc(String temperatureDesc) {
        this.temperatureDesc = temperatureDesc;
    }
}
