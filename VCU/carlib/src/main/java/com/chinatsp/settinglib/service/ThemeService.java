package com.chinatsp.settinglib.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.chinatsp.settinglib.listener.IThemeChangeListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ThemeService implements IThemeService {

    private final String THEME_KEY = "system_theme";
    private final String TAG = "ThemeService";
    private static final int SHOW_MODE_NIGHT = 0;
    private static final int SHOW_MODE_DAY = 1;


    private final Map<String, IThemeChangeListener> listeners = new ConcurrentHashMap<>();
    private ContentObserver mContentObserver;
    private Context mContext;

    public ThemeService(Context context) {
        this.mContext = context.getApplicationContext();
        init();
    }

    public void init() {
        if (mContentObserver == null) {
            ContentResolver resolver = mContext.getContentResolver();
            Uri uri = Settings.System.getUriFor(THEME_KEY);
            mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    boolean night = readSysNightDayMode(resolver);
                    Log.e(TAG, "updateShowMode night:" + night + " , ...." + hashCode());
                    notifyListeners(night);
                }
            };
            resolver.registerContentObserver(uri, false, mContentObserver);
        }
    }

    private void notifyListeners(boolean night) {
        for (String tag : listeners.keySet()) {
            IThemeChangeListener listener = listeners.get(tag);
            if (listener != null) {
                listener.onChange(night);
            }
        }
    }

    public boolean isNight() {
        return readSysNightDayMode(mContext.getContentResolver());
    }

    @Override
    public void addListener(String tag, IThemeChangeListener listener) {
        if (TextUtils.isEmpty(tag) || listener == null) {
            Log.w(TAG, "registerListener fail: tag is empty or listener is null.");
            return;
        }
        listeners.put(tag, listener);
    }

    @Override
    public void removeListener(String tag) {
        if (TextUtils.isEmpty(tag)) {
            Log.w(TAG, "unregisterListener fail: tag is empty.");
            return;
        }
        listeners.remove(tag);
    }

    private boolean readSysNightDayMode(ContentResolver resolver) {
        String theme = Settings.System.getString(resolver, THEME_KEY);
        if (TextUtils.isEmpty(theme)) {
            theme = C62xThemes.SYSTEM_THEME_TYPE0;
        }
        return theme.equals(C62xThemes.SYSTEM_THEME_TYPE0);
    }

    @Override
    public void onStopListen() {
        ContentResolver resolver = mContext.getContentResolver();
        if (mContentObserver != null) {
            resolver.unregisterContentObserver(mContentObserver);
        }
    }
    public static final class C62xThemes {
        public static final String SYSTEM_THEME_TYPE0 = "type1"; // 经典模式
        public static final String SYSTEM_THEME_TYPE1 = "type2"; // 运动模式
        public static final String SYSTEM_THEME_TYPE2 = "type3"; // 关联驾驶模式
    }
}
