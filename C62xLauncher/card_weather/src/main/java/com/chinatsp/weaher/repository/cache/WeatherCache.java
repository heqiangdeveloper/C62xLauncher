package com.chinatsp.weaher.repository.cache;

import android.text.TextUtils;

import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WeatherCache implements IWeatherCache{
    private final List<String> mCityList = new ArrayList<>();
    private List<WeatherInfo> mDefaultWeather;
    private final ConcurrentHashMap<String, List<WeatherInfo>> mWeatherMap = new ConcurrentHashMap<>();
    @Override
    public void saveCityList(List<String> cityList) {
        mCityList.clear();
        if (cityList != null && !cityList.isEmpty()) {
            mCityList.addAll(cityList);
        }
    }

    @Override
    public void saveCityWeather(String city, List<WeatherInfo> weather) {
        if (TextUtils.isEmpty(city) || weather == null) {
            return;
        }
        mWeatherMap.put(city, weather);
    }

    @Override
    public void saveDefaultWeather(List<WeatherInfo> weather) {
        mDefaultWeather = weather;
    }

    @Override
    public List<String> getCityList() {
        return mCityList;
    }

    @Override
    public List<WeatherInfo> getWeatherByCity(String city) {
        return mWeatherMap.get(city);
    }

    @Override
    public List<WeatherInfo> getDefaultWeather() {
        return mDefaultWeather;
    }
}
