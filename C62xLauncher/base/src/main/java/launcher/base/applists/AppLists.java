package launcher.base.applists;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

import launcher.base.R;
import launcher.base.utils.property.PropertyUtils;

/*
*  launcher 黑名单
 */
public class AppLists {
    public static final String aospSettings = "com.android.settings";//原生设置
    public static final String launcher = "com.chinatsp.launcher";//luancher
    public static final String CarTrustAgentService = "com.android.car.trust";//CarTrustAgentService
    public static final String AVMCalibration = "com.mediatek.avmcalibration";//AVMCalibration
    public static final String aospFilemanager = "com.android.documentsui";//文件
    public static final String tfactory = "com.chinatsp.tfactoryapp";//工厂设置
    public static final String subscriber = "com.google.android.car.vms.subscriber";//VmsSubscriberClientSample
    public static final String avmDemo = "com.mediatek.avm";//AVMDemo
    public static final String carcorderdemo = "com.mediatek.carcorderdemo";//Carcorder Demo
    public static final String b561Radio = "com.oushang.radio";//b561 Radio

    public static final String btPhone = "com.chinatsp.phone";//蓝牙电话
    public static final String filemanager = "com.chinatsp.filemanager";//文件管理
    public static final String systemSettings = "com.chinatsp.settings";//系统设置
    public static final String vehicleSettings = "com.chinatsp.vehicle.settings";//车辆设置
    public static final String buryPoint = "com.oushang.datastat";//埋点
    public static final String dvr = "com.miren.dvrc62xf06";//DVR
    public static final String iquting = "com.tencent.wecarflow";//爱趣听
    public static final String media = "com.chinatsp.media";//多媒体
    public static final String usercenter = "com.chinatsp.usercenter";//个人中心
    public static final String iot = "com.uaes.adviser";//行车顾问  com.uaes.iot是中间件层，后期会去掉图标
    public static final String ifly = "com.iflytek.autofly.voicecoreservice";//语音
    public static final String userbook = "com.deheshuntian.baic_c62x_f06_om";//电子说明书
    public static final String appmarket = "com.huawei.appmarket.car.landscape";//华为应用商城
    public static final String volcano = "com.bytedance.byteautoservice";//火山车娱
    public static final String amap = "com.autonavi.amapauto";//高德地图
    public static final String easyconn = "net.easyconn";//亿连手机互联
    public static final String weather = "com.iflytek.autofly.weather";//天气
    public static final String applet = "com.iflytek.autofly.applet";//讯飞语音中转服务
    public static final String ota = "com.hmi.beic62.pc";//BEIJING OS,OTA升级用

    public static final String APPMANAGEMENT = "com.chinatsp.appmanagement";//应用管理，自定义的包名，实际不存在此应用
    public static final String WelcomeAnimate = "com.android.welcome";//WelcomeAnimate
    public static final String NEW_PAL_SERVER = "com.tencent.tai.pal.server.app";//NEW_PAL_SERVER
    public static final String Communication = "com.uaes.iot";//Communication
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
            b561Radio,
            applet,
            WelcomeAnimate,
            NEW_PAL_SERVER,
            Communication,
            ota
    );

    //不可删除的应用名单，其他的应用根据是否是系统应用来判断
    public static List<String> cannotUninstallListApps = Arrays.asList(
            APPMANAGEMENT
    );

    //不出现在应用管理中的应用名单,移动至RecentAppHelper中
//    public static List<String> notInAppManageListApps = Arrays.asList(
//            launcher,
//            buryPoint
//    );

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
        if (context == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        if(cannotUninstallListApps.contains(packageName)){
            return true;
        }
        if(!PropertyUtils.checkPkgInstalled(context,packageName)){
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

    /*
    *  根据包名，获取默认的图片
     */
    public static Drawable getResId(Context context, String pkgName){
        int resId = -1;
        switch (pkgName){
            case systemSettings:
                resId = R.drawable.ic_app_settings;
                break;
            case vehicleSettings:
                resId = R.drawable.ic_app_carsettings;
                break;
            case media:
                resId = R.drawable.ic_app_media;
                break;
            case usercenter:
                resId = R.drawable.ic_app_account;
                break;
            case btPhone:
                resId = R.drawable.ic_app_phone;
                break;
            case APPMANAGEMENT:
                resId = R.drawable.ic_appmanagement_new;
                break;
            case iot:
                resId = R.drawable.ic_app_driving_assistant;
                break;
            case dvr:
                resId = R.drawable.ic_app_dvr;
                break;
            case userbook:
                resId = R.drawable.ic_app_instructions;
                break;
            case appmarket:
                resId = R.drawable.ic_app_store;
                break;
            case amap:
                resId = R.drawable.ic_app_map;
                break;
            case easyconn:
                resId = R.drawable.ic_app_yilian;
                break;
//            case weather:
//                resId = R.drawable.ic_app_weather;
//                break;
            default:
                break;
        }
        if(resId == -1){//如果是第三方应用
            return null;
        }else {
            return context.getResources().getDrawable(resId);
        }
    }
}
