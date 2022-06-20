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

public class CarLauncher extends AppCompatActivity {
    private IntentFilter intentFilter;
    private AppInstallStatusReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}