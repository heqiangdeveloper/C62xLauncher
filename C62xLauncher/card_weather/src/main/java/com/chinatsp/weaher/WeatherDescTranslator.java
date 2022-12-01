package com.chinatsp.weaher;

import java.util.HashMap;

public class WeatherDescTranslator {
    HashMap<String, Integer> resMap = new HashMap<>();

    public WeatherDescTranslator() {
        resMap.put("晴", R.string.weather_desc_sunny);
        resMap.put("多云", R.string.weather_desc_cloudy);
        resMap.put("雨", R.string.weather_desc_rain);
        resMap.put("雾", R.string.weather_desc_fog);
        resMap.put("雪", R.string.weather_desc_snow);
        resMap.put("雷阵雨", R.string.weather_desc_thunder_storm);
        resMap.put("阴", R.string.weather_desc_overcast);
        resMap.put("雾霾", R.string.weather_desc_haze);
        resMap.put("风", R.string.weather_desc_windy);
        resMap.put("未知", R.string.weather_desc_unknown);
        resMap.put("小雨", R.string.weather_desc_light_rain);
        resMap.put("中雨", R.string.weather_desc_moderate_rain);
        resMap.put("大雨", R.string.weather_desc_heavy_rain);
        resMap.put("暴雨", R.string.weather_desc_rain_storm);
        resMap.put("阵雨", R.string.weather_desc_shower);
        resMap.put("小雪", R.string.weather_desc_light_snow);
        resMap.put("中雪", R.string.weather_desc_moderate_snow);
        resMap.put("大雪", R.string.weather_desc_heavy_snow);
        resMap.put("暴雪", R.string.weather_desc_blizzard);
        resMap.put("雨夹雪", R.string.weather_desc_sleet);
        resMap.put("冰雹", R.string.weather_desc_hail);
    }

    public int getWeatherDescription(String origin) {
        if (origin == null || origin.isEmpty()) {
            return R.string.weather_desc_unknown;
        }
        Integer val = resMap.get(origin);
        return val != null ? val : -1;
    }
}
