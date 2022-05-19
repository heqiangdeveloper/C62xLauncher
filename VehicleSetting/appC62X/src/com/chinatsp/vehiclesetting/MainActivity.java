package com.chinatsp.vehiclesetting;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(android.R.style.Theme_Wallpaper_NoTitleBar);//设置壁纸对应的背景
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.setting_main_activity);

        startService(new Intent(this, VehicleService.class));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
