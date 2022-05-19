package com.chinatsp.vehiclesetting;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.chinatsp.settinglib.LogUtils;
import com.chinatsp.settinglib.SettingManager;


public class MyApplication extends Application {
    public static int W;
    public static int H;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("MyApplication onCreate");
        SettingManager.init(getApplicationContext());

        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        W = dm.widthPixels;
        H = dm.heightPixels;
        LogUtils.d("W:" + W + ",H:" + H);
    }
}
