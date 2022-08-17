package com.common.xui.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class ThreadUtil {

    private static ThreadUtil instance;
    private Handler mWorkThrredHandler;
    private final Handler mMainThreadHandler;

    private ThreadUtil(){
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        HandlerThread thread = new HandlerThread("work_thread");
        thread.start();
        this.mWorkThrredHandler = new Handler(thread.getLooper());
    }

    public static ThreadUtil getInstance() {
        synchronized (ThreadUtil.class) {
            if (instance == null) {
                synchronized (ThreadUtil.class) {
                    instance = new ThreadUtil();
                }
            }
        }
        return instance;
    }

    public void postToMainThread(Runnable runnable) {
        mMainThreadHandler.post(runnable);
    }

    public void postToMainThread(Runnable runnable, long delayTime) {
        mMainThreadHandler.postDelayed(runnable, delayTime);
    }

    public Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public void postToWorkThread(Runnable runnable, long delayTime) {
        mWorkThrredHandler.postDelayed(runnable, delayTime);
    }


    public void postToWorkThread(Runnable runnable) {
        mWorkThrredHandler.post(runnable);
    }

    public Handler getWorkThrredHandler() {
        return mWorkThrredHandler;
    }

}
