package com.chinatsp.weaher;

import android.content.Context;

import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.List;

public interface IWeatherCardView {
    Context getContext();

    void refreshCityList(List<String> cityFromCache);

    void refreshDefault();

    void refreshData(List<WeatherInfo> weatherList);
}
