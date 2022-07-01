package com.chinatsp.apppanel.AppConfigs;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.Arrays;
import java.util.List;

/*
*  launcher 黑名单
 */
public class AppLists {
    private static final String aospSettings = "com.android.settings";//原生设置
    private static final String launcher = "com.chinatsp.launcher";//luancher
    private static final String CarTrustAgentService = "com.android.car.trust";//CarTrustAgentService
    private static final String AVMCalibration = "com.mediatek.avmcalibration";//AVMCalibration
    private static final String aospFilemanager = "com.android.documentsui";//文件
    private static final String tfactory = "com.chinatsp.tfactoryapp";//工厂设置
    private static final String subscriber = "com.google.android.car.vms.subscriber";//VmsSubscriberClientSample
    private static final String avmDemo = "com.mediatek.avm";//AVMDemo
    private static final String carcorderdemo = "com.mediatek.carcorderdemo";//Carcorder Demo
    private static final String b561Radio = "com.oushang.radio";//b561 Radio

    private static final String appstore = "com.cusc.appstore";//应用商城
    private static final String settings = "com.chinatsp.settings";//设置
    private static final String btPhone = "com.chinatsp.phone";//蓝牙电话
    private static final String filemanager = "com.chinatsp.filemanager";//文件管理
    private static final String systemSettings = "com.chinatsp.vehiclesetting";//系统设置
    private static final String vehicleSettings = "com.chinatsp.vehicle.settings";//车辆设置
    //不显示在桌面的应用名单
    public static List<String> blackListApps = Arrays.asList(
            aospSettings,
            launcher,
            CarTrustAgentService,
            AVMCalibration,
            aospFilemanager,
            tfactory,
            subscriber,
            avmDemo,
            carcorderdemo,
            b561Radio
    );

    //不可删除的应用名单
    public static List<String> cannotUninstallListApps = Arrays.asList(
            appstore,
            settings,
            btPhone,
            filemanager,
            systemSettings,
            vehicleSettings
    );

    /*
     *  判断某个应用是否属于黑名单中的应用
     */
    public static boolean isInBlackListApp(String packageName){
        for (String packages:AppLists.blackListApps) {
            if(packages.equals(packageName)){
                return true;
            }
        }
        return false;
    }
    /*
     * 指定应用是否可卸载
     * @packageName 包名
     * @return 1可卸载,0不可卸载
     */
    public static int packageUninstallStatus(String packageName){
        int status = 1;
        for(String packages:AppLists.cannotUninstallListApps){
            if(packages.equals(packageName)){
                status = 0;
                break;
            }
        }
        return status;
    }

    /*
     * 指定应用是否是系统应用
     * @packageName 包名
     * @return true是 false不是
     */
    public static boolean isSystemApplication(Context context, String packageName) {
        if (context == null) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null || packageName == null || packageName.length() == 0) {
            return false;
        }

        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
