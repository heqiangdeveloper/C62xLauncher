package com.chinatsp.launcher;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.chinatsp.widgetcards.manager.CardManager;

import card.theme.ThemeService;
import launcher.base.service.AppServiceManager;

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

    private void initServices() {
        CardManager.getInstance().init(this);
        AppServiceManager.addService(AppServiceManager.SERVICE_THEME, new ThemeService(this));
    }
}
