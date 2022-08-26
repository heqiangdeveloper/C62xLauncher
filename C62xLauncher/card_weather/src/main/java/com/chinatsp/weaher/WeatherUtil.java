package com.chinatsp.weaher;

import android.content.res.Resources;
import android.text.TextUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import launcher.base.utils.EasyLog;

public class WeatherUtil {
    public static final String TAG = "WeatherUtil";

    public static String getToday() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy.MM.dd  EE");
        return df.format(date);
    }

    public static String fixTemperatureDesc(String origin, Resources resources) {
        if (TextUtils.isEmpty(origin)) {
            return origin;
        }
        return origin + "  " + resources.getString(R.string.symbol_celsius);
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
}
