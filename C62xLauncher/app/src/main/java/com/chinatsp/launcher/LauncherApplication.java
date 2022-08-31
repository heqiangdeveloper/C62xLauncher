package com.chinatsp.launcher;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.chinatsp.carservice.AppCarService;
import com.chinatsp.iquting.service.IqutingBindService;
import com.chinatsp.widgetcards.manager.CardManager;

import card.theme.ThemeService;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.service.AppServiceManager;
import launcher.base.service.platform.PlatformService;

public class LauncherApplication extends Application {
    private static final String TAG = "Launcher_version";
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        initLog();
        initServices();
    }

    private void initServices() {
        CardManager.getInstance().init(this);
        AppServiceManager.addService(AppServiceManager.SERVICE_PLATFORM, new PlatformService());
        AppServiceManager.addService(AppServiceManager.SERVICE_THEME, new ThemeService(this));
        AppServiceManager.addService(AppServiceManager.SERVICE_CAR, new AppCarService(this));
        NetworkStateReceiver.getInstance().registerReceiver(this);//注册网络监听
        IqutingBindService.getInstance().bindPlayService(this);//注册爱趣听播放服务
        IqutingBindService.getInstance().bindContentService(this);//注册爱趣听内容服务
    }

    private void initLog() {
        int versionCode = getVersionCode(mContext);
        String versionName = getVersionName(mContext);
        Log.d(TAG, "APP_INFO:" + "VersionCode:" + versionCode + ",VersionName:" + versionName);
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
}
