package com.chinatsp.launcher;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import com.chinatsp.carservice.AppCarService;
import com.chinatsp.drawer.search.manager.SearchManager;
import com.chinatsp.iquting.service.IqutingBindService;
import com.chinatsp.iquting.service.TencentSdkService;
import com.chinatsp.navigation.another.EasyConnNaviController;
import com.chinatsp.navigation.repository.NaviRepository;
import com.chinatsp.widgetcards.manager.CardManager;

import card.theme.ThemeService;
import launcher.base.applists.AppLists;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.service.AppServiceManager;
import launcher.base.service.platform.PlatformService;
import launcher.base.utils.EasyLog;

public class LauncherApplication extends Application {
    private static final String TAG = "Launcher_version";
    private static final String LAUNCHER_BOOT_ACTION = "com.chinatsp.launcher.bootCompleted";

    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        initLog();
        initServices();
        // 发出本应用启动前台的广播
        sendLauncherBroadcast();
        initNavigation();
    }

    private void initServices() {
        CardManager.getInstance().init(this);
        SearchManager.getInstance().init(this);
        AppServiceManager.addService(AppServiceManager.SERVICE_PLATFORM, new PlatformService());
        AppServiceManager.addService(AppServiceManager.SERVICE_THEME, new ThemeService(this));
        AppServiceManager.addService(AppServiceManager.SERVICE_CAR, new AppCarService(this));
        AppServiceManager.addService(AppServiceManager.SERVICE_TENCENT_SDK, new TencentSdkService());
        NetworkStateReceiver.getInstance().registerReceiver(this);//注册网络监听
        IqutingBindService.getInstance().bindPlayService(this);//注册爱趣听播放服务
        IqutingBindService.getInstance().bindContentService(this);//注册爱趣听内容服务
        //改为静态注册方式
        //registChangeSourceBroadcast();//注册爱趣听接受切源的广播
    }

    private void initLog() {
        int versionCode = getVersionCode(mContext);
        String versionName = getVersionName(mContext);
        Log.d(TAG, "APP_INFO VersionCode:" + versionCode + ",VersionName:" + versionName);
        EasyLog.appendOriginTag(versionCode+"");
    }

    public int getVersionCode(Context context) {
        if (context == null) return 0;
        int code = 0;
        try {
            code = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public String getVersionName(Context context) {
        if (context == null) return "";
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void sendLauncherBroadcast() {
        Intent intent = new Intent();
        intent.setPackage(AppLists.media);
        intent.setAction(LAUNCHER_BOOT_ACTION);
        // 避免静态注册的监听器收不到广播
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        // 设定为前台广播
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        EasyLog.i(TAG, "sendLauncherBroadcast : "+LAUNCHER_BOOT_ACTION);
        intent.setComponent(new ComponentName(AppLists.media, "com.chinatsp.media.broadcast.RemoteKeyReceiver"));
        sendBroadcast(intent);
    }

    private void registChangeSourceBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ChangeSourceReceiver.AQT_PLAY_ACTION);
        intentFilter.addAction(ChangeSourceReceiver.HARD_KEY_ACTION);
        registerReceiver(new ChangeSourceReceiver(),intentFilter);
    }

    private void initNavigation() {
        NaviRepository.getInstance().init(this);
        EasyConnNaviController.getInstance().init(this);
    }
}
