package com.chinatsp.settinglib.service;

import com.chinatsp.settinglib.listener.IThemeChangeListener;

public interface IThemeService {
    boolean isNight();

    void addListener(String tag, IThemeChangeListener listener);

    void removeListener(String tag);

    void onStopListen();
}
