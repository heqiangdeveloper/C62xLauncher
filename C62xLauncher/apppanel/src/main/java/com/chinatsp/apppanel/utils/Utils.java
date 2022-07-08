package com.chinatsp.apppanel.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class Utils {
    private static final String TAG = Utils.class.toString();
    /**
     * 容量单位相关
     */
    public static final long KB = 1024;
    public static final long MB = KB * KB;
    public static final long GB = MB * KB;
    /*
     *打开应用
     * @param packageName包名
     */
    public static void launchApp(Context context, String packageName){
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        }catch (Exception e){
            Toast.makeText(context,"该应用还未下载",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *强制停止应用程序
     * @param pkgName 包名
     */
    public static void forceStopPackage(Context context,String pkgName){
        try{
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(am, pkgName);
        }catch (Exception e){
            Log.d(TAG, "Exception : " + e);
        }
    }

    /*
    *单位转换
     */
    public static String byte2Format(long bytes) {
        //格式化小数
        DecimalFormat format = new DecimalFormat("###.00");
        if (bytes / GB > 1) {
            return format.format(bytes / GB) + "GB";
        } else if (bytes / MB > 1) {
            return format.format(bytes / MB) + "MB";
        } else if (bytes / KB > 1) {
            return format.format(bytes / KB) + "KB";
        } else {
            return bytes + "B";
        }

    }
}
