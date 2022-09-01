package com.chinatsp.launcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.chinatsp.apppanel.ApppanelActivity;
import com.chinatsp.apppanel.receiver.AppInstallStatusReceiver;
import com.chinatsp.iquting.receiver.BootBroadcastReceiver;

public class CarLauncher extends AppCompatActivity implements OnGestureAction {
    private IntentFilter intentFilter;
    private AppInstallStatusReceiver receiver;
    private BootBroadcastReceiver bootBroadcastReceiver;
    GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //监听开机广播
        registerBootBroadcast();
        //注册监听APP安装卸载广播
        registerAppInstallBroadcast();
        initVersionInfo();

        mGestureDetector = new GestureDetector(new SlideGestureListener(this, this));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(bootBroadcastReceiver);
    }

    @Override
    public void goAppPanel() {
        startActivity(new Intent(CarLauncher.this, ApppanelActivity.class));
    }
}