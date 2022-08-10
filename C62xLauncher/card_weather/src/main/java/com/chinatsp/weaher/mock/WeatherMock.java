package com.chinatsp.weaher.mock;

import com.chinatsp.weaher.WeatherBean;

import java.util.LinkedList;
import java.util.List;

public class WeatherMock {
    private static WeatherBean[] mWeatherBeans = new WeatherBean[]{
            new WeatherBean(WeatherBean.TYPE_UNKNOWN, "22 ~ 35","", "重庆"),
            new WeatherBean(WeatherBean.TYPE_SUNNY, "22 ~ 35","", "重庆"),
            new WeatherBean(WeatherBean.TYPE_CLOUDY, "22 ~ 35","", "重庆"),
            new WeatherBean(WeatherBean.TYPE_RAIN, "22 ~ 35","", "重庆"),
            new WeatherBean(WeatherBean.TYPE_FOG, "22 ~ 35","", "重庆"),
            new WeatherBean(WeatherBean.TYPE_SNOW, "22 ~ 35","", "重庆"),
            new WeatherBean(WeatherBean.TYPE_THUNDER_SHOWER, "22 ~ 35","", "重庆"),
            new WeatherBean(WeatherBean.TYPE_SMOG, "22 ~ 35","", "重庆"),
            new WeatherBean(WeatherBean.TYPE_OVERCAST, "22 ~ 35","", "重庆"),
            new WeatherBean(WeatherBean.TYPE_WINDY, "22 ~ 35","", "重庆"),
    };
    private static int mIndex;
    public static WeatherBean getWeatherBean() {
        int size = mWeatherBeans.length;
        int index = mIndex % size;
        mIndex++;
        return mWeatherBeans[index];
    }
}
