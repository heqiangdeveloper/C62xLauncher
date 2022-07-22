package launcher.base.service.theme;

import android.content.Context;

public interface IThemeService {
    boolean isNight();

    void addListener(String tag, OnThemeChangeListener listener);

    void removeListener(String tag);

    void onStopListen();
}
