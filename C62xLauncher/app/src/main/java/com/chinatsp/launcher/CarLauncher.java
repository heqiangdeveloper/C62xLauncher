package com.chinatsp.launcher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.chinatsp.apppanel.AppConfigs.Constant;
import com.chinatsp.apppanel.ApppanelActivity;
import com.chinatsp.apppanel.receiver.AppInstallStatusReceiver;
import com.chinatsp.apppanel.receiver.AppManagementReceiver;
import com.chinatsp.apppanel.service.AppStoreService;
import com.chinatsp.apppanel.service.LauncherService;
import com.chinatsp.iquting.receiver.BootBroadcastReceiver;

import launcher.base.applists.AppLists;
import launcher.base.utils.EasyLog;

public class CarLauncher extends AppCompatActivity implements OnGestureAction {
    private static final String TAG = CarLauncher.class.getName();
    private IntentFilter intentFilter;
    private AppInstallStatusReceiver receiver;
    private BootBroadcastReceiver bootBroadcastReceiver;
    private AppManagementReceiver appManagementReceiver;
    GestureDetector mGestureDetector;
    private Intent launcherServiceintent;
    private AppStoreService appStoreService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //监听开机广播
        registerBootBroadcast();
        //注册监听APP安装卸载广播
        registerAppInstallBroadcast();
        //注册监听打开或关闭应用管理的广播
        registerAppManagementBroadcast();
        //启动launcher服务，让应用商城连接
        startLauncherService();
        //绑定应用市场服务
        registerAppStoreService(getApplicationContext());
        initVersionInfo();

        mGestureDetector = new GestureDetector(new SlideGestureListener(this, this));

        EasyLog.d(TAG, "onCreate ... Hashcode:"+hashCode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo runningTaskInfo = activityManager.getRunningTasks(1).get(0);
        EasyLog.d(TAG, "onResume ... Hashcode:"+hashCode()+" , task: "+runningTaskInfo.id);
//        EasyLog.d(TAG, "onResume ... Hashcode:"+hashCode());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private void initVersionInfo() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        PackageManager pm = getPackageManager();
        TextView tvVersionName = findViewById(R.id.tvVersionName);
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            if (!TextUtils.isEmpty(packageInfo.versionName)) {
                tvVersionName.setText("版本 : Version " + packageInfo.versionName +" for debug"+ " \n" + "版本码: " + packageInfo.versionCode);
                tvVersionName.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.btnTestGoAppPanel).setVisibility(View.VISIBLE);
    }

    public void toApppanel(View view){
        startActivity(new Intent(CarLauncher.this, ApppanelActivity.class));
    }

    private void registerAppInstallBroadcast(){
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        receiver = new AppInstallStatusReceiver();
        registerReceiver(receiver,intentFilter);
    }

    private void registerBootBroadcast(){
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        bootBroadcastReceiver = new BootBroadcastReceiver();
        registerReceiver(bootBroadcastReceiver,intentFilter);
    }

    private void registerAppManagementBroadcast(){
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.APPMANEGEMENTBROADCAST);
        appManagementReceiver = new AppManagementReceiver();
        registerReceiver(appManagementReceiver,intentFilter);
    }

    private void registerAppStoreService(Context context){
        appStoreService = AppStoreService.getInstance(context);
        appStoreService.bindService();
    }

    private void unRegisterAppStoreService(){
        appStoreService.unbindService();
    }

    private void startLauncherService(){
        launcherServiceintent = new Intent(this, LauncherService.class);
        startService(launcherServiceintent);
    }

    private void stopLauncherService(){
        stopService(launcherServiceintent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(bootBroadcastReceiver);
        unregisterReceiver(appManagementReceiver);
        stopLauncherService();
        unRegisterAppStoreService();
        EasyLog.d(TAG, "onDestroy ... Hashcode:"+hashCode());
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyLog.d(TAG, "onStop ... Hashcode:"+hashCode());
    }

    @Override
    public void onPause() {
        super.onPause();
        EasyLog.d(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        EasyLog.d(TAG, "onRestart");
    }

    @Override
    public void goAppPanel() {
        startActivity(new Intent(CarLauncher.this, ApppanelActivity.class));
    }
}