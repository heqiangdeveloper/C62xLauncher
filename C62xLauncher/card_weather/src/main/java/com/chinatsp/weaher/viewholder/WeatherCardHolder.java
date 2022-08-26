package com.chinatsp.weaher.viewholder;

import android.view.View;

import com.chinatsp.weaher.repository.WeatherBean;
import com.iflytek.autofly.weather.entity.WeatherInfo;

public abstract class WeatherCardHolder {
    protected View mRootView;

    public WeatherCardHolder(View rootView) {
        mRootView = rootView;
    }

    public abstract void updateDefault();

    public abstract void updateWeather(WeatherInfo weatherInfo);
}
