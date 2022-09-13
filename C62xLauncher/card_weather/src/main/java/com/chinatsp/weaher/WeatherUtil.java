package com.chinatsp.weaher;

import android.content.res.Resources;
import android.text.TextUtils;

import com.chinatsp.weaher.repository.WeatherBean;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.lang.reflect.Constructor;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import launcher.base.utils.EasyLog;

public class WeatherUtil {
    public static final String TAG = "WeatherUtil";

    public static String getToday() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd  EE");
        return df.format(date);
    }

    public static String fixTemperatureDesc(String origin, Resources resources) {
        if (TextUtils.isEmpty(origin)) {
            return origin;
        }
        return origin + "  " + resources.getString(R.string.symbol_celsius);
    }

    public static int getWeekDayTextRes(DayOfWeek dayOfWeek) {
        int[] res = {R.string.weather_monday, R.string.weather_tuesday, R.string.weather_wednesday, R.string.weather_thursday,
                R.string.weather_friday, R.string.weather_saturday, R.string.weather_sunday};
        int index = 0;
        switch (dayOfWeek) {
            case MONDAY:
                index = 0;
                break;
            case TUESDAY:
                index = 1;
                break;
            case WEDNESDAY:
                index = 2;
                break;
            case THURSDAY:
                index = 3;
                break;
            case FRIDAY:
                index = 4;
                break;
            case SATURDAY:
                index = 5;
                break;
            case SUNDAY:
                index = 6;
                break;
        }
        return res[index];
    }

    public static void logD(String message) {
        EasyLog.d(TAG, message);
    }

    public static void logI(String message) {
        EasyLog.i(TAG, message);
    }

    public static void logE(String message) {
        EasyLog.e(TAG, message);
    }

    public static void logV(String message) {
        EasyLog.v(TAG, message);
    }

    public static void logW(String message) {
        EasyLog.w(TAG, message);
    }

    public static String parseTemperature(String temperatureNumStr, Resources resources) {
        try {
            int temperature = (int) Float.parseFloat(temperatureNumStr);
            return temperature + resources.getString(R.string.symbol_celsius);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resources.getString(R.string.weather_desc_unknown);
    }
    public static String getTemperatureRange(WeatherInfo dayWeatherBean, Resources resources) {
        String low = WeatherUtil.parseTemperature(dayWeatherBean.getLow(), resources);
        String high = WeatherUtil.parseTemperature(dayWeatherBean.getHigh(), resources);
        return low + "~" + high;
    }

    public static int getWeatherType(String type) {
        try {
            return Integer.parseInt(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WeatherBean.TYPE_UNKNOWN;
    }
}
