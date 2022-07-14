package com.chinatsp.drawer;

import android.view.View;

import androidx.annotation.NonNull;


import com.chinatsp.drawer.apps.AppsDrawerViewHelper;
import com.chinatsp.drawer.weather.WeatherDrawerViewHelper;

import launcher.base.recyclerview.BaseViewHolder;

public class DrawerAppsAndWeatherHolder extends BaseViewHolder<DrawerEntity> {
    public DrawerAppsAndWeatherHolder(@NonNull View itemView) {
        super(itemView);
        AppsDrawerViewHelper appsDrawerViewHelper = new AppsDrawerViewHelper(itemView);
        WeatherDrawerViewHelper weatherDrawerViewHelper = new WeatherDrawerViewHelper(itemView);
    }
}
