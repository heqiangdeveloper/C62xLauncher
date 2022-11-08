package com.chinatsp.weaher.repository;

import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.List;

import launcher.base.ipc.IRemoteDataCallback;

public interface IWeatherDataCallback extends IRemoteDataCallback {
    void onCityList(List<String> cityList);

    void onWeatherList(List<WeatherInfo> list);

    @Override
    default void notifyData(Object o){

    }
}
