
package com.chinatsp.vehiclesetting;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.chinatsp.settinglib.CarAdapter;
import com.chinatsp.settinglib.LogUtils;
import com.chinatsp.settinglib.SettingManager;

public class VehicleService extends Service {

    private HandlerThread mWorkThread = null;
    private Handler mWorkHandler = null;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("VehicleService onCreate");
        SettingManager.init(getApplicationContext());

        mWorkThread = new HandlerThread("thread_standby");
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper());

        SettingManager.getInstance().addCarListener(new CarAdapter() {

            @Override
            public void onCarServiceBound(boolean isBound) {

            }

            @Override
            public void onAccStatusChange(boolean isAccOn) {

            }

            @Override
            public void onPhotoReqChange(boolean isOn) {

            }

            @Override
            public void onReverseStatusChange(boolean on) {

            }

            @Override
            public void onAvmStatusChange(boolean on) {

            }
        });


    }

    @Override
    public void onDestroy() {
        mWorkThread.quitSafely();
        super.onDestroy();
    }

    class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
