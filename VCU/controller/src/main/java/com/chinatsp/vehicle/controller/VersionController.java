package com.chinatsp.vehicle.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

public class VersionController {
    private final ContentResolver mContentResolver;
    private final String VERSION_KEY = "com.chinatsp.systemui.key_update";
    private ICollapseListener mCollapseListener;
    /**
     * 不显示图标
     */
    private static final int STATUS_HIDE = -1;

    /**
     * 有新版本可更新
     */
    private static final int STATUS_AVAILABLE = 0;

    /**
     * 有预约安装
     */
    private static final int STATUS_AVAILABLE1 = 1;

    /**
     * 有下载提醒
     */
    private static final int STATUS_DOWNLOADING_NOTICE = 2;

    /**
     * 下载中
     */
    private static final int STATUS_DOWNLOADING = 3;

    /**
     * 下载暂停
     */
    private static final int STATUS_DOWNLOADING_PAUSE = 4;

    /**
     * 下载异常
     */
    private static final int STATUS_DOWNLOADING_ERROR = 5;

    /**
     * 下载完成
     */
    private static final int STATUS_DOWNLOADING_FINISH = 6;

    public VersionController(Context context, ICollapseListener collapseListener) {
        mCollapseListener = collapseListener;
        mContentResolver = context.getContentResolver();
        this.mCollapseListener = collapseListener;
    }

    private final ContentObserver mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            int value = Settings.System.getInt(mContentResolver, VERSION_KEY, STATUS_HIDE);
            Log.i("VersionController", "value: " + value);
            if (mCollapseListener != null) {
                mCollapseListener.onCollapse(value);
            }
        }
    };

    public void register() {
        Log.i("VersionController","register: ");
        Uri uri = Settings.System.getUriFor(VERSION_KEY);
        mContentResolver.registerContentObserver(uri, false, mObserver);
    }

    public void unRegister() {
        Uri uri = Settings.System.getUriFor(VERSION_KEY);
        mContentResolver.registerContentObserver(uri, false, mObserver);
    }
}
