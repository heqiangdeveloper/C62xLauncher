package com.chinatsp.vehiclesetting;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chinatsp.vehiclesetting.base.BaseActivity;
import com.chinatsp.vehiclesetting.base.FragmentPage;
import com.chinatsp.vehiclesetting.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(android.R.style.Theme_Wallpaper_NoTitleBar);//设置壁纸对应的背景
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //startService(new Intent(this, VehicleService.class));
        Fragment fragment = new FragmentPage();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_fragment, fragment).commit();
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
