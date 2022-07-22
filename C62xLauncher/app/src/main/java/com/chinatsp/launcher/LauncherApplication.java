package com.chinatsp.launcher;

import android.app.Application;

import com.chinatsp.widgetcards.manager.CardManager;

import card.theme.ThemeService;
import launcher.base.service.AppServiceManager;

public class LauncherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initServices();
    }

    private void initServices() {
        CardManager.getInstance().init(this);
        AppServiceManager.addService(AppServiceManager.SERVICE_THEME, new ThemeService(this));
    }
}
