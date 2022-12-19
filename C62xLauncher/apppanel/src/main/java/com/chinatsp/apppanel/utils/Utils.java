package com.chinatsp.apppanel.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

import launcher.base.service.AppServiceManager;
import launcher.base.service.tencentsdk.ITencentSdkService;
import launcher.base.utils.recent.RecentAppHelper;

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
        RecentAppHelper.launchApp(context,packageName);
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
        //DecimalFormat format = new DecimalFormat("###.00");
        DecimalFormat format = new DecimalFormat("###");
        if (bytes / GB > 1) {
            return format.format(bytes / GB) + "GB";
        } else if (bytes / MB > 1) {
            return format.format(bytes / MB) + "MB";
        } else if (bytes / KB > 1) {
            return format.format(bytes / KB) + "KB";
        } else {
            return format.format(bytes) + "B";
        }

    }

    /**
     * 校验字符串是否是纯数字
     * @param str 数字字符串
     * @return boolean
     */
    public static boolean isInteger(String str) {
        String s = "^[-+]?[\\d]*$";
        Pattern pattern = Pattern.compile(s);
        return pattern.matcher(str).matches();
    }

    /*
     *  退出爱趣听UI，但不改变播放状态
     */
    public static void closeWecarFlowUI() {
        ITencentSdkService service =
                (ITencentSdkService) AppServiceManager.getService(AppServiceManager.SERVICE_TENCENT_SDK);
        service.closeUI();
    }

    /**
     * 方法描述：判断某一应用是否正在运行
     * @param context   上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
