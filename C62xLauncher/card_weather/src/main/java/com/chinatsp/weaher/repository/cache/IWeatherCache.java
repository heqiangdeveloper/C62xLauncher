package com.chinatsp.weaher.repository.cache;

import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.List;

public interface IWeatherCache {

    void saveCityList(List<String> cityList);

    void saveCityWeather(String city, List<WeatherInfo> weather);

    void saveDefaultWeather(List<WeatherInfo> weather);

    List<String> getCityList();

    List<WeatherInfo> getWeatherByCity(String city);

    List<WeatherInfo> getDefaultWeather();

}
