package com.chinatsp.drawer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import launcher.base.utils.EasyLog;

public class CollapseController {
    private final Context mContext;
    private final ContentResolver mContentResolver;
    private final String KEY_QS_PANEL = "com.chiantsp.system.KEY_QSPANEL_STATUS";
    private static final int VALUE_OPEN = 1;
    private static final int VALUE_CLOSE = 0;
    private final String TAG = "CollapseController";
    private ICollapseListener mCollapseListener;

    public CollapseController(Context context, ICollapseListener collapseListener) {
        mContext = context;
        mCollapseListener = collapseListener;
        mContentResolver = mContext.getContentResolver();
        this.mCollapseListener = collapseListener;
    }

    private final ContentObserver mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            int value = Settings.Global.getInt(mContentResolver, KEY_QS_PANEL, 0);
            EasyLog.d(TAG, "onChange value:" + value);
            if (value == VALUE_OPEN) {
                // 打开
                onOpen();
            } else if (value == VALUE_CLOSE) {
                // 关闭
                onClose();
            }
        }


    };

    private void onOpen() {
        EasyLog.d(TAG, "onOpen");

    }

    private void onClose() {
        EasyLog.d(TAG, "onClose");
        if (mCollapseListener != null) {
            mCollapseListener.onCollapse();
        }
    }


    public void register() {
        Uri uri = Settings.Global.getUriFor(KEY_QS_PANEL);
        EasyLog.d(TAG, "register " + uri);
        mContentResolver.registerContentObserver(uri, false, mObserver);
    }

    public void unRegister() {
        Uri uri = Settings.Global.getUriFor(KEY_QS_PANEL);
        EasyLog.d(TAG, "unRegister " + uri);
        mContentResolver.registerContentObserver(uri, false, mObserver);
    }
}
