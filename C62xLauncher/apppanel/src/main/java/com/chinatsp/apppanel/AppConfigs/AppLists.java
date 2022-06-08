package com.chinatsp.apppanel.AppConfigs;

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
}
