package com.chinatsp.drawer;

import android.view.View;

import androidx.annotation.NonNull;


import com.chinatsp.drawer.apps.AppsDrawerViewHelper;
import com.chinatsp.drawer.weather.WeatherDrawerViewHelper;

import launcher.base.recyclerview.BaseViewHolder;

public class DrawerAppsAndWeatherHolder extends BaseViewHolder<DrawerEntity> {

    private final AppsDrawerViewHelper mAppsDrawerViewHelper;

    public DrawerAppsAndWeatherHolder(@NonNull View itemView) {
        super(itemView);
        mAppsDrawerViewHelper = new AppsDrawerViewHelper(itemView);
        WeatherDrawerViewHelper weatherDrawerViewHelper = new WeatherDrawerViewHelper(itemView);
    }

    @Override
    public void bind(int position, DrawerEntity drawerEntity) {
        super.bind(position, drawerEntity);
        mAppsDrawerViewHelper.onBindData();
    }
}
