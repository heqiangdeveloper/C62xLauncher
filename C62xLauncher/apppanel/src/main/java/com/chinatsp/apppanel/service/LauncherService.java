package com.chinatsp.apppanel.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

//此服务给应用商城连接，连接成功后，应该商城会将下载信息通知到LauncherBinder中
public class LauncherService extends Service {
    private static final String TAG = "LauncherService";
    private static LauncherBinder launcherBinder;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        launcherBinder = LauncherBinder.getInstance(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return launcherBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
