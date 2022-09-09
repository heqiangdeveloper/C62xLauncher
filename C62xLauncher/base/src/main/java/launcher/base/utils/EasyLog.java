package launcher.base.utils;

import android.util.Log;

import java.util.List;

public class EasyLog {

    public static boolean enableLog = true;
    public static final String ORIGIN_TAG = "HBTechLauncher";
    public static void d(String tag,String msg) {
        if (enableLog) {
            Log.d(ORIGIN_TAG, "["+tag+"] "+msg);
        }
    }

    public static void i(String tag,String msg) {
        if (enableLog) {
            Log.i(ORIGIN_TAG, "["+tag+"] "+msg);
        }
    }
    public static void e(String tag,String msg) {
        if (enableLog) {
            Log.e(ORIGIN_TAG, "["+tag+"] "+msg);
        }
    }

    public static void w(String tag, String msg) {
        if (enableLog) {
            Log.w(ORIGIN_TAG, "["+tag+"] "+msg);
        }
    }
    public static void v(String tag, String msg) {
        if (enableLog) {
            Log.v(ORIGIN_TAG, "["+tag+"] "+msg);
        }
    }

    public static void printStack(String tag) {
        if (enableLog) {
            Log.d(ORIGIN_TAG, "[" + tag + "] printStack: ");
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                Log.d(ORIGIN_TAG, "[" + tag + "] " + stackTraceElement);
            }
        }
    }
}
