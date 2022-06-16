package launcher.base.service.theme;

import android.content.Context;

public interface ITheme {
    boolean isNight(Context context);

    boolean registerListener(OnThemeChangeListener listener);

    boolean unregisterListener(OnThemeChangeListener listener);
}
