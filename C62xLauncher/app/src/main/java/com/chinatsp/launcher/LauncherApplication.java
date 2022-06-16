package com.chinatsp.launcher;

import android.app.Application;

import com.chinatsp.widgetcards.manager.CardManager;

public class LauncherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initServices();
    }

    private void initServices() {
        CardManager.getInstance().init(this);
    }
}
