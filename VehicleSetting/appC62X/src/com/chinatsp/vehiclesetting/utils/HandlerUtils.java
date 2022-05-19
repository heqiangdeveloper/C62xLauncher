package com.chinatsp.vehiclesetting.utils;

import android.os.Handler;
import android.os.Looper;

public class HandlerUtils {

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public static Handler get(){
        return mHandler;
    }
}
