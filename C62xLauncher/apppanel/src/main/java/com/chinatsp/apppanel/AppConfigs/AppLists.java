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
    public static List<String> blackListApps = Arrays.asList(
            aospSettings,
            launcher,
            CarTrustAgentService,
            AVMCalibration,
            aospFilemanager,
            tfactory,
            subscriber,
            avmDemo,
            carcorderdemo
    );
}
