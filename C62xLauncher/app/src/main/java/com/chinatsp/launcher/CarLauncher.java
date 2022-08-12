package com.chinatsp.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.chinatsp.apppanel.ApppanelActivity;
import com.chinatsp.apppanel.receiver.AppInstallStatusReceiver;
import com.chinatsp.iquting.receiver.BootBroadcastReceiver;

public class CarLauncher extends AppCompatActivity {
    private IntentFilter intentFilter;
    private AppInstallStatusReceiver receiver;
    private BootBroadcastReceiver bootBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //监听开机广播
        registerBootBroadcast();
        //注册监听APP安装卸载广播
        registerAppInstallBroadcast();
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
}