package com.chinatsp.vehicle.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

public class CollapseController {
    private final ContentResolver mContentResolver;
    private final String KEY_QS_PANEL = "com.chiantsp.system.KEY_QSPANEL_STATUS";
    private static final int VALUE_OPEN = 1;
    private static final int VALUE_CLOSE = 0;
    private ICollapseListener mCollapseListener;

    public CollapseController(Context context, ICollapseListener collapseListener) {
        mCollapseListener = collapseListener;
        mContentResolver = context.getContentResolver();
        this.mCollapseListener = collapseListener;
    }

    private final ContentObserver mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            int value = Settings.Global.getInt(mContentResolver, KEY_QS_PANEL, 0);
            if (value == VALUE_OPEN) {
                // 打开
                onOpen();
                Log.i("CollapseController", "open");
            } else if (value == VALUE_CLOSE) {
                // 关闭
                onClose();
                Log.i("CollapseController", "close");
            }
        }


    };

    private void onOpen() {

    }

    private void onClose() {
        if (mCollapseListener != null) {
            mCollapseListener.onCollapse(0);
        }
    }


    public void register() {
        Uri uri = Settings.Global.getUriFor(KEY_QS_PANEL);
        mContentResolver.registerContentObserver(uri, false, mObserver);
    }

    public void unRegister() {
        Uri uri = Settings.Global.getUriFor(KEY_QS_PANEL);
        mContentResolver.registerContentObserver(uri, false, mObserver);
    }
}
