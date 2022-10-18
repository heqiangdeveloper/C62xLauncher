package com.chinatsp.navigation;

import launcher.base.utils.EasyLog;

public class NavigationUtil {
    public static String TAG = "NavigationUtil";
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

    public static String getReadableDistanceKM(int distanceInMetres) {
        return distanceInMetres / 1000 + "km";
    }
    public static String getReadableRemainTime(int timeInSecond) {
        if (timeInSecond <= 60) {
            return "1min";
        }
        int hour = timeInSecond / (3600);
        int minute = (timeInSecond - hour * 3600) / 60;
        StringBuilder result = new StringBuilder() ;
        if (hour > 0) {
            result.append(hour).append("h").append(" ");
        }
        if (minute > 0) {
            result.append(minute).append("min");
        }
        return result.toString();
    }
}
