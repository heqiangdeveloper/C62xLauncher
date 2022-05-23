package com.chinatsp.settinglib;

import android.util.Log;

/**
 * @author common
 */
public class LogUtils {

    public static final String TAG = "VehicleSettings";
    public static final String TAG_OTHER = "OtherInfo";
    public static final String TAG_OTHER_INFO = "Other_Info";
    public static void d(String tag, String log) {
        Log.d(TAG + "-" + tag, log);
    }

    public static void e(String tag, String log) {
        Log.e(TAG + "-" + tag, log);
    }

    public static void w(String tag, String log) {
        Log.w(TAG + "-" + tag, log);
    }

    public static void d(String log) {
        Log.d(TAG, log);
    }

    public static void e(String log) {
        Log.e(TAG, log);
    }

    public static void dd(String tag, String log) {
        Log.d(TAG_OTHER + "-" + tag, log);
    }

    public static void ddd(String tag, String log) {
        Log.d(TAG_OTHER_INFO + "-" + tag, log);
    }

}
